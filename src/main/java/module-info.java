module com.example.currencycon.currencyconverter {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.currencyconverter to javafx.fxml;
    exports com.example.currencyconverter;
}