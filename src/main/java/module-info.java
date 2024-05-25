module com.example.afnd1 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.afnd1 to javafx.fxml;
    exports com.example.afnd1;
}