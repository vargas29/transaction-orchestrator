package co.com.tumipay.application.service;

import co.com.tumipay.application.port.out.EventPublisherPort;
import co.com.tumipay.application.port.out.TransactionRepositoryPort;
import co.com.tumipay.infrastructure.adapter.out.provider.PaymentProviderFactory;
import co.com.tumipay.application.port.out.PaymentProviderPort;
import co.com.tumipay.domain.model.Customer;
import co.com.tumipay.domain.model.Transaction;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionApplicationServiceTest {

    @Mock
    private TransactionRepositoryPort repositoryPort;

    @Mock
    private PaymentProviderFactory providerFactory;

    @Mock
    private PaymentProviderPort provider;

    @Mock
    private EventPublisherPort eventPublisher;

    private TransactionApplicationService service;

    private SimpleMeterRegistry meterRegistry;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        service = new TransactionApplicationService(repositoryPort, providerFactory, meterRegistry, eventPublisher);
    }

    @Test
    void execute_successfulProcessing_publishesEventsAndCompletes() {
        // Arrange
        Customer customer = new Customer("CC", "123", "+57", "3001234", "a@b.com", "Juan", "Perez");
        Transaction tx = new Transaction("TRX-1", 1000L, "USD", "CO", "PAYU", customer, "https://w", "https://r");

        when(repositoryPort.findByClientTransactionId(tx.getClientTransactionId())).thenReturn(null);
        when(repositoryPort.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));
        when(providerFactory.getProvider(tx.getPaymentMethodId())).thenReturn(provider);
        when(provider.processPayment(anyString(), anyLong(), anyString())).thenReturn("OK");
        when(repositoryPort.update(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        Transaction result = service.execute(tx);

        // Assert
        assertNotNull(result);
        assertEquals(Transaction.TransactionStatus.COMPLETED, result.getStatus());
        verify(repositoryPort, times(1)).save(any(Transaction.class));
        verify(providerFactory, times(1)).getProvider(tx.getPaymentMethodId());
        verify(provider, times(1)).processPayment(anyString(), anyLong(), anyString());
        verify(eventPublisher, atLeastOnce()).publish(any(), anyString());
    }

    @Test
    void execute_providerThrows_marksFailedAndPublishesFailedEvent() {
        // Arrange
        Customer customer = new Customer("CC", "123", "+57", "3001234", "a@b.com", "Juan", "Perez");
        Transaction tx = new Transaction("TRX-2", 2000L, "USD", "CO", "PAYU", customer, "https://w", "https://r");

        when(repositoryPort.findByClientTransactionId(tx.getClientTransactionId())).thenReturn(null);
        when(repositoryPort.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));
        when(providerFactory.getProvider(tx.getPaymentMethodId())).thenReturn(provider);
        when(provider.processPayment(anyString(), anyLong(), anyString())).thenThrow(new RuntimeException("down"));
        when(repositoryPort.update(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        Transaction result = service.execute(tx);

        // Assert
        assertNotNull(result);
        assertEquals(Transaction.TransactionStatus.FAILED, result.getStatus());
        verify(eventPublisher, atLeastOnce()).publish(any(), eq("transactions.failed"));
    }
}

