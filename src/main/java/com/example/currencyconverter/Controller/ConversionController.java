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

        // Check if inputs are sane (3 alphabetical letters)
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
                        double conversionRate = APIHandling.APIProcessing.ParseConversionResponse(
                                APIResponse.getKey(), toCurrencyInput.toUpperCase());
                        if (Double.isNaN(conversionRate)) {
                            ResultsText.setText("The output currency entered is not supported");
                        } else {
                            ResultsText.setText(String.format("The exchange rate of %s to \n %s is %f",
                                    fromCurrencyInput.toUpperCase(), toCurrencyInput.toUpperCase(),
                                            conversionRate).replaceAll("\\.?0+$", ""));
                                            // replace all trailing zeros with nothing
                            // If no amount of currency is specified, do not provide this result
                            if (AmountOfCurrency.getText().isEmpty()) {
                                ConvertedCurrencyValue.setText("");
                            }
                            // Also provide the total amount of money if conversion amount is specified
                            else {
                                String CurrencyInput = AmountOfCurrency.getText();
                                Double ConvertedCurrency = (Double.parseDouble(CurrencyInput) * conversionRate);
                                ConvertedCurrencyValue.setText(String.format("%s %s equals %.2f %s", CurrencyInput,
                                        fromCurrencyInput.toUpperCase(), ConvertedCurrency,
                                        toCurrencyInput.toUpperCase()));
                            }
                        }
                        break;
                    case 404: // invalid currency(s) case
                        ResultsText.setText("The conversion entered is not supported");
                        ConvertedCurrencyValue.setText("");
                        break;
                    case 422: // same currency case
                        ResultsText.setText(String.format("The conversion from %s to %s is 1...obviously",
                                fromCurrencyInput.toUpperCase(), toCurrencyInput.toUpperCase()));
                        ConvertedCurrencyValue.setText("");
                        break;
                    default: // unexpected error(s)
                        ResultsText.setText("An unexpected error occurred");
                        ConvertedCurrencyValue.setText("");
                }
            // catch especially the IOException, in an exceptional case
            } catch (Exception e) {
                ResultsText.setText("An unexpected error occurred, " +
                        "check your internet connection and try again");
                ConvertedCurrencyValue.setText("");
            }
        }
    }
}