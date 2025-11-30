package com.example.InventoryMangement.Product.Myrepo;

import com.example.InventoryMangement.Product.Entity.StockAdjustment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface StockAdjustmentRepo extends JpaRepository<StockAdjustment, Integer>{

}
