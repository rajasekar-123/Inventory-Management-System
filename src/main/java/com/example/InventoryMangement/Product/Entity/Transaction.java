package com.example.InventoryMangement.Product.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.time.LocalTime;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    // relation to a product (optional for invoice-level transactions)
    @ManyToOne
    @JoinColumn(name = "product_id")
    @JsonBackReference
    private Product product;

    // item-level details
    private Integer quantity;
    private Integer pricePerUnit;
    private Integer totalAmount; // consider BigDecimal if needed

    // reference to invoice/purchase master id
    private Long referenceId;

    // purchase-specific / business fields
    private LocalDate date;                // txn date
    private String name;                   // supplier or note
    private String invoiceNumber;
    private BigDecimal amountPaid;
    private BigDecimal balanceAmount;
    private String paymentType;
    private String status;
    private LocalTime time;


    public Transaction() {}

    // getters / setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public TransactionType getType() { return type; }
    public void setType(TransactionType type) { this.type = type; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Integer getPricePerUnit() { return pricePerUnit; }
    public void setPricePerUnit(Integer pricePerUnit) { this.pricePerUnit = pricePerUnit; }

    public Integer getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Integer totalAmount) { this.totalAmount = totalAmount; }

    public Long getReferenceId() { return referenceId; }
    public void setReferenceId(Long referenceId) { this.referenceId = referenceId; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public BigDecimal getAmountPaid() { return amountPaid; }
    public void setAmountPaid(BigDecimal amountPaid) { this.amountPaid = amountPaid; }

    public BigDecimal getBalanceAmount() { return balanceAmount; }
    public void setBalanceAmount(BigDecimal balanceAmount) { this.balanceAmount = balanceAmount; }

    public String getPaymentType() { return paymentType; }
    public void setPaymentType(String paymentType) { this.paymentType = paymentType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

}



/*@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Transaction 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer Id;
    
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    
    @Enumerated(EnumType.STRING)
    private TransactionType type;
    
    private String name;
    
    private LocalDate date;
    
    private Integer quantity;
    
    private Integer priceperunit;
    
    private String status;

	public Integer getId() {
		return Id;
	}

	public void setId(Integer id) {
		Id = id;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public TransactionType getType() {
		return type;
	}

	public void setType(TransactionType type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Integer getPriceperunit() {
		return priceperunit;
	}

	public void setPriceperunit(Integer priceperunit) {
		this.priceperunit = priceperunit;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "Transaction [Id=" + Id + ", product=" + product + ", type=" + type + ", name=" + name + ", date=" + date
				+ ", quantity=" + quantity + ", priceperunit=" + priceperunit + ", status=" + status + "]";
	}



}
*/