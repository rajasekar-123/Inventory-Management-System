package com.example.InventoryMangement.Sales.Service;

import com.example.InventoryMangement.Product.Entity.Product;
import com.example.InventoryMangement.Product.Myrepo.ProductRepo;
import com.example.InventoryMangement.Product.Entity.Transaction;
import com.example.InventoryMangement.Product.Entity.TransactionType;
import com.example.InventoryMangement.Product.Myrepo.TransactionRepo;
import com.example.InventoryMangement.Sales.Entity.Customer;
import com.example.InventoryMangement.Sales.Entity.Invoice;
import com.example.InventoryMangement.Sales.Entity.InvoiceItem;
import com.example.InventoryMangement.Sales.PDFGenrator.PdfGeneratorService;
import com.example.InventoryMangement.Sales.Repo.CustomerRepo;
import com.example.InventoryMangement.Sales.Repo.InvoiceRepo;
import com.example.InventoryMangement.Sales.WhatsappService.TwilioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class SalesService {

    private final InvoiceRepo invoiceRepo;
    private final CustomerRepo customerRepo;
    private final ProductRepo productRepo;
    private final PdfGeneratorService pdfGeneratorService;
    private final S3Service s3Service;
    private final TwilioService twilioService;
    private final TransactionRepo transactionRepository;

    @Value("${inventory.low-stock.threshold:5}")
    private int lowStockThreshold;

    @Value("${admin.whatsapp.number:}")
    private String adminNumber;

    @Autowired
    public SalesService(
            InvoiceRepo invoiceRepo,
            CustomerRepo customerRepo,
            ProductRepo productRepo,
            PdfGeneratorService pdfGeneratorService,
            S3Service s3Service,
            TwilioService twilioService,
            TransactionRepo transactionRepository
    ) {
        this.invoiceRepo = invoiceRepo;
        this.customerRepo = customerRepo;
        this.productRepo = productRepo;
        this.pdfGeneratorService = pdfGeneratorService;
        this.s3Service = s3Service;
        this.twilioService = twilioService;
        this.transactionRepository = transactionRepository;
    }

    /**
     * Create sale invoice:
     *  - Save customer if new
     *  - Calculate per-item totals including discount and GST (using product.taxRate)
     *  - Save invoice + items
     *  - Update product.currentStock and save a Transaction per item
     *  - Generate PDF, upload to S3, send WhatsApp (best effort)
     */
    public Invoice createSale(Invoice invoice) {

        // 1) Customer handling: save new customer first to avoid transient exception
        if (invoice.getCustomer() != null) {
            Customer c = invoice.getCustomer();
            if (c.getId() == null) {
                Customer saved = customerRepo.save(c);
                invoice.setCustomer(saved);
            } else {
                // ensure exists
                Customer existing = customerRepo.findById(Math.toIntExact(c.getId()))
                        .orElseThrow(() -> new RuntimeException("Customer not found"));
                invoice.setCustomer(existing);
            }
        }

        // 2) Invoice metadata
        invoice.setInvoiceNo("INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        invoice.setDate(new Date());
        invoice.setStatus("UNPAID");

        double grandTotal = 0.0;

        for (InvoiceItem item : invoice.getItems()) {

            Product product = productRepo.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + item.getProductId()));

            BigDecimal qty = BigDecimal.valueOf(item.getQty());
            BigDecimal price = BigDecimal.valueOf(item.getPrice());
            BigDecimal base = qty.multiply(price);

            BigDecimal discountPercent = BigDecimal.valueOf(item.getDiscount());
            BigDecimal discountAmount = base.multiply(discountPercent)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            BigDecimal taxable = base.subtract(discountAmount);

            BigDecimal gstRate = BigDecimal.valueOf(product.getTaxRate());
            BigDecimal gstAmount = taxable.multiply(gstRate)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            BigDecimal lineTotal = taxable.add(gstAmount);

            // Set computed fields in InvoiceItem
            item.setTaxableAmount(taxable.doubleValue());
            item.setGstPercentage(gstRate.doubleValue());
            item.setGstAmount(gstAmount.doubleValue());
            item.setTotalWithGst(lineTotal.doubleValue());

            item.setTotal(lineTotal.doubleValue());
            item.setInvoice(invoice);

            grandTotal += lineTotal.doubleValue();
        }




        // 4) Save invoice (and items via cascade)
        Invoice savedInvoice = invoiceRepo.save(invoice);

        // 5) Update stock & create transaction per item
        for (InvoiceItem item : savedInvoice.getItems()) {

            Integer productId = item.getProductId();
            Product product = productRepo.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found: " + productId));

            // Ensure currentStock is initialized
            if (product.getCurrentStock() == null) {
                product.setCurrentStock(product.getInitialStock() == null ? 0 : product.getInitialStock());
            }

            int qty = item.getQty();
            if (product.getCurrentStock() < qty) {
                throw new RuntimeException("Insufficient stock for product ID " + productId);
            }

            // reduce stock and save
            product.setCurrentStock(product.getCurrentStock() - qty);
            productRepo.save(product);

            // Create transaction record
            // Create transaction record
            Transaction tx = new Transaction();
            tx.setProduct(product);
            tx.setQuantity(qty);

// SALE DATE
            tx.setDate(LocalDate.from(LocalDateTime.now()));

// CUSTOMER NAME
            if (savedInvoice.getCustomer() != null) {
                tx.setName(savedInvoice.getCustomer().getName());
            } else {
                tx.setName("Unknown");
            }

            tx.setPricePerUnit((int) Math.round(item.getPrice()));
            tx.setTotalAmount((int) Math.round(item.getTotal()));
            tx.setType(TransactionType.SALE);

            tx.setReferenceId(savedInvoice.getId());
            tx.setInvoiceNumber(savedInvoice.getInvoiceNo());

            tx.setStatus("COMPLETED");

            transactionRepository.save(tx);



            // optional: low stock alert (you might want to notify)
            if (product.getCurrentStock() <= lowStockThreshold) {
                // you can add notification logic here, e.g. send to adminNumber via Twilio (if configured)
            }
        }

        // 6) Generate PDF, upload and send WhatsApp (best-effort)
        try {
            String pdfPath = pdfGeneratorService.generateInvoicePdf(savedInvoice);
            File pdfFile = new File(pdfPath);

            String fileUrl = s3Service.uploadFile(pdfFile);
            twilioService.sendPdf(fileUrl);

            System.out.println("✅ Invoice sent on WhatsApp: " + fileUrl);

        } catch (Exception e) {
            // Do not fail the whole sale just because PDF/S3/Twilio failed
            System.out.println("⚠ WhatsApp or S3 failed: " + e.getMessage());
        }

        return savedInvoice;
    }

    // helper methods for reading invoices, etc.
    public List<Invoice> getAllSales() {
        return invoiceRepo.findAll();
    }

    public Invoice getInvoice(Long id) {
        // InvoiceRepo uses Integer id in your repo; convert safely
        return invoiceRepo.findById(Math.toIntExact(id)).orElseThrow(() -> new RuntimeException("Invoice not found"));
    }

    public Customer saveCustomer(Customer c) {
        return customerRepo.save(c);
    }

    public List<Customer> getAllCustomers() {
        return customerRepo.findAll();
    }
}
