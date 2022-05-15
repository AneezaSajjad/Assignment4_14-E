package com.mycompany.atmmanagementsys;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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

public class BalanceTransferController implements Initializable {
    @FXML
    private Image image;
    @FXML
    private TextField num;

    @FXML
    private Label emailAddress;
    @FXML
    private Label userName;
    @FXML
    private Label cnic;
    @FXML
    private Label phoneNumber;
    @FXML
    String UserID;
    @FXML
    ImageView userImage;
    @FXML
    Button back;
    @FXML
    Button deposit;
    @FXML
    Button withdrawCashBtn;
    @FXML
    TextField accountNo;
    @FXML
    TextField transferAmount;
    @FXML
    Label wrongUser;
    Connection con;
    PreparedStatement ps;
    ResultSet rs;
    String UserID1 = "";
    private String  getConfirmation(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Alert");
        ButtonType type = new ButtonType("OK");
        alert.setContentText("This is a Confirmation alert");
        alert.showAndWait();
        return alert.getResult().getText();
    }
    private boolean accountIsActive(String account) throws SQLException {
        ps = con.prepareStatement("SELECT * FROM Bank_Account WHERE Account_No = ?");
        ps.setString(1,account);
        rs = ps.executeQuery();
        if(rs.next()){
            System.out.println(rs.getString("Status"));
            if(rs.getString("Status").equals("Active")){
                return true;
            }
            else{
                return false;
            }
        }
        else{
            return false;
        }
    }
    private void deductCash(String id, int amount) throws SQLException {
        ps = con.prepareStatement("SELECT * FROM Bank_Account WHERE Cust_ID = ?");
        ps.setString(1, id);
        rs = ps.executeQuery();
        if(rs.next()){
            amount = (rs.getInt("Balance") - (amount));
            ps = con.prepareStatement("UPDATE Bank_Account SET Balance =? WHERE Cust_ID = '" + id + "'");
            ps.setInt(1, amount);
            ps.executeUpdate();
            wrongUser.setText("Cash Has Been Transfered\n New Balance: "+amount);
        }
        else{
            wrongUser.setText("Some Technical Problems, Please Try Again");
        }

    }
    private void addCash(String account, int amount) throws SQLException {
        ps = con.prepareStatement("SELECT * FROM Bank_Account WHERE Account_No = ?");
        ps.setString(1, account);
        rs = ps.executeQuery();
        if(rs.next()){
            amount = (rs.getInt("Balance") + (amount));
            UserID1 = rs.getString("Cust_ID");
            ps = con.prepareStatement("UPDATE Bank_Account SET Balance =? WHERE Account_No = '" + account + "'");
            ps.setInt(1, amount);
            ps.executeUpdate();
        }
        else{
            wrongUser.setText("Some Technical Problems, Please Try Again");
        }

    }
    private void addToTransactionHistory(String type,int amount,String id,String id1) throws SQLException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        String todayDate = formatter.format(date);
        formatter = new SimpleDateFormat(" HH:mm:ss");
        String time = formatter.format(date);
        int transId = 0;
        ps = con.prepareStatement("SELECT * FROM Transaction_History");
        rs = ps.executeQuery();
        while (rs.next()){
             transId = rs.getInt("Transaction_ID");
        }
        if(type.equals("Withdraw")){
            PreparedStatement ps = con.prepareStatement("INSERT INTO Transaction_History VALUES (?,?,?,?,?,?,?,?)");
            ps.setInt(1, (transId + 1));
            ps.setString(2, id);
            ps.setString(3, "-");
            ps.setString(4, "Withdraw");
            ps.setInt(5, (amount));
            ps.setString(6, todayDate);
            ps.setString(7, time);
            ps.setString(8, id1);
            ps.executeUpdate();

        }
        else if(type.equals("Deposit")){
            PreparedStatement ps = con.prepareStatement("INSERT INTO Transaction_History VALUES (?,?,?,?,?,?,?,?)");
            ps.setInt(1, (transId + 1));
            ps.setString(2, id);
            ps.setString(3, "-");
            ps.setString(4, "Deposit");
            ps.setInt(5, (amount));
            ps.setString(6, todayDate);
            ps.setString(7, time);
            ps.setString(8, id1);
            ps.executeUpdate();
        }
        else{
            PreparedStatement ps = con.prepareStatement("INSERT INTO Transaction_History VALUES (?,?,?,?,?,?,?,?)");
            ps.setInt(1, (transId + 1));
            ps.setString(2, id);
            ps.setString(3, id1);
            ps.setString(4, "Transfer");
            ps.setInt(5, (amount));
            ps.setString(6, todayDate);
            ps.setString(7, time);
            ps.setString(8, "-");
            ps.executeUpdate();
        }
    }
    private boolean userHasAmount(String id, int amount) throws SQLException {
        ps = null;
        rs = null;
        ps = con.prepareStatement("SELECT * FROM Bank_Account WHERE Cust_ID = ?");
        ps.setString(1, id);
        rs = ps.executeQuery();
        if (rs.next()) {
           int NewBalance =  (rs.getInt("Balance") - amount);
           if(NewBalance >= 0){
               return true;
           }
           else{
               return false;
           }
        }
        return false;
    }
    public void GetUserID(String id, String Name,String phone, String email,String identifier,Image image) throws SQLException {
        UserID = id;
        userName.setText(Name);
        cnic.setText(identifier);
        emailAddress.setText(email);
        phoneNumber.setText(phone);
        System.out.println(image);
        userImage.setImage(image);



    }
    public void transferCashPressed(ActionEvent event) throws IOException {
        wrongUser.setText("");
        if(getConfirmation().equals("OK")) {
            try {
                if (transferAmount.getText().isEmpty() || Integer.parseInt(transferAmount.getText()) < 0) {
                    wrongUser.setText("Please Enter A Valid Amount");
                } else {
                    con = DbConnection.Connection();
                    if (accountIsActive(accountNo.getText())) {
                        rs.close();
                        ps.close();
                        if (userHasAmount(UserID, Integer.parseInt(transferAmount.getText()))) {
                            rs.close();
                            ps.close();
                            deductCash(UserID, Integer.parseInt(transferAmount.getText()));
                            addCash(accountNo.getText(), Integer.parseInt(transferAmount.getText()));
                            addToTransactionHistory("Transfer", Integer.parseInt(transferAmount.getText()), UserID, UserID1);
                        } else {
                            wrongUser.setText("You Do Not Have Enough Cash!!");
                        }
                    } else {
                        wrongUser.setText("The Account is not Available");
                    }
                    ps.close();
                    rs.close();
                    con.close();
                }
            } catch (NumberFormatException | SQLException e) {
                wrongUser.setText("Invalid Account Number/Number Format");
            }
            transferAmount.setText("");
            accountNo.setText("");
        }
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
