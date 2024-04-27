package com.balionis.dainius.sps.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "stocks")
public class StockRecord {


    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "stock_id", columnDefinition = "CHAR(36)")
    private String stockId;

    @Column(name = "ticker")
    private String ticker;

    @Column(name = "description")
    private String description;

    @Column(name = "shares_outstanding")
    private int sharesOutstanding;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "stock", cascade = CascadeType.ALL)
    private List<PriceRecord> prices = new ArrayList<>();

    public StockRecord() {
    }

    public StockRecord(String ticker, String description, int sharesOutstanding) {
        this.ticker = ticker;
        this.description = description;
        this.sharesOutstanding = sharesOutstanding;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            this.updatedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }


    public void addPrice(PriceRecord price) {
        price.setStock(this);
        prices.add(price);
    }

    public void removePrice(PriceRecord price) {
        price.setStock(null);
        prices.remove(price);
    }


    public String getStockId() {
        return stockId;
    }

    public String getTicker() {
        return ticker;
    }

    public String getDescription() {
        return description;
    }

    public int getSharesOutstanding() {
        return sharesOutstanding;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setStockId(String stockId) {
        this.stockId = stockId;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setSharesOutstanding(int sharesOutstanding) {
        this.sharesOutstanding = sharesOutstanding;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String toString() {
        return "{ stockId=" + stockId + ", ticker=" + ticker + ", description=" + description
                + ", sharesOutstanding=" + sharesOutstanding
                + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "}";
    }
}

