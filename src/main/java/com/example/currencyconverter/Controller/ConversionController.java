package com.example.currencyconverter.Controller;


import com.example.currencyconverter.Model.APIHandling;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.util.Pair;
import okhttp3.Response;

public class ConversionController {
    @FXML private TextField CurrencyFrom;
    @FXML private TextField CurrencyTo;
    @FXML private Label ResultsText;

    @FXML
    private void OnConvertButtonClick() {
        // Get input text from the GUI
        String fromCurrencyInput = CurrencyFrom.getText();
        String toCurrencyInput = CurrencyTo.getText();
        try {
            Pair<String, Integer> APIResponse =
                    APIHandling.APICalls.GetConversionByCurrencies(fromCurrencyInput, toCurrencyInput);
            // Switch on the HTTP code returned by the API call
            switch (APIResponse.getValue()) {
                case 200:
                    double conversionRate = APIHandling.APIProcessing.GetConversionRate(
                            APIResponse.getKey(), toCurrencyInput);
                    if (conversionRate == -1.0) {
                        ResultsText.setText("The output currency is not supported");
                    } else if (conversionRate == -2.0) {
                        ResultsText.setText("An unexpected error occurred");
                    } else {
                        ResultsText.setText(String.format("The conversion rate from %s to %s is %f",
                                fromCurrencyInput.toUpperCase(), toCurrencyInput.toUpperCase(), conversionRate));
                    } break;
                case 404:
                    ResultsText.setText("The conversion entered is not supported"); break;
                default:
                    ResultsText.setText("An unexpected error occurred");
            }
        }
        catch (Exception e) {
            ResultsText.setText("An unexpected error occurred, " +
                    "check your internet connection and try again");
        }
    }
}