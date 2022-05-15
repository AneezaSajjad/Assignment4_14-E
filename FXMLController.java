package com.mycompany.atmmanagementsys;

import java.io.*;
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
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

public class FXMLController implements Initializable {
    @FXML
    private TextField userId;
    @FXML
    private PasswordField password;
    @FXML
    private Label wrongUser;
    @FXML
    private Button loginb;
    @FXML
    private Hyperlink forgetPass;
    @FXML
    private  Button back;
    private String scr = "";
    Connection con;
    PreparedStatement ps;
    ResultSet rs;

    @FXML
    private  boolean isUserExist(String userId) throws SQLException {

    ps = con.prepareStatement("SELECT * FROM Users WHERE User_ID = ? ");
    ps.setString(1, userId);
    rs = ps.executeQuery();
    if(rs.next())
        return true;
        return false;
}
    @FXML
    private void Login(ActionEvent event) throws SQLException, IOException {
        wrongUser.setText("");
        con = DbConnection.Connection();
    String str = userId.getText();
            if(str.charAt(0) == 'C'){
                if(scr.equals("cus"))
                    if(isUserExist(str))
                CustomerPage();
                    else{
                        wrongUser.setText("Sorry This User Don't Exist");
                    }

                else{
                    wrongUser.setText("Sorry This User Don't Exist");
                }
            }
            else if(str.charAt(0) == 'M'){
                if(scr.equals("emp"))
                    if(isUserExist(str))
            AdminPage();
                    else{
                        wrongUser.setText("Sorry This User Don't Exist");

                    }
                else{
                    wrongUser.setText("Sorry This User Don't Exist");

                }
            }
            else{
                if(scr.equals("emp"))
                    if(isUserExist(str))
                EmployeePage();
                    else{
                        wrongUser.setText("Sorry This User Don't Exist");

                    }
                else{
                    wrongUser.setText("Sorry This User Don't Exist");

                }
            }
    }
    private boolean accountIsActive(String user) throws SQLException {
        ps = con.prepareStatement("SELECT * FROM Bank_Account WHERE Cust_ID = ?");
        ps.setString(1,user);
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

    @FXML
    public void setScreen(String name){
    scr = name;
    }
    public void backPressed(ActionEvent event) throws IOException{

        Stage stage = (Stage) back.getScene().getWindow();

        System.out.println(getClass());
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/MainScreen.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/MainScreen.css");
        stage.setMaximized(true);
        stage.setResizable(false);

        stage.setTitle("Main Screen");
        stage.setScene(scene);
        stage.show();
    }
    private  void CustomerPage()throws SQLException, IOException{

        if(accountIsActive(userId.getText())){
            ps = null;
            rs = null;
            ps = con.prepareStatement("SELECT * FROM Users WHERE User_ID = ? and Password = ?");
            ps.setString(1, userId.getText());
            ps.setString(2, password.getText());
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
            Stage stage = (Stage) loginb.getScene().getWindow();
            upc.GetUserID(userId.getText(), rs.getString("First_Name") + " " + rs.getString("Last_Name"), rs.getString("Phone_No"), rs.getString("Email"), rs.getString("CNIC"), image);

            stage.setTitle("User Page");
            stage.setMaximized(true);
            stage.setResizable(false);
            Scene scene = new Scene(root);
            scene.getStylesheets().add("/styles/UserPage.css");
            stage.setScene(scene);
            stage.show();
            wrongUser.setText("");
        }
        else{
            wrongUser.setText("Wrong Password Or UserID");
        }

        }
        else{
            wrongUser.setText("You Are Blocked!!");

        }
        ps.close();
        rs.close();
        con.close();
    }
    private  void AdminPage()throws SQLException, IOException{
        PreparedStatement ps = null;
        ResultSet rs = null;
        ps = con.prepareStatement("SELECT * FROM Users WHERE User_ID = ? and Password = ?");
        ps.setString(1, userId.getText());
        ps.setString(2, password.getText());
        rs = ps.executeQuery();
        if (rs.next()) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/AdminPage.fxml"));
            loader.load();
            Parent root = loader.getRoot();
            AdminPageController upc = loader.getController();
            InputStream is = rs.getBinaryStream("User_Image");
            Image image = new Image("/icons/edituser.png");
            if(is!=null) {
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
            Stage stage = (Stage) loginb.getScene().getWindow();

            upc.GetUserID(userId.getText(), rs.getString("First_Name")+" "+rs.getString("Last_Name"),rs.getString("Phone_No"),rs.getString("Email"),rs.getString("CNIC"),image);
            stage.setTitle("Admin Page");
            stage.setMaximized(true);
            stage.setResizable(false);
            Scene scene = new Scene(root);
            scene.getStylesheets().add("/styles/AdminPage.css");
            stage.setScene(scene);
            stage.show();
            wrongUser.setText("");

        }
        else{
            wrongUser.setText("Wrong Password Or UserID");
        }
        ps.close();
        rs.close();
        con.close();
    }
    private  void EmployeePage()throws SQLException, IOException{
        PreparedStatement ps = null;
        ResultSet rs = null;
        ps = con.prepareStatement("SELECT * FROM Users WHERE User_ID = ? and Password = ?");
        ps.setString(1, userId.getText());
        ps.setString(2, password.getText());
        rs = ps.executeQuery();
        if (rs.next()) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/EmployeePage.fxml"));
            loader.load();
            Parent root = loader.getRoot();
            EmployeePageController upc = loader.getController();
            InputStream is = rs.getBinaryStream("User_Image");
            Image image = new Image("/icons/edituser.png");
            if(is!=null) {
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
            Stage stage = (Stage) loginb.getScene().getWindow();
            upc.GetUserID(userId.getText(), rs.getString("First_Name")+" "+rs.getString("Last_Name"),rs.getString("Phone_No"),rs.getString("Email"),rs.getString("CNIC"),image);

            stage.setTitle("Employee Page");
            stage.setMaximized(true);
            stage.setResizable(false);
            Scene scene = new Scene(root);
            scene.getStylesheets().add("/styles/EmployeePage.css");
            stage.setScene(scene);
            stage.show();
            wrongUser.setText("");

        }
        else{
            wrongUser.setText("Wrong Password Or UserID");
        }
        ps.close();
        rs.close();
        con.close();
    }
    public void requestNewPassword(ActionEvent event) throws IOException{
    if(scr.equals("cus")){
        customerForgetPassword();
    }
    else{
        employeeForgetPassword();
    }

    }
    private void customerForgetPassword() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/forgetPasswordCustomer.fxml"));
        loader.load();
        Parent root = loader.getRoot();
        ForgetPasswordController aic = loader.getController();
        aic.setScreen("cus");
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/MainScreen.css");
        Stage stage = (Stage) forgetPass.getScene().getWindow();
        stage.setMaximized(true);
        stage.setResizable(false);
        stage.setTitle("Forgot Password");
        stage.setScene(scene);
        stage.show();
    }
    private void employeeForgetPassword() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/forgetPasswordEmployee.fxml"));
        loader.load();
        Parent root = loader.getRoot();
        ForgetPasswordController aic = loader.getController();
        aic.setScreen("emp");
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/MainScreen.css");
        Stage stage = (Stage) forgetPass.getScene().getWindow();
        stage.setMaximized(true);
        stage.setResizable(false);
        stage.setTitle("Forgot Password");
        stage.setScene(scene);
        stage.show();

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
}
