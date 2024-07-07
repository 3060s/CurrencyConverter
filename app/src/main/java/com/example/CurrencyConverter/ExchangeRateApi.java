package com.example.CurrencyConverter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.io.IOException;
import java.net.URL;


public class ExchangeRateApi {

    final String base_url = "https://v6.exchangerate-api.com/v6/";
    final String api_key = "9bfa0afa08db0a3aa9f1029a/latest/";
    final String currency = "USD";
    String url = base_url + api_key + currency;

    void createRequest() throws MalformedURLException, IOException {
        URL url = new URL(this.url);
        HttpURLConnection request = (HttpURLConnection) url.openConnection();
        request.connect();
    }

    public JSONObject getConversionRates() throws IOException, JSONException {
        URL url = new URL(this.url);
        HttpURLConnection request = (HttpURLConnection) url.openConnection();
        request.connect();

        BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
        StringBuilder stringBuilder = new StringBuilder();

        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }

        String response = stringBuilder.toString();
        JSONObject jsonObject = new JSONObject(response);

        return jsonObject.getJSONObject("conversion_rates");
    }

    public static double calculateExchangeRate(JSONObject conversionRates, String baseCurrency, String targetCurrency) throws JSONException {
        double baseRate = conversionRates.getDouble(baseCurrency);
        double targetRate = conversionRates.getDouble(targetCurrency);
        return targetRate / baseRate;
    }

    public static double convertCurrency(JSONObject conversionRates, String baseCurrency, String targetCurrency, double amount) throws JSONException {
        double exchangeRate = calculateExchangeRate(conversionRates, baseCurrency, targetCurrency);
        return amount * exchangeRate;
    }
}