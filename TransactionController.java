package com.mycompany.atmmanagementsys;
import com.itextpdf.html2pdf.HtmlConverter;
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
import javafx.scene.layout.Pane;
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

public class TransactionController implements Initializable {
    Connection con;
    PreparedStatement ps = null;
    ResultSet rs = null;
    String UserID;
    @FXML
    private TableView<TransactionData> TransactionTable;
    @FXML
    private TableColumn<TransactionData,String> transID ;
    @FXML
    private TableColumn<TransactionData,String> transType ;
    @FXML
    private TableColumn<TransactionData,String> transAmount ;
    @FXML
    private ObservableList<TransactionData> data;
    @FXML
    Button back;
    @FXML
    Button loadBtn;
    @FXML
    Button print;
    @FXML
    Button searchBtn;
    @FXML
    TextField amount;
    @FXML
    Label confirmation;
    @FXML
    Pane messagePane;
    String accountNumber;
    public void getUserID(String Id,String account) {
        UserID = Id;accountNumber = account;
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
    public void loadPressed(ActionEvent event) throws IOException, SQLException {
        messagePane.setVisible(false);
        confirmation.setText("");

        con = DbConnection.Connection();
        data = FXCollections.observableArrayList();
        PreparedStatement ps = con.prepareStatement("SELECT * FROM Transaction_History where Account_No_1 ='"+accountNumber+"' or Account_No_2 ='"+accountNumber+"'");
        ResultSet rs = null;
        try {
            rs = ps.executeQuery();
            while(rs.next()){
                data.add(new TransactionData(rs.getString("Transaction_ID"), rs.getString("Transaction_Type"), String.valueOf(rs.getInt("Amount"))));
            }
        }
        catch (Exception e){
            System.out.println(e);
        }
        System.out.println(data);
        transID.setCellValueFactory(new PropertyValueFactory<TransactionData,String>("TransID"));
        transType.setCellValueFactory(new PropertyValueFactory<TransactionData,String>("TransType"));
        transAmount.setCellValueFactory(new PropertyValueFactory<TransactionData,String>("TransAmount"));
        TransactionTable.setItems(data);
        ps.close();
        rs.close();
        con.close();

    }
    public void searchPressed(ActionEvent event) throws SQLException {
        messagePane.setVisible(false);
        confirmation.setText("");
        con = DbConnection.Connection();
        data = FXCollections.observableArrayList();
        PreparedStatement ps = con.prepareStatement("SELECT * FROM Transaction_History where (Account_No_1 ='"+accountNumber+"' or Account_No_2 ='"+accountNumber+"') and Amount = '"+amount.getText()+"'");
        ResultSet rs = null;
        try {
            int counter = 0;
            rs = ps.executeQuery();
            while(rs.next()){
                counter+=1;
                data.add(new TransactionData(rs.getString("Transaction_ID"), rs.getString("Transaction_Type"), String.valueOf(rs.getInt("Amount"))));
            }
            if(counter>0){

            }
            else{
                messagePane.setVisible(true);
                confirmation.setText("No Record Found!!");
            }
        }
        catch (Exception e){
            System.out.println(e);
        }
        System.out.println(data);
        transID.setCellValueFactory(new PropertyValueFactory<TransactionData,String>("TransID"));
        transType.setCellValueFactory(new PropertyValueFactory<TransactionData,String>("TransType"));
        transAmount.setCellValueFactory(new PropertyValueFactory<TransactionData,String>("TransAmount"));
        TransactionTable.setItems(data);
        ps.close();
        rs.close();
        con.close();
    }
    public void printPressed(ActionEvent event) throws Exception {
        // TransactionData transactionData = TransactionTable.getSelectionModel().getSelectedItem();
        // String transID = transactionData.getTransID();

        int res = 0;
        con = DbConnection.Connection();
        System.out.println(UserID);
        ps = con.prepareStatement("SELECT * FROM Users where User_ID ='"+UserID+"'");
        rs = ps.executeQuery();
        rs.next();
        String html_start = "<body> <h1>ABC Bank</h1><h4>F9 Branch, Islamabad</h4>"
                    + "<p>"+rs.getString("First_Name")+" "+rs.getString("Last_Name")+"<br>Account Number: "+accountNumber+"<br>Current Account"
                    + "<br></p><h4>E Bank Statement</h4>"
                    + "<table border=1 ><tr>"
                + "<th width=300px>Transaction ID</th><th width=300px >Account 1</th><th width=300px >Account 2</th><th width=300px >Transaction Type</th><th width=300px >Amount</th><th width=300px >Date</th><th width=300px >Time</th><th width=300px >Emp ID</th>"
                    + "</tr>";


        // make initial html code strings
        String html_end = "</table></body><style>table { text-align: center; }</style>";
        String html_data="";

        System.out.println("About to execute SQL");
        // get all the transactions
        System.out.println("User: " + UserID );
        int rows = 0; // just to make sure that we get data
        ps=null;
        // ------------ actual code ------------------------------------------
        ps = con.prepareStatement("SELECT * FROM Transaction_History where Account_No_1 ='"+accountNumber+"' or Account_No_2 ='"+accountNumber+"'");
        rs = null;
        try {
            System.out.println("Here I am");
            rs = ps.executeQuery();
           System.out.println("Getting lines");
           while(rs.next()){
                rows++;
                html_data += "<tr><td>"+rs.getString("Transaction_ID")+"</td>"
                        + "<td>"+rs.getString("Account_No_1")+"</td>"
                        + "<td>"+rs.getString("Account_No_2")+"</td>"
                        + "<td>"+rs.getString("Transaction_Type")+"</td>"
                        + "<td>"+rs.getString("Amount")+"</td>"
                        + "<td>"+rs.getString("T_Date")+"</td>"
                        + "<td>"+rs.getString("T_Time")+"</td>"
                        + "<td>"+rs.getString("User_ID")+"</td></tr>";
            }
         System.out.println("Got Data");
    }
        catch (SQLException throwables) {
            System.out.println("Unknown error");
        }

        // ------------ for sample pdf ------------------
        // just uncomment above lines and remove this

        // --------------------------

        // we get some transactions
        if( rows > 0 ) {
            String html = html_start + html_data + html_end;
            try {
                String file_name = "E_Statement_" + java.time.LocalDateTime.now() + ".pdf";
                file_name = file_name.replaceAll(":", "_");
                HtmlConverter.convertToPdf( html, new FileOutputStream(file_name));
                res = 1;
                System.out.println("PDF is successfully printed");
            }
            catch( Exception e) {
                System.out.println("Unknown error");
            }
        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}
