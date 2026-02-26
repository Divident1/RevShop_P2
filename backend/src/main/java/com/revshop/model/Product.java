package com.revshop.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private Double price;

    private Double mrp;

    private Double discountPercentage;

    @Column(nullable = false)
    private Integer quantity;

    private Double rating;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    // String-based category for backward compatibility with Kavya's code
    @Column(name = "category_name")
    private String categoryName;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    @JsonIgnoreProperties({ "password", "resetToken" })
    private User seller;

    private Boolean isActive = true;

    @Column(nullable = false)
    private Integer stockThreshold = 5;

    // ═══════════ GETTERS & SETTERS ═══════════

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getMrp() {
        return mrp;
    }

    public void setMrp(Double mrp) {
        this.mrp = mrp;
    }

    public Double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(Double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public User getSeller() {
        return seller;
    }

    public void setSeller(User seller) {
        this.seller = seller;
    }

    // Convenience method — returns seller's ID for backward compatibility
    public Long getSellerId() {
        return seller != null ? seller.getId() : null;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    // Alias for Jatin's code which uses isActive()
    public boolean isActive() {
        return isActive != null && isActive;
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }

    public Integer getStockThreshold() {
        return stockThreshold;
    }

    public void setStockThreshold(Integer stockThreshold) {
        this.stockThreshold = stockThreshold;
    }
}