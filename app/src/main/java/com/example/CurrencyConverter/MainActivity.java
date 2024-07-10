package com.example.CurrencyConverter;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.javafirstproject.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private JSONObject conversionRates;

    private class FetchConversionRatesTask extends android.os.AsyncTask<Void, Void, JSONObject> {
        private Exception exception;

        protected JSONObject doInBackground(Void... voids) {
            try {
                ExchangeRateApi api = new ExchangeRateApi();
                return api.getConversionRates();
            } catch (Exception e) {
                this.exception = e;
                return null;
            }
        }

        protected void onPostExecute(JSONObject conversionRates) {
            if (this.exception != null) {
                this.exception.printStackTrace();
                return;
            }

            MainActivity.this.conversionRates = conversionRates;

            Spinner baseCurrencySpinner = findViewById(R.id.spinner_base_currency);
            Spinner targetCurrencySpinner = findViewById(R.id.spinner_target_currency);
            Iterator<String> keys = conversionRates.keys();
            List<String> currencies = new ArrayList<>();
            while (keys.hasNext()) {
                currencies.add(keys.next());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, currencies);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            baseCurrencySpinner.setAdapter(adapter);
            targetCurrencySpinner.setAdapter(adapter);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new FetchConversionRatesTask().execute();

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Spinner baseCurrencySpinner = findViewById(R.id.spinner_base_currency);
                Spinner targetCurrencySpinner = findViewById(R.id.spinner_target_currency);
                TextView textView = findViewById(R.id.textViewID);
                TextView convertedAmount = findViewById(R.id.convertedAmountID);

                String baseCurrency = baseCurrencySpinner.getSelectedItem().toString();
                String targetCurrency = targetCurrencySpinner.getSelectedItem().toString();

                try {
                    double exchangeRate = ExchangeRateApi.calculateExchangeRate(conversionRates, baseCurrency, targetCurrency);
                    textView.setText("Exchange Rate: " + exchangeRate);

                    EditText baseAmount = findViewById(R.id.baseAmountID);
                    String amountText = baseAmount.getText().toString();
                    double amount = 0;
                    if (!amountText.equals("Converted Amount")) {
                        try {
                            amount = Double.parseDouble(amountText);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                    double convertedAmountValue = ExchangeRateApi.convertCurrency(conversionRates, baseCurrency, targetCurrency, amount);
                    String formattedAmount = String.format("%.2f", convertedAmountValue);
                    convertedAmount.setText("Converted Amount: " + formattedAmount);
                } catch (JSONException e) {
                    e.printStackTrace();
                    textView.setText("Error calculating exchange rate");
                }
            }
        });

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}