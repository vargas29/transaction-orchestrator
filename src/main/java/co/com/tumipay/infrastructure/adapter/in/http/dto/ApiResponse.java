package co.com.tumipay.infrastructure.adapter.in.http.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO genérico para respuestas de la API.
 * Utiliza generics para permitir diferentes tipos de datos en la respuesta.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T> {

    @JsonProperty("response_code")
    private String response_code;

    @JsonProperty("response_message")
    private String response_message;

    @JsonProperty("data")
    private T data;

    /**
     * Crea una respuesta exitosa (código 000)
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .response_code("000")
                .response_message("Successful operation")
                .data(data)
                .build();
    }

    /**
     * Crea una respuesta de error
     */
    public static <T> ApiResponse<T> error(String code, String message) {
        return ApiResponse.<T>builder()
                .response_code(code)
                .response_message(message)
                .data(null)
                .build();
    }
}

