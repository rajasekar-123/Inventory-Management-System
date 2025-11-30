package com.example.InventoryMangement.purchase.dto;


public class PurchaseDTO {
    private Integer productId;
    private Integer quantity;
    private Integer purchasePrice;


    // getters & setters
    public Integer getProductId() { return productId; }
    public void setProductId(Integer productId) { this.productId = productId; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public Integer getPurchasePrice() { return purchasePrice; }
    public void setPurchasePrice(Integer purchasePrice) { this.purchasePrice = purchasePrice; }
}