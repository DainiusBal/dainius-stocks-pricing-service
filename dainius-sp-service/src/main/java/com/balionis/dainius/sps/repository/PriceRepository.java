package com.balionis.dainius.sps.repository;

import com.balionis.dainius.sps.model.Price;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PriceRepository extends JpaRepository<Price, Long> {
    List<Price> findByStockIdAndPricingDateBetween(Long stockId, LocalDate fromDate, LocalDate toDate);
}



