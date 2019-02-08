package com.northwindlabs.kartikeya.currencyconverter;

public class Currency {
    String base;
    double rate;

    public void setBase(String base) {
        this.base = base;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public String getBase() {
        return base;
    }

    public double getRate() {
        return rate;
    }
}
