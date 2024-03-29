package com.balionis.dainius.sps.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.*;

@Entity
@Table(name = "prices")
public class Price {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "price_id")
    private Long priceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id")
    private Stock stock;

    @Column(name = "pricing_date")
    private LocalDate pricingDate;

    @Column(name = "price")
    private BigDecimal priceValue;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Price(){
    }

    public Price(BigDecimal priceValue, LocalDate pricingDate, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.priceValue = priceValue;
        this.pricingDate = pricingDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }




    public Long getPriceId() {
        return priceId;
    }

    public Stock getStock() {
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


    public void setPriceId(Long priceId) {
        this.priceId = priceId;
    }

    public void setStock(Stock stock) {
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


}
