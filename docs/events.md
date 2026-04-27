Eventos emitidos por Transaction Orchestrator

Topics sugeridos (Kafka):
- transactions.created
- transactions.completed
- transactions.failed

Eventos (payload JSON):

TransactionCreatedEvent
{
  "transactionId": "uuid-system",
  "clientTransactionId": "TRX-001-001",
  "amountInCents": 100000,
  "currencyCode": "USD",
  "paymentMethodId": "PAYU_CREDIT_CARD",
  "createdAt": "2026-04-27T12:00:00"
}

TransactionCompletedEvent
{
  "transactionId": "uuid-system",
  "clientTransactionId": "TRX-001-001",
  "processedAt": "2026-04-27T12:00:10",
  "providerReference": "PAYU-123456"
}

TransactionFailedEvent
{
  "transactionId": "uuid-system",
  "clientTransactionId": "TRX-001-001",
  "failedAt": "2026-04-27T12:00:05",
  "reason": "Timeout connecting to provider"
}

Cómo consumir
- Consumidores Kafka pueden suscribirse a los topics indicados.
- Los eventos usan serialización JSON (Spring Kafka `JsonSerializer`).

Notas
- Mantener baja cardinalidad en tags/keys.
- Garantizar idempotencia en consumidores (usar `clientTransactionId`).

