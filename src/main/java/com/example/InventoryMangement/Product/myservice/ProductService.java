package com.example.InventoryMangement.Product.myservice;

import java.util.List;
import java.util.Optional;

import com.example.InventoryMangement.Product.Entity.Product;
import com.example.InventoryMangement.Product.Myrepo.ProductRepo;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    @Autowired
    private ProductRepo repo;

    // Create product
    public Product addProduct(Product product) {

        if (product.getCurrentStock() == null) {
            product.setCurrentStock(product.getInitialStock());
        }

        return repo.save(product);
    }

    // Update product
    public Product updateProduct(Integer id, Product updatedProduct) {

        Optional<Product> optionalProduct = repo.findById(id);

        if (optionalProduct.isPresent()) {

            Product existing = optionalProduct.get();

            if (updatedProduct.getName() != null)
                existing.setName(updatedProduct.getName());

            if (updatedProduct.getUnitType() != null)
                existing.setUnitType(updatedProduct.getUnitType());

            if (updatedProduct.getSalesPrice() != null)
                existing.setSalesPrice(updatedProduct.getSalesPrice());

            if (updatedProduct.getPurchasePrice() != null)
                existing.setPurchasePrice(updatedProduct.getPurchasePrice());

            if (updatedProduct.getTaxRate() != null)
                existing.setTaxRate(updatedProduct.getTaxRate());

            if (updatedProduct.getInitialStock() != null) {
                existing.setInitialStock(updatedProduct.getInitialStock());
            }

            // CurrentStock never decreased directly by user.
            // Only sales, purchase, adjustment modules change stock.
            if (updatedProduct.getCurrentStock() != null) {
                existing.setCurrentStock(updatedProduct.getCurrentStock());
            }

            if (updatedProduct.getAtPrice() != null)
                existing.setAtPrice(updatedProduct.getAtPrice());

            if (updatedProduct.getLowStock() != null)
                existing.setLowStock(updatedProduct.getLowStock());

            if (updatedProduct.getUpdateDate() != null)
                existing.setUpdateDate(updatedProduct.getUpdateDate());

            return repo.save(existing);
        }

        return null;
    }

    public String deleteProduct(Integer id) {
        repo.deleteById(id);
        return "Successfully deleted...";
    }

    public Optional<Product> viewProductsById(Integer id) {
        return repo.findById(id);
    }

    public List<Product> viewAllProducts() {
        return repo.findAll();
    }






}
