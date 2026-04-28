package co.com.tumipay.infrastructure.adapter.out.provider;

import co.com.tumipay.application.port.out.PaymentProviderPort;
import co.com.tumipay.domain.PaymentMethod;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class PaymentProviderFactoryTest {

    @Test
    void factory_returnsProviderForKnownMethods() {
        PaymentProviderPort payu = new PayuProviderAdapter();
        PaymentProviderPort stripe = new StripeProviderAdapter();

        PaymentProviderFactory factory = new PaymentProviderFactory(Arrays.asList(payu, stripe));

        PaymentProviderPort pPayu = factory.getProvider(PaymentMethod.PAYU.name());
        assertNotNull(pPayu);
        assertEquals(PaymentMethod.PAYU, pPayu.supports());

        PaymentProviderPort pStripe = factory.getProvider(PaymentMethod.STRIPE.name());
        assertNotNull(pStripe);
        assertEquals(PaymentMethod.STRIPE, pStripe.supports());
    }
}

