package co.com.tumipay.infrastructure.adapter.in.http.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para solicitud de creación de transacción.
 * Cumple con especificación de campos snake_case y validaciones obligatorias.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateTransactionRequest {


    @JsonProperty("transaction_id")
    @NotBlank(message = "transaction_id es requerido")
    private String transaction_id;

    @JsonProperty("amount_in_cents")
    @NotNull(message = "amount_in_cents es requerido")
    @Positive(message = "amount_in_cents debe ser positivo")
    private Long amount_in_cents;

    @JsonProperty("currency_code")
    @NotBlank(message = "currency_code es requerido")
    @Pattern(regexp = "^[A-Z]{3}$", message = "currency_code debe ser ISO 4217")
    private String currency_code;

    @JsonProperty("country_code")
    @NotBlank(message = "country_code es requerido")
    @Pattern(regexp = "^[A-Z]{2}$", message = "country_code debe ser ISO 3166")
    private String country_code;

    @JsonProperty("payment_method_id")
    @NotBlank(message = "payment_method_id es requerido")
    private String payment_method_id;

    @JsonProperty("webhook_url")
    @NotBlank(message = "webhook_url es requerido")
    @Pattern(regexp = "^https?://.*", message = "webhook_url debe ser válida")
    private String webhook_url;

    @JsonProperty("redirect_url")
    @NotBlank(message = "redirect_url es requerido")
    @Pattern(regexp = "^https?://.*", message = "redirect_url debe ser válida")
    private String redirect_url;

    @JsonProperty("customer")
    @NotNull(message = "customer es requerido")
    @Valid
    private CustomerRequest customer;

    // ✅ OPCIONALES

    @JsonProperty("transaction_description")
    private String transaction_description;

    @JsonProperty("link_expiration_time")
    private Long link_expiration_time;

    // =========================================================
    // 🧩 CUSTOMER
    // =========================================================

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CustomerRequest {

        @JsonProperty("document_type")
        @NotBlank(message = "customer.document_type es requerido")
        private String document_type;

        @JsonProperty("document_number")
        @NotBlank(message = "customer.document_number es requerido")
        private String document_number;

        @JsonProperty("country_calling_code")
        @NotBlank(message = "customer.country_calling_code es requerido")
        private String country_calling_code;

        @JsonProperty("phone_number")
        @NotBlank(message = "customer.phone_number es requerido")
        @Pattern(regexp = "^\\d+$", message = "customer.phone_number debe ser numérico")
        private String phone_number;

        @JsonProperty("email")
        @NotBlank(message = "customer.email es requerido")
        @Email(message = "customer.email debe ser válido")
        private String email;

        @JsonProperty("first_name")
        @NotBlank(message = "customer.first_name es requerido")
        private String first_name;

        @JsonProperty("second_name")
        private String second_name;

        @JsonProperty("first_surname")
        @NotBlank(message = "customer.first_surname es requerido")
        private String first_surname;

        @JsonProperty("second_surname")
        private String second_surname;
    }
}

