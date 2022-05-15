package com.mycompany.atmmanagementsys;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.sqlite.core.DB;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

public class LoanRequestController implements Initializable {
    String UserID,accountNumber;

    Connection con;
    PreparedStatement ps = null;
    ResultSet rs = null;
    @FXML
    javafx.scene.control.Button back;
    @FXML
    javafx.scene.control.Button  reqLoan;
    @FXML
    private TextArea reason;
    @FXML
    private TextField amount;
    @FXML
    private Label wrongUsr;



    public void getUserID(String Id,String account) {
        UserID = Id;accountNumber = account;
    }
    public void backPressed(ActionEvent event) throws IOException, SQLException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/UserLoanController.fxml"));
        loader.load();
        Parent root = loader.getRoot();
        UserLoanController upc = loader.getController();
        upc.getUserID(UserID,accountNumber);
        Stage stage = (Stage) back.getScene().getWindow();
        stage.setTitle("Loan Page");
        stage.setMaximized(true);
        stage.setResizable(false);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    public void reqLoanPressed() throws SQLException {
            updateLoanRequestStatus(amount.getText(),reason.getText(),accountNumber);

    }
    public boolean updateLoanRequestStatus(String amount,String reason,String accountNumber) throws SQLException {

        boolean bool = false;
        if (amount.isEmpty()|| reason.isEmpty()) {
        }
        else{
            if(Integer.parseInt(amount)<0){
                bool =  true;
            }
            if(bool){
                con.close();
                ps.close();
                rs.close();
                return false;
            }
            con = DbConnection.Connection();
            ps = con.prepareStatement("SELECT * FROM Loan_Requests WHERE Account_No = ?");
            ps.setString(1, accountNumber);
            rs = ps.executeQuery();
            bool = false;
            while(rs.next()){
                if(rs.getString("Status").equals("-")){
                    bool = true;
                }
            }
            if(bool){
                con.close();
                rs.close();
                wrongUsr.setText("Already Pending Request!!");

                return false;
            }
            rs.close();
            ps.close();

            ps = con.prepareStatement("SELECT h.Loan_ID,h.Status,l.Amount FROM Loan_History h, Loan_Requests l where l.Request_ID = h.Request_ID and l.Account_No = '"+accountNumber+"'");
            rs = null;

            rs = ps.executeQuery();
            bool = false;
            while(rs.next()){
                if(rs.getString("Status").equals("Pending")){
                    bool = true;
                }                }
            if(bool){
                con.close();
                rs.close();
                wrongUsr.setText("Already Pending Loan!!");
                return false;
            }
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            Date date = new Date();
            String todayDate = formatter.format(date);
            formatter = new SimpleDateFormat(" HH:mm:ss");
            String time = formatter.format(date);
            int requestId = 0;
            ps = con.prepareStatement("SELECT * FROM Loan_Requests");
            rs = ps.executeQuery();
            while (rs.next()){
                requestId = rs.getInt("Request_ID");
            }
            PreparedStatement ps = con.prepareStatement("INSERT INTO Loan_Requests VALUES (?,?,?,?,?,?,?)");
            ps.setInt(1, (requestId + 1));
            ps.setString(2, accountNumber);
            ps.setString(3, amount);
            ps.setString(4, reason);
            ps.setString(5, "-");
            ps.setString(6, todayDate);
            ps.setString(7, time);
            ps.executeUpdate();
            con.close();
            rs.close();
            ps.close();
            return true;
        }
        return  false;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
