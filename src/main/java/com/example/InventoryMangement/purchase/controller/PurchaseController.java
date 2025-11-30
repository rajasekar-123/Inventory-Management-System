package com.example.InventoryMangement.purchase.controller;

import com.example.InventoryMangement.purchase.dto.PurchaseItemDTO;
import com.example.InventoryMangement.purchase.dto.PurchaseRequestDTO;
import com.example.InventoryMangement.purchase.entity.PurchaseMaster;
import com.example.InventoryMangement.purchase.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("api/purchase")
@CrossOrigin(origins = "http://localhost:4200")
public class PurchaseController {

    @Autowired
    private PurchaseService purchaseService;

    @PostMapping("/add")
    public String addPurchase(@RequestBody PurchaseRequestDTO dto) {
        return purchaseService.addPurchase(dto);
    }

    @GetMapping("/{id}")
    public PurchaseMaster getById(@PathVariable Integer id) {
        return purchaseService.getPurchaseById(id);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Integer id) {
        return purchaseService.deletePurchase(id);
    }

    @GetMapping
    public List<PurchaseMaster> getAllPurchases() {
        return purchaseService.getAllPurchases();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody PurchaseItemDTO productUpdateDTO) {
        // Call the service method to update the product
        String result = purchaseService.updateProduct(id, productUpdateDTO);

        if (result.equals("Product updated successfully")) {
            // Return success response with the updated product information
            return ResponseEntity.ok(result);
        } else {
            // Return an error message if product is not found or any issue occurs
            return ResponseEntity.status(404).body(result);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updatePurchase(
            @PathVariable Long id,
            @RequestBody PurchaseItemDTO dto) {
        return ResponseEntity.ok(purchaseService.updateProduct(id,dto));
    }




}
