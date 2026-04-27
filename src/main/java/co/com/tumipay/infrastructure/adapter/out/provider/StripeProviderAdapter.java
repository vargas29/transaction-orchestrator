package co.com.tumipay.infrastructure.adapter.out.provider;

import org.springframework.stereotype.Component;
import java.util.UUID;

/**
 * Adaptador para Stripe
 */
@Component
public class StripeProviderAdapter {

    public String processPayment(String transactionId, Long amountInCents, String paymentMethodId) {
        // Simulación de procesamiento con Stripe
        String stripeTransactionId = "STRIPE_" + UUID.randomUUID().toString();

        System.out.println("[STRIPE] Procesando pago:");
        System.out.println("  - Transaction ID: " + transactionId);
        System.out.println("  - Amount: " + amountInCents + " centavos");
        System.out.println("  - Stripe ID: " + stripeTransactionId);

        return stripeTransactionId;
    }

    public boolean refundPayment(String transactionId) {
        System.out.println("[STRIPE] Procesando reembolso para: " + transactionId);
        return true;
    }
}

