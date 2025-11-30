package com.example.InventoryMangement.Product.Entity;

import jakarta.validation.constraints.*;

import java.time.LocalTime;

public class TransactionsDTO {

    @NotNull(message = "Product ID is required")
    private Integer productId;

    @NotNull(message = "Transaction type is required")
    private TransactionType type;   // use enum

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    private Integer quantity;

    @NotNull(message = "Price per unit is required")
    @Positive(message = "Price per unit must be positive")
    private Integer pricePerUnit;

    // status optional — don't force frontend to set for adjustments
    private String status;


    private LocalTime time;
    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }
    // getters/setters
    public Integer getProductId() { return productId; }
    public void setProductId(Integer productId) { this.productId = productId; }

    public TransactionType getType() { return type; }
    public void setType(TransactionType type) { this.type = type; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Integer getPricePerUnit() { return pricePerUnit; }
    public void setPricePerUnit(Integer pricePerUnit) { this.pricePerUnit = pricePerUnit; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

}

