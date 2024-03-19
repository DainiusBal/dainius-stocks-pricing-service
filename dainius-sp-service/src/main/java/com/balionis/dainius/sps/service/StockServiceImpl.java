package com.balionis.dainius.sps.service;

import com.balionis.dainius.sps.model.Price;
import com.balionis.dainius.sps.model.Stock;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;


@Service
public class StockServiceImpl implements StockService {

    // For the sake of this example, let's assume we have an in-memory list to store stocks
    private List<Stock> stocks = new ArrayList<>();

    @Override
    public Stock addOrUpdateStock(Stock stock) {
        // Check if the stock already exists in the list
        Stock existingStock = findStockByTicker(stock.getTicker());
        if (existingStock != null) {
            // Update the existing stock
            existingStock.setDescription(stock.getDescription());
            existingStock.setSharesOutstanding(stock.getSharesOutstanding());
            return existingStock;
        } else {
            // Add the new stock to the list
            stocks.add(stock);
            return stock;
        }
    }

    @Override
    public List<Stock> getAllStocks() {
        // Return all stocks in the list
        return stocks;
    }

    @Override
    public void addOrUpdatePrice(String ticker, double price, String date) {

    }

    @Override
    public List<Price> getPriceHistory(String ticker, String fromDate, String toDate) {
        return null;
    }

    @Override
    public List<Stock> findStocksByTicker(String ticker) {
        // Find stocks in the list that match the given ticker
        List<Stock> matchingStocks = new ArrayList<>();
        for (Stock stock : stocks) {
            if (stock.getTicker().startsWith(ticker)) {
                matchingStocks.add(stock);
            }
        }
        return matchingStocks;
    }

    // Helper method to find a stock by ticker
    private Stock findStockByTicker(String ticker) {
        for (Stock stock : stocks) {
            if (stock.getTicker().equals(ticker)) {
                return stock;
            }
        }
        return null; // Stock not found
    }

    // Implement additional methods as needed
}

