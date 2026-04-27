package co.com.tumipay.application.port.in;

import co.com.tumipay.domain.model.Transaction;

/**
 * Puerto de entrada (use case) para crear transacciones
 */
public interface CreateTransactionUseCase {
    
    /**
     * Crea una nueva transacción
     */
    Transaction execute(Transaction transaction);
}

