package techcompany.UIcomponent.EditCardInfoModal;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EditCardInfoController {

    @FXML private TextField idField;
    @FXML private TextField ownerNameField;
    @FXML private TextField carModelField;
    @FXML private TextField carColorField;
    @FXML private TextField licensePlateField;
    @FXML private TextField brandField;
    private int edit;

    private Stage dialogStage;
    private boolean saveClicked = false;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setData(String id, String ownerName, String carModel,
                        String carColor, String licensePlate, String brandLabel) {
        idField.setText(id);
        ownerNameField.setText(ownerName);
        carModelField.setText(carModel);
        carColorField.setText(carColor);
        licensePlateField.setText(licensePlate);
        brandField.setText(brandLabel);
    }

    public boolean isSaveClicked() {
        return saveClicked;
    }

    public String getId() {
        return idField.getText();
    }

    public String getOwnerName() {
        return ownerNameField.getText();
    }

    public String getCarModel() {
        return carModelField.getText();
    }

    public String getCarColor() {
        return carColorField.getText();
    }

    public String getLicensePlate() {
        return licensePlateField.getText();
    }

    public String getBrand() {
        return brandField.getText();
    }

    @FXML
    private void handleSave() {
        saveClicked = true;
        dialogStage.close();
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }
}