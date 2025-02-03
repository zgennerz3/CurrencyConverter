package com.example.currencyconverter.Model;

import javafx.util.Pair;
import okhttp3.*;
import com.google.gson.*;

import java.io.IOException;

/**
 * A utility class containing methods used for querying and interpreting the responses of queries
 * to the Frankfurter currency conversion API
 */
public class APIHandling {
    /**
     * A static class containing methods to query the Frankfurter API
     */
    public static class APICalls {
        /**
         * A single instance of the HTTP client used for all requests
          */
        private static final OkHttpClient APIClient = new OkHttpClient();

        /**
         * Fetches the conversion data between two currencies from the Frankfurter API
         * @param fromCurrency the currency to convert from
         * @param toCurrency the currency to convert to
         * @return A Pair containing the body of the API response as a String,
         * and the HTTP code as an Integer
         * @throws IOException If an I/O error occurs during processing
         */
        public static Pair<String, Integer> GetConversionByCurrencies(
                String fromCurrency, String toCurrency) throws IOException {
            // Make the call to the API using the specified currencies
            Request request = new Request.Builder().url(
                    String.format("https://api.frankfurter.dev/v1/latest?base=%s&symbols=%s",
                            fromCurrency, toCurrency))
                    .build();
            Call call = APIClient.newCall(request);
            Response response = call.execute();
            String responseBodyString;

            // If the response has a null body, coalesce it to an empty string
            if (response.body() != null) {
                responseBodyString = response.body().string();
            }
            else {
                responseBodyString = "";
            }
            return new Pair<>(responseBodyString, response.code());
        }
    }

    /**
     * A static class containing methods to interpret the bodies of responses from the Frankfurter API
     */
    public static class APIProcessing {
        /**
         * Extracts the conversion from the base currency to a specified currency from a body of a
         * Frankfurter API response
         * @param APIResponseBody The body of a response from the Frankfurter API, as a string
         * @param toCurrency The currency to convert to (from the base currency used in the query)
         * @return The conversion rate, as a double, or -1.0 if parsing fails
         */
        public static double ParseConversionResponse(String APIResponseBody, String toCurrency)
                throws JsonSyntaxException {
            try {
                JsonObject jsonResponse = JsonParser.parseString(APIResponseBody).getAsJsonObject();
                JsonObject jsonRates = jsonResponse.getAsJsonObject("rates");
                return Double.parseDouble(jsonRates.get(toCurrency).toString());
            }
            catch (NullPointerException e) {
                return Double.NaN; // return NaN if parsing fails due to a bad query
            }
        }
    }
}
