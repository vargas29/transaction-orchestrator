package co.com.tumipay.infrastructure.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import co.com.tumipay.application.port.out.TransactionRepositoryPort;
import co.com.tumipay.domain.model.Customer;
import co.com.tumipay.domain.model.Transaction;
import co.com.tumipay.infrastructure.adapter.out.persistence.entity.CustomerEntity;
import co.com.tumipay.infrastructure.adapter.out.persistence.entity.TransactionEntity;
import co.com.tumipay.infrastructure.adapter.out.persistence.repository.TransactionRepository;
import org.springframework.stereotype.Component;

/**
 * Adaptador de persistencia para transacciones usando Spring Data JPA
 */
@Component
@RequiredArgsConstructor
public class TransactionPersistenceAdapter implements TransactionRepositoryPort {

    private final TransactionRepository transactionRepository;

    @Override
    public Transaction save(Transaction transaction) {
        TransactionEntity entity = mapToEntity(transaction);
        TransactionEntity saved = transactionRepository.save(entity);
        return mapToDomain(saved);
    }

    @Override
    public Transaction findById(String transactionId) {
        return transactionRepository.findByTransactionId(transactionId)
                .map(this::mapToDomain)
                .orElse(null);
    }

    @Override
    public Transaction update(Transaction transaction) {
        TransactionEntity entity = mapToEntity(transaction);
        TransactionEntity updated = transactionRepository.save(entity);
        return mapToDomain(updated);
    }
    @Override
    public Transaction findByClientTransactionId(String clientTransactionId) {
        return transactionRepository.findByTransactionId(clientTransactionId)
                .map(this::mapToDomain)
                .orElse(null);
    }
    private TransactionEntity mapToEntity(Transaction domain) {
        CustomerEntity customerEntity = mapCustomerToEntity(domain.getCustomer());

        return TransactionEntity.builder()
                .transactionId(domain.getClientTransactionId())
                .amountInCents(domain.getAmountInCents())
                .currencyCode(domain.getCurrencyCode())
                .countryCode(domain.getCountryCode())
                .paymentMethodId(domain.getPaymentMethodId())
                .transactionDescription(domain.getTransactionDescription())
                .status(TransactionEntity.TransactionStatus.valueOf(domain.getStatus().name()))
                .processingDate(domain.getProcessingDate())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .webhookUrl(domain.getWebhookUrl())
                .redirectUrl(domain.getRedirectUrl())
                .linkExpirationTime(domain.getLinkExpirationTime())
                .customer(customerEntity)
                .build();
    }

    private Transaction mapToDomain(TransactionEntity entity) {
        Transaction transaction = new Transaction();
        transaction.setId(String.valueOf(entity.getId()));
        transaction.setClientTransactionId(entity.getTransactionId());
        transaction.setAmountInCents(entity.getAmountInCents());
        transaction.setCurrencyCode(entity.getCurrencyCode());
        transaction.setCountryCode(entity.getCountryCode());
        transaction.setPaymentMethodId(entity.getPaymentMethodId());
        transaction.setTransactionDescription(entity.getTransactionDescription());
        transaction.setStatus(Transaction.TransactionStatus.valueOf(entity.getStatus().name()));
        transaction.setProcessingDate(entity.getProcessingDate());
        transaction.setUpdatedAt(entity.getUpdatedAt());
        transaction.setWebhookUrl(entity.getWebhookUrl());
        transaction.setRedirectUrl(entity.getRedirectUrl());
        transaction.setLinkExpirationTime(entity.getLinkExpirationTime());
        transaction.setCustomer(mapCustomerToDomain(entity.getCustomer()));
        return transaction;
    }

    private CustomerEntity mapCustomerToEntity(Customer domain) {
        return CustomerEntity.builder()
                .documentType(domain.getDocument_type())
                .documentNumber(domain.getDocument_number())
                .countryCallingCode(domain.getCountry_calling_code())
                .phoneNumber(domain.getPhone_number())
                .email(domain.getEmail())
                .firstName(domain.getFirst_name())
                .secondName(domain.getSecond_name())
                .firstSurname(domain.getFirst_surname())
                .secondSurname(domain.getSecond_surname())
                .build();
    }

    private Customer mapCustomerToDomain(CustomerEntity entity) {
        Customer customer = new Customer();
        customer.setDocument_type(entity.getDocumentType());
        customer.setDocument_number(entity.getDocumentNumber());
        customer.setCountry_calling_code(entity.getCountryCallingCode());
        customer.setPhone_number(entity.getPhoneNumber());
        customer.setEmail(entity.getEmail());
        customer.setFirst_name(entity.getFirstName());
        customer.setSecond_name(entity.getSecondName());
        customer.setFirst_surname(entity.getFirstSurname());
        customer.setSecond_surname(entity.getSecondSurname());
        return customer;
    }
}

