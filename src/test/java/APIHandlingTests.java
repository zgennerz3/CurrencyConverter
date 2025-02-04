import com.example.currencyconverter.Model.APIHandling;
import javafx.util.Pair;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class APIHandlingTests {
    /**
     * A single instance of the HTTP client used for all requests during unit testing
     */
    private static final OkHttpClient APIClient = new OkHttpClient();

    /**
     * Fetches the conversion data from USD to EUR from the Frankfurter API
     * @return The body of the API response, as a String
     * @throws IOException If an I/O error occurs during processing
     */
    public static String GetSpecificAPIResponseBody() throws IOException {
        Request request = new Request.Builder().url(
                "https://api.frankfurter.dev/v1/latest?base=USD&symbols=EUR").build();
        Call call = APIClient.newCall(request);
        Response APIResponse = call.execute();
        if (APIResponse.body() != null) {
            return APIResponse.body().string();
        }
        else {
            return "";
        }
    }

    /**
     * Fetches the conversion data from USD to all valid currencies from the Frankfurter API
     * @return The body of the API response, as a String
     * @throws IOException If an I/O error occurs during processing
     */
    public static String GetGeneralAPIResponseBody() throws IOException {
        Request request = new Request.Builder().url(
                "https://api.frankfurter.dev/v1/latest?base=USD").build();
        Call call = APIClient.newCall(request);
        Response APIResponse = call.execute();
        if (APIResponse.body() != null) {
            return APIResponse.body().string();
        }
        else {
            return "";
        }
    }

    /**
     * Fetches the error message from an invalid query to the Frankfurter API
     * @return The body of the API response, as a String
     * @throws IOException If an I/O error occurs during processing
     */
    public static String GetBadAPIResponseBody() throws IOException {
        Request request = new Request.Builder().url(
                "https://api.frankfurter.dev/v1/latest?base=XXX").build();
        Call call = APIClient.newCall(request);
        Response APIResponse = call.execute();
        if (APIResponse.body() != null) {
            return APIResponse.body().string();
        }
        else {
            return "";
        }
    }

    /**
     * Tests for a successful HTTP code and non-empty body for a valid conversion
     */
    @Test @Order(0)
    void testGoodGetConversionByCurrencies() {
        try {
            Pair<String, Integer> APIResponse = APIHandling.APICalls.GetConversionByCurrencies("USD", "eur");
            assertEquals(APIResponse.getValue(), 200);
            assertFalse(APIResponse.getKey().isBlank());
        }
        catch (IOException e) {
            fail("Test failed due to IO exception");
        }
    }
    /**
     * Tests for a 'not found' HTTP code for an invalid conversion
     */
    @Test @Order(0)
    void testBadGetConversionByCurrencies() {
        try {
            Pair<String, Integer> APIResponse = APIHandling.APICalls.GetConversionByCurrencies(
                    "USD", "XXX");
            assertEquals(APIResponse.getValue(), 404);
        }
        catch (IOException e) {
            fail("Test failed due to IO exception");
        }
    }

    /**
     * Tests for a valid conversion rate from a valid query body
     */
    @Test @Order(1)
    void testGoodGetConversionRate() {
        try {
            String responseBody = GetSpecificAPIResponseBody();
            double conversionRate = APIHandling.APIProcessing.ParseConversionResponse(
                    responseBody, "EUR");
            assertNotEquals(conversionRate, Double.NaN);
        }
        catch (IOException e) {
            fail("Test failed due to IO exception");
        }
    }
    /**
     * Tests for a NaN (parsing failed) return from a failed query body
     */
    @Test @Order(1)
    void testBadGetConversionRate() {
        try {
            String responseBody = GetSpecificAPIResponseBody();
            double conversionRate = APIHandling.APIProcessing.ParseConversionResponse(
                    responseBody, "XXX");
            assertEquals(conversionRate, Double.NaN);
        }
        catch (IOException e) {
            fail("Test failed due to IO exception");
        }
    }

    /**
     * Tests for a non-error, non-empty response body from a valid query
     */
    @Test @Order(2)
    void testGoodGetValidConversions() {
        try {
            String responseBody = APIHandling.APICalls.GetValidConversions("USD");
            assertNotEquals(responseBody, "{\"message\":\"not found\"}");
            assertFalse(responseBody.isBlank());
        }
        catch (IOException e) {
            fail("Test failed due to IO exception");
        }
    }

    /**
     * Tests for the expected error response body from an invalid query
     */
    @Test @Order(2)
    void testBadGetValidConversions() {
        try {
            String responseBody = APIHandling.APICalls.GetValidConversions("XXX");
            assertEquals(responseBody, "{\"message\":\"not found\"}");
        }
        catch (IOException e) {
            fail("Test failed due to IO exception");
        }
    }

    /**
     * Tests for a non-empty list of valid conversions from a valid query body
     */
    @Test @Order(3)
    void testGoodParseValidConversions() {
        try {
            String responseBody = GetGeneralAPIResponseBody();
            ArrayList<String> validCurrencies =
                    new ArrayList<>(APIHandling.APIProcessing.ParseValidConversions(responseBody));
            assertFalse(validCurrencies.isEmpty());
        }
        catch (IOException e) {
            fail("Test failed due to IO exception");
        }
    }

    /**
     * Tests for an expected empty list of valid conversions from an invalid query body
     */
    @Test @Order(3)
    void testBadParseValidConversions() {
        try {
            String responseBody = GetBadAPIResponseBody();
            ArrayList<String> validCurrencies =
                    new ArrayList<>(APIHandling.APIProcessing.ParseValidConversions(responseBody));
            assertTrue(validCurrencies.isEmpty());
        }
        catch (IOException e) {
            fail("Test failed due to IO exception");
        }
    }
}
