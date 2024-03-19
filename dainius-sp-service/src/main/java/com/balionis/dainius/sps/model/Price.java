package com.balionis.dainius.sps.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Price {
    private String ticker;
    private BigDecimal price;
    private LocalDate date;

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Price{" +
                "ticker='" + ticker + '\'' +
                ", price=" + price +
                ", date=" + date +
                '}';
    }
}
