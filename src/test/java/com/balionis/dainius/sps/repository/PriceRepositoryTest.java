package com.balionis.dainius.sps.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.balionis.dainius.sps.model.PriceRecord;
import com.balionis.dainius.sps.model.StockRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@DataJpaTest
@ActiveProfiles("test")
public class PriceRepositoryTest {

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private PriceRepository priceRepository;

    @Test
    void testFindByStockIdAndPricingDateBetween() {

        LocalDate today = LocalDate.now();
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        LocalDateTime specificTime = LocalDateTime.of(2024, 4, 16, 10, 0, 0);

        StockRecord stock = new StockRecord();
        stock.setTicker("AAPL");
        StockRecord savedStock = stockRepository.save(stock);

        PriceRecord priceToday = new PriceRecord();
        priceToday.setStock(savedStock);
        priceToday.setPricingDate(today);
        priceToday.setPriceValue(BigDecimal.valueOf(123.45));
        priceToday.setCreatedAt(specificTime);
        priceToday.setUpdatedAt(specificTime);
        priceRepository.save(priceToday);

        PriceRecord priceTomorrow = new PriceRecord();
        priceTomorrow.setStock(savedStock);
        priceTomorrow.setPricingDate(tomorrow);
        priceTomorrow.setPriceValue(BigDecimal.valueOf(112.12));
        priceTomorrow.setCreatedAt(specificTime);
        priceTomorrow.setUpdatedAt(specificTime);
        priceRepository.save(priceTomorrow);

        List<PriceRecord> resultBothDates = priceRepository.findByTickerAndPricingDateBetween(savedStock.getTicker(), today, tomorrow);

        assertEquals(List.of(priceToday, priceTomorrow), resultBothDates);

        List<PriceRecord> resultToday = priceRepository.findByTickerAndPricingDateBetween(savedStock.getTicker(), today, today);

        assertEquals(List.of(priceToday), resultToday);

        PriceRecord resultPriceToday = resultToday.get(0);
        assertEquals(today, resultPriceToday.getPricingDate());
        assertThat(resultPriceToday.getPriceValue()).isCloseTo(BigDecimal.valueOf(123.45), within(BigDecimal.valueOf(0.01)));

        assertEquals(specificTime, resultPriceToday.getCreatedAt());
        assertEquals(specificTime, resultPriceToday.getUpdatedAt());
    }

    @Test
    void testCreateAtOnSave() {

        LocalDateTime now = LocalDateTime.now();

        StockRecord stock = new StockRecord();
        stock.setTicker("AAPL");
        StockRecord savedStock = stockRepository.save(stock);

        PriceRecord price = new PriceRecord();
        price.setStock(savedStock);
        price.setPricingDate(LocalDate.now());
        price.setPriceValue(BigDecimal.valueOf(123.45));

        PriceRecord savedPrice = priceRepository.save(price);

        assertThat(savedPrice.getCreatedAt()).isCloseTo(now, within(10, ChronoUnit.SECONDS));
        assertThat(savedPrice.getUpdatedAt()).isCloseTo(now, within(10, ChronoUnit.SECONDS));

        LocalDateTime savedPriceUpdatedAt = savedPrice.getUpdatedAt();

        savedPrice.setPriceValue(BigDecimal.valueOf(121.12));
        // IMPORTANT! Price.onUpdate is not getting called when calling just "save". Hence, setting it explicitly.
        savedPrice.setUpdatedAt(LocalDateTime.now().plusNanos(100));
        PriceRecord updatedPrice = priceRepository.saveAndFlush(savedPrice);

        assertThat(updatedPrice.getPriceValue()).isCloseTo(BigDecimal.valueOf(121.12), within(BigDecimal.valueOf(0.01)));
        assertThat(updatedPrice.getUpdatedAt()).isAfter(savedPriceUpdatedAt);
    }

    @Test
    void testToString() {

        PriceRecord price = new PriceRecord();
        price.setPricingDate(LocalDate.now());
        price.setPriceValue(BigDecimal.valueOf(123.45));

        assertThat(price.toString()).isNotEmpty();

        StockRecord stock = new StockRecord();
        stock.setTicker("AAPL");
        price.setStock(stock);

        assertThat(price.toString()).isNotEmpty();
    }
}

