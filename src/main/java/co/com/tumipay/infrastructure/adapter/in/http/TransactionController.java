package co.com.tumipay.infrastructure.adapter.in.http;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import co.com.tumipay.application.port.in.CreateTransactionUseCase;
import co.com.tumipay.application.port.in.GetTransactionUseCase;
import co.com.tumipay.domain.model.Transaction;
import co.com.tumipay.infrastructure.adapter.in.http.dto.ApiResponse;
import co.com.tumipay.infrastructure.adapter.in.http.dto.CreateTransactionRequest;
import co.com.tumipay.infrastructure.adapter.in.http.dto.TransactionResponseDto;
import co.com.tumipay.infrastructure.adapter.in.http.mapper.TransactionMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST adaptador de entrada (inbound)
 * Expone los endpoints para crear y consultar transacciones
 */
@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final CreateTransactionUseCase createTransactionUseCase;
    private final GetTransactionUseCase getTransactionUseCase;
    private final TransactionMapper mapper;

    /**
     * POST /api/v1/transactions
     * Crear una nueva transacción
     *
     * @param request DTO con los datos de la transacción
     * @return Respuesta con datos de la transacción creada
     */
    @PostMapping
    public ResponseEntity<ApiResponse<TransactionResponseDto>> createTransaction(
            @Valid @RequestBody CreateTransactionRequest request) {
        
        // Convertir DTO a entidad de dominio
        Transaction transaction = mapper.toDomain(request);
        
        // Ejecutar el caso de uso
        Transaction createdTransaction = createTransactionUseCase.execute(transaction);
        
        // Convertir entidad a DTO de respuesta
        TransactionResponseDto response = mapper.toResponse(createdTransaction);
        
        // Retornar respuesta exitosa
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }

    /**
     * GET /api/v1/transactions/{transaction_id}
     * Consultar una transacción por su ID
     *
     * @param transactionId ID de la transacción a consultar
     * @return Respuesta con datos de la transacción
     */
    @GetMapping("/{transaction_id}")
    public ResponseEntity<ApiResponse<TransactionResponseDto>> getTransaction(
            @PathVariable("transaction_id") String transactionId) {
        
        // Ejecutar el caso de uso
        Transaction transaction = getTransactionUseCase.execute(transactionId);
        
        // Convertir entidad a DTO de respuesta
        TransactionResponseDto response = mapper.toResponse(transaction);
        
        // Retornar respuesta exitosa
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.success("Transaction Orchestrator is running"));
    }
}

