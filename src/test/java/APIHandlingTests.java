import com.example.currencyconverter.Model.APIHandling;
import javafx.util.Pair;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class APIHandlingTests {
    private static final OkHttpClient APIClient = new OkHttpClient();

    public static String GetAPIResponseBody() throws IOException {
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

    // Tests if the method returns a successful HTTP code and non-empty body for a valid conversion
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
    // Tests if the method returns a 'not found' HTTP code for an invalid conversion
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

    //
    @Test @Order(1)
    void testGoodGetConversionRate() {
        try {
            String responseBody = GetAPIResponseBody();
            double conversionRate = APIHandling.APIProcessing.GetConversionRate(
                    responseBody, "EUR");
            assertNotEquals(conversionRate, -1.0);
        }
        catch (IOException e) {
            fail("Test failed due to IO exception");
        }
    }
    //
    @Test @Order(1)
    void testBadGetConversionRate() {
        try {
            String responseBody = GetAPIResponseBody();
            double conversionRate = APIHandling.APIProcessing.GetConversionRate(
                    responseBody, "XXX");
            assertEquals(conversionRate, -1.0);
        }
        catch (IOException e) {
            fail("Test failed due to IO exception");
        }
    }
}
