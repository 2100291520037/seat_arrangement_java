module com.example.goldenwithui {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.datatransfer;


    opens com.example.goldenwithui to javafx.fxml;
    exports com.example.goldenwithui;
}