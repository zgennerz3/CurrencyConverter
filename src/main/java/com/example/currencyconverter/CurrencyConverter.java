package com.example.currencyconverter;

import com.example.currencyconverter.Model.CurrencyPairDAO;
import com.example.currencyconverter.Model.DatabaseConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

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
        // Launch the app only if the database table is set up correctly first
        try {
            CurrencyPairDAO DAO = new CurrencyPairDAO(() -> {
                try {
                    return DatabaseConnection.getConnection();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            DAO.createTable();
            launch();
        }
        catch (SQLException | RuntimeException e) {
            System.err.println("Error in application startup (database)" + e.getMessage());
        }
    }
}
