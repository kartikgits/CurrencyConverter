package com.northwindlabs.kartikeya.currencyconverter;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private static final String RATE_REQUEST_URL = "https://api.exchangeratesapi.io/latest?base=USD&symbols=INR";

    private EditText usdToInrEditText, inrToUsdEditText;
    private Button usdToInrButton, inrToUsdButton, backButton;
    private TextView showRateTextView, connectionErrorTextView;
    private LinearLayout showRateView, midLayout, midNextLayout;
    private ProgressBar progressBar;

    private Currency currency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeVariables();


        ConnectivityManager cm = (ConnectivityManager) getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (isConnected) {
            progressBar.setVisibility(View.VISIBLE);
            new RateLoader().execute(RATE_REQUEST_URL);
        }
        else {
            midLayout.setVisibility(View.GONE);
            midNextLayout.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            connectionErrorTextView.setVisibility(View.VISIBLE);
        }
    }

    private void initializeVariables(){
        usdToInrEditText = findViewById(R.id.usd_entry);
        inrToUsdEditText = findViewById(R.id.inr_entry);

        usdToInrButton = findViewById(R.id.usd_to_inr);
        inrToUsdButton = findViewById(R.id.inr_to_usd);

        showRateTextView = findViewById(R.id.show_rate_textview);

        showRateView = findViewById(R.id.show_rate_view);
        midLayout = findViewById(R.id.mid_layout);
        midNextLayout = findViewById(R.id.mid_next_layout);

        progressBar = findViewById(R.id.progress_bar);
    }

    private class RateLoader extends AsyncTask<String, Void, Currency> {
        private static final String LOG_TAG_LOCAL = "RateLoader";

        @Override
        protected Currency doInBackground(String... urls) {
            Log.i(LOG_TAG_LOCAL, "loadInBackground called.");
            // Don't perform the request if there are no URLs
            if (urls.length < 1 || urls[0] == null) {
                return null;
            }
            Currency currency = QueryRates.fetchCurrencyData(urls[0]);
            return currency;
        }

        protected void onPostExecute(Currency currency) {
            // If there is no result, do nothing.
            if (currency == null) {
                return;
            }
            //update UI
            updateUi(currency);
        }
    }

    private void updateUi(Currency currency){
        usdToInrButton.setEnabled(true);
        inrToUsdButton.setEnabled(true);
        progressBar.setVisibility(View.INVISIBLE);
        this.currency= currency;
    }

    public void currencyConvert(View view){
        midLayout.setVisibility(View.GONE);
        midNextLayout.setVisibility(View.GONE);
        showRateView.setVisibility(View.VISIBLE);
        double money;
        switch (view.getId()){
            case R.id.usd_to_inr:
                try {
                    money = Double.valueOf(usdToInrEditText.getText().toString());
                    showRateTextView.setText(String.format("INR %.2f",(money*currency.getRate())));
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(this, "Invalid Input", Toast.LENGTH_SHORT).show();
                    showRateView.setVisibility(View.GONE);
                    midLayout.setVisibility(View.VISIBLE);
                    midNextLayout.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.inr_to_usd:
                try {
                    money = Double.valueOf(inrToUsdEditText.getText().toString());
                    showRateTextView.setText(String.format("USD %.2f",(money*(1/currency.getRate()))));
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(this, "Invalid Input", Toast.LENGTH_SHORT).show();
                    showRateView.setVisibility(View.GONE);
                    midLayout.setVisibility(View.VISIBLE);
                    midNextLayout.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    public void backButton(View view){
        showRateView.setVisibility(View.GONE);
        midLayout.setVisibility(View.VISIBLE);
        midNextLayout.setVisibility(View.VISIBLE);
    }
}
