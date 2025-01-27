import com.example.currencyconverter.Model.APIHandling;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.fail;

public class APIHandlingTests {
    public Response GetAPIResponse() throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(
                "https://api.frankfurter.dev/v1/latest?base=%s&symbols=USD").build();
        Call call = client.newCall(request);
        return call.execute();
    }

    @Test @Order(0)
    void TestGetConversionsByCurrencies() {
    }
}
