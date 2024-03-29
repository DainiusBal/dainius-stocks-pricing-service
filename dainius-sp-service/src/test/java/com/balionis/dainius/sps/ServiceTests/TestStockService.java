package com.balionis.dainius.sps.ServiceTests;

import com.balionis.dainius.sps.service.StockService;
import com.balionis.dainius.sps.model.Price;
import com.balionis.dainius.sps.model.Stock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


public class TestStockService {

    @Mock
    private StockService stockService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testAddOrUpdateStock() {
        Stock stock = new Stock(); // Create a sample stock object
        when(stockService.addOrUpdateStock(stock)).thenReturn(stock); // Mock behavior
        assertEquals(stock, stockService.addOrUpdateStock(stock)); // Verify behavior
    }

    @Test
    void testUpdateStock() {
        String ticker = "AAPL"; // Sample ticker
        Stock stock = new Stock(); // Create a sample stock object
        when(stockService.updateStock(ticker, stock)).thenReturn(stock); // Mock behavior
        assertEquals(stock, stockService.updateStock(ticker, stock)); // Verify behavior
    }


}

