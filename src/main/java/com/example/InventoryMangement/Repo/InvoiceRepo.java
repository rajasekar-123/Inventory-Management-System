package com.example.InventoryMangement.Repo;

import com.example.InventoryMangement.Entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepo  extends JpaRepository<Invoice,Integer> {
}
