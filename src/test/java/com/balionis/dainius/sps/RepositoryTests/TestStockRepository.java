package com.balionis.dainius.sps.RepositoryTests;

import com.balionis.dainius.sps.model.Stock;
import com.balionis.dainius.sps.repository.StockRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

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


        when(mockStockRepository.findById(stock.getStockId())).thenReturn(Optional.of(stock));


        Optional<Stock> result = stockRepository.findById(stock.getStockId());


        assertEquals(Optional.of(stock), result);
    }
}



