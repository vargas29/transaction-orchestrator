package co.com.tumipay.application.service;

import co.com.tumipay.infrastructure.adapter.out.provider.PaymentProviderFactory;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import co.com.tumipay.application.port.in.CreateTransactionUseCase;
import co.com.tumipay.application.port.in.GetTransactionUseCase;
import co.com.tumipay.application.port.out.PaymentProviderPort;
import co.com.tumipay.application.port.out.TransactionRepositoryPort;
import co.com.tumipay.application.port.out.EventPublisherPort;
import co.com.tumipay.domain.model.Transaction;
import org.springframework.stereotype.Service;

/**
 * Servicio de aplicación que orquesta la creación y consulta de transacciones
 */
@Service
@RequiredArgsConstructor
public class TransactionApplicationService implements CreateTransactionUseCase, GetTransactionUseCase {

    private final TransactionRepositoryPort repositoryPort;
    private final PaymentProviderFactory providerFactory;
    private final MeterRegistry meterRegistry;
    private final EventPublisherPort eventPublisher;

    // Métricas
    private Counter totalTransactions;
    private Counter successTransactions;
    private Counter failedTransactions;
    private Timer processingTimer;

    @Override
    public Transaction execute(Transaction transaction) {

        // 1️⃣ Validación de negocio
        transaction.validate();

        // Métrica: total recibido
        if (totalTransactions != null) totalTransactions.increment();

        // 2️⃣ Idempotencia
        Transaction existing = repositoryPort.findByClientTransactionId(
                transaction.getClientTransactionId()
        );

        if (existing != null) {
            return existing;
        }

        // 3️⃣ Cambiar estado
        transaction.markAsProcessing();

        // 4️⃣ Persistir antes de procesar (importante)
        Transaction savedTransaction = repositoryPort.save(transaction);

        // Publicar evento creado
        eventPublisher.publish(
                new co.com.tumipay.application.event.TransactionCreatedEvent(
                        savedTransaction.getId(),
                        savedTransaction.getClientTransactionId(),
                        savedTransaction.getAmountInCents(),
                        savedTransaction.getCurrencyCode(),
                        savedTransaction.getPaymentMethodId(),
                        savedTransaction.getCreatedAt()
                ),
                "transactions.created"
        );

        try {
            // 5️⃣ Obtener provider dinámicamente
            PaymentProviderPort provider =
                    providerFactory.getProvider(transaction.getPaymentMethodId());

            // 6️⃣ Ejecutar pago (medir tiempo)
            processingTimer.record(() -> {
                String providerResponse = provider.processPayment(
                        transaction.getClientTransactionId(),
                        transaction.getAmountInCents(),
                        transaction.getPaymentMethodId()
                );

                // Métrica: contador por provider (usa tag 'provider')
                meterRegistry.counter("transactions_by_provider", "provider", transaction.getPaymentMethodId())
                        .increment();

                // (opcional) guardar referencia del proveedor
                // savedTransaction.setProviderReference(providerResponse);
            });

            // 7️⃣ Estado exitoso
            savedTransaction.markAsCompleted();
            if (successTransactions != null) successTransactions.increment();

            // Publicar evento completado
            eventPublisher.publish(
                    new co.com.tumipay.application.event.TransactionCompletedEvent(
                            savedTransaction.getId(),
                            savedTransaction.getClientTransactionId(),
                            savedTransaction.getProcessingDate(),
                            null
                    ),
                    "transactions.completed"
            );

        } catch (Exception e) {

            // 8️⃣ Estado fallido
            savedTransaction.markAsFailed();
            if (failedTransactions != null) failedTransactions.increment();

            // Publicar evento fallido
            eventPublisher.publish(
                    new co.com.tumipay.application.event.TransactionFailedEvent(
                            savedTransaction.getId(),
                            savedTransaction.getClientTransactionId(),
                            java.time.LocalDateTime.now(),
                            e.getMessage()
                    ),
                    "transactions.failed"
            );

            // 🔥 (opcional recomendado)
            // log.error("Error procesando pago", e);
        }

        // 9️⃣ Persistir resultado final
        return repositoryPort.update(savedTransaction);
    }

    @PostConstruct
    private void initMetrics() {
        this.totalTransactions = Counter.builder("transactions_total")
                .description("Total number of transactions processed")
                .register(meterRegistry);

        this.successTransactions = Counter.builder("transactions_success_total")
                .description("Total successful transactions")
                .register(meterRegistry);

        this.failedTransactions = Counter.builder("transactions_failed_total")
                .description("Total failed transactions")
                .register(meterRegistry);

        this.processingTimer = Timer.builder("transactions_processing_time_seconds")
                .description("Time spent processing transactions")
                .register(meterRegistry);
    }

    // 🔎 Consulta
    @Override
    public Transaction execute(String transactionId) {

        Transaction transaction = repositoryPort.findById(transactionId);

        if (transaction == null) {
            throw new IllegalArgumentException(
                    "Transaction not found: " + transactionId
            );
        }

        return transaction;
    }
}