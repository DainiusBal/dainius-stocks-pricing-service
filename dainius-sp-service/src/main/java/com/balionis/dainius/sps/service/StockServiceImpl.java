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
    public Stock updateStock(String ticker, Stock stock) {
        Stock existingStock = stockRepository.findByTicker(ticker);
        if (existingStock == null) {
            throw new RuntimeException("Stock with ticker " + ticker + " not found");
        }
        existingStock.setDescription(stock.getDescription());
        existingStock.setSharesOutstanding(stock.getSharesOutstanding());
        return stockRepository.save(existingStock);
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
            Long stockId = stock.getStockId();
            return priceRepository.findByStockIdAndPricingDateBetween(stockId, fromDate, toDate);
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public List<Stock> findStocksByTicker(String ticker) {
        return stockRepository.findByTickerContaining(ticker);
    }
}


