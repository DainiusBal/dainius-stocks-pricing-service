package com.balionis.dainius.sps.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "stocks")
public class Stock {


    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "stock_id", columnDefinition = "CHAR(36)")
    private String id;

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

    public Stock() {
    }

    public Stock(String ticker, String description, int sharesOutstanding) {
        this.ticker = ticker;
        this.description = description;
        this.sharesOutstanding = sharesOutstanding;
    }

    @OneToMany(mappedBy = "stock", cascade = CascadeType.ALL)
    private List<Price> prices = new ArrayList<>();


    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void addPrice(Price price) {
        price.setStock(this);
        prices.add(price);
    }

    public void removePrice(Price price) {
        price.setStock(null);
        prices.remove(price);
    }


    public String getStockId() {
        return id;
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

    public void setStockId(String id) {
        this.id = id;
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
}

