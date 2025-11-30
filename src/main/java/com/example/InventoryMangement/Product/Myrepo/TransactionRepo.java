package com.example.InventoryMangement.Product.Myrepo;

import com.example.InventoryMangement.Product.Entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
public interface TransactionRepo extends JpaRepository<Transaction, Long> {
    // Today Sales (Product name, Qty)
    @Query(value = """
        SELECT p.name, SUM(t.quantity)
        FROM transactions t
        JOIN product p ON t.product_id = p.id
        WHERE DATE(t.created_at) = CURDATE()
        GROUP BY p.name
        """, nativeQuery = true)
    List<Object[]> todaySales();

    // Today Revenue
    @Query(value = """
        SELECT COALESCE(SUM(t.total_amount),0)
        FROM transactions t
        WHERE DATE(t.created_at) = CURDATE()
        """, nativeQuery = true)
    Double todayRevenue();


    // Monthly Sales
    @Query(value = """
        SELECT p.name, SUM(t.quantity)
        FROM transactions t
        JOIN product p ON t.product_id = p.id
        WHERE MONTH(t.created_at) = MONTH(CURDATE())
        AND YEAR(t.created_at) = YEAR(CURDATE())
        GROUP BY p.name
        """, nativeQuery = true)
    List<Object[]> monthlySales();

    // Monthly Revenue
    @Query(value = """
        SELECT COALESCE(SUM(t.total_amount),0)
        FROM transactions t
        WHERE MONTH(t.created_at) = MONTH(CURDATE())
        AND YEAR(t.created_at) = YEAR(CURDATE())
        """, nativeQuery = true)
    Double monthlyRevenue();


    // Yearly Sales
    @Query(value = """
        SELECT p.name, SUM(t.quantity)
        FROM transactions t
        JOIN product p ON t.product_id = p.id
        WHERE YEAR(t.created_at) = YEAR(CURDATE())
        GROUP BY p.name
        """, nativeQuery = true)
    List<Object[]> yearlySales();

    //Yearly Revenue
    @Query(value = """
        SELECT COALESCE(SUM(t.total_amount),0)
        FROM transactions t
        WHERE YEAR(t.created_at) = YEAR(CURDATE())
        """, nativeQuery = true)
    Double yearlyRevenue();


    // Total Active Customers
    @Query(value = """
        SELECT COUNT(DISTINCT customer_id)
        FROM invoice
        """, nativeQuery = true)
    Long activeCustomers();


    // Top Selling Products
    @Query(value = """
        SELECT p.name AS name, SUM(t.quantity) AS totalSold
        FROM transactions t
        JOIN product p ON t.product_id = p.id
        GROUP BY p.name
        ORDER BY totalSold DESC
        LIMIT 5
        """, nativeQuery = true)
    List<Map<String, Object>> topSellingProducts();


    //Active Customer Details
    @Query(value = """
        SELECT c.id, c.name, c.phone, COUNT(i.id) AS totalPurchases
        FROM customer c
        JOIN invoice i ON c.id = i.customer_id
        GROUP BY c.id, c.name, c.phone
        ORDER BY totalPurchases DESC
        """, nativeQuery = true)
    List<Map<String, Object>> activeCustomerDetails();
    void deleteByInvoiceNumber(String invoiceNumber);

    List<Transaction> findByProductIdAndDateBetween(Integer productId, LocalDate start, LocalDate today);
}
