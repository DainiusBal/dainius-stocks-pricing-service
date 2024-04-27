package com.balionis.dainius.sps.service;

import com.balionis.dainius.sps.model.PriceRecord;
import com.balionis.dainius.sps.model.StockRecord;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface StockService {

    StockRecord addOrUpdateStock(StockRecord stock);
    List<StockRecord> findStocksByTicker(String ticker);
    PriceRecord addOrUpdatePrice(String ticker,BigDecimal priceValue, LocalDate date);
    List<PriceRecord> findPricesByTickerAndDates(String ticker, LocalDate fromDate, LocalDate toDate);
}
