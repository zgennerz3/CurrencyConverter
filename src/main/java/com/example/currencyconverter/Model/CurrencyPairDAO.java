package com.example.currencyconverter.Model;

import java.sql.*;
import java.util.function.Supplier;

/**
 * A utility class containing methods used for interacting with the database table of supported
 * currency pairs
 */
public class CurrencyPairDAO {
    /**
     * The supplied connection for the DAO to use
     */
    private final Supplier<Connection> connectionSupplier;

    /**
     * Instantiates a new CurrencyPairDAO with the supplied connection
     * @param connectionSupplier The connection to open the DAO with
     */
    public CurrencyPairDAO(Supplier<Connection> connectionSupplier) {
        this.connectionSupplier = connectionSupplier;
    }

    /**
     * Returns the connection used by the DAO
     * @return The connection used by the DAO
     */
    private Connection getConnection() {
        return connectionSupplier.get();
    }

    /** Creates the currencyPairs table in the database if it does not already exist (on first launch) */
    public void CreateTable() throws SQLException {
        try (Statement createTable = getConnection().createStatement()) {
            createTable.execute("CREATE TABLE IF NOT EXISTS currencyPairs ("
                    + "fromCurrency TEXT NOT NULL, "
                    + "toCurrency TEXT NOT NULL, "
                    + "PRIMARY KEY (fromCurrency, toCurrency))");
        }
    }

    /**
     * Inserts a given currency pair into the database
     * @param fromCurrency The base of the currency pair to be inserted
     * @param toCurrency The supported conversion of the currency pair to be inserted
     */
    public void InsertIgnore(String fromCurrency, String toCurrency) throws SQLException {
        String sql = "INSERT OR IGNORE INTO currencyPairs VALUES ( ?, ?)";
        try (PreparedStatement insertPair = getConnection().prepareStatement(sql)) {
            insertPair.setString(1, fromCurrency);
            insertPair.setString(2, toCurrency);
            insertPair.executeUpdate();
        }
    }
}
