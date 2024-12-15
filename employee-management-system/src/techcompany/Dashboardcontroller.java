/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package techcompany;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import techcompany.entities.Response;
import techcompany.util.Constant;
import techcompany.util.Utils;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;


public class Dashboardcontroller implements Initializable {

    @FXML
    public Button changePin_btn;

    @FXML
    public Button enable_card;

    @FXML
    public Label label_show_noti_form_create;

    @FXML
    public TextField id_card_form_create;

    @FXML
    private AnchorPane main_form;

    @FXML
    private AnchorPane home_form;

    @FXML
    private AnchorPane addUser_form;

    @FXML
    private Button addEmployee_btn;

    @FXML
    private AnchorPane addEmployee_form;
 
    @FXML
    private Button addUser_btn;

    @FXML
    private Button home_btn;

    @FXML
    private AnchorPane depDesig_form;

    private Connection connect;
    private Statement statement;
    private PreparedStatement prepare;
    private ResultSet result;

    public Dashboardcontroller() {
    }


    public void switchForm(ActionEvent event) {
        if (event.getSource() == home_btn) {

            home_form.setVisible(true);
            addEmployee_form.setVisible(false);
            addUser_form.setVisible(false);
            depDesig_form.setVisible(false);

        } else if (event.getSource() == addEmployee_btn) {
                home_form.setVisible(false);
                addEmployee_form.setVisible(true);
                addUser_form.setVisible(false);
                depDesig_form.setVisible(false);
        } else if (event.getSource() == addUser_btn) {
                home_form.setVisible(false);
                addEmployee_form.setVisible(false);
                addUser_form.setVisible(true);
                depDesig_form.setVisible(false);

        } else if (event.getSource() == changePin_btn) {
                home_form.setVisible(false);
                addEmployee_form.setVisible(false);
                addUser_form.setVisible(false);
                depDesig_form.setVisible(true);
        }
    }



    public void close() {
        System.exit(0);
    }

    public void minimize() {
        Stage stage = (Stage) main_form.getScene().getWindow();
        stage.setIconified(true);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void enableCard() {
        Response response = Utils.connectCardAndGetID();
        if(response.errorCode == Constant.SUCCESS) {
            label_show_noti_form_create.setText("Success!!!");
            id_card_form_create.setText(response.data);
        } else {
            label_show_noti_form_create.setText("Error " + response.getErrorCode() + ": " + response.data);
        }
    }
}
