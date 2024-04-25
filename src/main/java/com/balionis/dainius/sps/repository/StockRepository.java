package com.balionis.dainius.sps.repository;

import com.balionis.dainius.sps.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StockRepository extends JpaRepository<Stock, String> {
    List<Stock> findByTickerContaining(String ticker);
    Stock findByTicker(String ticker);

    Optional<Stock> findById(String id);
}

