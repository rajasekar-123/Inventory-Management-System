package com.example.InventoryMangement.purchase.controller;

import com.example.InventoryMangement.purchase.entity.Supplier;
import com.example.InventoryMangement.purchase.repository.SupplierRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("api/supplier")
@CrossOrigin(origins = "http://localhost:4200")
public class SupplierController {

    @Autowired
    private SupplierRepo supplierRepo;



    @PostMapping("/add")
    public Supplier add(@RequestBody Supplier supplier) {
        return supplierRepo.save(supplier);
    }

    @GetMapping("/all")
    public List<Supplier> list() {
        return supplierRepo.findAll();
    }
}
