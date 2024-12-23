module employee.management.system {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.smartcardio;

    opens techcompany;
    exports techcompany.UIcomponent.EditCardInfoModal to javafx.fxml;
}