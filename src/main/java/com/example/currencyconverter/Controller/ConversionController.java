package com.example.currencyconverter.Controller;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class ConversionController {
    @FXML private TextField CurrencyInput;
    @FXML private TextField CurrencyOutput;

    @FXML
    private void OnConvertButtonClick() {
        String fromCurrencyInput = CurrencyInput.getText();
        String toCurrencyInput = CurrencyOutput.getText();
    }
}