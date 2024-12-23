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
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import techcompany.entities.Car;
import techcompany.entities.Response;
import techcompany.service.CarService;
import techcompany.service.BalanceService;
import techcompany.util.Constant;
import techcompany.util.Utils;
import javafx.stage.FileChooser;
import java.io.File;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;


public class Dashboardcontroller  implements Initializable {

    @FXML
    public Button changePin_btn;

    @FXML
    public Button enable_card;

    @FXML
    public Label label_show_noti_form_create;

    @FXML
    public Label label_show_noti_form_balance;


    @FXML
    public TextField id_card;

    @FXML
    public TextField type_of_car;

    @FXML
    public TextField OwnerName;

    @FXML
    public TextField model_car;

    @FXML
    public TextField color_car;

    @FXML
    public TextField number_of_car;

    @FXML
    public TextField pin_code;

    @FXML
    public Button add_car;

    @FXML
    public ImageView choose_image_view;

    @FXML
    public Button choose_image;

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

    // Start khai báo button của phần kết nối
    @FXML
    private Label pinErrorText;
    @FXML
    private Label idLabel;
    @FXML
    private Label ownerNameLabel;
    @FXML
    private Label carModelLabel;
    @FXML
    private Label carColorLabel;
    @FXML
    private Label licensePlateLabel;
    @FXML
    private Label carBranchLabel;
    @FXML
    private Label statusLabel;
    @FXML
    private TextField pinInput;

    @FXML
    private Button disconnectCardBtn;

    @FXML
    private Button connectCardBtn;

    @FXML
    private Button updateImageBtn;

    @FXML
    ImageView imgPreview;

    // End khai báo button của phần kết nói

    private int incorrectPinAttempts = 0;
    private int MAX_INCORRECT_ATTEMPTS = 5;
    private Connection connect;
    private Statement statement;
    private PreparedStatement prepare;
    private ResultSet result;
    private byte[] imageByte;

    // Thêm các phần tử liên quan đến chức năng nạp/trừ tiền
    @FXML
    private Label balanceLabel;

    @FXML
    private TextField amountInput;

    @FXML
    private Button depositButton;

    @FXML
    private Button withdrawButton;

    private BalanceService balanceService;

    private String idCard;

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
                getSoDu();
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
    public void initialize(URL location, ResourceBundle resources){

    }

    public void enableCard() {
        Response response = Utils.connectCardAndGetID();
        if(response.errorCode == Constant.SUCCESS) {
            label_show_noti_form_create.setText("Success!!!");
            idCard = response.getdata();
            id_card.setText(response.data);
        } else {
            label_show_noti_form_create.setText("Error " + response.getErrorCode() + ": " + response.data);
        }
    }



    //Logic cho tab kết nối
    @FXML
    private void handleConnectCard() {
        String pin = pinInput.getText();
        if (isValidPin(pin)) {
            byte[] pinBytes = pin.getBytes(StandardCharsets.UTF_8);
            byte ins = (byte) 02;
            byte lc = (byte) pinBytes.length;
            Response response = Utils.saveAndGetData(ins, lc, pinBytes);
            if(response.errorCode == Constant.SUCCESS) {
                String infor = response.getdata();
                String[] parts = infor.split("@");
                id_card.setText(idCard);
                licensePlateLabel.setText(parts[0]);
                carBranchLabel.setText(parts[1]);
                carModelLabel.setText(parts[2]);
                carColorLabel.setText(parts[3]);
                ownerNameLabel.setText(parts[4]);
            }
            else {
                incorrectPinAttempts++;
                if (incorrectPinAttempts >= MAX_INCORRECT_ATTEMPTS) {
                    connectCardBtn.setDisable(true);
                    pinErrorText.setVisible(true);
                    pinErrorText.setText("Bạn đã nhập sai mã PIN quá 3 lần. Vui lòng thử lại sau.");
                } else {
                    pinErrorText.setVisible(true);
                    int remainingAttempts = MAX_INCORRECT_ATTEMPTS - incorrectPinAttempts;
                    pinErrorText.setText("Sai mã PIN: Bạn còn " + remainingAttempts + " lần nhập lại");
                }
            }
        } else {
            // Display an error message for invalid PIN
            pinErrorText.setVisible(true);
            pinErrorText.setText("Invalid PIN. Please enter a valid PIN.");
        }
    }

    @FXML
    private void handleDisconnectCard() {

    }

    @FXML
    private void handleEditAvatar() {

    }


    // Helper method to validate PIN code
    private boolean isValidPin(String pin) {
        return pin != null && !pin.isEmpty();
    }
    public void createCar() {
        String idCard = id_card.getText();
        String OwnerCar = OwnerName.getText();
        String modelCar = model_car.getText();
        String colorCar = color_car.getText();
        String typeOfCar = type_of_car.getText();
        String numberOfCar = number_of_car.getText();
        String pinCode = String.valueOf(pin_code.getText());
        Car car = new Car(OwnerCar, modelCar, typeOfCar, colorCar, numberOfCar, pinCode);
        String carStr = car.toString();
        byte[] bytes = carStr.getBytes(StandardCharsets.UTF_8);
        byte ins = (byte) 01;
        byte lc = (byte) bytes.length;
        Response response = Utils.saveAndGetData(ins, lc, bytes);
        if(response.errorCode == Constant.SUCCESS) {
            pinCode = "";
            String publicKey = response.getdata();
            BigDecimal balance = new BigDecimal("1000000.00");
            car.setBalance(balance);
            car.setPin(pinCode);
            car.setPublicKey(publicKey);
            car.setIdCard(idCard);
            CarService.createCarInfo(connect, car);
        }
    }

    public byte[] chooseimage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            try {
                imageByte = Utils.getBytesFromFile(selectedFile);
                Image image = new Image(selectedFile.toURI().toString());
                choose_image_view.setImage(image);
                choose_image_view.setFitWidth(120);
                choose_image_view.setFitHeight(120);
                choose_image_view.setPreserveRatio(false);
                choose_image_view.setSmooth(true);
                byte ins = (byte) 07;
                byte lc = (byte) imageByte.length;
                Response response = Utils.saveAndGetData(ins, lc, imageByte);
                if(response.errorCode == Constant.SUCCESS) {
                    System.out.println("Image chosen");
                }
                return imageByte;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Không có ảnh nào được chọn.");
        }
        return null;
    }

    public void getSoDu(){
        byte ins = (byte) 06;
        Response response = Utils.getData(ins);
        String result = response.getdata().replace("@", "");
        System.out.println("Số dư: " + result);
        if(result.equals("")) {
            balanceService.setBalance(0.0);
            balanceLabel.setText("Số dư: 0.0đ");
        }
        else {
            balanceService.setBalance(Double.parseDouble(response.getdata()));
            balanceLabel.setText(response.getdata());
        }
    }

    // Xử lý sự kiện nạp tiền
    @FXML
    public void handleDeposit(ActionEvent event) {
        try {
            double amount = Double.parseDouble(amountInput.getText());
            String message = balanceService.deposit(amount);
            balanceLabel.setText("Số dư: " + balanceService.getBalance() + " đ");
            amountInput.clear();
            label_show_noti_form_balance.setText(message);
        } catch (NumberFormatException e) {
            label_show_noti_form_balance.setText("Vui lòng nhập số tiền hợp lệ!");
        }
    }

    // Xử lý sự kiện trừ tiền
    @FXML
    public void handleWithdraw(ActionEvent event) {
        try {
            double amount = Double.parseDouble(amountInput.getText());
            String message = balanceService.withdraw(amount);
            balanceLabel.setText("Số dư: " + balanceService.getBalance() + " đ");
            amountInput.clear();
            label_show_noti_form_balance.setText(message);
        } catch (NumberFormatException e) {
            label_show_noti_form_balance.setText("Vui lòng nhập số tiền hợp lệ!");
        }
    }

}
