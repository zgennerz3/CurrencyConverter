package com.example.currencyconverter;

import com.example.currencyconverter.Model.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

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
            System.err.println("Error in application startup (database setup): " + e.getMessage());
            System.exit(1);
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

            // Retrieve and store every other valid conversion based on the valid 'EUR to ...' currencies,
            // running each call/store method in parallel using threads
            @SuppressWarnings("unchecked") // every thread here is void, warning safe to ignore
            CompletableFuture<Void>[] aPICallStoreThreads = new CompletableFuture[validCurrenciesEUR.size()];
            ArrayList<Throwable> threadErrors = new ArrayList<>();
            for (int i = 0; i < validCurrenciesEUR.size(); i++) {
                String currencyToCall = validCurrenciesEUR.get(i);
                aPICallStoreThreads[i] = CompletableFuture.runAsync(() -> {
                    try {
                        String aPIResponseBody = APIHandling.APICalls.GetValidConversions(currencyToCall);
                        ArrayList<String> validCurrencies = (ArrayList<String>)
                                APIHandling.APIProcessing.ParseValidConversions(aPIResponseBody);
                        for (String toCurrency : validCurrencies) {
                            DAO.InsertIgnore(currencyToCall, toCurrency);
                        }
                    } catch (Exception e) { // Log exception(s), preventing data races
                        synchronized (threadErrors) {
                            threadErrors.add(e);
                        }
                    }
                });
            }
            // Execute all threads and then proceed, or shut down on fail(s)
            CompletableFuture.allOf(aPICallStoreThreads).join();
            if (!threadErrors.isEmpty()) {
                System.err.println("Error in application startup (database population stage 2): ");
                threadErrors.forEach(e -> System.err.println(e.getMessage()));
                System.exit(1);
            }
        }
        // Shut down if db population fails
        catch (RuntimeException | SQLException | IOException e) {
            System.err.println("Error in application startup (database population stage 1): "
                    + e.getMessage());
            System.exit(1);
        }

        launch();
    }
}
