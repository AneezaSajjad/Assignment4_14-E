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
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class EmployeePageController implements Initializable {
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
    String name;
    String Cnic;
    String userPhone;
    String userEmail;
    Image img;
    @FXML
    ImageView userImage;
    @FXML
    Button back;
    @FXML
    Button chngPass;
    @FXML
    Button depositCashBtn;
    @FXML
    Button withdrawCashBtn;
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

    public void changePassword(ActionEvent event) throws IOException{
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/ChangePassword.fxml"));
        loader.load();
        Parent root = loader.getRoot();
        AccountInfoController aic = loader.getController();
        aic.getUserID(UserID);
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/ChangePassword.css");
        Stage stage = (Stage) chngPass.getScene().getWindow();
        stage.setMaximized(true);
        stage.setResizable(false);
        stage.setTitle("Change Password");
        stage.setScene(scene);
        stage.show();
    }

    public void backPressed(ActionEvent event) throws IOException{
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/Scene.fxml"));
        loader.load();
        Parent root = loader.getRoot();
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/Styles.css");
        Stage stage = (Stage) back.getScene().getWindow();
        stage.setMaximized(true);
        stage.setResizable(false);
        FXMLController fxm = loader.getController();
        fxm.setScreen("emp");
        stage.setTitle("Employee Login Screen");
        stage.setScene(scene);
        stage.show();
    }

    public void depositCash(ActionEvent event) throws IOException, SQLException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/DepositCash.fxml"));
        loader.load();
        Parent root = loader.getRoot();
        Scene scene = new Scene(root);
        Stage stage = (Stage) depositCashBtn.getScene().getWindow();
        stage.setMaximized(true);
        stage.setResizable(false);
        DepositCashController fxm = loader.getController();
        fxm.GetUserID(UserID,name,userPhone,userEmail,Cnic,img);
        stage.setTitle("Deposit Cash Screen");
        stage.setScene(scene);
        stage.show();
    }

    public void withdrawCash(ActionEvent event) throws IOException, SQLException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/WithdrawCash.fxml"));
        loader.load();
        Parent root = loader.getRoot();
        Scene scene = new Scene(root);
        Stage stage = (Stage) withdrawCashBtn.getScene().getWindow();
        stage.setMaximized(true);
        stage.setResizable(false);
        WithdrawCashController fxm = loader.getController();
        fxm.GetUserID(UserID,name,userPhone,userEmail,Cnic,img);
        stage.setTitle("Withdraw Cash Screen");
        stage.setScene(scene);
        stage.show();
    }


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
}
