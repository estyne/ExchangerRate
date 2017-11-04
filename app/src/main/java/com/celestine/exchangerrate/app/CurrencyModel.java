package com.celestine.exchangerrate.app;

/**
 * Created by celestine on 04/11/2017.
 */

public class CurrencyModel {
    private String currency;
    private Double rate;

    public CurrencyModel(String currency,Double rate){
        this.currency=currency;
        this.rate=rate;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }
}
