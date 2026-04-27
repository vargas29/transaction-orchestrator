package co.com.tumipay.infrastructure.adapter.in.http.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para respuesta de error estandarizada
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {
    private String response_code;
    private String response_message;
    private List<FieldErrorDetail> errors;

    @Data
    @Builder
    public static class FieldErrorDetail {
        private String field;
        private String message;
    }
}