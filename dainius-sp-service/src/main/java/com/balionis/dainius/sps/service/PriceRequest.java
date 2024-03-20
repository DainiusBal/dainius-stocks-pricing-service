package com.balionis.dainius.sps.service;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PriceRequest {
    private BigDecimal price;
    private String date;

    public BigDecimal getPrice() {
        return price;
    }

    public LocalDate getDate() {
        return LocalDate.parse(date);
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
