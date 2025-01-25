package com.example.currencyconverter.Model;

import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.Request;
import okhttp3.Call;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


public class API {
    public static class APICalls {
        public static Response GetConversionByCurrencies(String fromCurrency, String toCurrency) throws IOException {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(String.format(
                    "https://api.frankfurter.dev/v1/latest?base=%s&symbols=%s",
                            fromCurrency, toCurrency))
                    .build();
            Call call = client.newCall(request);
            return call.execute();
        }
    }

    public static class APIProcessing {
        public static double GetConversionRate(@NotNull Response APIResponse, String toCurrency) {
            String patternString = String.format("\"%s\":(.*?)}", toCurrency);
            Pattern pattern = Pattern.compile(patternString);
            try {
                assert APIResponse.body() != null;

                String conversionString = "-1.0";
                Matcher matches = pattern.matcher(APIResponse.body().string());
                if (matches.find()) {
                    conversionString = matches.group(1);
                }
                return Double.parseDouble(conversionString);
            }
            catch (IOException e) {
                return -2.0;
            }
        }
    }
}
