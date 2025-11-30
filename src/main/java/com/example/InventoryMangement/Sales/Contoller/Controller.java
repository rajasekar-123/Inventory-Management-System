package com.example.InventoryMangement.Sales.Contoller;

import com.example.InventoryMangement.Sales.Entity.Customer;
import com.example.InventoryMangement.Sales.Entity.Invoice;
import com.example.InventoryMangement.Sales.Service.SalesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sales")

@CrossOrigin(origins = "http://localhost:4200")
public class Controller {
      @Autowired

     private final SalesService salesService;

    public Controller(SalesService salesService) {
        this.salesService = salesService;
    }


    @PostMapping("/create")
    public ResponseEntity<Invoice> create(@RequestBody Invoice invoice)
    {
        return ResponseEntity.ok(salesService.createSale(invoice));
    }


    @GetMapping("/all")
    public List<Invoice> all() {
        return salesService.getAllSales();
    }

    @GetMapping("/{id}")
    public Invoice getOne(@PathVariable Long id) {
        return salesService.getInvoice(id);
    }

    @PostMapping("/customer")
    public Customer addCustomer(@RequestBody Customer c) {
        return salesService.saveCustomer(c);
    }

    @GetMapping("/customers")
    public List<Customer> allCustomers() {
        return salesService.getAllCustomers();
    }
}

