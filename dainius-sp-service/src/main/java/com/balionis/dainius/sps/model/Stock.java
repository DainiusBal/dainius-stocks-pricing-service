package com.balionis.dainius.sps.model;

public class Stock {
    private String ticker;
    private String description;
    private int sharesOutstanding;

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getSharesOutstanding() {
        return sharesOutstanding;
    }

    public void setSharesOutstanding(int sharesOutstanding) {
        this.sharesOutstanding = sharesOutstanding;
    }

    @Override
    public String toString() {
        return "Stock{" +
                "ticker='" + ticker + '\'' +
                ", description='" + description + '\'' +
                ", sharesOutstanding=" + sharesOutstanding +
                '}';
    }
}
