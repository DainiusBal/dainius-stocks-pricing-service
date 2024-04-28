package com.balionis.dainius.sps.service;

import com.balionis.dainius.sps.generated.model.AddPriceResponse;
import com.balionis.dainius.sps.generated.model.FindPricesResponse;
import com.balionis.dainius.sps.generated.model.Price;
import com.balionis.dainius.sps.generated.model.Stock;
import com.balionis.dainius.sps.model.PriceRecord;
import com.balionis.dainius.sps.model.StockRecord;
import com.balionis.dainius.sps.repository.StockRepository;
import com.balionis.dainius.sps.repository.PriceRepository;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

        return new Stock().stockId(UUID.fromString(savedStock.getStockId()))
                .ticker(savedStock.getTicker())
                .description(savedStock.getDescription())
                .sharesOutstanding(savedStock.getSharesOutstanding());
    }

    @Override
    public List<Stock> findStocksByTicker(String ticker) {
        List<StockRecord> records = Strings.isBlank(ticker)
                ? stockRepository.findAll()
                : stockRepository.findByTickerContaining(ticker);

        return records.stream().map(r ->
                new Stock().stockId(UUID.fromString(r.getStockId()))
                        .ticker(r.getTicker())
                        .description(r.getDescription())
                        .sharesOutstanding(r.getSharesOutstanding())
        ).toList();
    }

    @Override
    public AddPriceResponse addOrUpdatePrice(String ticker, Price price) {
        Optional<PriceRecord> existingPrice = priceRepository.findByTickerAndPricingDate(ticker, price.getPricingDate());

        PriceRecord updatedRecord = existingPrice.map(r -> {
            r.setPriceValue(price.getPriceValue());
            r.setPricingDate(price.getPricingDate());
            return r;
        }).orElseGet(() -> {
            Optional<StockRecord> existingStock = stockRepository.findByTicker(ticker);
            StockRecord stock = existingStock.orElseThrow(() -> new RuntimeException("Unknown ticker: " + ticker));

            PriceRecord r = new PriceRecord();
            r.setStock(stock);
            r.setPriceValue(price.getPriceValue());
            r.setPricingDate(price.getPricingDate());
            return r;
        });
        PriceRecord savedPrice = priceRepository.save(updatedRecord);

        return new AddPriceResponse().stockId(UUID.fromString(savedPrice.getStock().getStockId()))
                .price(new Price().priceId(UUID.fromString(savedPrice.getPriceId()))
                        .priceValue(savedPrice.getPriceValue())
                        .pricingDate(savedPrice.getPricingDate()));
    }

    @Override
    public FindPricesResponse findPricesByTickerAndDates(String ticker, LocalDate fromDate, LocalDate toDate) {
        Optional<StockRecord> existingStock = stockRepository.findByTicker(ticker);
        StockRecord stock = existingStock.orElseThrow(() -> new RuntimeException("Unknown ticker: " + ticker));

        List<PriceRecord> priceRecords = priceRepository.findByStockIdAndPricingDateBetween(stock.getStockId(), fromDate, toDate);
        List<Price> prices = priceRecords.stream().map(r ->
                new Price().priceId(UUID.fromString(r.getPriceId()))
                        .priceValue(r.getPriceValue())
                        .pricingDate(r.getPricingDate())
        ).toList();

        return  new FindPricesResponse().stockId(UUID.fromString(stock.getStockId())).prices(prices);
    }
}
