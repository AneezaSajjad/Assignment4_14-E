package com.mycompany.atmmanagementsys;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class UserPageController implements Initializable {
    @FXML
    private Label emailAddress;
    @FXML
    private Label userName;
    @FXML
    private Label cnic;
    @FXML
    private Label phoneNumber;
    @FXML
    String name;
    String Cnic;
    String userPhone;
    String userEmail;
    Image img;
    Connection con;
    PreparedStatement ps = null;
    ResultSet rs = null;
    @FXML
    String UserID;
    @FXML
    ImageView userImage;
    @FXML
    Button back;
    @FXML
    Button chngPass;
    @FXML
    Button balanceTransferBtn;
    @FXML
    Button transactionButton;
    @FXML
    Button loanBtn;
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
    public void GetUserID(String id, String Name,String phone, String email,String identifier,Image image) throws SQLException {
        UserID = id;
        name = Name;
        Cnic = identifier;
        userEmail = email;
        userPhone = phone;
        img = image;
        userName.setText(Name);
        cnic.setText(identifier);
        emailAddress.setText(email);
        phoneNumber.setText(phone);
        System.out.println(image);
        userImage.setImage(image);

    }
    public void backPressed(ActionEvent event) throws IOException{
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/Scene1.fxml"));
        loader.load();
        Parent root = loader.getRoot();
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/Styles.css");
        Stage stage = (Stage) back.getScene().getWindow();
        stage.setResizable(false);
        stage.setMaximized(true);
        FXMLController fxm = loader.getController();
        fxm.setScreen("cus");
        stage.sizeToScene();
        stage.setTitle("Employee/Admin Login Screen");
        stage.setScene(scene);
        stage.show();
    }
    @FXML
    public void ChangePassword(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/ChangePassword.fxml"));
        loader.load();
        Parent root = loader.getRoot();
        AccountInfoController aic = loader.getController();
        aic.getUserID(UserID);
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/ChangePassword.css");
        Stage stage = (Stage) chngPass.getScene().getWindow();
        stage.setResizable(false);
        stage.setMaximized(true);
        stage.sizeToScene();
        stage.setTitle("Change Password");
        stage.setScene(scene);
        stage.show();
    }
    public void balanceTransfer(ActionEvent event) throws IOException, SQLException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/BalanceTransfer.fxml"));
        loader.load();
        Parent root = loader.getRoot();
        Scene scene = new Scene(root);
        Stage stage = (Stage) balanceTransferBtn.getScene().getWindow();
        stage.setMaximized(true);
        stage.setResizable(false);
        BalanceTransferController fxm = loader.getController();
        fxm.GetUserID(UserID,name,userPhone,userEmail,Cnic,img);
        stage.setTitle("Deposit Cash Screen");
        stage.setScene(scene);
        stage.show();
    }
    public void transactionPressed(ActionEvent event) throws IOException, SQLException {
        con = DbConnection.Connection();
        String account="";
        System.out.println(UserID);

        ps = con.prepareStatement("SELECT * FROM Bank_Account where Cust_ID = '"+UserID+"'");
        try{
            rs = ps.executeQuery();
            rs.next();
            account = rs.getString("Account_No");

        }
        catch (Exception e){
            System.out.println(e);
        }
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/ShowTransactions.fxml"));
        loader.load();
        Parent root = loader.getRoot();
        Scene scene = new Scene(root);
        Stage stage = (Stage) transactionButton.getScene().getWindow();
        stage.setMaximized(true);
            TransactionController fxm = loader.getController();
            fxm.getUserID(UserID,account);
        stage.setResizable(false);

        stage.setTitle("Deposit Cash Screen");
        stage.setScene(scene);
        stage.show();
    }
    public void loanPressed(ActionEvent event) throws IOException, SQLException {
        con = DbConnection.Connection();
        String account="";
        System.out.println(UserID);

        ps = con.prepareStatement("SELECT * FROM Bank_Account where Cust_ID = '"+UserID+"'");
        try{
            rs = ps.executeQuery();
            rs.next();
            account = rs.getString("Account_No");

        }
        catch (Exception e){
            System.out.println(e);
        }
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/UserLoanController.fxml"));
        loader.load();
        Parent root = loader.getRoot();
        Scene scene = new Scene(root);
        Stage stage = (Stage) loanBtn.getScene().getWindow();
        stage.setMaximized(true);
        stage.setResizable(false);
        UserLoanController fxm = loader.getController();
        System.out.println(account);
        fxm.getUserID(UserID,account);
        stage.setTitle("Loan Screen");
        stage.setScene(scene);
        stage.show();
        rs.close();
        ps.close();
        con.close();
    }

}







