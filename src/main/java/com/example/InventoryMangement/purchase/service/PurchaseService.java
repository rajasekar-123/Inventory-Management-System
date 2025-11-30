package com.example.InventoryMangement.purchase.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.example.InventoryMangement.Product.Entity.Product;
import com.example.InventoryMangement.Product.Entity.Transaction;
import com.example.InventoryMangement.Product.Entity.TransactionType;
import com.example.InventoryMangement.Product.Entity.UnitType;
import com.example.InventoryMangement.Product.Myrepo.ProductRepo;
import com.example.InventoryMangement.Product.Myrepo.TransactionRepo;
import com.example.InventoryMangement.purchase.dto.PurchaseItemDTO;
import com.example.InventoryMangement.purchase.dto.PurchaseRequestDTO;
import com.example.InventoryMangement.purchase.entity.Purchase;
import com.example.InventoryMangement.purchase.entity.PurchaseMaster;
import com.example.InventoryMangement.purchase.entity.Supplier;
import com.example.InventoryMangement.purchase.repository.PurchaseMasterRepo;
import com.example.InventoryMangement.purchase.repository.PurchaseRepo;
import com.example.InventoryMangement.purchase.repository.SupplierRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

@Service
public class PurchaseService {

    @Autowired private PurchaseMasterRepo purchaseMasterRepo;
    @Autowired private PurchaseRepo purchaseRepo;
    @Autowired private ProductRepo productRepo;
    @Autowired private TransactionRepo transactionRepo;
    @Autowired private SupplierRepo supplierRepo;

    @Transactional
    public String addPurchase(PurchaseRequestDTO dto) {
        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new RuntimeException("Purchase items cannot be empty");
        }

        // Find or create the supplier
        Supplier supplier = findOrCreateSupplier(dto);

        // Create purchase master record
        PurchaseMaster master = new PurchaseMaster();
        master.setSupplier(supplier);
        master.setInvoiceNumber(dto.getInvoiceNumber());
        master.setPurchaseDate(LocalDate.now());
        master.setPaymentType(dto.getPaymentType());
        master.setAmountPaid(dto.getAmountPaid());

        try {
            master = purchaseMasterRepo.save(master);
            System.out.println("PurchaseMaster saved: " + master.getInvoiceNumber());
        } catch (Exception e) {
            System.err.println("Error saving PurchaseMaster: " + e.getMessage());
            throw new RuntimeException("Failed to save PurchaseMaster");
        }

        int grandTotal = 0;

        // Loop through the items and create or update products
        for (PurchaseItemDTO itemDto : dto.getItems()) {



            Product product = null;

            if (itemDto.getProductId() != null) {
                product = productRepo.findById(itemDto.getProductId()).orElse(null);
            }

            if (product == null) {
                if (itemDto.getProductname() == null || itemDto.getProductname().trim().isEmpty()) {
                    throw new IllegalArgumentException("Product name is required");
                }

                product = new Product();
                product.setName(itemDto.getProductname());
                product.setUnitType(UnitType.PIECE);
                product.setSalesPrice(itemDto.getPricePerUnit());
                product.setPurchasePrice(itemDto.getPricePerUnit());
                product.setTaxRate(itemDto.getGstPercentage());
                product.setInitialStock(itemDto.getQuantity());
                product.setCurrentStock(itemDto.getQuantity());
                product.setAtPrice(itemDto.getPricePerUnit());
                product.setLowStock(5);
                product.setUpdateDate(LocalDate.now());

                product = productRepo.save(product);
                System.out.println("Product saved: " + product.getName());
            } else {
                product.setCurrentStock(product.getCurrentStock() + itemDto.getQuantity());
                product.setUpdateDate(LocalDate.now());
                productRepo.save(product);
                System.out.println("Product updated: " + product.getName());
            }

            // Create transaction per item
            Transaction txn = new Transaction();
            txn.setType(TransactionType.PURCHASE);
            txn.setProduct(product);
            txn.setQuantity(itemDto.getQuantity());
            txn.setPricePerUnit(itemDto.getPricePerUnit());
            txn.setTotalAmount(grandTotal);
            txn.setDate(master.getPurchaseDate());
            txn.setTime(master.getPurchaseTime());
            txn.setName(master.getSupplier() != null ? master.getSupplier().getName() : dto.getSupplierName());
            txn.setInvoiceNumber(master.getInvoiceNumber());
            if (master.getId() != null) {
                txn.setReferenceId(master.getId().longValue());
            }


            txn.setStatus("COMPLETED");
            transactionRepo.save(txn);



            // Create purchase entry linking the product with the purchase master
            Purchase purchase = new Purchase();
            purchase.setPurchaseMaster(master);
            purchase.setProduct(product);
            purchase.setQuantity(itemDto.getQuantity());
            purchase.setPricePerUnit(itemDto.getPricePerUnit());
            purchase.setGstPercentage(itemDto.getGstPercentage());
            purchase.setTotalAmount(itemDto.getQuantity() * itemDto.getPricePerUnit());
            purchaseRepo.save(purchase);

            grandTotal += purchase.getTotalAmount();
        }

        master.setGrandTotal(grandTotal);
        purchaseMasterRepo.save(master);

        return "Purchase added successfully with Invoice Number: " + master.getInvoiceNumber();
    }

    // New Method: Update an existing product
    @Transactional
    public String updateProduct(Long productId, PurchaseItemDTO productUpdateDTO) {
        try {
            // Check if the product exists
            Product existingProduct = productRepo.findById(Math.toIntExact(productId)).orElse(null);
            if (existingProduct == null) {
                return "Product not found";
            }

            // Update product fields based on the received DTO
            if (productUpdateDTO.getProductname() != null && !productUpdateDTO.getProductname().trim().isEmpty()) {
                existingProduct.setName(productUpdateDTO.getProductname());
            }
            if (productUpdateDTO.getPricePerUnit() != null) {
                existingProduct.setPurchasePrice(productUpdateDTO.getPricePerUnit());
                existingProduct.setSalesPrice(productUpdateDTO.getPricePerUnit()); // Adjust this according to logic
            }
            if (productUpdateDTO.getGstPercentage() != null) {
                existingProduct.setTaxRate(productUpdateDTO.getGstPercentage());
            }

            // Update quantity if necessary (e.g., add more stock)
            if (productUpdateDTO.getQuantity() != null) {
                existingProduct.setCurrentStock(existingProduct.getCurrentStock() + productUpdateDTO.getQuantity());
            }

            // Save the updated product
            productRepo.save(existingProduct);
            return "Product updated successfully";
        } catch (Exception e) {
            return "Error updating product: " + e.getMessage();
        }
    }

    // Helper method to handle finding or creating the supplier
    private Supplier findOrCreateSupplier(PurchaseRequestDTO dto) {
        Supplier supplier = null;

        // 1. Try to find supplier by ID
        if (dto.getSupplierId() != null) {
            supplier = supplierRepo.findById(dto.getSupplierId()).orElse(null);
        }



        // 3. Create a new supplier if not found
        if (supplier == null) {
            supplier = new Supplier();
            supplier.setName(dto.getSupplierName());
            supplier.setAddress(dto.getSupplierAddress());
            supplier.setPhoneNumber(dto.getSupplierPhone());
            supplier = supplierRepo.save(supplier); // Save the new supplier
        }

        return supplier;
    }

    @Transactional
    public String deletePurchase(Integer id) {
        PurchaseMaster master = purchaseMasterRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase not found"));

        List<Purchase> items = purchaseRepo.findByPurchaseMasterId(id);

        for (Purchase item : items) {
            Product product = item.getProduct();

            if (product.getCurrentStock() < item.getQuantity()) {
                throw new RuntimeException("Stock cannot go negative while deleting purchase");
            }

            product.setCurrentStock(product.getCurrentStock() - item.getQuantity());
            productRepo.save(product);
        }

        purchaseRepo.deleteAll(items);
        transactionRepo.deleteByInvoiceNumber(master.getInvoiceNumber());
        purchaseMasterRepo.delete(master);

        return "Purchase deleted successfully";
    }

    public List<PurchaseMaster> getAllPurchases() {
        return purchaseMasterRepo.findAll();
    }

    public PurchaseMaster getPurchaseById(Integer id) {
        return purchaseMasterRepo.findById(id).orElse(null); // Returns null if not found
    }
}
