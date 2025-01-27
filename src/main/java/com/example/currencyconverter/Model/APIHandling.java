package com.example.currencyconverter.Model;

import javafx.util.Pair;
import okhttp3.*;

import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


public class APIHandling {
    public static class APICalls {
        private static final OkHttpClient APIClient = new OkHttpClient();

        public static Pair<String, Integer> GetConversionByCurrencies(String fromCurrency, String toCurrency) throws IOException {
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

    public static class APIProcessing {
        public static double GetConversionRate(String APIResponseBody, String toCurrency) {
            // Search the body of the API response for the 'to' currency and extract the conversion.
            // If no match is found, the special value -1.0 is returned (conversions are always +ve).
            String toCurrencyUpper = toCurrency.toUpperCase(); // bc API results are always in all caps
            String patternString = String.format("\"%s\":(.*?)}", toCurrencyUpper);
            Pattern pattern = Pattern.compile(patternString);
            String conversionString;
            Matcher matches = pattern.matcher(APIResponseBody);
            if (matches.find()) {
                conversionString = matches.group(1);
            }
            else {
                conversionString = "-1.0";
            }
            return Double.parseDouble(conversionString);
        }
    }
}
