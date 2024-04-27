package com.balionis.dainius.sps.service;

import com.balionis.dainius.sps.generated.model.AddPriceResponse;
import com.balionis.dainius.sps.generated.model.FindPricesResponse;
import com.balionis.dainius.sps.generated.model.Price;
import com.balionis.dainius.sps.generated.model.Stock;
import com.balionis.dainius.sps.model.PriceRecord;
import com.balionis.dainius.sps.model.StockRecord;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface StockService {

    Stock addOrUpdateStock(Stock stock);
    List<Stock> findStocksByTicker(String ticker);
    AddPriceResponse addOrUpdatePrice(String ticker, Price price);
    FindPricesResponse findPricesByTickerAndDates(String ticker, LocalDate fromDate, LocalDate toDate);
}
