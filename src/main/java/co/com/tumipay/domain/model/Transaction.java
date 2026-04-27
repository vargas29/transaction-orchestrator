package co.com.tumipay.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad de dominio Transaction
 */
public class Transaction {

    private String id;
    private String clientTransactionId;
    private Long amountInCents;
    private String currencyCode;
    private String countryCode;
    private String paymentMethodId;
    private String transactionDescription;
    private Customer customer;
    private TransactionStatus status;
    private LocalDateTime processingDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String webhookUrl;
    private String redirectUrl;
    private Long linkExpirationTime;

    public enum TransactionStatus {
        PENDING, PROCESSING, COMPLETED, FAILED, CANCELLED, REFUNDED
    }

    public Transaction() {
        this.id = UUID.randomUUID().toString();
        this.status = TransactionStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public Transaction(String clientTransactionId, Long amountInCents, String currencyCode,
                       String countryCode, String paymentMethodId, Customer customer,
                       String webhookUrl, String redirectUrl) {
        this();
        this.clientTransactionId = clientTransactionId;
        this.amountInCents = amountInCents;
        this.currencyCode = currencyCode;
        this.countryCode = countryCode;
        this.paymentMethodId = paymentMethodId;
        this.customer = customer;
        this.webhookUrl = webhookUrl;
        this.redirectUrl = redirectUrl;
        this.processingDate = LocalDateTime.now();
    }

    public void validate() {
        if (clientTransactionId == null || clientTransactionId.isBlank()) {
            throw new IllegalArgumentException("Client transaction id is required");
        }
        if (amountInCents == null || amountInCents <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        if (currencyCode == null || !currencyCode.matches("^[A-Z]{3}$")) {
            throw new IllegalArgumentException("Invalid currency code");
        }
        if (countryCode == null || !countryCode.matches("^[A-Z]{2}$")) {
            throw new IllegalArgumentException("Invalid country code");
        }
        if (paymentMethodId == null || paymentMethodId.isBlank()) {
            throw new IllegalArgumentException("Payment method is required");
        }
        if (customer == null) {
            throw new IllegalArgumentException("Customer is required");
        }

        customer.validate();
    }

    // 🔥 Encapsulación de estados
    public void markAsProcessing() {
        this.status = TransactionStatus.PROCESSING;
        this.processingDate = LocalDateTime.now();
    }

    public void markAsCompleted() {
        this.status = TransactionStatus.COMPLETED;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsFailed() {
        this.status = TransactionStatus.FAILED;
        this.updatedAt = LocalDateTime.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getClientTransactionId() { return clientTransactionId; }
    public void setClientTransactionId(String clientTransactionId) { this.clientTransactionId = clientTransactionId; }

    public Long getAmountInCents() { return amountInCents; }
    public void setAmountInCents(Long amountInCents) { this.amountInCents = amountInCents; }

    public String getCurrencyCode() { return currencyCode; }
    public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }

    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }

    public String getPaymentMethodId() { return paymentMethodId; }
    public void setPaymentMethodId(String paymentMethodId) { this.paymentMethodId = paymentMethodId; }

    public String getTransactionDescription() { return transactionDescription; }
    public void setTransactionDescription(String transactionDescription) { this.transactionDescription = transactionDescription; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public TransactionStatus getStatus() { return status; }
    public void setStatus(TransactionStatus status) { this.status = status; }

    public LocalDateTime getProcessingDate() { return processingDate; }
    public void setProcessingDate(LocalDateTime processingDate) { this.processingDate = processingDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getWebhookUrl() { return webhookUrl; }
    public void setWebhookUrl(String webhookUrl) { this.webhookUrl = webhookUrl; }

    public String getRedirectUrl() { return redirectUrl; }
    public void setRedirectUrl(String redirectUrl) { this.redirectUrl = redirectUrl; }

    public Long getLinkExpirationTime() { return linkExpirationTime; }
    public void setLinkExpirationTime(Long linkExpirationTime) { this.linkExpirationTime = linkExpirationTime; }
}