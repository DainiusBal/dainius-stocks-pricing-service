package com.balionis.dainius.sps.repository;

import com.balionis.dainius.sps.model.StockRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<StockRecord, String> {
    List<StockRecord> findByTickerContaining(String ticker);

    Optional<StockRecord> findByTicker(String ticker);

}

