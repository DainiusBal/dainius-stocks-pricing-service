package com.balionis.dainius.sps.controller;

import com.balionis.dainius.sps.service.PriceRequest;
import com.balionis.dainius.sps.model.PriceRecord;
import com.balionis.dainius.sps.model.StockRecord;
import com.balionis.dainius.sps.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/stocks")
public class StockController {

    private final StockService stockService;

    @Autowired
    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @PostMapping
    public ResponseEntity<?> addStock(@RequestBody StockRecord stock) {

        List<StockRecord> existingStocks = stockService.findStocksByTicker(stock.getTicker());
        if (!existingStocks.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("A stock with ticker '" + stock.getTicker() + "' already exists.");
        }

        // Assuming the ID is generated as a UUID in the service layer
        StockRecord createdStock = stockService.addOrUpdateStock(stock);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStock);
    }

    @PostMapping("/{ticker}")
    public ResponseEntity<StockRecord> updateStock(@PathVariable String ticker, @RequestBody StockRecord stock) {
        StockRecord updatedStock = stockService.addOrUpdateStock(stock);
        return ResponseEntity.ok(updatedStock);
    }

    @GetMapping
    public ResponseEntity<?> getStocks(@RequestParam(required = false) String ticker) {
        List<StockRecord> stocks = stockService.findStocksByTicker(ticker);
        if (stocks.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No stocks found");
        } else {
            return ResponseEntity.ok(stocks);
        }
    }

    @PostMapping("/{ticker}/prices")
    public ResponseEntity<?> addPrice(@PathVariable String ticker, @RequestBody PriceRequest request) {
        try {
            stockService.addOrUpdatePrice(ticker, request.getPrice(), request.getDate());
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to add price: " + e.getMessage());
        }
    }

    @GetMapping("/{ticker}/prices")
    public ResponseEntity<?> getPrices(
            @PathVariable String ticker,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        List<StockRecord> existingStocks = stockService.findStocksByTicker(ticker);
        if (existingStocks.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Stock with ticker '" + ticker + "' not found.");
        }

        List<PriceRecord> priceHistory = stockService.findPricesByTickerAndDates(ticker, fromDate, toDate);
        return ResponseEntity.ok(priceHistory);
    }
}



