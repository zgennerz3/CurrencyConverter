import static org.junit.jupiter.api.Assertions.*;

import com.example.currencyconverter.Model.CurrencyPairDAO;
import org.junit.jupiter.api.*;
import java.sql.*;

public class CurrencyPairDAOTests {
    private Connection connection;
    private CurrencyPairDAO DAO;

    /**
     * Sets up a mock database in memory to test DAO classes without modifying the actual database
     * @throws SQLException If accessing or querying the database fails
     */
    @BeforeEach
    void setUp() throws SQLException {
        // Set up a mock database in memory for testing
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");
        DAO = new CurrencyPairDAO(() -> connection);
        DAO.CreateTable();
    }

    /**
     * Tests for the successful insertion of a currency pair into the database
     * @throws SQLException If accessing or querying the database fails
     */
    @Test @Order(0)
    void testInsert() throws SQLException {
        DAO.InsertIgnore("USD", "EUR");
        try (Statement stmt = connection.createStatement();
             ResultSet RS = stmt.executeQuery("SELECT COUNT(*) FROM currencyPairs")) {
            assertTrue(RS.next());
            assertEquals(1, RS.getInt(1));
        }
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.close();
    }
}