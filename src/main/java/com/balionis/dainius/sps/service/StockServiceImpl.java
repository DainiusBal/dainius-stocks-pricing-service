package com.balionis.dainius.sps.service;

import com.balionis.dainius.sps.model.PriceRecord;
import com.balionis.dainius.sps.model.StockRecord;
import com.balionis.dainius.sps.repository.StockRepository;
import com.balionis.dainius.sps.repository.PriceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
    public StockRecord addOrUpdateStock(StockRecord stock) {
        return stockRepository.save(stock);
    }


    @Override
    public StockRecord updateStock(String ticker, StockRecord updatedStock) {
        System.out.println("Updating stock for ticker: " + ticker);

        Optional<StockRecord> existingStockOptional = stockRepository.findByTicker(ticker);
        System.out.println("Existing stock: " + existingStockOptional);

        if (existingStockOptional.isEmpty()) {
            throw new RuntimeException("Stock with ticker " + ticker + " not found");
        }

        StockRecord existingStock =  existingStockOptional.get();
        existingStock.setDescription(updatedStock.getDescription());
        existingStock.setSharesOutstanding(updatedStock.getSharesOutstanding());
        System.out.println("Updated stock: " + existingStock);

        try {
            StockRecord savedStock = stockRepository.save(existingStock);
            System.out.println("Stock updated successfully: " + savedStock);
            return savedStock;
        } catch (Exception e) {
            throw new RuntimeException("Failed to update stock with ticker " + ticker, e);
        }
    }


    @Override
    public List<StockRecord> getAllStocks() {
        return stockRepository.findAll();
    }

    @Override
    public void addOrUpdatePrice(String ticker, BigDecimal priceValue, LocalDate date) {
        Optional<StockRecord> stock = stockRepository.findByTicker(ticker);
        if (stock.isPresent()) {
            PriceRecord price = new PriceRecord();
            price.setStock(stock.get());
            price.setPriceValue(priceValue);
            price.setPricingDate(date);
            priceRepository.save(price);
        } else {
            throw new RuntimeException("Unknown ticker: " + ticker);
        }
    }


    @Override
    public List<PriceRecord> getPriceHistory(String ticker, LocalDate fromDate, LocalDate toDate) {

        Optional<StockRecord> stock = stockRepository.findByTicker(ticker);

        if (stock.isPresent()) {
            String stockId = stock.get().getStockId();
            List<PriceRecord> prices = priceRepository.findByStockIdAndPricingDateBetween(stockId, fromDate, toDate);
            return prices;
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public List<StockRecord> findStocksByTicker(String ticker) {
        return stockRepository.findByTickerContaining(ticker);
    }

}


