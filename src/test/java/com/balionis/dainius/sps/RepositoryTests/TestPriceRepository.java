package com.balionis.dainius.sps.RepositoryTests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.balionis.dainius.sps.repository.PriceRepository;
import com.balionis.dainius.sps.model.Price;
import com.balionis.dainius.sps.model.Stock;
import com.balionis.dainius.sps.repository.StockRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@DataJpaTest
public class TestPriceRepository {

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private PriceRepository priceRepository;

    @Test
    void testFindByStockIdAndPricingDateBetween() {

        LocalDate today = LocalDate.now();
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        LocalDateTime specificTime = LocalDateTime.of(2024, 4, 16, 10, 0, 0);

        Stock stock = new Stock();
        stock.setTicker("AAPL");
        Stock savedStock = stockRepository.save(stock);

        Price priceToday = new Price();
        priceToday.setStock(savedStock);
        priceToday.setPricingDate(today);
        priceToday.setPriceValue(BigDecimal.valueOf(123.45));
        priceToday.setCreatedAt(specificTime);
        priceToday.setUpdatedAt(specificTime);
        priceRepository.save(priceToday);

        Price priceTomorrow = new Price();
        priceTomorrow.setStock(savedStock);
        priceTomorrow.setPricingDate(tomorrow);
        priceTomorrow.setPriceValue(BigDecimal.valueOf(112.12));
        priceTomorrow.setCreatedAt(specificTime);
        priceTomorrow.setUpdatedAt(specificTime);
        priceRepository.save(priceTomorrow);

        List<Price> resultBothDates = priceRepository.findByStockIdAndPricingDateBetween(savedStock.getStockId(), today, tomorrow);

        assertEquals(List.of(priceToday, priceTomorrow), resultBothDates);

        List<Price> resultToday = priceRepository.findByStockIdAndPricingDateBetween(savedStock.getStockId(), today, today);

        assertEquals(List.of(priceToday), resultToday);

        Price resultPriceToday = resultToday.get(0);
        assertEquals(today, resultPriceToday.getPricingDate());
        assertThat(resultPriceToday.getPriceValue()).isCloseTo(BigDecimal.valueOf(123.45), within(BigDecimal.valueOf(0.01)));

        assertEquals(specificTime, resultPriceToday.getCreatedAt());
        assertEquals(specificTime, resultPriceToday.getUpdatedAt());
    }

    @Test
    void testCreateAtOnSave() {

        LocalDateTime now = LocalDateTime.now();

        Stock stock = new Stock();
        stock.setTicker("AAPL");
        Stock savedStock = stockRepository.save(stock);

        Price price = new Price();
        price.setStock(savedStock);
        price.setPricingDate(LocalDate.now());
        price.setPriceValue(BigDecimal.valueOf(123.45));

        Price savedPrice = priceRepository.save(price);

        assertThat(savedPrice.getCreatedAt()).isCloseTo(now, within(10, ChronoUnit.SECONDS));
        assertThat(savedPrice.getUpdatedAt()).isCloseTo(now, within(10, ChronoUnit.SECONDS));

        LocalDateTime savedPriceUpdatedAt = savedPrice.getUpdatedAt();

        savedPrice.setPriceValue(BigDecimal.valueOf(121.12));
        // IMPORTANT! Price.onUpdate is not getting called when calling just "save". Hence, setting it explicitly.
        savedPrice.setUpdatedAt(LocalDateTime.now().plusNanos(100));
        Price updatedPrice = priceRepository.saveAndFlush(savedPrice);

        assertThat(updatedPrice.getPriceValue()).isCloseTo(BigDecimal.valueOf(121.12), within(BigDecimal.valueOf(0.01)));
        assertThat(updatedPrice.getUpdatedAt()).isAfter(savedPriceUpdatedAt);
    }

    @Test
    void testToString() {

        Price price = new Price();
        price.setPricingDate(LocalDate.now());
        price.setPriceValue(BigDecimal.valueOf(123.45));

        assertThat(price.toString()).isNotEmpty();

        Stock stock = new Stock();
        stock.setTicker("AAPL");
        price.setStock(stock);

        assertThat(price.toString()).isNotEmpty();
    }
}

