package com.balionis.dainius.sps.controller;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import com.balionis.dainius.sps.DainiusStocksPricingServiceApplication;
import com.balionis.dainius.sps.generated.model.AddPriceResponse;
import com.balionis.dainius.sps.generated.model.FindPricesResponse;
import com.balionis.dainius.sps.generated.model.Price;
import com.balionis.dainius.sps.generated.model.Stock;
import com.balionis.dainius.sps.service.StockService;
import io.restassured.builder.RequestSpecBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@ActiveProfiles("test")
@SpringBootTest(classes = {DainiusStocksPricingServiceApplication.class}, webEnvironment = RANDOM_PORT)
public class StockControllerTest {

    @LocalServerPort
    private int randomServerPort;

    @MockBean
    private StockService stockService;

    private RequestSpecBuilder requestSpecBuilder;

    @BeforeEach
    void setUp() {
        requestSpecBuilder = new RequestSpecBuilder().setPort(randomServerPort);
    }

    @Test
    public void testGetStocks() {
        List<Stock> mockStocks = Collections.singletonList(
                new Stock().ticker("IBM.N")
                        .description("International Business Machines")
                        .sharesOutstanding(98000000));

        when(stockService.findStocksByTicker(eq("IBM.N"))).thenReturn(mockStocks);

        var stocks = given().spec(requestSpecBuilder.build())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .log().all()
                .params("ticker", "IBM.N")
                .get("/api/v1/stocks")
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .jsonPath().getList(".", Stock.class);

        assertThat(stocks).hasSize(1);

        var stock = stocks.get(0);
        assertEquals("IBM.N", stock.getTicker());
        assertEquals("International Business Machines", stock.getDescription());
        assertEquals(98000000, stock.getSharesOutstanding());

        verify(stockService).findStocksByTicker(anyString());
    }

    @Test
    public void testAddStock() {
        Stock newStock = new Stock()
                .stockId(UUID.randomUUID())
                .ticker("IBM.N")
                .description("International Business Machines")
                .sharesOutstanding(98000000);

        when(stockService.addOrUpdateStock(newStock)).thenReturn(newStock);

        var savedStock = given().spec(requestSpecBuilder.build())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .log().all()
                .body(newStock)
                .post("/api/v1/stocks")
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .as(Stock.class);

        assertEquals("IBM.N", savedStock.getTicker());
        assertEquals("International Business Machines", savedStock.getDescription());
        assertEquals(98000000, savedStock.getSharesOutstanding());

        verify(stockService).addOrUpdateStock(any(Stock.class));
    }

    @Test
    public void testAddPrice() {

        UUID stockId = UUID.randomUUID();
        Price price = new Price().priceId(UUID.randomUUID())
                .pricingDate(LocalDate.of(2024, 3, 10))
                .priceValue(BigDecimal.valueOf(19.99));
        AddPriceResponse addPriceResponse = new AddPriceResponse().price(price).stockId(stockId);

        when(stockService.addOrUpdatePrice("IBM.N", price)).thenReturn(addPriceResponse);

        var response = given().spec(requestSpecBuilder.build())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .log().all()
                .body(price)
                .pathParam("ticker", "IBM.N")
                .post("/api/v1/stocks/{ticker}/prices")
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(AddPriceResponse.class);

        assertNotNull(response.getPrice());

        Price savedPrice = response.getPrice();

        assertEquals(LocalDate.of(2024, 3, 10), savedPrice.getPricingDate());
        assertEquals(BigDecimal.valueOf(19.99), savedPrice.getPriceValue());

        verify(stockService).addOrUpdatePrice(eq("IBM.N"), any(Price.class));
    }

    @Test
    public void testGetPrices() {

        List<Price> priceHistory = new ArrayList<>();
        priceHistory.add(new Price().priceId(UUID.randomUUID()).priceValue(BigDecimal.valueOf(19.99)).pricingDate(LocalDate.of(2024, 3, 10)));

        FindPricesResponse findPricesResponse = new FindPricesResponse().prices(priceHistory).stockId(UUID.randomUUID());

        when(stockService.findPricesByTickerAndDates(eq("IBM.N"), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(findPricesResponse);

        var prices = given().spec(requestSpecBuilder.build())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .log().all()
                .pathParam("ticker", "IBM.N")
                .params("fromDate", "2024-03-09", "toDate", "2024-03-11")
                .get("/api/v1/stocks/{ticker}/prices")
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .as(FindPricesResponse.class);

        assertThat(prices.getPrices()).hasSize(1);

        var price = prices.getPrices().get(0);
        assertEquals(LocalDate.of(2024, 3, 10), price.getPricingDate());
        assertEquals(BigDecimal.valueOf(19.99), price.getPriceValue());

        verify(stockService).findPricesByTickerAndDates(anyString(), any(LocalDate.class), any(LocalDate.class));
    }
}
