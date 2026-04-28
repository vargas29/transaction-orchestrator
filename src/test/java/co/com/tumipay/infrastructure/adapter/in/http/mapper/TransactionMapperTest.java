package co.com.tumipay.infrastructure.adapter.in.http.mapper;

import co.com.tumipay.domain.model.Transaction;
import co.com.tumipay.infrastructure.adapter.in.http.dto.CreateTransactionRequest;
import co.com.tumipay.infrastructure.adapter.in.http.dto.TransactionResponseDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TransactionMapperTest {

    private final TransactionMapper mapper = new TransactionMapper();

    @Test
    void toDomain_and_back_mapping_isConsistent() {
        CreateTransactionRequest.CustomerRequest customerReq = CreateTransactionRequest.CustomerRequest.builder()
                .document_type("CC")
                .document_number("123")
                .country_calling_code("+57")
                .phone_number("3001234")
                .email("a@b.com")
                .first_name("Juan")
                .first_surname("Perez")
                .build();

        CreateTransactionRequest req = CreateTransactionRequest.builder()
                .transaction_id("TRX-1")
                .amount_in_cents(1000L)
                .currency_code("USD")
                .country_code("CO")
                .payment_method_id("PAYU")
                .webhook_url("https://w")
                .redirect_url("https://r")
                .customer(customerReq)
                .build();

        Transaction domain = mapper.toDomain(req);
        assertNotNull(domain);
        assertEquals("TRX-1", domain.getClientTransactionId());
        assertEquals(1000L, domain.getAmountInCents());

        TransactionResponseDto dto = mapper.toResponse(domain);
        assertNotNull(dto);
        assertEquals(domain.getId(), dto.getId());
        assertEquals(domain.getClientTransactionId(), dto.getClient_transaction_id());
    }
}

