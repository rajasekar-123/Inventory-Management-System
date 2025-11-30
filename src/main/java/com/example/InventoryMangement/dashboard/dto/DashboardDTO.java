package com.example.InventoryMangement.dashboard.dto;

import java.util.List;
import java.util.Map;

public class DashboardDTO {

    // Top Cards
    public Long activeCustomers;
    public Integer totalStock;
    public Double totalMonthlyRevenue;

    // Product stock
    public List<String> productNames;
    public List<Integer> productStock;

    // Sales
    public List<Integer> todaySales;
    public List<Integer> monthlySales;

    // Low stock
    public List<String> lowStockProducts;

    // Customer details
    public List<Map<String, Object>> activeCustomerDetails;

    // Top selling
    public List<Map<String, Object>> topSellingProducts;
}
