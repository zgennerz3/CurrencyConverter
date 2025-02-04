package com.example.currencyconverter;

import com.example.currencyconverter.Model.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class CurrencyConverter extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(CurrencyConverter.class.getResource(
                "View/FXML/conversion-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 720, 480);
        stage.setTitle("Currency Converter");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        // Check the database can be set up correctly before launch
        try {
            CurrencyPairDAO DAO = new CurrencyPairDAO(() -> {
                try { return DatabaseConnection.getConnection(); }
                catch (SQLException e) { throw new RuntimeException(e); }});
            DAO.CreateTable();
        }
        catch (SQLException | RuntimeException e) {
            System.err.println("Error in application startup (database setup)" + e.getMessage());
        }

        // Update (or populate) the database with valid conversion pairs before launch,
        // starting with EUR, because the API uses the European Central Bank
        try {
            // Retrieve valid EUR to ... conversions
            CurrencyPairDAO DAO = new CurrencyPairDAO(() -> {
                try { return DatabaseConnection.getConnection(); }
                catch (SQLException e) { throw new RuntimeException(e); }});
            String APIResponseBody = APIHandling.APICalls.GetValidConversions("EUR");
            ArrayList<String> validCurrenciesEUR = (ArrayList<String>)
                    APIHandling.APIProcessing.ParseValidConversions(APIResponseBody);
            // Store EUR to ... conversions
            for (String toCurrency : validCurrenciesEUR) {
                DAO.InsertIgnore("EUR", toCurrency);
            }
            // Retrieve and store every other valid conversion based on the valid EUR conversions
            for (String nextCurrency : validCurrenciesEUR) {
                String aPIResponseBody = APIHandling.APICalls.GetValidConversions(nextCurrency);
                ArrayList<String> validCurrencies = (ArrayList<String>)
                        APIHandling.APIProcessing.ParseValidConversions(aPIResponseBody);
                for (String toCurrency : validCurrencies) {
                    DAO.InsertIgnore(nextCurrency, toCurrency);
                }
            }
        }
        catch (RuntimeException | SQLException | IOException e) {
            System.err.println("Error in application startup (database population)" + e.getMessage());
        }

        launch();
    }
}
