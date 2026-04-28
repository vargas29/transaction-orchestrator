package co.com.tumipay.infrastructure.adapter.out.persistence;

import co.com.tumipay.infrastructure.adapter.out.persistence.entity.TransactionEntity;
import co.com.tumipay.infrastructure.adapter.out.persistence.repository.TransactionRepository;
import co.com.tumipay.domain.model.Customer;
import co.com.tumipay.domain.model.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class TransactionPersistenceAdapterTest {

    @Mock
    private TransactionRepository transactionRepository;

    private TransactionPersistenceAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new TransactionPersistenceAdapter(transactionRepository);
    }

    @Test
    void save_and_findByClientTransactionId_roundtrip() {
        Customer customer = new Customer("CC","123","+57","300","a@b.com","J","P");
        Transaction tx = new Transaction("TRX-10", 500L, "USD", "CO", "PAYU", customer, "w", "r");

        when(transactionRepository.save(any(TransactionEntity.class))).thenAnswer(inv -> {
            TransactionEntity e = inv.getArgument(0);
            e.setId(UUID.randomUUID());
            return e;
        });

        when(transactionRepository.findByTransactionId(tx.getClientTransactionId())).thenReturn(Optional.empty());

        Transaction saved = adapter.save(tx);
        assertNotNull(saved);
        assertNotNull(saved.getId());
    }
}

