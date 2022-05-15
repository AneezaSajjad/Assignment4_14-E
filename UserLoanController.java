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

public class UserLoanController implements Initializable {
    Connection con ;
    PreparedStatement ps = null;
    ResultSet rs = null;
    String UserID,accountNumber;
    @FXML
    private TableView<LoanData> LoanTableReq,LoanTableApp;
    @FXML
    private TableColumn<LoanData,String> requestID,loanID ;
    @FXML
    private TableColumn<LoanData,String> requestAmount,loanAmount ;
    @FXML
    private TableColumn<LoanData,String> requestStatus,loanStatus;
    @FXML
    private ObservableList<LoanData> data;
    @FXML
    Button back;
    @FXML
    Button reqLoan;
    @FXML
    Label wrongUsr;
    @FXML
    TextField requestIDField;
    @FXML
    TextField LoanIDField;

    public UserLoanController() throws SQLException {
    }

    public void getUserID(String Id,String account) {
        UserID = Id;accountNumber = account;
    }
    public void backPressed(ActionEvent event) throws IOException, SQLException {
        con = DbConnection.Connection();
        ps = null;
        rs = null;
        ps = con.prepareStatement("SELECT * FROM Users WHERE User_ID = ?");
        ps.setString(1, UserID);
        rs = ps.executeQuery();

        if (rs.next()) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/UserPage.fxml"));

            loader.load();
            Parent root = loader.getRoot();
            UserPageController upc = loader.getController();
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

            stage.setTitle("User Page");
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
    private void loadDataReq() throws SQLException {
        wrongUsr.setText("");
        con = DbConnection.Connection();
        data = FXCollections.observableArrayList();
         ps = con.prepareStatement("SELECT * FROM Loan_Requests where Account_No = '"+accountNumber+"'");
         rs = null;
        try {
            rs = ps.executeQuery();
            while(rs.next()){
                data.add(new LoanData(rs.getString("Request_ID")," "," ",rs.getString("Status"),rs.getString("Amount") ));
            }
        }
        catch (Exception e){
            System.out.println(e);
        }
        System.out.println(data);
        requestID.setCellValueFactory(new PropertyValueFactory<LoanData,String>("LoanID"));
        requestStatus.setCellValueFactory(new PropertyValueFactory<LoanData,String>("LoanStatus"));
        requestAmount.setCellValueFactory(new PropertyValueFactory<LoanData,String>("LoanAmount"));
        LoanTableReq.setItems(data);
        ps.close();
        rs.close();
        con.close();
    }
    private void loadDataApp() throws SQLException {
        wrongUsr.setText("");
        con = DbConnection.Connection();
        data = FXCollections.observableArrayList();
         ps = con.prepareStatement("SELECT h.Loan_ID,h.Status,l.Amount FROM Loan_History h, Loan_Requests l where l.Request_ID = h.Request_ID and l.Account_No = '"+accountNumber+"'");
         rs = null;
        try {
            rs = ps.executeQuery();
            while(rs.next()){
                data.add(new LoanData(rs.getString("Loan_ID")," "," ",rs.getString("Status"),rs.getString("Amount") ));
            }
        }
        catch (Exception e){
            System.out.println(e);
        }
        System.out.println(data);
        loanID.setCellValueFactory(new PropertyValueFactory<LoanData,String>("LoanID"));
        loanStatus.setCellValueFactory(new PropertyValueFactory<LoanData,String>("LoanStatus"));
        loanAmount.setCellValueFactory(new PropertyValueFactory<LoanData,String>("LoanAmount"));
        LoanTableApp.setItems(data);
        ps.close();
        rs.close();
        con.close();

    }
    public void loadPressedReq(ActionEvent event) throws IOException, SQLException {
        loadDataReq();
    }
    public void loadPressedApp(ActionEvent event) throws IOException, SQLException {
        loadDataApp();
    }
    public void reqLoanPressed() throws IOException, SQLException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/UserLoanRequestController.fxml"));
        loader.load();
        Parent root = loader.getRoot();
        Scene scene = new Scene(root);
        Stage stage = (Stage) reqLoan.getScene().getWindow();
        stage.setMaximized(true);
        stage.setResizable(false);
        LoanRequestController fxm = loader.getController();
        fxm.getUserID(UserID,accountNumber);
        stage.setTitle("Loan Request Screen");
        stage.setScene(scene);
        stage.show();
    }
    private void withDrawCash(String accountNo,int NewBalance) throws SQLException {
        ps = con.prepareStatement("UPDATE Bank_Account SET Balance =? WHERE Account_No = '" + accountNo+ "'");
        ps.setInt(1, NewBalance);
        ps.executeUpdate();
        wrongUsr.setText("Loan Has Been Paid Successfully");
    }
    public void searchPressedReq(ActionEvent event) throws SQLException {
        wrongUsr.setText("");
        con = DbConnection.Connection();
        data = FXCollections.observableArrayList();
        ps = con.prepareStatement("SELECT * FROM Loan_Requests where Account_No = '"+accountNumber+"' and Request_ID = '"+requestIDField.getText()+"'");
        rs = null;
        try {
            rs = ps.executeQuery();
            if(rs.next()){
                data.add(new LoanData(rs.getString("Request_ID")," "," ",rs.getString("Status"),rs.getString("Amount") ));
            }
            else{
                wrongUsr.setText("No Record Available!!");
            }
        }
        catch (Exception e){
            System.out.println(e);
        }
        System.out.println(data);
        requestID.setCellValueFactory(new PropertyValueFactory<LoanData,String>("LoanID"));
        requestStatus.setCellValueFactory(new PropertyValueFactory<LoanData,String>("LoanStatus"));
        requestAmount.setCellValueFactory(new PropertyValueFactory<LoanData,String>("LoanAmount"));
        LoanTableReq.setItems(data);
        ps.close();
        rs.close();
        con.close();

    }
    public void searchPressedApp(ActionEvent event) throws SQLException {
        wrongUsr.setText("");
        con = DbConnection.Connection();
        data = FXCollections.observableArrayList();
        ps = con.prepareStatement("SELECT h.Loan_ID,h.Status,l.Amount FROM Loan_History h, Loan_Requests l where l.Request_ID = h.Request_ID and l.Account_No = '"+accountNumber+"' and h.Loan_ID = '"+LoanIDField.getText()+"'");
        rs = null;
        try {
            rs = ps.executeQuery();
            if(rs.next()){
                data.add(new LoanData(rs.getString("Loan_ID")," "," ",rs.getString("Status"),rs.getString("Amount") ));
            }
            else{
                wrongUsr.setText("No Record Available!!");
            }
        }
        catch (Exception e){
            System.out.println(e);
        }
        System.out.println(data);
        loanID.setCellValueFactory(new PropertyValueFactory<LoanData,String>("LoanID"));
        loanStatus.setCellValueFactory(new PropertyValueFactory<LoanData,String>("LoanStatus"));
        loanAmount.setCellValueFactory(new PropertyValueFactory<LoanData,String>("LoanAmount"));
        LoanTableApp.setItems(data);
        ps.close();
        rs.close();
        con.close();

    }
    public boolean updatePayStatus(String loanID,String loanAmount,String accountNumber) throws SQLException {

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        String todayDate = formatter.format(date);
        formatter = new SimpleDateFormat(" HH:mm:ss");
        String time = formatter.format(date);
        con = DbConnection.Connection();
        ps = con.prepareStatement("SELECT * FROM Loan_History WHERE Loan_ID = ?");
        ps.setString(1, loanID);
        rs = ps.executeQuery();
        boolean bool = false;
        if(rs.next()){
            System.out.println(rs.getString("Status"));
            if(!rs.getString("Status").equals("Pending")){
                bool = true;
            }
        }
        if(bool){

            con.close();
            rs.close();
            wrongUsr.setText("Already Paid!!");
            return false;
        }
        bool = false;
        ps = con.prepareStatement("SELECT * FROM Bank_Account WHERE Account_No = ?");
        ps.setString(1, accountNumber);
        rs = ps.executeQuery();
        if (rs.next()) {
            int NewBalance = (rs.getInt("Balance") - Integer.parseInt(loanAmount));
            if (NewBalance >= 0) {
                withDrawCash(accountNumber, NewBalance);
            } else {
                bool = true;
            }
            if(bool){
                con.close();
                rs.close();
                wrongUsr.setText("Not Enough Amount!");

                return false;
            }
            System.out.println(loanID);
            ps = con.prepareStatement("UPDATE Loan_History set Status = 'Paid', Loan_Return_Date = ? ,Loan_Return_Time =? where Loan_ID = ?");
            ps.setString(3, loanID);
            ps.setString(1, todayDate);
            ps.setString(2, time);

            ps.executeUpdate();
            rs.close();
            ps.close();
            con.close();
            loadDataApp();
            return true;
        }
        rs.close();
        ps.close();
        con.close();
        return  false;
    }

    public void payPressed(ActionEvent event) throws SQLException {
        wrongUsr.setText("");

        LoanData loanData = LoanTableApp.getSelectionModel().getSelectedItem();
        String loanID = loanData.getLoanID();
        String loanAmount = loanData.getLoanAmount();
        updatePayStatus(loanID,loanAmount,accountNumber);
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
