package co.com.tumipay.application.event;

import java.time.LocalDateTime;

public class TransactionFailedEvent {
    private String transactionId;
    private String clientTransactionId;
    private LocalDateTime failedAt;
    private String reason;

    public TransactionFailedEvent() {}

    public TransactionFailedEvent(String transactionId, String clientTransactionId, LocalDateTime failedAt, String reason) {
        this.transactionId = transactionId;
        this.clientTransactionId = clientTransactionId;
        this.failedAt = failedAt;
        this.reason = reason;
    }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    public String getClientTransactionId() { return clientTransactionId; }
    public void setClientTransactionId(String clientTransactionId) { this.clientTransactionId = clientTransactionId; }
    public LocalDateTime getFailedAt() { return failedAt; }
    public void setFailedAt(LocalDateTime failedAt) { this.failedAt = failedAt; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}

