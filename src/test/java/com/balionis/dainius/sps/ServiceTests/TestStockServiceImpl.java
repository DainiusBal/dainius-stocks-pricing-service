package com.balionis.dainius.sps.ServiceTests;


import com.balionis.dainius.sps.controller.StockController;
import com.balionis.dainius.sps.model.Price;
import com.balionis.dainius.sps.model.Stock;
import com.balionis.dainius.sps.repository.PriceRepository;
import com.balionis.dainius.sps.repository.StockRepository;
import com.balionis.dainius.sps.service.StockServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;


public class TestStockServiceImpl {

    @Mock
    private StockRepository stockRepository;

    @Mock
    private StockController stockController;

    @Mock
    private PriceRepository priceRepository;

    @InjectMocks
    private StockServiceImpl stockService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }



    @Test
    void testUpdateStock() {
        // Arrange
        String ticker = "AAPL";
        Stock updatedStock = new Stock(ticker, "Updated Description", 2000000);

        // Mock the behavior of stockRepository.findByTicker() to return an existing stock
        Stock existingStock = new Stock(ticker, "Existing Stock", 1000000);
        when(stockRepository.findByTicker(ticker)).thenReturn(existingStock);

        // Mock the behavior of stockRepository.save() to return the updated stock
        when(stockRepository.save(existingStock)).thenReturn(updatedStock);

        // Act
        Stock result = stockService.updateStock(ticker, updatedStock);

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
        Stock stock = new Stock();
        when(stockRepository.save(stock)).thenReturn(stock);

        // Act
        Stock result = stockService.addOrUpdateStock(stock);

        // Assert
        assertEquals(stock, result);
        verify(stockRepository, times(1)).save(stock);
    }


    @Test
    void testUpdateExistingStock() {
        // Arrange
        String ticker = "AAPL";
        Stock updatedStock = new Stock(ticker, "Updated description", 128000000);
        Stock existingStock = new Stock(ticker, "Old description", 1000000);

        // Mock the behavior of stockRepository.findByTicker
        when(stockRepository.findByTicker(ticker)).thenReturn(existingStock);
        // Mock the behavior of stockRepository.save
        when(stockRepository.save(existingStock)).thenReturn(updatedStock);

        // Act
        Stock result = stockService.updateStock(ticker, updatedStock);

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
        Stock updatedStock = new Stock(ticker, "Updated description", 128000000);

        when(stockRepository.findByTicker(ticker)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> stockService.updateStock(ticker, updatedStock));

        verify(stockRepository, never()).save(updatedStock);
    }


    @Test
    public void testGetAllStocks() {

        Stock stock1 = new Stock();
        stock1.setTicker("AAPL");
        stock1.setDescription("Apple Inc.");
        stock1.setSharesOutstanding(1000000);

        Stock stock2 = new Stock();
        stock2.setTicker("GOOGL");
        stock2.setDescription("Alphabet Inc.");
        stock2.setSharesOutstanding(500000);

        when(stockRepository.findAll()).thenReturn(Arrays.asList(stock1, stock2));

        List<Stock> result = stockService.getAllStocks();

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
        Stock stock = new Stock(ticker, "Apple Inc.", 1000000);
        BigDecimal priceValue = BigDecimal.valueOf(200.50);
        LocalDate date = LocalDate.now();

        when(stockRepository.findByTicker(ticker)).thenReturn(stock);

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

        when(stockRepository.findByTicker(invalidTicker)).thenReturn(null);

        assertThrows(RuntimeException.class, () ->
                stockService.addOrUpdatePrice(invalidTicker, priceValue, date));
    }


    @Test
    void testFindStocksByTicker() {

        String ticker = "AAPL";
        Stock stock1 = new Stock(ticker, "Apple Inc.", 1000000);
        Stock stock2 = new Stock("GOOGL", "Alphabet Inc.", 500000);
        List<Stock> expectedStocks = Arrays.asList(stock1);

        when(stockRepository.findByTickerContaining(ticker)).thenReturn(expectedStocks);

        List<Stock> result = stockService.findStocksByTicker(ticker);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(expectedStocks.get(0), result.get(0));
        verify(stockRepository, times(1)).findByTickerContaining(ticker);
    }


    @Test
    void testGetPriceHistoryWhenStockFoundWithNoPrices() {
        // Create and persist a stock with no associated prices
        Stock stock = new Stock("AAPL", "Apple Inc.", 1000000);
        stockRepository.save(stock);

        List<Price> result = stockService.getPriceHistory("AAPL", LocalDate.of(2022, 1, 1), LocalDate.of(2022, 12, 31));

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(stockRepository, times(1)).findByTicker("AAPL");
        verify(priceRepository, never()).findByStockIdAndPricingDateBetween(anyLong(), any(LocalDate.class), any(LocalDate.class));
    }


    @Test
    public void testGetPriceHistory_WhenStockFound() {

        String ticker = "AAPL";
        LocalDate fromDate = LocalDate.of(2023, 1, 1); // Example from date
        LocalDate toDate = LocalDate.of(2023, 1, 31); // Example to date
        Stock stock = new Stock();

        when(stockRepository.findByTicker(ticker)).thenReturn(stock);

        List<Price> result = stockService.getPriceHistory(ticker, fromDate, toDate);

        verify(priceRepository, times(1)).findByStockIdAndPricingDateBetween(stock.getStockId(), fromDate, toDate);
    }


    @Test
    public void testGetPriceHistory_WhenStockNotFound() {

        String ticker = "AAPL";
        LocalDate fromDate = LocalDate.of(2023, 1, 1); // Example from date
        LocalDate toDate = LocalDate.of(2023, 1, 31); // Example to date

        when(stockRepository.findByTicker(ticker)).thenReturn(null);

        List<Price> result = stockService.getPriceHistory(ticker, fromDate, toDate);

        assertEquals(Collections.emptyList(), result);
        verify(priceRepository, never()).findByStockIdAndPricingDateBetween(anyLong(), any(LocalDate.class), any(LocalDate.class));
    }


}





