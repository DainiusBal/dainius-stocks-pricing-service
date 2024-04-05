package com.balionis.dainius.sps.RepositoryTests;

import com.balionis.dainius.sps.repository.PriceRepository;
import com.balionis.dainius.sps.model.Price;
import com.balionis.dainius.sps.model.Stock;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@DataJpaTest
public class TestPriceRepository {

    @Autowired
    private PriceRepository priceRepository;

    @MockBean
    private PriceRepository mockPriceRepository;

    @Test
    void testFindByStockIdAndPricingDateBetween() {

        Stock stock = new Stock();
        stock.setTicker("AAPL");
        stock.setDescription("Apple Inc.");
        stock.setSharesOutstanding(1000000);


        Price price = new Price();
        price.setStock(stock);
        price.setPricingDate(LocalDate.now());
        price.setPriceValue(BigDecimal.valueOf(123.45));

        when(mockPriceRepository.findByStockIdAndPricingDateBetween(stock.getStockId(), LocalDate.now(), LocalDate.now()))
                .thenReturn(List.of(price));

        List<Price> result = priceRepository.findByStockIdAndPricingDateBetween(stock.getStockId(), LocalDate.now(), LocalDate.now());

        assertEquals(List.of(price), result);
    }
}

