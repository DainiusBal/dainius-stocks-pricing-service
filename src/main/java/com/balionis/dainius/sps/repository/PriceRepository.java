package com.balionis.dainius.sps.repository;

import com.balionis.dainius.sps.model.Price;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface PriceRepository extends JpaRepository<Price, String> {
    @Query("select p from Price p where p.stock.stockId = ?1 AND p.pricingDate >= ?2 AND p.pricingDate <= ?3")
    List<Price> findByStockIdAndPricingDateBetween(String stockId, LocalDate fromDate, LocalDate toDate);
}



