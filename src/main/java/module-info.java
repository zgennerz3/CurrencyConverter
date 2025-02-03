module com.example.currencycon.currencyconverter {
    requires javafx.controls;
    requires javafx.fxml;
    requires okhttp3;
    requires annotations;
    requires com.google.gson;

    opens com.example.currencyconverter to javafx.fxml;
    exports com.example.currencyconverter;
    exports com.example.currencyconverter.Controller;
    exports com.example.currencyconverter.Model;
    opens com.example.currencyconverter.Controller to javafx.fxml;
}