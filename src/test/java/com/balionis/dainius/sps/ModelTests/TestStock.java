package com.balionis.dainius.sps.ModelTests;

import com.balionis.dainius.sps.model.Price;
import com.balionis.dainius.sps.model.Stock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestStock {

    private Price price;
    private List<Price> prices;

    @BeforeEach
    void setUp() {
        price = new Price();
        prices = new ArrayList<>();
    }



    @Test
    void testPrePersistAndPreUpdate() {

        Stock stock = new Stock();


        LocalDateTime createdAtBeforeSave = stock.getCreatedAt();
        LocalDateTime updatedAtBeforeSave = stock.getUpdatedAt();

        testSaveStockToDatabase(stock);

        LocalDateTime createdAtAfterSave = stock.getCreatedAt();
        LocalDateTime updatedAtAfterSave = stock.getUpdatedAt();


        assertNotNull(createdAtAfterSave);

        assertNotNull(updatedAtAfterSave);

        assertEquals(createdAtAfterSave, updatedAtAfterSave);

        stock.setDescription("Updated description");

        LocalDateTime createdAtAfterUpdate = stock.getCreatedAt();
        LocalDateTime updatedAtAfterUpdate = stock.getUpdatedAt();

        assertNotEquals(updatedAtBeforeSave, updatedAtAfterUpdate);
        assertEquals(createdAtAfterSave, createdAtAfterUpdate);
    }


    private void testSaveStockToDatabase(Stock stock) {

        LocalDateTime now = LocalDateTime.now();
        stock.setCreatedAt(now);
        stock.setUpdatedAt(now);
    }

    @Test
    void testSetStockId() {

        Stock stock = new Stock();
        Long id = 1234L;

        stock.setStockId(id);
        assertEquals(id, stock.getStockId());
    }



    @Test
    void testAddPrice() {

        Stock stock = new Stock();
        Price newPrice = new Price();

        stock.addPrice(newPrice);

        assertEquals(stock, newPrice.getStock());
    }

    @Test
    void testRemovePrice() {

        Stock stock = new Stock();
        Price newPrice = new Price();
        stock.addPrice(newPrice);

        stock.removePrice(newPrice);

        assertNull(newPrice.getStock());
    }


}

