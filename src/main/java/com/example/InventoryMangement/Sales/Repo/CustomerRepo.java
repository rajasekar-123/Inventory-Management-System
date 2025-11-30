package com.example.InventoryMangement.Sales.Repo;

import com.example.InventoryMangement.Sales.Entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepo extends JpaRepository<Customer,Integer>
{

}
