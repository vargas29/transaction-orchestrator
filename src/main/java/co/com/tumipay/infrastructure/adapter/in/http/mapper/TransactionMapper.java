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
                request.getTransaction_id(),
                request.getAmount_in_cents(),
                request.getCurrency_code(),
                request.getCountry_code(),
                request.getPayment_method_id(),
                customer,
                request.getWebhook_url(),
                request.getRedirect_url()
        );

        tx.setTransactionDescription(request.getTransaction_description());
        tx.setLinkExpirationTime(request.getLink_expiration_time());

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
                customerRequest.getDocument_type(),
                customerRequest.getDocument_number(),
                customerRequest.getCountry_calling_code(),
                customerRequest.getPhone_number(),
                customerRequest.getEmail(),
                customerRequest.getFirst_name(),
                customerRequest.getFirst_surname()
        );
        customer.setSecond_name(customerRequest.getSecond_name());
        customer.setSecond_surname(customerRequest.getSecond_surname());
        return customer;
    }
}