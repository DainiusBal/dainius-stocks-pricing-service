package com.balionis.dainius.sps.RepositoryTests;

import com.balionis.dainius.sps.model.Stock;
import com.balionis.dainius.sps.repository.StockRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@ActiveProfiles("test")
@EnableAutoConfiguration
@ExtendWith(SpringExtension.class)
@EntityScan(basePackageClasses = Stock.class)
@EnableJpaRepositories(basePackageClasses = {StockRepository.class})
public class TestStockRepository {

    @Autowired
    private StockRepository stockRepository;

    @Test
    void testFindStocksByTicker() {
        // Define test data
        String ticker = "AAPL";
        Stock stock = new Stock();
        stock.setTicker(ticker);
        stock.setDescription("AAPL");
        stock.setSharesOutstanding(100);

        // Call the method being tested
        stockRepository.save(stock);
        List<Stock> result = stockRepository.findByTickerContaining(ticker);

        // Assert the result
        assertThat(result.size()).isEqualTo(1);

        Stock resultStock = result.get(0);
        assertThat(resultStock.getTicker()).isEqualTo(ticker);
        assertThat(resultStock.getDescription()).isEqualTo(stock.getDescription());
        assertThat(resultStock.getSharesOutstanding()).isEqualTo(stock.getSharesOutstanding());
        assertThat(resultStock.getStockId()).isNotEmpty();
        assertThat(resultStock.getCreatedAt()).isCloseTo(LocalDateTime.now(), within(10, ChronoUnit.SECONDS));
        assertThat(resultStock.getUpdatedAt()).isCloseTo(LocalDateTime.now(), within(10, ChronoUnit.SECONDS));
    }
}






