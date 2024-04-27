package com.balionis.dainius.sps.controller;

import com.balionis.dainius.sps.generated.model.AddPriceResponse;
import com.balionis.dainius.sps.generated.model.FindPricesResponse;
import com.balionis.dainius.sps.generated.model.Price;
import com.balionis.dainius.sps.generated.model.Stock;
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
@RequestMapping("/api/v1")
public class StockController {

    private final StockService stockService;

    @Autowired
    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @PostMapping("/stocks")
    public ResponseEntity<?> addStock(@RequestBody Stock stock) {

        List<Stock> existingStocks = stockService.findStocksByTicker(stock.getTicker());
        if (!existingStocks.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("A stock with ticker '" + stock.getTicker() + "' already exists.");
        }

        // Assuming the ID is generated as a UUID in the service layer
        Stock createdStock = stockService.addOrUpdateStock(stock);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStock);
    }

    @GetMapping
    public ResponseEntity<?> getStocks(@RequestParam(required = false) String ticker) {
        List<Stock> stocks = stockService.findStocksByTicker(ticker);
        return ResponseEntity.ok(stocks);
    }

    @PostMapping("/{ticker}/prices")
    public ResponseEntity<?> addPrice(@PathVariable String ticker, @RequestBody Price price) {
        AddPriceResponse response = stockService.addOrUpdatePrice(ticker, price);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{ticker}/prices")
    public ResponseEntity<?> getPrices(
            @PathVariable String ticker,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        FindPricesResponse response = stockService.findPricesByTickerAndDates(ticker, fromDate, toDate);
        return ResponseEntity.ok(response);
    }
}



