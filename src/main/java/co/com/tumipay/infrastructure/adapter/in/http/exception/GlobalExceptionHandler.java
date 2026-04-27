package co.com.tumipay.infrastructure.adapter.in.http.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.List;

/**
 * Manejador global de excepciones para API
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    // 🔥 VALIDACIONES
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex) {

        List<ErrorResponse.FieldErrorDetail> errors =
                ex.getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .map(error -> ErrorResponse.FieldErrorDetail.builder()
                                .field(toSnakeCase(error.getField()))
                                .message(error.getDefaultMessage())
                                .build())
                        .toList();

        ErrorResponse response = ErrorResponse.builder()
                .response_code("001")
                .response_message("Validation error")
                .errors(errors)
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    // 🔥 NEGOCIO
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex) {

        ErrorResponse response = ErrorResponse.builder()
                .response_code("002")
                .response_message(ex.getMessage())
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    // 🔥 GENERAL
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {

        ErrorResponse response = ErrorResponse.builder()
                .response_code("999")
                .response_message("Internal server error")
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    // 🔧 helper
    private String toSnakeCase(String value) {
        return value.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }
}