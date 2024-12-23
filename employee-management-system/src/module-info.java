module employee.management.system {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.smartcardio;
    requires java.desktop;

    opens techcompany;
    opens techcompany.UIcomponent.EditCardInfoModal to javafx.fxml;
    exports techcompany.UIcomponent.EditCardInfoModal to javafx.fxml;
}