package com.balionis.dainius.sps.service;

import com.balionis.dainius.sps.generated.model.AddPriceResponse;
import com.balionis.dainius.sps.generated.model.FindPricesResponse;
import com.balionis.dainius.sps.generated.model.Price;
import com.balionis.dainius.sps.generated.model.Stock;
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
        StockRecord stockRecord = new StockRecord(ticker, "New Description", 2000000);
        stockRecord.setStockId(UUID.randomUUID().toString());

        when(stockRepository.findByTicker(eq(ticker))).thenReturn(Optional.empty());
        when(stockRepository.save(any(StockRecord.class))).thenReturn(stockRecord);

        Stock stock = new Stock().ticker(ticker).description("New Description").sharesOutstanding(2000000);

        Stock result = stockService.addOrUpdateStock(stock);

        assertEquals(stock.getDescription(), result.getDescription());
        assertEquals(stock.getSharesOutstanding(), result.getSharesOutstanding());

        verify(stockRepository).findByTicker(ticker);
        verify(stockRepository).save(any(StockRecord.class));
    }

    @Test
    void testAddOrUpdateStock_shouldUpdateStock() {
        String ticker = "AAPL";
        UUID stockId = UUID.randomUUID();
        StockRecord updatedStock = new StockRecord(ticker, "Updated description", 128000000);
        updatedStock.setStockId(stockId.toString());
        StockRecord existingStock = new StockRecord(ticker, "Old description", 1000000);
        existingStock.setStockId(stockId.toString());

        when(stockRepository.findByTicker(ticker)).thenReturn(Optional.of(existingStock));
        when(stockRepository.save(existingStock)).thenReturn(updatedStock);

        Stock stock = new Stock().ticker(ticker).description("Updated description").sharesOutstanding(128000000);

        Stock result = stockService.addOrUpdateStock(stock);

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
        stock.setStockId(UUID.randomUUID().toString());

        BigDecimal priceValue = BigDecimal.valueOf(200.50);
        LocalDate pricingDate = LocalDate.now();

        PriceRecord priceRecord = new PriceRecord();
        priceRecord.setStock(stock);
        priceRecord.setPriceValue(priceValue);
        priceRecord.setPricingDate(pricingDate);
        priceRecord.setPriceId(UUID.randomUUID().toString());

        when(priceRepository.findByTickerAndPricingDate(ticker, pricingDate)).thenReturn(Optional.of(priceRecord));
        when(priceRepository.save(priceRecord)).thenReturn(priceRecord);

        Price price = new Price().priceValue(priceValue).pricingDate(pricingDate);

        AddPriceResponse result = stockService.addOrUpdatePrice(ticker, price);

        assertNotNull(result);
        assertEquals(stock.getStockId(), result.getStockId().toString());
        assertEquals(priceValue, result.getPrice().getPriceValue());
        assertEquals(pricingDate, result.getPrice().getPricingDate());

        verify(priceRepository).findByTickerAndPricingDate(ticker, pricingDate);
        verify(priceRepository).save(priceRecord);
        verifyNoInteractions(stockRepository);
    }

    @Test
    public void testAddOrUpdatePrice_shouldAddPrice() {
        String ticker = "AAPL";
        StockRecord stockRecord = new StockRecord(ticker, "Apple Inc.", 1000000);
        stockRecord.setStockId(UUID.randomUUID().toString());
        BigDecimal priceValue = BigDecimal.valueOf(200);
        LocalDate pricingDate = LocalDate.now();

        PriceRecord priceRecord = new PriceRecord();
        priceRecord.setStock(stockRecord);
        priceRecord.setPriceValue(priceValue);
        priceRecord.setPricingDate(pricingDate);
        priceRecord.setPriceId(UUID.randomUUID().toString());

        Price price = new Price().priceValue(priceValue).pricingDate(pricingDate);

        when(stockRepository.findByTicker(ticker)).thenReturn(Optional.of(stockRecord));
        when(priceRepository.findByTickerAndPricingDate(ticker, pricingDate)).thenReturn(Optional.empty());
        when(priceRepository.save(any(PriceRecord.class))).thenReturn(priceRecord);

        AddPriceResponse result = stockService.addOrUpdatePrice(ticker, price);

        assertNotNull(result);
        assertEquals(stockRecord.getStockId(), result.getStockId().toString());
        assertEquals(priceValue, result.getPrice().getPriceValue());
        assertEquals(pricingDate, result.getPrice().getPricingDate());

        // verify(stockRepository).findByTicker(ticker);
        // verify(priceRepository).save(price);
    }

    @Test
    public void testAddOrUpdatePrice_shouldThrowExceptionWithInvalidTicker() {

        String invalidTicker = "INVALID";
        BigDecimal priceValue = BigDecimal.valueOf(200.50);
        LocalDate pricingDate = LocalDate.now();

        Price price = new Price().pricingDate(pricingDate).priceValue(priceValue);

        assertThrows(RuntimeException.class, () ->
                stockService.addOrUpdatePrice(invalidTicker, price));
    }

    @Test
    void testFindStocksByTicker() {

        String ticker = "AAPL";
        StockRecord stock1 = new StockRecord("AAPL.N", "Apple Inc.", 1000000);
        stock1.setStockId(UUID.randomUUID().toString());
        StockRecord stock2 = new StockRecord("AAPL.L", "Apple Inc.", 900000);
        stock2.setStockId(UUID.randomUUID().toString());
        List<StockRecord> expectedStocks = Arrays.asList(stock1, stock2);

        when(stockRepository.findByTickerContaining(ticker)).thenReturn(expectedStocks);

        List<Stock> result = stockService.findStocksByTicker(ticker);

        assertNotNull(result);
        assertEquals(2, result.size());

        Stock stock = result.get(0);
        assertEquals(stock1.getTicker(), stock.getTicker());
        assertEquals(stock2.getDescription(), stock.getDescription());
        assertEquals(stock1.getSharesOutstanding(), stock.getSharesOutstanding());

        verify(stockRepository).findByTickerContaining(ticker);
    }

    @Test
    void testFindStocksByTicker_emptyTicker() {

        String ticker = "";
        StockRecord stock1 = new StockRecord("AAPL.N", "Apple Inc.", 1000000);
        stock1.setStockId(UUID.randomUUID().toString());
        StockRecord stock2 = new StockRecord("AAPL.L", "Apple Inc.", 900000);
        stock2.setStockId(UUID.randomUUID().toString());
        List<StockRecord> expectedStocks = Arrays.asList(stock1, stock2);

        when(stockRepository.findAll()).thenReturn(expectedStocks);

        List<Stock> result = stockService.findStocksByTicker(ticker);

        assertNotNull(result);
        assertEquals(2, result.size());

        Stock stock = result.get(0);
        assertEquals(stock1.getTicker(), stock.getTicker());
        assertEquals(stock2.getDescription(), stock.getDescription());
        assertEquals(stock1.getSharesOutstanding(), stock.getSharesOutstanding());

        verify(stockRepository).findAll();
    }

    @Test
    void testFindPricesByTickerAndDates() {

        String ticker = "AAPL";
        String stockId = UUID.randomUUID().toString();
        LocalDate fromDate = LocalDate.of(2023, 1, 1); // Example from date
        LocalDate toDate = LocalDate.of(2023, 1, 31); // Example to date
        StockRecord stock = new StockRecord();
        stock.setStockId(stockId);
        stock.setTicker(ticker);

        PriceRecord price = new PriceRecord();
        price.setStock(stock);
        price.setPricingDate(fromDate);
        price.setPriceValue(BigDecimal.valueOf(123));
        price.setPriceId(UUID.randomUUID().toString());

        when(stockRepository.findByTicker(ticker)).thenReturn(Optional.of(stock));
        when(priceRepository.findByStockIdAndPricingDateBetween(stockId, fromDate, toDate)).thenReturn(List.of(price));

        FindPricesResponse result = stockService.findPricesByTickerAndDates(ticker, fromDate, toDate);

        assertNotNull(result);
        assertEquals(1, result.getPrices().size());

        verify(priceRepository).findByStockIdAndPricingDateBetween(stockId, fromDate, toDate);
    }

    @Test
    void testFindPricesByTickerAndDates_unknownStock() {

        String ticker = "AAPL";
        LocalDate fromDate = LocalDate.of(2023, 1, 1); // Example from date
        LocalDate toDate = LocalDate.of(2023, 1, 31); // Example to date

        when(stockRepository.findByTicker(ticker)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
            stockService.findPricesByTickerAndDates(ticker, fromDate, toDate));

        verify(stockRepository).findByTicker(ticker);
    }
}
