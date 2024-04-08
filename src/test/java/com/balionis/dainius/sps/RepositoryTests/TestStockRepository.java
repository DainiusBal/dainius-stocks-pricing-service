package com.balionis.dainius.sps.RepositoryTests;

import com.balionis.dainius.sps.model.Stock;
import com.balionis.dainius.sps.repository.StockRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@DataJpaTest
public class TestStockRepository {

    @Autowired
    private StockRepository stockRepository;

    @MockBean
    private StockRepository mockStockRepository;

    @Test
    void testFindByTicker() {
        Stock stock = new Stock();
        stock.setTicker("AAPL");
        stock.setDescription("Apple Inc.");
        stock.setSharesOutstanding(1000000);

        // Generate a random UUID
        UUID stockUuid = UUID.randomUUID();
        stock.setStockId(stockUuid);

        // Mock the findById method to return the Stock object
        when(mockStockRepository.findById(stockUuid)).thenReturn(Optional.of(stock));

        // Call the method being tested
        Optional<Stock> result = stockRepository.findById(stockUuid);

        // Assert the result
        assertEquals(Optional.of(stock), result);
    }
}




