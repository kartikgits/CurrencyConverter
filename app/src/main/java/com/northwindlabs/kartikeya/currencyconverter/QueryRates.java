package com.northwindlabs.kartikeya.currencyconverter;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

public class QueryRates {

    private QueryRates(){};

    private static Currency extractFromJSON(String ratesJSON){
        if (TextUtils.isEmpty(ratesJSON)){
            return null;
            //make sure to return default rates
        }

        Currency currency = new Currency();
        try {
            JSONObject baseJSON = new JSONObject(ratesJSON);
            String rateBase = baseJSON.getString("base");
            currency.setBase(rateBase);
            JSONObject rate = baseJSON.getJSONObject("rates");
            if (rateBase.equals("USD")){
                double currencyRate = rate.getDouble("INR");
                currency.setRate(currencyRate);
            } else if (rateBase.equals("INR")){
                double currencyRate = rate.getDouble("USD");
                currency.setRate(currencyRate);
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
        return currency;
    }

    public static Currency fetchCurrencyData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e("QueryRates", "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response
        Currency currency = extractFromJSON(jsonResponse);

        return currency;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e("QueryRates", "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e("QueryRates", "Problem retrieving the currency JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e("QueryRates", "Problem building the URL ", e);
        }
        return url;
    }

}
