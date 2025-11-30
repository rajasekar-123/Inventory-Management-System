package com.example.InventoryMangement.Product.Myrepo;

import java.util.List;
import java.util.Optional;

import com.example.InventoryMangement.Product.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface ProductRepo extends JpaRepository<Product, Integer> {

    @Query(value = "SELECT p.name FROM Product p", nativeQuery = true)
    List<String> getAllProductNames();

    @Query("SELECT p.initialStock FROM Product p")
    List<Integer> getAllStockQty();

    @Query(value = "SELECT COALESCE(SUM(current_stock), 0) FROM product", nativeQuery = true)
    Integer totalStockQty();

    @Query("SELECT p FROM Product p WHERE p.currentStock <= p.lowStock")
    List<Product> findLowStockProducts();
}
