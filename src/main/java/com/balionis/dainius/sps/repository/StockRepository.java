package com.balionis.dainius.sps.repository;

import com.balionis.dainius.sps.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
    List<Stock> findByTickerContaining(String ticker);
    Stock findByTicker(String ticker);
}

