package co.com.tumipay.domain;

public enum PaymentMethod {
    PAYU,
    STRIPE;

    public static PaymentMethod from(String value) {
        try {
            return PaymentMethod.valueOf(value.toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Unsupported payment method: " + value);
        }
    }
}