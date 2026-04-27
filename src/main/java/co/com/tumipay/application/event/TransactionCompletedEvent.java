package co.com.tumipay.application.event;

import java.time.LocalDateTime;

public class TransactionCompletedEvent {
    private String transactionId;
    private String clientTransactionId;
    private LocalDateTime processedAt;
    private String providerReference;

    public TransactionCompletedEvent() {}

    public TransactionCompletedEvent(String transactionId, String clientTransactionId, LocalDateTime processedAt, String providerReference) {
        this.transactionId = transactionId;
        this.clientTransactionId = clientTransactionId;
        this.processedAt = processedAt;
        this.providerReference = providerReference;
    }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    public String getClientTransactionId() { return clientTransactionId; }
    public void setClientTransactionId(String clientTransactionId) { this.clientTransactionId = clientTransactionId; }
    public LocalDateTime getProcessedAt() { return processedAt; }
    public void setProcessedAt(LocalDateTime processedAt) { this.processedAt = processedAt; }
    public String getProviderReference() { return providerReference; }
    public void setProviderReference(String providerReference) { this.providerReference = providerReference; }
}

