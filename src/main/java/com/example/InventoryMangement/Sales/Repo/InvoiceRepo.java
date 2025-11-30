package com.example.InventoryMangement.Sales.Repo;

import com.example.InventoryMangement.Sales.Entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepo  extends JpaRepository<Invoice,Integer> {
}
