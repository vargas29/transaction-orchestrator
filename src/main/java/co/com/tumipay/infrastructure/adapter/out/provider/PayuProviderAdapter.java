package co.com.tumipay.infrastructure.adapter.out.provider;

import co.com.tumipay.application.port.out.PaymentProviderPort;
import co.com.tumipay.domain.PaymentMethod;
import org.springframework.stereotype.Component;
import java.util.UUID;

/**
 * Adaptador para Payu
 */
@Component
public class PayuProviderAdapter implements PaymentProviderPort {

    @Override
    public PaymentMethod supports() {
        return PaymentMethod.PAYU;
    }

    @Override
    public String processPayment(String transactionId, Long amountInCents, String paymentMethodId) {
        return "PAYU_" + UUID.randomUUID();
    }

    @Override
    public boolean refundPayment(String transactionId) {
        return true;
    }
}

