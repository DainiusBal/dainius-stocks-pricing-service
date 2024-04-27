package com.balionis.dainius.sps.service;

import com.balionis.dainius.sps.model.PriceRecord;
import com.balionis.dainius.sps.model.StockRecord;
import com.balionis.dainius.sps.repository.PriceRepository;
import com.balionis.dainius.sps.repository.StockRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StockServiceImplTest {

    @Mock
    private StockRepository stockRepository;

    @Mock
    private PriceRepository priceRepository;

    @InjectMocks
    private StockServiceImpl stockService;

    @Test
    void testAddOrUpdateStock_shouldAddStock() {
        String ticker = "AAPL";
        StockRecord stock = new StockRecord(ticker, "New Description", 2000000);

        when(stockRepository.findByTicker(eq(ticker))).thenReturn(Optional.empty());

        when(stockRepository.save(any(StockRecord.class))).thenReturn(stock);

        StockRecord result = stockService.addOrUpdateStock(stock);

        assertEquals(stock.getDescription(), result.getDescription());
        assertEquals(stock.getSharesOutstanding(), result.getSharesOutstanding());

        verify(stockRepository).findByTicker(ticker);
        verify(stockRepository).save(any(StockRecord.class));
    }

    @Test
    void testAddOrUpdateStock_shouldUpdateStock() {
        String ticker = "AAPL";
        StockRecord updatedStock = new StockRecord(ticker, "Updated description", 128000000);
        StockRecord existingStock = new StockRecord(ticker, "Old description", 1000000);

        when(stockRepository.findByTicker(ticker)).thenReturn(Optional.of(existingStock));
        when(stockRepository.save(existingStock)).thenReturn(updatedStock);

        StockRecord result = stockService.addOrUpdateStock(updatedStock);

        assertNotNull(result);
        assertEquals(updatedStock.getTicker(), result.getTicker());
        assertEquals(updatedStock.getDescription(), result.getDescription());
        assertEquals(updatedStock.getSharesOutstanding(), result.getSharesOutstanding());

        verify(stockRepository).findByTicker(ticker);
        verify(stockRepository).save(existingStock);
    }

    @Test
    public void testAddOrUpdatePrice_shouldUpdatePrice() {
        String ticker = "AAPL";
        StockRecord stock = new StockRecord(ticker, "Apple Inc.", 1000000);
        BigDecimal priceValue = BigDecimal.valueOf(200.50);
        LocalDate pricingDate = LocalDate.now();

        PriceRecord price = new PriceRecord();
        price.setStock(stock);
        price.setPriceValue(priceValue);
        price.setPricingDate(pricingDate);

        when(priceRepository.findByTickerAndPricingDate(ticker, pricingDate)).thenReturn(Optional.of(price));
        when(priceRepository.save(price)).thenReturn(price);

        PriceRecord result = stockService.addOrUpdatePrice(ticker, priceValue, pricingDate);

        assertNotNull(result);
        assertEquals(stock, result.getStock());
        assertEquals(priceValue, result.getPriceValue());
        assertEquals(pricingDate, result.getPricingDate());

        verify(priceRepository).findByTickerAndPricingDate(ticker, pricingDate);
        verify(priceRepository).save(price);
        verifyNoInteractions(stockRepository);
    }

    @Test
    public void testAddOrUpdatePrice_shouldAddPrice() {
        String ticker = "AAPL";
        StockRecord stock = new StockRecord(ticker, "Apple Inc.", 1000000);
        BigDecimal priceValue = BigDecimal.valueOf(200);
        LocalDate pricingDate = LocalDate.now();

        PriceRecord price = new PriceRecord();
        price.setStock(stock);
        price.setPriceValue(priceValue);
        price.setPricingDate(pricingDate);

        when(stockRepository.findByTicker(ticker)).thenReturn(Optional.of(stock));
        when(priceRepository.findByTickerAndPricingDate(ticker, pricingDate)).thenReturn(Optional.empty());
        when(priceRepository.save(any(PriceRecord.class))).thenReturn(price);

        PriceRecord result = stockService.addOrUpdatePrice(ticker, priceValue, pricingDate);

        assertNotNull(result);
        assertEquals(stock, result.getStock());
        assertEquals(priceValue, result.getPriceValue());
        assertEquals(pricingDate, result.getPricingDate());

        // verify(stockRepository).findByTicker(ticker);
        // verify(priceRepository).save(price);
    }

    @Test
    public void testAddOrUpdatePrice_shouldThrowExceptionWithInvalidTicker() {

        String invalidTicker = "INVALID";
        BigDecimal priceValue = BigDecimal.valueOf(200.50);
        LocalDate date = LocalDate.now();

        assertThrows(RuntimeException.class, () ->
                stockService.addOrUpdatePrice(invalidTicker, priceValue, date));
    }

    @Test
    void testFindStocksByTicker() {

        String ticker = "AAPL";
        StockRecord stock1 = new StockRecord("AAPL.N", "Apple Inc.", 1000000);
        StockRecord stock2 = new StockRecord("AAPL.L", "Apple Inc.", 900000);
        List<StockRecord> expectedStocks = Arrays.asList(stock1, stock2);

        when(stockRepository.findByTickerContaining(ticker)).thenReturn(expectedStocks);

        List<StockRecord> result = stockService.findStocksByTicker(ticker);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(stock1, result.get(0));

        verify(stockRepository).findByTickerContaining(ticker);
    }

    @Test
    void testFindPricesByTickerAndDates() {

        String ticker = "AAPL";
        LocalDate fromDate = LocalDate.of(2023, 1, 1); // Example from date
        LocalDate toDate = LocalDate.of(2023, 1, 31); // Example to date
        StockRecord stock = new StockRecord();
        stock.setTicker(ticker);

        PriceRecord price = new PriceRecord();
        price.setStock(stock);
        price.setPricingDate(fromDate);
        price.setPriceValue(BigDecimal.valueOf(123));

        when(priceRepository.findByTickerAndPricingDateBetween(ticker, fromDate, toDate)).thenReturn(List.of(price));

        List<PriceRecord> result = stockService.findPricesByTickerAndDates(ticker, fromDate, toDate);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(priceRepository).findByTickerAndPricingDateBetween(ticker, fromDate, toDate);
    }
}
