package com.example.currencyconverter.Model;

import javafx.util.Pair;
import okhttp3.*;
import com.google.gson.*;

import java.io.IOException;
import java.util.*;

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
         * Fetches the list of supported conversions for a specified base (from) currency
         * @param fromCurrency The currency to get valid conversions for
         * @return A String containing the response body from the API
         * @throws IOException If an I/O error occurs during processing
         */
        public static String GetValidConversions(String fromCurrency) throws IOException {
            // Make a call to the API using the specified base (from) currency
            Request request = new Request.Builder().url(
                            String.format("https://api.frankfurter.dev/v1/latest?base=%s",
                                    fromCurrency)).build();
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
            return responseBodyString;
        }

        /**
         * Fetches the conversion data between two currencies from the Frankfurter API
         * @param fromCurrency The currency to convert from
         * @param toCurrency The currency to convert to
         * @return A Pair containing the body of the API response as a String,
         * and the HTTP code as an Integer
         * @throws IOException If an I/O error occurs during processing
         */
        public static Pair<String, Integer> GetConversionByCurrencies(
                String fromCurrency, String toCurrency) throws IOException {
            // Make a call to the API using the specified currencies
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
         * Extracts the valid conversion currencies (from the base currency) from the body of a
         * Frankfurter API response
         * @param APIResponseBody The body of a response from the Frankfurter API, as a string
         * @return An ArrayList of Strings of the valid conversion currencies, or an empty list if
         * parsing fails
         */
        public static List<String> ParseValidConversions(String APIResponseBody) {
            try {
                JsonObject jsonResponse = JsonParser.parseString(APIResponseBody).getAsJsonObject();
                JsonObject jsonRates = jsonResponse.getAsJsonObject("rates");
                Set<String> validCurrencies = jsonRates.keySet();
                return new ArrayList<>(validCurrencies);
            }
            catch (NullPointerException | JsonSyntaxException e) {
                return Collections.emptyList(); // return as a special value if parsing fails
            }
        }

        /**
         * Extracts the conversion from the base currency to a specified currency from a body of a
         * Frankfurter API response
         * @param APIResponseBody The body of a response from the Frankfurter API, as a string
         * @param toCurrency The currency to convert to (from the base currency used in the query)
         * @return The conversion rate as a double, or NaN if parsing fails
         */
        public static double ParseConversionResponse(String APIResponseBody, String toCurrency) {
            try {
                JsonObject jsonResponse = JsonParser.parseString(APIResponseBody).getAsJsonObject();
                JsonObject jsonRates = jsonResponse.getAsJsonObject("rates");
                return Double.parseDouble(jsonRates.get(toCurrency).toString());
            }
            catch (NullPointerException | JsonSyntaxException e) {
                return Double.NaN; // return as a special value if parsing fails
            }
        }
    }
}
