package co.com.tumipay.application.event;

import java.time.LocalDateTime;

public class TransactionCreatedEvent {
    private String transactionId;
    private String clientTransactionId;
    private Long amountInCents;
    private String currencyCode;
    private String paymentMethodId;
    private LocalDateTime createdAt;

    public TransactionCreatedEvent() {}

    public TransactionCreatedEvent(String transactionId, String clientTransactionId, Long amountInCents, String currencyCode, String paymentMethodId, LocalDateTime createdAt) {
        this.transactionId = transactionId;
        this.clientTransactionId = clientTransactionId;
        this.amountInCents = amountInCents;
        this.currencyCode = currencyCode;
        this.paymentMethodId = paymentMethodId;
        this.createdAt = createdAt;
    }

    // getters and setters
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    public String getClientTransactionId() { return clientTransactionId; }
    public void setClientTransactionId(String clientTransactionId) { this.clientTransactionId = clientTransactionId; }
    public Long getAmountInCents() { return amountInCents; }
    public void setAmountInCents(Long amountInCents) { this.amountInCents = amountInCents; }
    public String getCurrencyCode() { return currencyCode; }
    public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }
    public String getPaymentMethodId() { return paymentMethodId; }
    public void setPaymentMethodId(String paymentMethodId) { this.paymentMethodId = paymentMethodId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

