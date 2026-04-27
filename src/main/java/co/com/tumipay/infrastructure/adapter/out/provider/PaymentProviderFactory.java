package co.com.tumipay.infrastructure.adapter.out.provider;

import co.com.tumipay.application.port.out.PaymentProviderPort;
import co.com.tumipay.domain.PaymentMethod;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class PaymentProviderFactory {

    private final Map<PaymentMethod, PaymentProviderPort> registry;

    public PaymentProviderFactory(List<PaymentProviderPort> providers) {

        this.registry = providers.stream()
                .collect(Collectors.toMap(
                        PaymentProviderPort::supports,
                        provider -> provider
                ));
    }

    public PaymentProviderPort getProvider(String paymentMethod) {

        PaymentMethod method = PaymentMethod.from(paymentMethod);

        PaymentProviderPort provider = registry.get(method);

        if (provider == null) {
            throw new IllegalArgumentException("Unsupported payment method: " + method);
        }

        return provider;
    }
}
