package com.balionis.dainius.sps.controller;

import com.balionis.dainius.sps.generated.model.AddPriceResponse;
import com.balionis.dainius.sps.generated.model.FindPricesResponse;
import com.balionis.dainius.sps.generated.model.Price;
import com.balionis.dainius.sps.generated.model.Stock;
import com.balionis.dainius.sps.service.StockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1")
public class StockController {

    private final StockService stockService;

    @Autowired
    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @PostMapping("/stocks")
    public ResponseEntity<?> addStock(@RequestBody Stock stock) {
        log.info("addStock: stock={}", stock);
        Stock createdStock = stockService.addOrUpdateStock(stock);
        log.info("addStock: createdStock={}", createdStock);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStock);
    }

    @GetMapping("/stocks")
    public ResponseEntity<?> getStocks(@RequestParam(required = false) String ticker) {
        log.info("getStocks: ticker={}", ticker);
        List<Stock> stocks = stockService.findStocksByTicker(ticker);
        log.info("getStocks: stocks={}", stocks);
        return ResponseEntity.ok(stocks);
    }

    @PostMapping("/stocks/{ticker}/prices")
    public ResponseEntity<?> addPrice(@PathVariable String ticker, @RequestBody Price price) {
        log.info("addPrice: price={}", price);
        AddPriceResponse response = stockService.addOrUpdatePrice(ticker, price);
        log.info("addPrice: response={}", response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stocks/{ticker}/prices")
    public ResponseEntity<?> getPrices(
            @PathVariable String ticker,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        log.info("getPrices: ticker={}, fromDate={}, toDate={}", ticker, fromDate, toDate);
        FindPricesResponse response = stockService.findPricesByTickerAndDates(ticker, fromDate, toDate);
        log.info("getPrices: response={}", response);
        return ResponseEntity.ok(response);
    }
}



