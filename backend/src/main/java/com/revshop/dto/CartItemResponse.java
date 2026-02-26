package com.revshop.dto;

public class CartItemResponse {

    private Long cartItemId;
    private Long productId;
    private String productName;
    private String productDescription;
    private Double price;
    private Double mrp;
    private Double discountPercentage;
    private Integer quantity;
    private Integer availableStock;
    private Double subtotal;

    public CartItemResponse() {
    }

    public CartItemResponse(Long cartItemId, Long productId, String productName,
            String productDescription, Double price, Double mrp,
            Double discountPercentage, Integer quantity,
            Integer availableStock, Double subtotal) {
        this.cartItemId = cartItemId;
        this.productId = productId;
        this.productName = productName;
        this.productDescription = productDescription;
        this.price = price;
        this.mrp = mrp;
        this.discountPercentage = discountPercentage;
        this.quantity = quantity;
        this.availableStock = availableStock;
        this.subtotal = subtotal;
    }

    // ═══════════ GETTERS & SETTERS ═══════════

    public Long getCartItemId() {
        return cartItemId;
    }

    public void setCartItemId(Long cartItemId) {
        this.cartItemId = cartItemId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
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

    public Integer getAvailableStock() {
        return availableStock;
    }

    public void setAvailableStock(Integer availableStock) {
        this.availableStock = availableStock;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }
}
