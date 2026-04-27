package co.com.tumipay.application.port.out;

import co.com.tumipay.domain.model.Transaction;

/**
 * Puerto de salida para persistencia de transacciones
 */
public interface TransactionRepositoryPort {
    
    /**
     * Guarda una transacción
     */
    Transaction save(Transaction transaction);
    
    /**
     * Busca una transacción por ID
     */
    Transaction findById(String transactionId);
    
    /**
     * Actualiza una transacción
     */
    Transaction update(Transaction transaction);

    Transaction findByClientTransactionId(String clientTransactionId);
}

