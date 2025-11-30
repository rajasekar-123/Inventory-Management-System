package com.example.InventoryMangement.Product.myservice;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.example.InventoryMangement.Product.Entity.*;
import com.example.InventoryMangement.Product.Myrepo.ProductRepo;
import com.example.InventoryMangement.Product.Myrepo.TransactionRepo;
import com.example.InventoryMangement.Sales.Repo.InvoiceRepo;
import com.example.InventoryMangement.purchase.entity.Purchase;
import com.example.InventoryMangement.purchase.entity.PurchaseMaster;
import com.example.InventoryMangement.purchase.repository.PurchaseRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepo transRepo;

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private InvoiceRepo invoiceRepo;

    @Autowired
    private PurchaseRepo purchaseRepo;


    public void recordtransaction(TransactionsDTO dto) {
        // Log the incoming transaction data
        System.out.println("Recording Transaction with Product ID: " + dto.getProductId());

        // Validate Product ID
        if (dto.getProductId() == null || dto.getProductId() == 0) {
            throw new RuntimeException("Invalid Product ID provided");
        }

        // Fetch the product from the database
        Product product = productRepo.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Create the Transaction object
        Transaction transaction = new Transaction();
        transaction.setProduct(product);
        transaction.setType(dto.getType());
        transaction.setName(dto.getName());
        transaction.setDate(LocalDate.now());
        transaction.setQuantity(dto.getQuantity());
        transaction.setPricePerUnit(dto.getPricePerUnit());
        transaction.setTotalAmount(dto.getQuantity() * dto.getPricePerUnit());
        transaction.setTime(dto.getTime());

        // Set status for PURCHASE/Sale
        if (dto.getType() == TransactionType.PURCHASE || dto.getType() == TransactionType.SALE) {
            transaction.setStatus(dto.getStatus());
        } else {
            transaction.setStatus("COMPLETED");
        }

        // Save transaction
        transRepo.save(transaction);

        // If the transaction is a SALE or REDUCE_ADJUSTMENT, reduce stock
        if (dto.getType() == TransactionType.SALE || dto.getType() == TransactionType.REDUCE_ADJUSTMENT) {
            if (product.getCurrentStock() < dto.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getId());
            }
            product.setCurrentStock(product.getCurrentStock() - dto.getQuantity());
        } else if (dto.getType() == TransactionType.PURCHASE || dto.getType() == TransactionType.ADD_ADJUSTMENT
                || dto.getType() == TransactionType.OPENING_STOCK) {
            // Log stock before and after the update for purchase transactions
            System.out.println("Before Purchase Update: " + product.getCurrentStock());
            product.setCurrentStock(product.getCurrentStock() + dto.getQuantity());
            System.out.println("After Purchase Update: " + product.getCurrentStock());
        }

        // Save the updated product stock
        productRepo.save(product);
        productRepo.flush(); // Ensure changes are committed to the database
    }



    public List<Transaction> getAllTransaction() {
        return transRepo.findAll();
    }

    public Transaction getTransactionById(Long id) {
        return transRepo.findById(id).orElseThrow(() -> new RuntimeException("Transaction not found"));
    }




}
