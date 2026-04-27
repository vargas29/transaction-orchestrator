package co.com.tumipay.infrastructure.adapter.out.persistence.repository;

import co.com.tumipay.infrastructure.adapter.out.persistence.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio Spring Data JPA para transacciones
 */
@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {
    
    /**
     * Busca una transacción por su transaction_id único
     */
    Optional<TransactionEntity> findByTransactionId(String transactionId);
}

