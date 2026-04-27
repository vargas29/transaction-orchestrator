package co.com.tumipay.infrastructure.adapter.in.http;

import co.com.tumipay.application.port.in.CreateTransactionUseCase;
import co.com.tumipay.application.port.in.GetTransactionUseCase;
import co.com.tumipay.domain.model.Customer;
import co.com.tumipay.domain.model.Transaction;
import co.com.tumipay.infrastructure.adapter.in.http.dto.CreateTransactionRequest;
import co.com.tumipay.infrastructure.adapter.in.http.mapper.TransactionMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests de integración para TransactionController
 */
@SpringBootTest
@AutoConfigureMockMvc
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CreateTransactionUseCase createTransactionUseCase;

    @MockBean
    private GetTransactionUseCase getTransactionUseCase;

    @Autowired
    private TransactionMapper mapper;

    private CreateTransactionRequest validRequest;
    private Transaction mockTransaction;

    @BeforeEach
    void setUp() {
        // Preparar request válido
        validRequest = CreateTransactionRequest.builder()
                .transaction_id("TRX-001-001")
                .amount_in_cents(100000L)
                .currency_code("USD")
                .country_code("CO")
                .payment_method_id("PAYU_CREDIT_CARD")
                .webhook_url("https://example.com/webhook")
                .redirect_url("https://example.com/success")
                .transaction_description("Compra test")
                .link_expiration_time(3600L)
                .customer(CreateTransactionRequest.CustomerRequest.builder()
                        .document_type("CC")
                        .document_number("1234567890")
                        .country_calling_code("+57")
                        .phone_number("3001234567")
                        .email("test@example.com")
                        .first_name("Juan")
                        .first_surname("Pérez")
                        .build())
                .build();

        // Preparar transacción simulada
        Customer customer = new Customer("CC", "1234567890", "+57", "3001234567",
                "test@example.com", "Juan", "Pérez");
        mockTransaction = new Transaction("TRX-001-001", 100000L, "USD", "CO",
                "PAYU_CREDIT_CARD", customer, "https://example.com/webhook",
                "https://example.com/success");
        mockTransaction.setId("1");
        mockTransaction.setStatus(Transaction.TransactionStatus.COMPLETED);
    }

    @Test
    void testCreateTransactionSuccess() throws Exception {
        // Arrange
        when(createTransactionUseCase.execute(any(Transaction.class)))
                .thenReturn(mockTransaction);

        // Act & Assert
        mockMvc.perform(post("/api/v1/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJsonString(validRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.response_code").value("000"))
                .andExpect(jsonPath("$.response_message").value("Successful operation"))
                .andExpect(jsonPath("$.data.transaction_id").value("TRX-001-001"))
                .andExpect(jsonPath("$.data.amount_in_cents").value(100000))
                .andExpect(jsonPath("$.data.status").value("COMPLETED"));
    }

    @Test
    void testCreateTransactionMissingRequiredField() throws Exception {
        // Arrange: Remover campo requerido
        CreateTransactionRequest invalidRequest = CreateTransactionRequest.builder()
                .transaction_id("TRX-001-001")
                // amount_in_cents falta
                .currency_code("USD")
                .country_code("CO")
                .payment_method_id("PAYU_CREDIT_CARD")
                .webhook_url("https://example.com/webhook")
                .redirect_url("https://example.com/success")
                .customer(validRequest.getCustomer())
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/v1/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJsonString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.response_code").value("001"))
                .andExpect(jsonPath("$.response_message").value("Validation error"));
    }

    @Test
    void testCreateTransactionInvalidCurrencyCode() throws Exception {
        // Arrange: Currency code inválido
        validRequest.setCurrency_code("USDD");

        // Act & Assert
        mockMvc.perform(post("/api/v1/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJsonString(validRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.response_code").value("001"));
    }

    @Test
    void testCreateTransactionNegativeAmount() throws Exception {
        // Arrange: Amount negativo
        validRequest.setAmount_in_cents(-100L);

        // Act & Assert
        mockMvc.perform(post("/api/v1/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJsonString(validRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.response_code").value("001"));
    }

    @Test
    void testCreateTransactionInvalidEmail() throws Exception {
        // Arrange: Email inválido
        validRequest.getCustomer().setEmail("invalid-email");

        // Act & Assert
        mockMvc.perform(post("/api/v1/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJsonString(validRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.response_code").value("001"));
    }

    @Test
    void testGetTransactionSuccess() throws Exception {
        // Arrange
        when(getTransactionUseCase.execute("TRX-001-001"))
                .thenReturn(mockTransaction);

        // Act & Assert
        mockMvc.perform(get("/api/v1/transactions/TRX-001-001"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response_code").value("000"))
                .andExpect(jsonPath("$.response_message").value("Successful operation"))
                .andExpect(jsonPath("$.data.transaction_id").value("TRX-001-001"));
    }

    @Test
    void testGetTransactionNotFound() throws Exception {
        // Arrange
        when(getTransactionUseCase.execute("NONEXISTENT"))
                .thenThrow(new IllegalArgumentException("Transacción no encontrada"));

        // Act & Assert
        mockMvc.perform(get("/api/v1/transactions/NONEXISTENT"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.response_code").value("002"));
    }

    @Test
    void testHealthCheck() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/transactions/health"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response_code").value("000"))
                .andExpect(jsonPath("$.data").value("Transaction Orchestrator is running"));
    }

    /**
     * Utilidad para convertir objeto a JSON
     */
    private String toJsonString(Object obj) throws Exception {
        return new com.fasterxml.jackson.databind.ObjectMapper()
                .writeValueAsString(obj);
    }
}

