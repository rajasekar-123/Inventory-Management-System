package com.example.InventoryMangement.purchase.repository;

import java.util.Optional;

import com.example.InventoryMangement.purchase.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;


public interface SupplierRepo extends JpaRepository<Supplier, Integer> {
	Optional<Supplier> findByName(String name);
	
	Optional<Supplier> findByPhoneNumber(String phoneNumber);
}
