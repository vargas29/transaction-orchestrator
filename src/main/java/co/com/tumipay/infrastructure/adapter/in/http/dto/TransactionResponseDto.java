package co.com.tumipay.infrastructure.adapter.in.http.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponseDto {

    // ID generado por el sistema
    @JsonProperty("transaction_id")
    private String id;

    // ID enviado por el cliente
    @JsonProperty("client_transaction_id")
    private String client_transaction_id;

    @JsonProperty("amount_in_cents")
    private Long amount_in_cents;

    @JsonProperty("currency_code")
    private String currency_code;

    @JsonProperty("country_code")
    private String country_code;

    @JsonProperty("payment_method_id")
    private String payment_method_id;

    @JsonProperty("transaction_description")
    private String transaction_description;

    @JsonProperty("processing_date")
    private LocalDateTime processing_date;

    @JsonProperty("status")
    private String status;
}