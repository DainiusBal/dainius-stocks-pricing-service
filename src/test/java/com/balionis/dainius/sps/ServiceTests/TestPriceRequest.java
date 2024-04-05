package com.balionis.dainius.sps.ServiceTests;

import com.balionis.dainius.sps.service.PriceRequest;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class TestPriceRequest {

    @Test
    void testGettersAndSetters() {

        PriceRequest priceRequest = new PriceRequest();


        BigDecimal expectedPrice = BigDecimal.valueOf(123.45);
        String expectedDate = "2024-03-21";
        priceRequest.setPrice(expectedPrice);
        priceRequest.setDate(expectedDate);


        assertEquals(expectedPrice, priceRequest.getPrice());
        assertEquals(LocalDate.parse(expectedDate), priceRequest.getDate());
    }
}

