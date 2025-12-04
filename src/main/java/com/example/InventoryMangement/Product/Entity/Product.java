package com.example.InventoryMangement.Product.Entity;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Product name is required")
    private String name;

    @NotNull(message = "Unit type is required")
    @Enumerated(EnumType.STRING)
    private UnitType unitType;

    @NotNull(message = "Sales price is required")
    @Positive(message = "Sales price must be positive")
    private Integer salesPrice;

    @NotNull(message = "Purchase price is required")
    @Positive(message = "Purchase price must be positive")
    private Integer purchasePrice;

    @NotNull(message = "Tax rate is required")
    @Min(value = 0, message = "Tax rate cannot be negative")
    private Integer taxRate;

  
    @NotNull(message = "Initial quantity is required")
    @Min(value = 0, message = "Initial quantity cannot be negative")
    private Integer initialStock;

    @NotNull
    @Min(value = 0)
    private Integer currentStock;

    @NotNull(message = "At price is required")
    @Positive(message = "At price must be positive")
    private Integer atPrice;

    @NotNull(message = "Low stock value is required")
    @Min(value = 0, message = "Low stock must be 0 or more")
    private Integer lowStock;

    @NotNull(message = "Update date is required")
    private LocalDate updateDate;


    @OneToMany(mappedBy = "product")
    @JsonManagedReference
    private List<Transaction> transactions;


    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public UnitType getUnitType() { return unitType; }
    public void setUnitType(UnitType unitType) { this.unitType = unitType; }

    public Integer getSalesPrice() { return salesPrice; }
    public void setSalesPrice(Integer salesPrice) { this.salesPrice = salesPrice; }

    public Integer getPurchasePrice() { return purchasePrice; }
    public void setPurchasePrice(Integer purchasePrice) { this.purchasePrice = purchasePrice; }

    public Integer getTaxRate() { return taxRate; }
    public void setTaxRate(Integer taxRate) { this.taxRate = taxRate; }

    public Integer getInitialStock() { return initialStock; }
    public void setInitialStock(Integer initialStock) { this.initialStock = initialStock; }

    public Integer getCurrentStock() { return currentStock; }
    public void setCurrentStock(Integer currentStock) { this.currentStock = currentStock; }

    public Integer getAtPrice() { return atPrice; }
    public void setAtPrice(Integer atPrice) { this.atPrice = atPrice; }

    public Integer getLowStock() { return lowStock; }
    public void setLowStock(Integer lowStock) { this.lowStock = lowStock; }

    public LocalDate getUpdateDate() { return updateDate; }
    public void setUpdateDate(LocalDate updateDate) { this.updateDate = updateDate; }

    @Override
    public String toString() {
        return "Product [name=" + name + ", unitType=" + unitType + ", salesPrice=" + salesPrice
                + ", purchasePrice=" + purchasePrice + ", taxRate=" + taxRate + ", initialStock=" + initialStock
                + ", atPrice=" + atPrice + ", lowStock=" + lowStock + ", updateDate=" + updateDate + "]";
    }


}
