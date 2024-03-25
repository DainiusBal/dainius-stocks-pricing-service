package com.balionis.dainius.sps.controller;

import com.balionis.dainius.sps.model.Stock;
import com.balionis.dainius.sps.service.StockService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@WebMvcTest(StockController.class)
public class StockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StockService stockService;

    @Test
    public void testAddStock() throws Exception {
        when(stockService.findStocksByTicker(anyString())).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/stocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"ticker\": \"AAPL\", \"description\": \"Apple Inc.\", \"sharesOutstanding\": 1000000}"))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    public void testUpdateStock() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/stocks/IBM.N")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\": \"Updated Description\", \"sharesOutstanding\": 2000000}"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testGetStocks() throws Exception {
        // Define test data
        Stock stock1 = new Stock();
        stock1.setTicker("IBM.N");
        stock1.setDescription("International Business Machines");
        stock1.setSharesOutstanding(98000000);

        Stock stock2 = new Stock();
        stock2.setTicker("AAPL");
        stock2.setDescription("Apple Inc.");
        stock2.setSharesOutstanding(200000000);

        // Mock the behavior of the stockService
        Mockito.when(stockService.getAllStocks()).thenReturn(List.of(stock1, stock2));

        // Perform GET request and verify the response
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/stocks"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].ticker").value("IBM.N"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value("International Business Machines"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].sharesOutstanding").value(98000000))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].ticker").value("AAPL"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].description").value("Apple Inc."))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].sharesOutstanding").value(200000000));
    }



    @Test
    public void testAddPrice() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/stocks/IBM.N/prices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"price\": 123.45, \"date\": \"2024-03-21\"}"))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }
}


