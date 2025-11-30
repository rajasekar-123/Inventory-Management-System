package com.example.InventoryMangement.dashboard.service;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.InventoryMangement.Product.Myrepo.ProductRepo;
import com.example.InventoryMangement.Product.Myrepo.TransactionRepo;
import com.example.InventoryMangement.dashboard.dto.DashboardDTO;

@Service
public class DashboardService {

    @Autowired private ProductRepo productRepo;
    @Autowired private TransactionRepo transactionRepo;

    public DashboardDTO getDashboard(String period) {

        DashboardDTO dto = new DashboardDTO();

        // Stock remains same for all filters
        dto.totalStock = Optional.ofNullable(productRepo.totalStockQty()).orElse(0);
        dto.productNames = productRepo.getAllProductNames();
        dto.productStock = productRepo.getAllStockQty();

        List<Object[]> sales;

        // Dropdown logic
        if ("today".equalsIgnoreCase(period)) {

            sales = transactionRepo.todaySales();
            dto.totalMonthlyRevenue = Optional.ofNullable(transactionRepo.todayRevenue()).orElse(0.0);

        } else if ("year".equalsIgnoreCase(period)) {

            sales = transactionRepo.yearlySales();
            dto.totalMonthlyRevenue = Optional.ofNullable(transactionRepo.yearlyRevenue()).orElse(0.0);

        } else {
            // Default Month
            sales = transactionRepo.monthlySales();
            dto.totalMonthlyRevenue = Optional.ofNullable(transactionRepo.monthlyRevenue()).orElse(0.0);
        }

        // Map sales data
        Map<String, Integer> salesMap = new HashMap<>();
        for (Object[] row : sales) {
            salesMap.put(row[0].toString(), ((Number) row[1]).intValue());
        }

        dto.todaySales = new ArrayList<>();
        dto.monthlySales = new ArrayList<>();

        for (String name : dto.productNames) {
            int qty = salesMap.getOrDefault(name, 0);
            if ("today".equalsIgnoreCase(period)) {
                dto.todaySales.add(qty);
            } else {
                dto.monthlySales.add(qty);
            }

        }

        // Low stock
        dto.lowStockProducts = new ArrayList<>();
//        productRepo.findByInitialStockLessThan(0)
//                .forEach(p -> dto.lowStockProducts.add(p.getName()));
        
        productRepo.findLowStockProducts()
        .forEach(p -> dto.lowStockProducts.add(p.getName()));

        // Active customers
        dto.activeCustomers = transactionRepo.activeCustomers();
        
        dto.topSellingProducts = transactionRepo.topSellingProducts();
        dto.activeCustomerDetails = transactionRepo.activeCustomerDetails();

        return dto;
    }
}