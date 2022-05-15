package com.mycompany.atmmanagementsys;

import com.email.durgesh.Email;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Random;
import java.util.ResourceBundle;

public class ForgetPasswordController implements Initializable {
    @FXML
    Button submit;
    @FXML
    Button back;
    @FXML
    private Label wrongUser;
    @FXML
    private TextField emailAddress;
    @FXML
    private TextField accountNo;
    String scr = "";
    String pass;
    String email;
    Connection con;
    PreparedStatement ps = null;
    ResultSet rs = null;

    @FXML
    public void submitPressed(ActionEvent event) throws IOException, SQLException {
        if(scr.equals("cus")){
            if(checkEmailAndAccountNoCustomer()){
                rs.close();
                ps.close();
                pass = getRandomPassword();
                updatePassword(email,pass);
                sendEmail(pass,emailAddress.getText());

            }
        }
        else{
            if(checkEmailAndAccountNoEmployee()){
                rs.close();
                ps.close();
                pass = getRandomPassword();
                sendEmail(pass,emailAddress.getText());
                updatePassword(email,pass);

            }
        }
    }
    private String getRandomPassword() {
        int min = 1;
        int max = 10000;
        int random_int1 = (int)Math.floor(Math.random()*(max-min+1)+min);
        int random_int2 = (int)Math.floor(Math.random()*(max-min+1)+min);

        return  String.valueOf(random_int1)+String.valueOf(random_int2);
    }
    private void updatePassword(String email,String Password) throws SQLException {
        Connection con = DbConnection.Connection();
        PreparedStatement ps = con.prepareStatement("UPDATE Users SET Password = ? WHERE Email = '"+email+"'");
        ps.setString(1, Password);
        ps.executeUpdate();
    }
    private boolean checkEmailAndAccountNoCustomer() throws SQLException {
       con = DbConnection.Connection();
       ps = null;
       rs = null;
        ps = con.prepareStatement("SELECT * FROM Users WHERE Email = ? and User_Type = 'C' ");
        ps.setString(1, emailAddress.getText());
        //ps.setString(2, accountNo.getText());
        rs = ps.executeQuery();
        if(rs.next()){
            wrongUser.setText("Please Check your email");
            email =  emailAddress.getText();
            return true;
        }
        else{
            wrongUser.setText("Sorry!! This Email Don't Exist");

            return  false;

        }
    }
    private boolean checkEmailAndAccountNoEmployee() throws SQLException {
        con = DbConnection.Connection();
        ps = con.prepareStatement("SELECT * FROM Users WHERE Email = ? and User_Type != 'C'  ");
        ps.setString(1, emailAddress.getText());
        //ps.setString(2, accountNo.getText());
        rs = ps.executeQuery();
        if(rs.next()){
            wrongUser.setText("Please Check your email");
            email = emailAddress.getText();

            return  true;
        }
        else{
            wrongUser.setText("Sorry!! This Email Don't Exist");
            return  false;

        }
    }
    public void setScreen(String name){
        scr = name;
    }
    public void backPressed(ActionEvent event) throws IOException{
        if(scr.equals("emp")) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/Scene.fxml"));
            loader.load();
            Parent root = loader.getRoot();
            Scene scene = new Scene(root);
            scene.getStylesheets().add("/styles/Styles.css");
            Stage stage = (Stage) back.getScene().getWindow();
            stage.setMaximized(true);
            FXMLController aic = loader.getController();
            aic.setScreen("emp");
            stage.setResizable(false);
            stage.setTitle("Employee Login Screen");
            stage.setScene(scene);
            stage.show();
        }
        else{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/Scene1.fxml"));
            loader.load();
            Parent root = loader.getRoot();
            Scene scene = new Scene(root);
            scene.getStylesheets().add("/styles/Styles.css");
            Stage stage = (Stage) back.getScene().getWindow();
            stage.setMaximized(true);
            FXMLController aic = loader.getController();
            aic.setScreen("cus");
            stage.setResizable(false);
            stage.setTitle("Customer Login Screen");
            stage.setScene(scene);
            stage.show();
        }
    }
    public  void sendEmail(String password,String recEmail){
        try {
            Email email = new Email("softw697@gmail.com","Nuisb123");
            email.setFrom("softw697@gmail.com","Software Engineering");
            email.setSubject("New Password");
            System.out.println(password);
            System.out.println(recEmail);

            email.setContent(password,"text/html");
            email.addRecipient(recEmail);
            email.send();

        }
        catch (MessagingException | UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
