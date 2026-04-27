package co.com.tumipay.application.port.out;

public interface EventPublisherPort {
    void publish(Object event, String topic);
}

