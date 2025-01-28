package com.example.currencyconverter.Controller;


import com.example.currencyconverter.Model.APIHandling;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.util.Pair;

public class ConversionController {
    @FXML private TextField CurrencyFrom;
    @FXML private TextField CurrencyTo;
    @FXML private Label ResultsText;
    @FXML private TextField AmountOfCurrency;
    @FXML private Label ConvertedCurrencyValue;

    @FXML
    private void OnConvertButtonClick() {
        // Get input text from the GUI
        String fromCurrencyInput = CurrencyFrom.getText();
        String toCurrencyInput = CurrencyTo.getText();

        // Check if inputs are sane (3 alphabetical letter)
        if (fromCurrencyInput.length() != 3 || toCurrencyInput.length() != 3
            || !fromCurrencyInput.matches("[a-zA-Z]+") || !toCurrencyInput.matches("[a-zA-Z]+")) {
            ResultsText.setText("Please enter valid currency codes");
        }
        else {
            // Attempt to perform currency conversion
            try {
                Pair<String, Integer> APIResponse = APIHandling.APICalls.GetConversionByCurrencies(
                        fromCurrencyInput, toCurrencyInput);
                // Switch on the HTTP code returned by the API call
                switch (APIResponse.getValue()) {
                    case 200: // successful case
                        double conversionRate = APIHandling.APIProcessing.GetConversionRate(
                                APIResponse.getKey(), toCurrencyInput);
                        if (conversionRate == -1.0) {
                            ResultsText.setText("The output currency entered is not supported");
                        } else {
                            ResultsText.setText(String.format("The conversion rate from %s to %s is %f",
                                    fromCurrencyInput.toUpperCase(), toCurrencyInput.toUpperCase(), conversionRate)
                                    .replaceAll("\\.?0+$", "")); // remove trailing zeros
                            // Check if user input for amount of currency to convert is empty
                            if (AmountOfCurrency.getText().isEmpty()) {
                                break;
                            }
                            else {
                                ConvertedCurrencyValue.setText("Working");
                                //ConvertSpecifiedAmount();
                            }
                        }
                        break;
                    case 404: // invalid currency(s) case
                        ResultsText.setText("The conversion entered is not supported");
                        break;
                    case 422: // both currency case
                        ResultsText.setText(String.format("The conversion from %s to %s is 1...obviously",
                                fromCurrencyInput.toUpperCase(), toCurrencyInput.toUpperCase()));
                        break;
                    default: // unexpected error(s)
                        ResultsText.setText("An unexpected error occurred");
                }
            } catch (Exception e) {
                ResultsText.setText("An unexpected error occurred, " +
                        "check your internet connection and try again");
            }
        }
    }

//    Multiplies user entered value by the conversion rate
    private void ConvertSpecifiedAmount() {
        APIHandling.APIProcessing.GetConversionRate.
        ConvertedCurrencyValue.setText("this function is working");
    }

////    Maybe refactor the regex to use here
//    private String ConversionRateFinder(String APIResponse) {
//        // regex logic
//    }
}