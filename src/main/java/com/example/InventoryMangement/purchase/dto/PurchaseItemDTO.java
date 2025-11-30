package com.example.InventoryMangement.purchase.dto;

import com.example.InventoryMangement.Product.Entity.Product;

public class PurchaseItemDTO {
    private Integer productId;
    private Integer quantity;
    private Integer pricePerUnit;
    private Integer gstPercentage;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    private String productName;

    public String getProductname() {
        return productName;
    }

    public void setProductname(String productname) {
        this.productName = productname;
    }



    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(Integer pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    public Integer getGstPercentage() {
        return gstPercentage;
    }

    public void setGstPercentage(Integer gstPercentage) {
        this.gstPercentage = gstPercentage;
    }

    // getters & setters...
}
