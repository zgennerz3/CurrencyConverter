module com.example.currencycon.currencyconverter {
    requires javafx.controls;
    requires javafx.fxml;
    requires okhttp3;
    requires annotations;


    opens com.example.currencyconverter to javafx.fxml;
    exports com.example.currencyconverter;
    exports com.example.currencyconverter.Controller;
    opens com.example.currencyconverter.Controller to javafx.fxml;
}