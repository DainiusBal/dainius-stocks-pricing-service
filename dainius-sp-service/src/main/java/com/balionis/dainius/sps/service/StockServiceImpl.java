package com.balionis.dainius.sps.service;

import com.balionis.dainius.sps.model.Price;
import com.balionis.dainius.sps.model.Stock;
import com.balionis.dainius.sps.repository.StockRepository;
import com.balionis.dainius.sps.repository.PriceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Service
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;
    private final PriceRepository priceRepository;

    @Autowired
    public StockServiceImpl(StockRepository stockRepository, PriceRepository priceRepository) {
        this.stockRepository = stockRepository;
        this.priceRepository = priceRepository;
    }

    @Override
    public Stock addOrUpdateStock(Stock stock) {
        return stockRepository.save(stock);
    }

    @Override
    public List<Stock> getAllStocks() {
        return stockRepository.findAll();
    }

    @Override
    public void addOrUpdatePrice(String ticker, BigDecimal priceValue, LocalDate date) {
        Stock stock = stockRepository.findByTicker(ticker);
        if (stock != null) {
            Price price = new Price();
            price.setStock(stock);
            price.setPriceValue(priceValue);
            price.setPricingDate(date);  // Set the pricingDate directly
            priceRepository.save(price);
        } else {
            throw new IllegalArgumentException("Unknown ticker: " + ticker);
        }
    }

    @Override
    public List<Price> getPriceHistory(String ticker, LocalDate fromDate, LocalDate toDate) {
        // First, find the stock by its ticker
        Stock stock = stockRepository.findByTicker(ticker);
        if (stock != null) {
            // If the stock is found, get its ID
            Long stockId = stock.getStockId();
            // Then, use the repository method to find prices for that stock within the specified date range
            return priceRepository.findByStockIdAndPricingDateBetween(stockId, fromDate, toDate);
        } else {
            // If the stock is not found, return an empty list or handle the error as needed
            return Collections.emptyList();
        }
    }

    @Override
    public List<Stock> findStocksByTicker(String ticker) {
        // Use the repository method to find stocks by ticker containing the specified substring
        return stockRepository.findByTickerContaining(ticker);
    }
}
