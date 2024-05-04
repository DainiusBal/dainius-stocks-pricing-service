package com.balionis.dainius.sps.repository;

import com.balionis.dainius.sps.model.StockRecord;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@DataJpaTest
@ActiveProfiles("test")
public class StockRepositoryTest {

    @Autowired
    private StockRepository stockRepository;

    @Test
    void testFindStocksByTicker() {

        String ticker = "AAPL";
        LocalDateTime specificTime = LocalDateTime.of(2024, 4, 16, 10, 0, 0);

        log.info("specificTime={}", specificTime);

        StockRecord stock = new StockRecord();
        stock.setTicker(ticker);
        stock.setDescription("Apple");
        stock.setSharesOutstanding(100);
        stock.setCreatedAt(specificTime);
        stock.setUpdatedAt(specificTime);
        stockRepository.save(stock);

        List<StockRecord> result = stockRepository.findByTickerContaining(ticker);

        assertEquals(1, result.size());
        assertEquals("AAPL", result.get(0).getTicker());
        assertEquals("Apple", result.get(0).getDescription());
        assertEquals(100, result.get(0).getSharesOutstanding());

        LocalDateTime actualCreatedAt = result.get(0).getCreatedAt();
        LocalDateTime actualUpdatedAt = result.get(0).getUpdatedAt();

        assertEquals(specificTime, actualCreatedAt);
        assertEquals(specificTime, actualUpdatedAt);
    }
}
