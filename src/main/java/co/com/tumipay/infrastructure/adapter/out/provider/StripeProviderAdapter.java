package co.com.tumipay.infrastructure.adapter.out.provider;

import co.com.tumipay.application.port.out.PaymentProviderPort;
import co.com.tumipay.domain.PaymentMethod;
import org.springframework.stereotype.Component;
import java.util.UUID;

/**
 * Adaptador para Stripe
 */
@Component
public class StripeProviderAdapter implements PaymentProviderPort {

    @Override
    public PaymentMethod supports() {
        return PaymentMethod.STRIPE;
    }

    @Override
    public String processPayment(String transactionId, Long amountInCents, String paymentMethodId) {
        // Simulación de procesamiento con Stripe
        String stripeTransactionId = "STRIPE_" + UUID.randomUUID().toString();

        System.out.println("[STRIPE] Procesando pago:");
        System.out.println("  - Transaction ID: " + transactionId);
        System.out.println("  - Amount: " + amountInCents + " centavos");
        System.out.println("  - Stripe ID: " + stripeTransactionId);

        return stripeTransactionId;
    }

    @Override
    public boolean refundPayment(String transactionId) {
        System.out.println("[STRIPE] Procesando reembolso para: " + transactionId);
        return true;
    }
}

