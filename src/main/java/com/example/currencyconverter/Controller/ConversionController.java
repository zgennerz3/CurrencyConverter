package com.example.currencyconverter.Controller;


import com.example.currencyconverter.Model.API;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import okhttp3.Response;

public class ConversionController {
    @FXML private TextField CurrencyFrom;
    @FXML private TextField CurrencyTo;
    @FXML private Label ResultsText;

    @FXML
    private void OnConvertButtonClick() {
        // Get input text from the GUI
        // TODO: make this go to upper case
        String fromCurrencyInput = CurrencyFrom.getText();
        String toCurrencyInput = CurrencyTo.getText();

        try {
            Response APIResponse = API.APICalls.GetConversionByCurrencies(fromCurrencyInput, toCurrencyInput);
            switch (APIResponse.code()) {
                case 200:
                    double conversionRate = API.APIProcessing.GetConversionRate(APIResponse, toCurrencyInput);
                    if (conversionRate == -1.0) {
                        ResultsText.setText("The output currency is not supported");
                    } else if (conversionRate == -2.0) {
                        ResultsText.setText("An unexpected error occurred");
                    } else {
                        ResultsText.setText("The conversion rate from " + fromCurrencyInput
                                + " to " + toCurrencyInput + " is " + Double.toString(conversionRate));
                    }
                    break;
                case 404:
                    ResultsText.setText("The input currency is not supported");
                    break;
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