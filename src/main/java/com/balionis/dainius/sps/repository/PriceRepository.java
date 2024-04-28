package com.balionis.dainius.sps.repository;

import com.balionis.dainius.sps.model.PriceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PriceRepository extends JpaRepository<PriceRecord, String> {
    @Query("select p from PriceRecord p where p.stock.stockId = ?1 AND p.pricingDate >= ?2 AND p.pricingDate <= ?3")
    List<PriceRecord> findByStockIdAndPricingDateBetween(String stockId, LocalDate fromDate, LocalDate toDate);

    @Query("select p from PriceRecord p where p.stock.ticker = ?1 AND p.pricingDate = ?2")
    Optional<PriceRecord> findByTickerAndPricingDate(String ticker, LocalDate pricingDate);
}



