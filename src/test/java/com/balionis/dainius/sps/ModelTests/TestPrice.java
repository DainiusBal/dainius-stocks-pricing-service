package com.balionis.dainius.sps.ModelTests;

import com.balionis.dainius.sps.model.Price;
import com.balionis.dainius.sps.model.Stock;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;


public class TestPrice {

    @Test
    void testGettersAndSetters() {

        Price price = new Price();


        Stock stock = new Stock();
        stock.setTicker("AAPL");
        stock.setDescription("Apple Inc.");
        stock.setSharesOutstanding(1000000);

        LocalDate pricingDate = LocalDate.of(2022, 3, 21);
        BigDecimal priceValue = new BigDecimal("123.45");
        LocalDateTime now = LocalDateTime.now();

        price.setStock(stock);
        price.setPricingDate(pricingDate);
        price.setPriceValue(priceValue);
        price.setCreatedAt(now);
        price.setUpdatedAt(now);

        // Check values using getters
        assertEquals(stock, price.getStock());
        assertEquals(pricingDate, price.getPricingDate());
        assertEquals(priceValue, price.getPriceValue());
        assertEquals(now, price.getCreatedAt());
        assertEquals(now, price.getUpdatedAt());
    }

    @Test
    void testPrePersistAndPreUpdate() {

        Price price = new Price();


        LocalDateTime createdAtBeforeSave = price.getCreatedAt();
        LocalDateTime updatedAtBeforeSave = price.getUpdatedAt();

        savePriceToDatabase(price);


        LocalDateTime createdAtAfterSave = price.getCreatedAt();
        LocalDateTime updatedAtAfterSave = price.getUpdatedAt();

        assertNotNull(createdAtAfterSave);
        assertNotNull(updatedAtAfterSave);
        assertEquals(createdAtAfterSave, updatedAtAfterSave);

        price.setPriceValue(new BigDecimal("200.00"));


        LocalDateTime createdAtAfterUpdate = price.getCreatedAt();
        LocalDateTime updatedAtAfterUpdate = price.getUpdatedAt();


        assertNotEquals(updatedAtBeforeSave, updatedAtAfterUpdate);

        assertEquals(createdAtAfterSave, createdAtAfterUpdate);
    }

    private void savePriceToDatabase(Price price) {

        LocalDateTime now = LocalDateTime.now();
        price.setCreatedAt(now);
        price.setUpdatedAt(now);
    }

    @Test
    void testGetPriceId(){
        Price price = new Price();
        Long id = 1478L;

        price.setPriceId(id);
        assertEquals(id, price.getPriceId());

    }


}

