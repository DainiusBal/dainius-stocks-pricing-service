package com.balionis.dainius.sps.service;

import com.balionis.dainius.sps.model.Price;
import com.balionis.dainius.sps.model.Stock;

import java.util.List;

public interface StockService {
    Stock addOrUpdateStock(Stock stock);
    List<Stock> findStocksByTicker(String ticker);
    List<Stock> getAllStocks();
    // New methods for managing stock prices
    void addOrUpdatePrice(String ticker, double price, String date);
    List<Price> getPriceHistory(String ticker, String fromDate, String toDate);
}
