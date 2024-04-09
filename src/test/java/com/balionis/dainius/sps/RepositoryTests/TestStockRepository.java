package com.balionis.dainius.sps.RepositoryTests;

import com.balionis.dainius.sps.model.Stock;
import com.balionis.dainius.sps.repository.StockRepository;
import com.balionis.dainius.sps.service.StockService;
import com.balionis.dainius.sps.service.StockServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class TestStockRepository {

    @Mock
    private StockRepository mockStockRepository;

    @InjectMocks
    private StockServiceImpl stockService;

    @Test
    void testFindStocksByTicker() {
        // Define test data
        String ticker = "AAPL";
        Stock stock = new Stock();
        stock.setTicker("AAPL");

        // Mock the behavior of the repository's findByTickerContaining method
        when(mockStockRepository.findByTickerContaining(ticker)).thenReturn(Collections.singletonList(stock));

        // Call the method being tested
        List<Stock> result = stockService.findStocksByTicker(ticker);

        // Assert the result
        assertEquals(Collections.singletonList(stock), result);
    }
}






