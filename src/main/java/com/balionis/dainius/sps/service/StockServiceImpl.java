package com.balionis.dainius.sps.service;

import com.balionis.dainius.sps.model.PriceRecord;
import com.balionis.dainius.sps.model.StockRecord;
import com.balionis.dainius.sps.repository.StockRepository;
import com.balionis.dainius.sps.repository.PriceRepository;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
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
        Optional<StockRecord> existingStock = stockRepository.findByTicker(stock.getTicker());

        StockRecord record = existingStock.map(r -> {
            r.setDescription(stock.getDescription());
            r.setSharesOutstanding(stock.getSharesOutstanding());
            return r;
        }).orElseGet(() -> {
            StockRecord r = new StockRecord();
            r.setTicker(stock.getTicker());
            r.setDescription(stock.getDescription());
            r.setSharesOutstanding(stock.getSharesOutstanding());
            return r;
        });

        StockRecord savedStock = stockRepository.save(record);

        return savedStock;
    }

    @Override
    public List<StockRecord> findStocksByTicker(String ticker) {
        List<StockRecord> records = Strings.isBlank(ticker)
                ? stockRepository.findAll()
                : stockRepository.findByTickerContaining(ticker);

        return records;
    }

    @Override
    public PriceRecord addOrUpdatePrice(String ticker, BigDecimal priceValue, LocalDate pricingDate) {
        Optional<PriceRecord> existingPrice = priceRepository.findByTickerAndPricingDate(ticker, pricingDate);

        PriceRecord updatedRecord = existingPrice.map(r -> {
            r.setPriceValue(priceValue);
            r.setPricingDate(pricingDate);
            return r;
        }).orElseGet(() -> {
            Optional<StockRecord> existingStock = stockRepository.findByTicker(ticker);
            StockRecord stock = existingStock.orElseThrow(() -> new RuntimeException("Unknown ticker: " + ticker));

            PriceRecord r = new PriceRecord();
            r.setStock(stock);
            r.setPriceValue(priceValue);
            r.setPricingDate(pricingDate);
            return r;
        });
        PriceRecord savedPrice = priceRepository.save(updatedRecord);

        return savedPrice;
    }

    @Override
    public List<PriceRecord> findPricesByTickerAndDates(String ticker, LocalDate fromDate, LocalDate toDate) {
        return priceRepository.findByTickerAndPricingDateBetween(ticker, fromDate, toDate);
    }
}
