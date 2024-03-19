package com.balionis.dainius.sps.controller;

import com.balionis.dainius.sps.model.Stock;
import com.balionis.dainius.sps.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/stocks")
public class StockController {

    private final StockService stockService;

    @Autowired
    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @PostMapping
    public ResponseEntity<?> addOrUpdateStock(@RequestBody Stock stock) {
        Stock createdOrUpdatedStock = stockService.addOrUpdateStock(stock);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrUpdatedStock);
    }

    @GetMapping
    public ResponseEntity<?> findStocks(@RequestParam(required = false) String ticker) {
        if (ticker == null || ticker.isEmpty()) {
            return ResponseEntity.ok(stockService.getAllStocks());
        } else {
            return ResponseEntity.ok(stockService.findStocksByTicker(ticker));
        }
    }
}
