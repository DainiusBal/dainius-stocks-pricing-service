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
import java.util.Optional;
import java.util.UUID;


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
    public Stock updateStock(String ticker, Stock updatedStock) {
        System.out.println("Updating stock for ticker: " + ticker);

        Stock existingStock = stockRepository.findByTicker(ticker);
        System.out.println("Existing stock: " + existingStock);

        if (existingStock == null) {
            throw new RuntimeException("Stock with ticker " + ticker + " not found");
        }

        existingStock.setDescription(updatedStock.getDescription());
        existingStock.setSharesOutstanding(updatedStock.getSharesOutstanding());
        System.out.println("Updated stock: " + existingStock);


        try {
            Stock savedStock = stockRepository.save(existingStock);
            System.out.println("Stock updated successfully: " + savedStock);
            return savedStock;
        } catch (Exception e) {
            throw new RuntimeException("Failed to update stock with ticker " + ticker, e);
        }
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
            price.setPricingDate(date);
            priceRepository.save(price);
        } else {
            throw new RuntimeException("Unknown ticker: " + ticker);
        }
    }


    @Override
    public List<Price> getPriceHistory(String ticker, LocalDate fromDate, LocalDate toDate) {

        Stock stock = stockRepository.findByTicker(ticker);

        if (stock != null) {
            String stockId = stock.getStockId();
            List<Price> prices = priceRepository.findByStockIdAndPricingDateBetween(stockId, fromDate, toDate);
            return prices;
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public List<Stock> findStocksByTicker(String ticker) {
        return stockRepository.findByTickerContaining(ticker);
    }

}


