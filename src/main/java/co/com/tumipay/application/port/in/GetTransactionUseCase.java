package co.com.tumipay.application.port.in;

import co.com.tumipay.domain.model.Transaction;

/**
 * Puerto de entrada (use case) para consultar transacciones
 */
public interface GetTransactionUseCase {
    
    /**
     * Obtiene una transacción por su ID
     */
    Transaction execute(String transactionId);
}

