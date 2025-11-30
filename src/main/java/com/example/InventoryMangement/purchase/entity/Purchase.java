package com.example.InventoryMangement.purchase.entity;
import com.example.InventoryMangement.Product.Entity.Product;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;

import java.time.LocalDate;

@Entity
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "purchase_master_id")
    @JsonBackReference
    private PurchaseMaster purchaseMaster;

    @ManyToOne
    @JsonBackReference
    private Product product;

    private Integer quantity;
    private Integer pricePerUnit;
    private Integer gstPercentage;
    private Integer gstAmount;
    private Integer totalAmount;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public PurchaseMaster getPurchaseMaster() {
        return purchaseMaster;
    }

    public void setPurchaseMaster(PurchaseMaster purchaseMaster) {
        this.purchaseMaster = purchaseMaster;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
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

    public Integer getGstAmount() {
        return gstAmount;
    }

    public void setGstAmount(Integer gstAmount) {
        this.gstAmount = gstAmount;
    }

    public Integer getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Integer totalAmount) {
        this.totalAmount = totalAmount;
    }



    // getters & setters...
}
