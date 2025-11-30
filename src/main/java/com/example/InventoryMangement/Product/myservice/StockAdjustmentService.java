package com.example.InventoryMangement.Product.myservice;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.InventoryMangement.Product.Entity.*;
import com.example.InventoryMangement.Product.Myrepo.ProductRepo;
import com.example.InventoryMangement.Product.Myrepo.StockAdjustmentRepo;
import com.example.InventoryMangement.Product.Myrepo.TransactionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StockAdjustmentService {

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private StockAdjustmentRepo stockAdjustmentRepo;

    @Autowired
    private TransactionRepo transactionRepo;

    @Transactional
    public void adjustStock(Integer productId, StockAdjustment adjustment) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (adjustment.getAdjustmentType() == AdjustmentType.ADD) {
            product.setCurrentStock(product.getCurrentStock() + adjustment.getQty());
        } else if (adjustment.getAdjustmentType() == AdjustmentType.REDUCE) {
            if (product.getCurrentStock() < adjustment.getQty()) {
                throw new RuntimeException("Insufficient stock to reduce");
            }
            product.setCurrentStock(product.getCurrentStock() - adjustment.getQty());
        }

        // persist product and adjustment
        productRepo.save(product);
        adjustment.setProduct(product);
        stockAdjustmentRepo.save(adjustment);

        // create transaction record
        Transaction trans = new Transaction();
        trans.setProduct(product);
        trans.setDate(LocalDate.now());
        trans.setName(adjustment.getDetails());
        trans.setQuantity(adjustment.getQty());
        trans.setPricePerUnit(adjustment.getPerPiecePrice());
        trans.setTotalAmount(adjustment.getQty() * (adjustment.getPerPiecePrice() == null ? 0 : adjustment.getPerPiecePrice()));
        trans.setStatus("Stock Adjustment");


        trans.setType(adjustment.getAdjustmentType() == AdjustmentType.ADD
                ? TransactionType.ADD_ADJUSTMENT
                : TransactionType.REDUCE_ADJUSTMENT);

        transactionRepo.save(trans);
    }
}


