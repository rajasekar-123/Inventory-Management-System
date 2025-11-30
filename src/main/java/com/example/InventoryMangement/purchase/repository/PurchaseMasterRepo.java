package com.example.InventoryMangement.purchase.repository;

import com.example.InventoryMangement.purchase.entity.PurchaseMaster;
import org.springframework.data.jpa.repository.JpaRepository;



public interface PurchaseMasterRepo extends JpaRepository<PurchaseMaster, Integer> {
}

