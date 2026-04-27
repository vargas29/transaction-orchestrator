package co.com.tumipay.infrastructure.adapter.in.http.mapper;

import co.com.tumipay.domain.model.Customer;
import co.com.tumipay.domain.model.Transaction;
import co.com.tumipay.infrastructure.adapter.in.http.dto.CreateTransactionRequest;
import co.com.tumipay.infrastructure.adapter.in.http.dto.TransactionResponseDto;
import org.springframework.stereotype.Component;

/**
 * Mapper manual para convertir entre DTOs HTTP y entidades de dominio.
 * Se implementó manualmente para evitar problemas con MapStruct + Lombok
 * cuando los nombres de propiedades contienen guiones bajos.
 */

@Component
public class TransactionMapper {

    public Transaction toDomain(CreateTransactionRequest request) {
        if (request == null) return null;

        Customer customer = toDomain(request.getCustomer());

        Transaction tx = new Transaction(
                request.getClientTransactionId(),
                request.getAmountInCents(),
                request.getCurrencyCode(),
                request.getCountryCode(),
                request.getPaymentMethodId(),
                customer,
                request.getWebhookUrl(),
                request.getRedirectUrl()
        );

        tx.setTransactionDescription(request.getTransactionDescription());
        tx.setLinkExpirationTime(request.getLinkExpirationTime());

        return tx;
    }

    public TransactionResponseDto toResponse(Transaction domain) {
        if (domain == null) return null;

        return TransactionResponseDto.builder()
                .id(domain.getId())
                .client_transaction_id(domain.getClientTransactionId())
                .amount_in_cents(domain.getAmountInCents())
                .currency_code(domain.getCurrencyCode())
                .country_code(domain.getCountryCode())
                .payment_method_id(domain.getPaymentMethodId())
                .transaction_description(domain.getTransactionDescription())
                .processing_date(domain.getProcessingDate())
                .status(domain.getStatus() != null ? domain.getStatus().name() : null)
                .build();
    }

    public Customer toDomain(CreateTransactionRequest.CustomerRequest customerRequest) {
        if (customerRequest == null) return null;

        Customer customer = new Customer(
                customerRequest.getDocumentType(),
                customerRequest.getDocumentNumber(),
                customerRequest.getCountryCallingCode(),
                customerRequest.getPhoneNumber(),
                customerRequest.getEmail(),
                customerRequest.getFirstName(),
                customerRequest.getFirstSurname()
        );
        customer.setSecond_name(customerRequest.getSecondName());
        customer.setSecond_surname(customerRequest.getSecondSurname());
        return customer;
    }
}