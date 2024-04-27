package com.balionis.dainius.sps.service;

import com.balionis.dainius.sps.model.PriceRecord;
import com.balionis.dainius.sps.model.StockRecord;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface StockService {

    StockRecord addOrUpdateStock(StockRecord stock);
    StockRecord updateStock(String ticker, StockRecord stock);
    List<StockRecord> findStocksByTicker(String ticker);
    List<StockRecord> getAllStocks();
    void addOrUpdatePrice(String ticker, BigDecimal price, LocalDate date);
    List<PriceRecord> getPriceHistory(String ticker, LocalDate fromDate, LocalDate toDate);
}
