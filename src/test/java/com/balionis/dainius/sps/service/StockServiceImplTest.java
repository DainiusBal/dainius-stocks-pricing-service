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
    void testUpdateStock() {
        // Arrange
        String ticker = "AAPL";
        StockRecord updatedStock = new StockRecord(ticker, "Updated Description", 2000000);

        // Mock the behavior of stockRepository.findByTicker() to return an existing stock
        StockRecord existingStock = new StockRecord(ticker, "Existing Stock", 1000000);
        when(stockRepository.findByTicker(ticker)).thenReturn(Optional.of(existingStock));

        // Mock the behavior of stockRepository.save() to return the updated stock
        when(stockRepository.save(existingStock)).thenReturn(updatedStock);

        // Act
        StockRecord result = stockService.updateStock(ticker, updatedStock);

        // Assert
        assertEquals(updatedStock.getDescription(), result.getDescription());
        assertEquals(updatedStock.getSharesOutstanding(), result.getSharesOutstanding());

        // Verify that stockRepository.findByTicker() was called once with the correct argument
        verify(stockRepository, times(1)).findByTicker(ticker);

        // Verify that stockRepository.save() was called once with the existing stock
        verify(stockRepository, times(1)).save(existingStock);
    }

    @Test
    void testAddOrUpdateStock() {
        // Arrange
        StockRecord stock = new StockRecord();
        when(stockRepository.save(stock)).thenReturn(stock);

        // Act
        StockRecord result = stockService.addOrUpdateStock(stock);

        // Assert
        assertEquals(stock, result);
        verify(stockRepository, times(1)).save(stock);
    }

    @Test
    void testUpdateExistingStock() {
        // Arrange
        String ticker = "AAPL";
        StockRecord updatedStock = new StockRecord(ticker, "Updated description", 128000000);
        StockRecord existingStock = new StockRecord(ticker, "Old description", 1000000);

        // Mock the behavior of stockRepository.findByTicker
        when(stockRepository.findByTicker(ticker)).thenReturn(Optional.of(existingStock));
        // Mock the behavior of stockRepository.save
        when(stockRepository.save(existingStock)).thenReturn(updatedStock);

        // Act
        StockRecord result = stockService.updateStock(ticker, updatedStock);

        // Assert
        assertNotNull(result);
        assertEquals(updatedStock.getTicker(), result.getTicker());
        assertEquals(updatedStock.getDescription(), result.getDescription());
        assertEquals(updatedStock.getSharesOutstanding(), result.getSharesOutstanding());
        // Verify that save is called once with the existingStock after updating
        verify(stockRepository, times(1)).save(existingStock);
    }

    @Test
    void testUpdateNonExistingStock() {
        // Arrange
        String ticker = "AAPL";
        StockRecord updatedStock = new StockRecord(ticker, "Updated description", 128000000);

        when(stockRepository.findByTicker(ticker)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> stockService.updateStock(ticker, updatedStock));

        verify(stockRepository, never()).save(updatedStock);
    }

    @Test
    public void testGetAllStocks() {

        StockRecord stock1 = new StockRecord();
        stock1.setTicker("AAPL");
        stock1.setDescription("Apple Inc.");
        stock1.setSharesOutstanding(1000000);

        StockRecord stock2 = new StockRecord();
        stock2.setTicker("GOOGL");
        stock2.setDescription("Alphabet Inc.");
        stock2.setSharesOutstanding(500000);

        when(stockRepository.findAll()).thenReturn(Arrays.asList(stock1, stock2));

        List<StockRecord> result = stockService.getAllStocks();

        assertEquals(2, result.size());
        assertEquals("AAPL", result.get(0).getTicker());
        assertEquals("Apple Inc.", result.get(0).getDescription());
        assertEquals(1000000, result.get(0).getSharesOutstanding());
        assertEquals("GOOGL", result.get(1).getTicker());
        assertEquals("Alphabet Inc.", result.get(1).getDescription());
        assertEquals(500000, result.get(1).getSharesOutstanding());

        verify(stockRepository, times(1)).findAll();
    }

    @Test
    public void testAddOrUpdatePrice_WithValidTicker_ShouldAddPrice() {
        // Arrange
        String ticker = "AAPL";
        StockRecord stock = new StockRecord(ticker, "Apple Inc.", 1000000);
        BigDecimal priceValue = BigDecimal.valueOf(200.50);
        LocalDate date = LocalDate.now();

        when(stockRepository.findByTicker(ticker)).thenReturn(Optional.of(stock));

        // Act
        stockService.addOrUpdatePrice(ticker, priceValue, date);

        verify(priceRepository).save(argThat(price ->
                price.getStock().equals(stock) &&
                        price.getPriceValue().compareTo(priceValue) == 0 &&
                        price.getPricingDate().equals(date)));
    }

    @Test
    public void testAddOrUpdatePrice_WithInvalidTicker_ShouldThrowException() {

        String invalidTicker = "INVALID";
        BigDecimal priceValue = BigDecimal.valueOf(200.50);
        LocalDate date = LocalDate.now();

        assertThrows(RuntimeException.class, () ->
                stockService.addOrUpdatePrice(invalidTicker, priceValue, date));
    }

    @Test
    void testFindStocksByTicker() {

        String ticker = "AAPL";
        StockRecord stock1 = new StockRecord(ticker, "Apple Inc.", 1000000);
        StockRecord stock2 = new StockRecord("GOOGL", "Alphabet Inc.", 500000);
        List<StockRecord> expectedStocks = Arrays.asList(stock1);

        when(stockRepository.findByTickerContaining(ticker)).thenReturn(expectedStocks);

        List<StockRecord> result = stockService.findStocksByTicker(ticker);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(expectedStocks.get(0), result.get(0));
        verify(stockRepository, times(1)).findByTickerContaining(ticker);
    }

    @Test
    void testGetPriceHistory_WhenStockFoundWithNoPrices() {
        // Create and persist a stock with no associated prices
        StockRecord stock = new StockRecord("AAPL", "Apple Inc.", 1000000);
        stockRepository.save(stock);

        List<PriceRecord> result = stockService.getPriceHistory("AAPL", LocalDate.of(2022, 1, 1), LocalDate.of(2022, 12, 31));

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(stockRepository, times(1)).findByTicker("AAPL");
        verify(priceRepository, never()).findByStockIdAndPricingDateBetween(any(String.class), any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    void testGetPriceHistory_WhenStockFound() {

        String ticker = "AAPL";
        LocalDate fromDate = LocalDate.of(2023, 1, 1); // Example from date
        LocalDate toDate = LocalDate.of(2023, 1, 31); // Example to date
        StockRecord stock = new StockRecord();

        when(stockRepository.findByTicker(ticker)).thenReturn(Optional.of(stock));

        List<PriceRecord> result = stockService.getPriceHistory(ticker, fromDate, toDate);

        verify(priceRepository, times(1)).findByStockIdAndPricingDateBetween(stock.getStockId(), fromDate, toDate);
    }

    @Test
    public void testGetPriceHistory_WhenStockNotFound() {

        String ticker = "AAPL";
        LocalDate fromDate = LocalDate.of(2023, 1, 1); // Example from date
        LocalDate toDate = LocalDate.of(2023, 1, 31); // Example to date

        when(stockRepository.findByTicker(ticker)).thenReturn(Optional.empty());

        List<PriceRecord> result = stockService.getPriceHistory(ticker, fromDate, toDate);

        assertEquals(Collections.emptyList(), result);
        verify(priceRepository, never()).findByStockIdAndPricingDateBetween(any(String.class), any(LocalDate.class), any(LocalDate.class));
    }
}
