package com.balionis.dainius.sps.controller;

import com.balionis.dainius.sps.model.PriceRecord;
import com.balionis.dainius.sps.model.StockRecord;
import com.balionis.dainius.sps.repository.StockRepository;
import com.balionis.dainius.sps.service.PriceRequest;
import com.balionis.dainius.sps.service.StockService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StockControllerTest {

    @Mock
    private StockService stockService;

    @InjectMocks
    private StockController stockController;

    @Test
    public void testGetStocks() {
        // Mock data
        List<StockRecord> mockStocks = Collections.singletonList(new StockRecord("IBM.N", "International Business Machines", 98000000));

        when(stockService.findStocksByTicker("IBM.N")).thenReturn(mockStocks);

        ResponseEntity<?> response = stockController.getStocks("IBM.N");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockStocks, response.getBody());

        when(stockService.findStocksByTicker("GOOGL")).thenReturn(Collections.emptyList());

        response = stockController.getStocks("GOOGL");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("No stocks found", response.getBody());

        response = stockController.getStocks("");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("No stocks found", response.getBody());

        response = stockController.getStocks("*");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("No stocks found", response.getBody());
    }

    @Test
    public void testAddStockWhenStockDoesNotExist() {
        // Mock data for a new stock
        StockRecord newStock = new StockRecord("IBM.N", "International Business Machines", 98000000);

        when(stockService.findStocksByTicker("IBM.N")).thenReturn(Collections.emptyList());

        when(stockService.addOrUpdateStock(newStock)).thenReturn(newStock);

        ResponseEntity<?> response = stockController.addStock(newStock);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(newStock, response.getBody());
    }

    @Test
    public void testAddStockWhenStockAlreadyExists() {

        StockRecord existingStock = new StockRecord("IBM.N", "International Business Machines", 98000000);

        when(stockService.findStocksByTicker("IBM.N")).thenReturn(Collections.singletonList(existingStock));

        ResponseEntity<?> response = stockController.addStock(existingStock);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("A stock with ticker 'IBM.N' already exists.", response.getBody());
    }

    @Test
    public void testAddPrice_Success() {

        StockService stockService = mock(StockService.class);
        StockController controller = new StockController(stockService);

        PriceRequest request = new PriceRequest();
        request.setPrice(BigDecimal.valueOf(19.99));
        request.setDate("2024-03-10");

        StockRecord stock = new StockRecord();
        stock.setTicker("IBM.N");

        PriceRecord price = new PriceRecord();
        price.setStock(stock);
        price.setPriceValue(request.getPrice());
        price.setPricingDate(request.getDate());

        when(stockService.addOrUpdatePrice("IBM.N", BigDecimal.valueOf(19.99), LocalDate.of(2024, 3, 10))).thenReturn(price);

        ResponseEntity<?> response = controller.addPrice("IBM.N", request);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void testAddPrice_Failure() {

        StockService stockService = mock(StockService.class);
        StockController controller = new StockController(stockService);

        PriceRequest request = new PriceRequest();
        request.setPrice(BigDecimal.valueOf(19.99));
        request.setDate("2024-03-10");

        doThrow(new RuntimeException("Failed to add price")).when(stockService).addOrUpdatePrice(eq("IBM.N"), any(BigDecimal.class), any(LocalDate.class));

        ResponseEntity<?> response = controller.addPrice("IBM.N", request);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Failed to add price"));
    }

    @Test
    public void testGetPrices() {

        StockService stockService = mock(StockService.class);
        StockController controller = new StockController(stockService);

        List<PriceRecord> priceHistory = new ArrayList<>();
        priceHistory.add(new PriceRecord(BigDecimal.valueOf(19.99), LocalDate.of(2024, 3, 10), LocalDateTime.now(), LocalDateTime.now()));

        when(stockService.findStocksByTicker("IBM.N")).thenReturn(Collections.singletonList(new StockRecord()));

        when(stockService.findPricesByTickerAndDates(eq("IBM.N"), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(priceHistory);

        ResponseEntity<?> response = controller.getPrices("IBM.N", LocalDate.of(2024, 3, 1), LocalDate.of(2024, 3, 15));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(priceHistory, response.getBody());
    }
}
