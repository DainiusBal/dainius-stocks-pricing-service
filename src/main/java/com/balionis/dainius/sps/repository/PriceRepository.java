package com.balionis.dainius.sps.repository;

import com.balionis.dainius.sps.model.Price;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface PriceRepository extends JpaRepository<Price, Long> {
    List<Price> findByStockIdAndPricingDateBetween(UUID stockId, LocalDate fromDate, LocalDate toDate);
}



