package com.balionis.dainius.sps.model;

import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import javax.persistence.*;

@Entity
@Table(name = "prices")
public class PriceRecord {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "price_id", columnDefinition = "CHAR(36)")
    private String priceId;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "stock_id")
    private StockRecord stock;

    @Column(name = "pricing_date")
    private LocalDate pricingDate;

    @Column(name = "price")
    private BigDecimal priceValue;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public PriceRecord(){
    }

    public PriceRecord(BigDecimal priceValue, LocalDate pricingDate, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.priceValue = priceValue;
        this.pricingDate = pricingDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            this.createdAt = now;
        }
        if (updatedAt == null) {
            this.updatedAt = now;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public String getPriceId() {
        return priceId;
    }

    public StockRecord getStock() {
        return stock;
    }

    public LocalDate getPricingDate() {
        return pricingDate;
    }

    public BigDecimal getPriceValue() {
        return priceValue;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }


    public void setPriceId(String priceId) { this.priceId = priceId; }

    public void setStock(StockRecord stock) {
        this.stock = stock;
    }

    public void setPricingDate(LocalDate pricingDate) {
        this.pricingDate = pricingDate;
    }

    public void setPriceValue(BigDecimal priceValue) {
        this.priceValue = priceValue;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String toString() {
        return "{ priceId=" + priceId + ", pricingDate=" + pricingDate + ", priceValue=" + priceValue
                + ", stockId=" + (stock != null ? stock.getStockId() : "null")
                + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "}";
    }

}
