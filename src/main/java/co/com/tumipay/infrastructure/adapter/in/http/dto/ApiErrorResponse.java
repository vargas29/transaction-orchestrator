package co.com.tumipay.infrastructure.adapter.in.http.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ApiErrorResponse {

    @JsonProperty("response_code")
    private String responseCode;

    @JsonProperty("response_message")
    private String responseMessage;

    @JsonProperty("errors")
    private List<FieldErrorDetail> errors;

    @Data
    @Builder
    public static class FieldErrorDetail {

        @JsonProperty("field")
        private String field;

        @JsonProperty("message")
        private String message;
    }
}