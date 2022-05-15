package com.mycompany.atmmanagementsys;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

public class ManagerLoanController implements Initializable {
    @FXML
    private TableView<LoanData> LoanTable;
    @FXML
    private TableColumn<LoanData,String> loanID ;
    @FXML
    private TableColumn<LoanData,String> accountNo ;
    @FXML
    private TableColumn<LoanData,String> reason ;
    @FXML
    private TableColumn<LoanData,String> status ;
    @FXML
    private TableColumn<LoanData,String> loanAmount ;
    @FXML
    private Label wrongUsr;
    @FXML
    private ObservableList<LoanData> data;
    String UserID;
    Connection con;
    PreparedStatement ps = null;
    ResultSet rs = null;
    @FXML
    Button back;
    @FXML
    Button loadBtn;
    @FXML
    TextField accountNoField;
    public void getUserID(String Id) {
        UserID = Id;
    }
    public void backPressed(ActionEvent event) throws IOException, SQLException {
        Connection con = DbConnection.Connection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        ps = con.prepareStatement("SELECT * FROM Users WHERE User_ID = ?");
        ps.setString(1, UserID);
        rs = ps.executeQuery();

        if (rs.next()) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/AdminPage.fxml"));

            loader.load();
            Parent root = loader.getRoot();
            AdminPageController upc = loader.getController();
            InputStream is = rs.getBinaryStream("User_Image");
            Image image = new Image("/icons/edituser.png");
            if (is != null) {
                OutputStream os = new FileOutputStream(new File("photo.jpg"));
                byte[] content = new byte[1024];
                int size = 0;
                while ((size = is.read(content)) != -1) {
                    os.write(content, 0, size);
                }
                os.close();
                is.close();
                image = new Image("file:photo.jpg", 250, 250, true, true);
            }
            Stage stage = (Stage) back.getScene().getWindow();
            upc.GetUserID(UserID, rs.getString("First_Name") + " " + rs.getString("Last_Name"), rs.getString("Phone_No"), rs.getString("Email"), rs.getString("CNIC"), image);

            stage.setTitle("Admin Page");
            stage.setMaximized(true);
            stage.setResizable(false);
            Scene scene = new Scene(root);
            scene.getStylesheets().add("/styles/AdminPage.css");
            stage.setScene(scene);
            stage.show();

        }
        ps.close();
        rs.close();
        con.close();
    }
    private void loadData() throws SQLException {
        con = DbConnection.Connection();
        data = FXCollections.observableArrayList();
        PreparedStatement ps = con.prepareStatement("SELECT * FROM Loan_Requests");
        ResultSet rs = null;
        try {
            rs = ps.executeQuery();
            while(rs.next()){
                data.add(new LoanData(rs.getString("Request_ID"), rs.getString("Account_No"),rs.getString("Purpose"),rs.getString("Status"),rs.getString("Amount")));
            }
        }
        catch (Exception e){
            System.out.println(e);
        }

        loanID.setCellValueFactory(new PropertyValueFactory<LoanData,String>("LoanID"));
        accountNo.setCellValueFactory(new PropertyValueFactory<LoanData,String>("AccountNo"));
        loanAmount.setCellValueFactory(new PropertyValueFactory<LoanData,String>("LoanAmount"));
        reason.setCellValueFactory(new PropertyValueFactory<LoanData,String>("LoanReason"));
        status.setCellValueFactory(new PropertyValueFactory<LoanData,String>("LoanStatus"));
        LoanTable.setItems(data);
        ps.close();
        rs.close();
        con.close();

    }
    public void loadPressed(ActionEvent event) throws IOException, SQLException {
        wrongUsr.setText("");
        loadData();
    }
    public void searchPressed(ActionEvent event) throws SQLException {
        wrongUsr.setText("");
        con = DbConnection.Connection();
        data = FXCollections.observableArrayList();
        PreparedStatement ps = con.prepareStatement("SELECT * FROM Loan_Requests where Account_No = '"+accountNoField.getText()+"'");
        ResultSet rs = null;
        try {
            rs = ps.executeQuery();
            int count = 0;
            while(rs.next()){
                count+=1;
                data.add(new LoanData(rs.getString("Request_ID"), rs.getString("Account_No"),rs.getString("Purpose"),rs.getString("Status"),rs.getString("Amount")));
            }
            if(count>0){}
            else{
                wrongUsr.setText("No Record Found!!");
            }
        }
        catch (Exception e){
            System.out.println(e);
        }

        loanID.setCellValueFactory(new PropertyValueFactory<LoanData,String>("LoanID"));
        accountNo.setCellValueFactory(new PropertyValueFactory<LoanData,String>("AccountNo"));
        loanAmount.setCellValueFactory(new PropertyValueFactory<LoanData,String>("LoanAmount"));
        reason.setCellValueFactory(new PropertyValueFactory<LoanData,String>("LoanReason"));
        status.setCellValueFactory(new PropertyValueFactory<LoanData,String>("LoanStatus"));
        LoanTable.setItems(data);
        ps.close();
        rs.close();
        con.close();

    }
    public void rejectPressed(ActionEvent event) throws SQLException {
    wrongUsr.setText("");
        LoanData loanData = LoanTable.getSelectionModel().getSelectedItem();
        String loanID = loanData.getLoanID();
        updateRejectStatus(loanID);

    }
    public boolean updateAcceptStatus(String loanID) throws SQLException {
        con = DbConnection.Connection();
        ps = con.prepareStatement("SELECT * FROM Loan_Requests WHERE Request_ID = ?");
        ps.setString(1, loanID);
        rs = ps.executeQuery();
        boolean bool = false;
        if(rs.next()){
            if(!rs.getString("Status").equals("-")){
                bool = true;

            }
        }
        if(bool){
            con.close();
            rs.close();
            wrongUsr.setText("Already Dealt");
            return false;
        }
        ps = con.prepareStatement("UPDATE Loan_Requests set Status = 'A' where Request_ID = ?");
        ps.setString(1, loanID);
        ps.executeUpdate();
        ps.close();
        con.close();
        rs.close();
        addToLoanHistory(loanID,"Pending");
        loadData();
        return  true;

    }
    public boolean updateRejectStatus(String loanID) throws SQLException {
        con = DbConnection.Connection();
        ps = con.prepareStatement("SELECT * FROM Loan_Requests WHERE Request_ID = ?");
        ps.setString(1, loanID);
        rs = ps.executeQuery();
        boolean bool = false;
        if(rs.next()){
            if(!rs.getString("Status").equals("-")){
                bool = true;

            }
        }
        if(bool){
            con.close();
            rs.close();
            wrongUsr.setText("Already Dealt!!");
            return false;
        }
        ps = con.prepareStatement("UPDATE Loan_Requests set Status = 'R' where Request_ID = ?");
        ps.setString(1, loanID);
        ps.executeUpdate();
        ps.close();
        con.close();
        rs.close();
        loadData();
        return true;
    }
    private void addToLoanHistory(String id,String status) throws SQLException {
        con = DbConnection.Connection();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        String todayDate = formatter.format(date);
        formatter = new SimpleDateFormat(" HH:mm:ss");
        String time = formatter.format(date);
        int loanID = 0;
        ps = con.prepareStatement("SELECT * FROM Loan_History");
        rs = ps.executeQuery();
        while (rs.next()){
            loanID = rs.getInt("Loan_ID");
        }

            PreparedStatement ps = con.prepareStatement("INSERT INTO Loan_History VALUES (?,?,?,?,?,?,?)");
            ps.setInt(1, (loanID + 1));
            ps.setString(2, id);
            ps.setString(3, status);
            ps.setString(4, todayDate);
            ps.setString(5, time);
            ps.setString(6, "-");
            ps.setString(7, "-");
            ps.executeUpdate();
            rs.close();
            ps.close();
            con.close();

    }
    public void acceptPressed(ActionEvent event) throws SQLException {
        wrongUsr.setText("");
        LoanData loanData = LoanTable.getSelectionModel().getSelectedItem();
        String loanID = loanData.getLoanID();
        updateAcceptStatus(loanID);
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
