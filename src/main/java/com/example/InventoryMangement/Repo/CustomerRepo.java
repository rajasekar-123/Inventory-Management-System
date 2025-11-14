package com.example.InventoryMangement.Repo;

import com.example.InventoryMangement.Entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepo extends JpaRepository<Customer,Integer>
{

}
