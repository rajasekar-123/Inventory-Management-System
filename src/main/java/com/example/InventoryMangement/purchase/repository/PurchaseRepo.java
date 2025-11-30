package com.example.InventoryMangement.purchase.repository;


import java.util.List;
import java.util.Optional;

import com.example.InventoryMangement.Product.Entity.Product;
import com.example.InventoryMangement.purchase.entity.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface PurchaseRepo extends JpaRepository<Purchase, Integer> {
	List<Purchase> findByPurchaseMasterId(Integer purchaseMasterId);




        // Custom query to find the purchase by product ID
        Optional<Purchase> findByProduct(Product product);

        // Alternatively, if you need to filter by more fields, you can use a custom query
        @Query("SELECT p FROM Purchase p WHERE p.product.id = :productId")
        Optional<Purchase> findPurchaseByProductId(@Param("productId") Long productId);
    }



