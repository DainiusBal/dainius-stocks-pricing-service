package com.balionis.dainius.sps.controller;

import com.balionis.dainius.sps.generated.model.AddPriceResponse;
import com.balionis.dainius.sps.generated.model.FindPricesResponse;
import com.balionis.dainius.sps.generated.model.Price;
import com.balionis.dainius.sps.generated.model.Stock;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

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
        List<Stock> mockStocks = Collections.singletonList(
                new Stock().ticker("IBM.N")
                        .description("International Business Machines")
                        .sharesOutstanding(98000000));

        when(stockService.findStocksByTicker("IBM.N")).thenReturn(mockStocks);

        ResponseEntity<?> response = stockController.getStocks("IBM.N");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockStocks, response.getBody());

        when(stockService.findStocksByTicker("GOOGL")).thenReturn(Collections.emptyList());

        response = stockController.getStocks("GOOGL");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(), response.getBody());
    }

    @Test
    public void testAddStock_whenStockDoesNotExist() {
        // Mock data for a new stock
        Stock newStock = new Stock().ticker("IBM.N").description("International Business Machines").sharesOutstanding(98000000);

        when(stockService.findStocksByTicker("IBM.N")).thenReturn(Collections.emptyList());

        when(stockService.addOrUpdateStock(newStock)).thenReturn(newStock);

        ResponseEntity<?> response = stockController.addStock(newStock);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(newStock, response.getBody());
    }

    @Test
    public void testAddStock_whenStockAlreadyExists() {

        Stock existingStock = new Stock().ticker("IBM.N").description("International Business Machines").sharesOutstanding(98000000);

        when(stockService.findStocksByTicker("IBM.N")).thenReturn(Collections.singletonList(existingStock));

        ResponseEntity<?> response = stockController.addStock(existingStock);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("A stock with ticker 'IBM.N' already exists.", response.getBody());
    }

    @Test
    public void testAddPrice_Success() {

        UUID stockId = UUID.randomUUID();
        Price price = new Price().pricingDate(LocalDate.of(2024, 3, 10)).priceValue(BigDecimal.valueOf(19.99));
        AddPriceResponse addPriceResponse = new AddPriceResponse().price(price).stockId(stockId);

        when(stockService.addOrUpdatePrice("IBM.N", price)).thenReturn(addPriceResponse);

        ResponseEntity<?> response = stockController.addPrice("IBM.N", price);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testAddPrice_UnknownTicker() {

        UUID stockId = UUID.randomUUID();
        Price price = new Price().pricingDate(LocalDate.of(2024, 3, 10)).priceValue(BigDecimal.valueOf(19.99));
        AddPriceResponse addPriceResponse = new AddPriceResponse().price(price).stockId(stockId);

        when(stockService.addOrUpdatePrice("IBM.N", price)).thenReturn(addPriceResponse);

        ResponseEntity<?> response = stockController.addPrice("IBM.N", price);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testGetPrices() {

        List<Price> priceHistory = new ArrayList<>();
        priceHistory.add(new Price().priceId(UUID.randomUUID()).priceValue(BigDecimal.valueOf(19.99)).pricingDate(LocalDate.of(2024, 3, 10)));

        FindPricesResponse findPricesResponse = new FindPricesResponse().prices(priceHistory).stockId(UUID.randomUUID());

        when(stockService.findPricesByTickerAndDates(eq("IBM.N"), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(findPricesResponse);

        ResponseEntity<?> response = stockController.getPrices("IBM.N", LocalDate.of(2024, 3, 1), LocalDate.of(2024, 3, 15));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(findPricesResponse, response.getBody());
    }
}
