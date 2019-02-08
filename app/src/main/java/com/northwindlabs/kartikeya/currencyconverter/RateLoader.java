package com.northwindlabs.kartikeya.currencyconverter;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

public class RateLoader extends AsyncTaskLoader<Currency>{
    private static final String LOG_TAG = "RateLoader";

    private String url = null;

    public RateLoader (Context context, String mUrl){
        super(context);
        Log.i(LOG_TAG, "RateLoader default constructor called.");
        url = mUrl;
    }

    @Override
    protected void onStartLoading(){
        Log.i(LOG_TAG, "onStartLoading called.");
        forceLoad();
    }

    @Override
    public Currency loadInBackground(){
        Log.i(LOG_TAG, "loadInBackground called.");
        // Don't perform the request if there are no URLs
        if (url == null){
            return null;
        }
        Currency currency = QueryRates.fetchCurrencyData(url);
        return currency;
    }
}