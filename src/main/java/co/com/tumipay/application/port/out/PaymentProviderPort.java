package co.com.tumipay.application.port.out;

import co.com.tumipay.domain.PaymentMethod;

/**
 * Puerto de salida para proveedores de pago
 */
public interface PaymentProviderPort {
    
    /**
     * Procesa un pago
     */
    String processPayment(String transactionId, Long amountInCents, String paymentMethodId);
    
    /**
     * Reembolsa un pago
     */
    boolean refundPayment(String transactionId);  PaymentMethod supports();

}

