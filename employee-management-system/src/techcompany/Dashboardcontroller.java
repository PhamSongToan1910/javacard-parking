/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package techcompany;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import techcompany.UIcomponent.EditCardInfoModal.EditCardInfoController;
import techcompany.entities.Car;
import techcompany.entities.History;
import techcompany.entities.Response;
import techcompany.service.CarService;
import techcompany.service.BalanceService;
import techcompany.service.HistoryService;
import techcompany.util.Constant;
import techcompany.util.Utils;
import techcompany.entities.CardInfo;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class Dashboardcontroller implements Initializable {

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
    public Button editCardInfo;

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
    private Button historyPage_btn;

    @FXML
    private AnchorPane historyPage;

    @FXML
    private Button addUser_btn;

    @FXML
    private Button home_btn;

    @FXML
    private AnchorPane depDesig_form;

    // Change PIN section
    @FXML
    private PasswordField oldPinField;
    @FXML
    private PasswordField newPinField;
    @FXML
    private PasswordField confirmPinField;
    @FXML
    private Label errorLabel;
    @FXML
    private Button getChangePin_btn;

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

    @FXML
    Label balanceLabel;

    // End khai báo button của phần kết nói

    // Start khai báo history
    @FXML
    private Button incomingCarBtn;

    @FXML
    private Button outgoingCarBtn;

    @FXML
    private TableView<History> historyTable;

    @FXML
    private TableColumn<History, String> timeIn;

    @FXML
    private TableColumn<History, String> timeOut;

    //

    private int incorrectPinAttempts = 0;
    private int MAX_INCORRECT_ATTEMPTS = 5;
    private Connection connect = database.connectDb();
    private byte[] imageByte;
    private long timeInLong;
    private long timeOutLong;
    private Car car;

    // Thêm các phần tử liên quan đến chức năng nạp/trừ tiền
    @FXML
    private Label brandLabel;

    @FXML
    private TextField amountInput;

    @FXML
    private Button depositButton;

    @FXML
    private Button withdrawButton;

    private BalanceService balanceService;

    private String idCard;

    public Dashboardcontroller() {
        this.balanceService = new BalanceService();
    }

    public void switchForm(ActionEvent event) {
        if (event.getSource() == home_btn) {

            home_form.setVisible(true);
            addEmployee_form.setVisible(false);
            historyPage.setVisible(false);
            addUser_form.setVisible(false);
            depDesig_form.setVisible(false);

        } else if (event.getSource() == addEmployee_btn) {
            home_form.setVisible(false);
            addEmployee_form.setVisible(true);
            historyPage.setVisible(false);
            addUser_form.setVisible(false);
            depDesig_form.setVisible(false);
        }
        else if(event.getSource() == historyPage_btn) {
            home_form.setVisible(false);
            addEmployee_form.setVisible(false);
            historyPage.setVisible(true);
            addUser_form.setVisible(false);
            depDesig_form.setVisible(false);
            initializeTableData();
        }
        else if (event.getSource() == addUser_btn) {
            home_form.setVisible(false);
            addEmployee_form.setVisible(false);
            historyPage.setVisible(false);
            addUser_form.setVisible(true);
            depDesig_form.setVisible(false);
            getSoDu();
        } else if (event.getSource() == changePin_btn) {
            home_form.setVisible(false);
            addEmployee_form.setVisible(false);
            historyPage.setVisible(false);
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
        if (response.errorCode == Constant.SUCCESS) {
            label_show_noti_form_create.setText("Đã kích hoạt thành công");
            idCard = response.getdata();
            id_card.setText(response.data);
        } else {
            label_show_noti_form_create.setText("Error " + response.getErrorCode() + ": " + response.data);
        }
    }

    // Logic cho tab kết nối

    private Stage primaryStage;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    private void handleConnectCard() throws Exception {
        String pin = pinInput.getText();
        if (isValidPin(pin)) {
            byte[] pinBytes = Utils.encryptData(pin,Utils.publicKey);
            System.out.println("mã hóa: " + pinBytes.toString());
            byte ins = (byte) 02;
            byte lc = (byte) pinBytes.length;
            Response response = Utils.login(ins, lc, pinBytes);
            if (response.errorCode == Constant.SUCCESS) {
                String infor = response.getdata();
                String[] parts = infor.split("@");
                idLabel.setText(idCard);
                licensePlateLabel.setText(parts[0]);
                brandLabel.setText(parts[1]);
                carModelLabel.setText(parts[2]);
                carColorLabel.setText(parts[3]);
                ownerNameLabel.setText(parts[4]);

                incorrectPinAttempts = 0;
                statusLabel.setText("Đã kết nối thẻ");
                pinErrorText.setVisible(false);
                disconnectCardBtn.setDisable(false);
                editCardInfo.setDisable(false);
                updateImageBtn.setDisable(false);
                pinErrorText.setText("");
                addUser_btn.setDisable(false);
                changePin_btn.setDisable(false);
            } else {
                incorrectPinAttempts++;
                if (incorrectPinAttempts >= MAX_INCORRECT_ATTEMPTS) {
                    editCardInfo.setDisable(true);
                    pinErrorText.setVisible(true);
                    pinErrorText.setText("Bạn đã nhập quá số lần cho phép.");


                } else {
                    pinErrorText.setVisible(true);
                    editCardInfo.setDisable(true);
                    disconnectCardBtn.setDisable(true);
                    updateImageBtn.setDisable(true);
                    statusLabel.setText("Xin hãy kết nối thẻ");
                    addUser_btn.setDisable(true);
                    changePin_btn.setDisable(true);
                    resetConnectField();
                    int remainingAttempts = MAX_INCORRECT_ATTEMPTS - incorrectPinAttempts;
                    pinErrorText.setText("Sai mã PIN: Bạn còn " + remainingAttempts + " lần nhập lại");
                }
            }
        } else {
            // Display an error message for invalid PIN
            pinErrorText.setVisible(true);
            pinErrorText.setText("Cần phải nhập mã PIN");
        }
    }

    private void resetConnectField() {
        idLabel.setText("Text ID");
        licensePlateLabel.setText("Tên chủ xe");
        brandLabel.setText("Hãng xe");
        carModelLabel.setText("Mẫu xe");
        carColorLabel.setText("Màu xe");
        ownerNameLabel.setText("Tên chủ xe");
    }

    @FXML
    private void handleDisconnectCard() {
        int input = (balanceService.getBalance() - 10000) / 10000;
        byte[] bytes = ByteBuffer.allocate(4).putInt(input).array();

        Response response = Utils.saveAndGetMonney((byte) 0x05, (byte) 0x00, bytes);

        if (response.errorCode == Constant.SUCCESS) {
            disconnectCardBtn.setDisable(true);
            editCardInfo.setDisable(true);
            updateImageBtn.setDisable(true);
            statusLabel.setText("Xin hãy kết nối thẻ");
            addUser_btn.setDisable(true);
            changePin_btn.setDisable(true);
            resetConnectField();
        } else {
            label_show_noti_form_balance.setText("Lỗi khi xử lý giao dịch.");
        }

    }

    @FXML
    private void handleEditAvatar() {

    }

    @FXML
    private void handleOpenModalEditCardInfo() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/techcompany/UIcomponent/EditCardInfoModal/EditCardInfoModal.fxml"));
            AnchorPane page = loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Information");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(((Stage) main_form.getScene().getWindow()));
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            EditCardInfoController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setData(idLabel.getText(), ownerNameLabel.getText(),
                    carModelLabel.getText(), carColorLabel.getText(),
                    licensePlateLabel.getText(), brandLabel.getText());

            dialogStage.showAndWait();

            if (controller.isSaveClicked()) {
                CardInfo cardInfo = new CardInfo(
                        controller.getId(),
                        controller.getOwnerName(),
                        controller.getCarModel(),
                        controller.getCarColor(),
                        controller.getLicensePlate(),
                        controller.getBrand());

                StringBuilder carInfor = new StringBuilder(controller.getLicensePlate()+"@"+controller.getBrand());
                carInfor.append("@"+controller.getCarModel()+ "@"+ controller.getCarColor()+ "@"+ controller.getOwnerName() );

                byte[] bytes = carInfor.toString().getBytes(StandardCharsets.UTF_8);
                byte ins = (byte) 8;
                byte lc = (byte) bytes.length;
                Response response = Utils.saveAndGetData(ins, lc, bytes);
                if (response.errorCode == Constant.SUCCESS) {
                    idLabel.setText(controller.getId());
                    licensePlateLabel.setText(controller.getLicensePlate());
                    brandLabel.setText(controller.getBrand());
                    carModelLabel.setText(controller.getCarModel());
                    carColorLabel.setText(controller.getCarColor());
                    ownerNameLabel.setText(controller.getOwnerName());

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Thành công");
                    alert.setHeaderText(null);
                    alert.setContentText("Sửa thông tin thành công");
                    alert.showAndWait();


                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Lỗi");
                    alert.setHeaderText(null);
                    alert.setContentText("Lỗi chưa lưu được thông tin");
                    alert.showAndWait();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Helper method to validate PIN code
    private boolean isValidPin(String pin) {
        return pin != null && !pin.isEmpty();
    }

    public void createCar() {
        if(pin_code.getText().equals("")){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText(null);
            alert.setContentText("Vui lòng nhập mã pin");
            alert.showAndWait();
            return ;
        }

        String idCard = id_card.getText();
        String OwnerCar = OwnerName.getText();
        String modelCar = model_car.getText();
        String colorCar = color_car.getText();
        String typeOfCar = type_of_car.getText();
        String numberOfCar = number_of_car.getText();
        String pinCode = String.valueOf(pin_code.getText());
        car = new Car(OwnerCar, modelCar, typeOfCar, colorCar, numberOfCar, pinCode);
        String carStr = car.toString();
        byte[] bytes = carStr.getBytes(StandardCharsets.UTF_8);
        byte ins = (byte) 01;
        byte lc = (byte) bytes.length;
        Response response = Utils.saveAndGetRSA(ins, lc, bytes);
        if (response.errorCode == Constant.SUCCESS) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Thành công");
            alert.setHeaderText(null);
            alert.setContentText(" Khởi tạo thẻ thành công");
            alert.showAndWait();

            id_card.setText("");
            OwnerName.setText("");
            model_car.setText("");
            color_car.setText("");
            type_of_car.setText("");
            number_of_car.setText("");
            pin_code.setText("");
            label_show_noti_form_create.setText("Chưa khỏi tạo thẻ");



            String publicKey = response.getdata();
            BigDecimal balance = new BigDecimal("1000000.00");
            car.setBalance(balance);
            car.setPin(pinCode);
            car.setPublicKey(publicKey);
            car.setIdCard(idCard);
            CarService.createCarInfo(connect, car);


        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText(null);
            alert.setContentText("Lỗi chưa khởi tạo được thẻ");
            alert.showAndWait();
        }
    }

    public byte[] chooseimage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));

        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            try {
                imageByte = Utils.getBytesFromFile(selectedFile);
                System.out.println("ảnh: ");
                for (byte b : imageByte) {
                    System.out.printf("%02X ", b); // In mỗi byte dưới dạng 2 ký tự hex, ví dụ: FF
                }
                System.out.println();

                Image image = new Image(selectedFile.toURI().toString());
                choose_image_view.setImage(image);
                choose_image_view.setFitWidth(120);
                choose_image_view.setFitHeight(120);
                choose_image_view.setPreserveRatio(false);
                choose_image_view.setSmooth(true);
                byte ins = (byte) 07;
                byte lc = (byte) imageByte.length;
                Response response = Utils.saveAndGetData(ins, lc, imageByte);
                if (response.errorCode == Constant.SUCCESS) {
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

    public void getSoDu() {
        byte ins = (byte) 0x06;
        Response response = Utils.getMonney(ins);
        String result = response.getdata().replace("@", "");
        System.out.println("Số dư: " + result);
        if (result.equals("")) {
            balanceService.setBalance(0);
            balanceLabel.setText("Số dư: 0đ");
        } else {
            balanceService.setBalance(Integer.parseInt(response.getdata()) * 10000);
            balanceLabel.setText("Số dư: " + (Integer.parseInt(result) * 10000) + " đ");
            amountInput.setText("");
        }
        label_show_noti_form_balance.setText("Vui lòng nhập bội của 10.000");
    }

    //Hanlde history page
    @FXML
    private void handleIncomingCar(ActionEvent event) {
        //Để truyền data vào xin hãy làm giốn initialData
        timeInLong = System.currentTimeMillis();
        HistoryService.createHistory(connect, new History(idCard, String.valueOf(timeInLong), String.valueOf(timeOutLong)));
        initializeTableData();
        incomingCarBtn.setDisable(true);
        outgoingCarBtn.setDisable(false);
    }

    @FXML
    private void handleOutgoingCar(ActionEvent event) {
        //Để truyền data vào bảng xin hãy làm giốn initialData
        if(car.getBalance().compareTo(new BigDecimal(10000)) >= 0) {
            timeOutLong = System.currentTimeMillis();
            HistoryService.updateHistory(connect, new History(idCard, String.valueOf(timeInLong), String.valueOf(timeOutLong)));
            initializeTableData();
            car.setBalance(car.getBalance().subtract(new BigDecimal(10000)));
            int input = car.getBalance().intValue() / 10000;
            System.out.println(input);
            byte[] bytes = ByteBuffer.allocate(4).putInt(input).array();

            Response response = Utils.saveAndGetMonney((byte) 0x05, (byte) 0x00, bytes);
            if (response.errorCode == Constant.SUCCESS) {
                incomingCarBtn.setDisable(false);
                outgoingCarBtn.setDisable(true);
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Lỗi");
                alert.setHeaderText(null);
                alert.setContentText("Không thể thanh toán");
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Lỗi");
            alert.setHeaderText(null);
            alert.setContentText("Số dư hiện tại không đủ vui lòng nạp thêm!!!");
            alert.showAndWait();
        }
    }

    ObservableList<History> initialData() {
        ObservableList<History> historyList = FXCollections.observableArrayList();
        List<History> histories = HistoryService.getHistoryList(connect, idCard);
        historyList.addAll(histories);
        return historyList;
    }

    private void initializeTableData() {
        // Set up the column cell factories - only for timeIn and timeOut
        timeIn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getTimeIn()));

        timeOut.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getTimeOut()));

        // Load the data
        historyTable.setItems(initialData());
    }

    // Xử lý sự kiện nạp tiền
    @FXML
    public void handleDeposit(ActionEvent event) {
        int amount = Integer.parseInt(amountInput.getText().trim());
        if (amount <= 0 || amount % 10000 != 0) {
            label_show_noti_form_balance.setText("Vui lòng nhập lại số tiền. Số tiền phải là bội của 10.000 VND.");
        } else {
            car.setBalance(car.getBalance().add(new BigDecimal(amount)));
            int input = car.getBalance().intValue() / 10000;
            byte[] bytes = ByteBuffer.allocate(4).putInt(input).array();

            Response response = Utils.saveAndGetMonney((byte) 0x05, (byte) 0x00, bytes);

            if (response.errorCode == Constant.SUCCESS) {
                String result = response.getdata();

                balanceLabel.setText("Số dư: " + (Integer.parseInt(result) * 10000) + " đ");
                amountInput.setText("");
            } else {
                // Hiển thị lỗi nếu có
                label_show_noti_form_balance.setText("Lỗi khi xử lý giao dịch.");
            }
        }
    }


    // Xử lý sự kiện trừ tiền
    @FXML
    public void handleWithdraw(ActionEvent event) {
        try {
            int amount = Integer.parseInt(amountInput.getText());
            String message = balanceService.withdraw(new BigDecimal(amount), car);
            int input = car.getBalance().intValue() / 10000;
            byte[] bytes = ByteBuffer.allocate(4).putInt(input).array();

            Response response = Utils.saveAndGetMonney((byte) 0x05, (byte) 0x00, bytes);

            if (response.errorCode == Constant.SUCCESS) {
                String result = response.getdata();

                balanceLabel.setText("Số dư: " + (Integer.parseInt(result) * 10000) + " đ");
                amountInput.setText("");
                label_show_noti_form_balance.setText(message);
            } else {
                // Hiển thị lỗi nếu có
                label_show_noti_form_balance.setText("Lỗi khi xử lý giao dịch.");
            }
        } catch (NumberFormatException e) {
            label_show_noti_form_balance.setText("Vui lòng nhập số tiền hợp lệ!");
        }
    }

    //Xử lý chức năng đổi mã PIN
    @FXML
    private void handleConfirmButton() {
        String oldPin = oldPinField.getText();
        String newPin = newPinField.getText();
        String confirmPin = confirmPinField.getText();

        try {
            byte[] bytes = oldPin.getBytes(StandardCharsets.UTF_8);
            Response response = Utils.changePassword((byte) 0x09, bytes);
            if (response.errorCode != Constant.SUCCESS) {
                errorLabel.setText("Mã PIN hiện tại nhập không đúng vui lòng nhập lại.");
            }
            else if (!newPin.equals(confirmPin)) {
                errorLabel.setText("Vui lòng nhập lại mã PIN mới không khớp!");
            }
            else{
                byte[] byte1s = newPin.getBytes(StandardCharsets.UTF_8);
                Response response1 = Utils.changePassword((byte) 0x04, byte1s);
                if(response1.errorCode == Constant.SUCCESS) {
                    errorLabel.setText("Thay đổi mã pin thành công!");
                    oldPinField.setText("");
                    newPinField.setText("");
                    confirmPinField.setText("");
                }
                else {
                    errorLabel.setText("Vui lòng nhập mã pin mới khác với mã pin cũ.");
                }
            }


        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
