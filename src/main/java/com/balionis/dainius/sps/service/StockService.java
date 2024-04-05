package com.balionis.dainius.sps.service;

import com.balionis.dainius.sps.model.Price;
import com.balionis.dainius.sps.model.Stock;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface StockService {

    Stock addOrUpdateStock(Stock stock);
    Stock updateStock(String ticker, Stock stock);
    List<Stock> findStocksByTicker(String ticker);
    List<Stock> getAllStocks();
    void addOrUpdatePrice(String ticker, BigDecimal price, LocalDate date);
    List<Price> getPriceHistory(String ticker, LocalDate fromDate, LocalDate toDate);
}
