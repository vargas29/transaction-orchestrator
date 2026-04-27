# 🏗️ ARQUITECTURA DEL MICROSERVICIO

## 1. Visión General de la Arquitectura Hexagonal

La arquitectura hexagonal (también conocida como "puertos y adaptadores") proporciona una separación clara entre las regiones internas (lógica de negocio) y las regiones externas (interfaces e infraestructura).

```
═══════════════════════════════════════════════════════════════════════════
                        ARQUITECTURA HEXAGONAL
═══════════════════════════════════════════════════════════════════════════

        CAPA EXTERNA (ADAPTADORES DE ENTRADA)
        ════════════════════════════════════════
        
                    REST API Controller
                (TransactionController)
                           │
                           │ Validación
                           │ Mapeo DTO → Dominio
                           │
        ┌──────────────────▼──────────────────┐
        │    PUERTOS DE ENTRADA (INTERFACES)  │
        │  CreateTransactionUseCase            │
        │  GetTransactionUseCase               │
        └──────────────────┬──────────────────┘
                           │
        ═══════════════════════════════════════════════════════════════════
        
        CAPA INTERNA (LÓGICA DE NEGOCIO PURA)
        ═════════════════════════════════════════
        
        ┌─────────────────────────────────────────────────────────────┐
        │          APPLICATION SERVICE LAYER                          │
        │  TransactionApplicationService                              │
        │  ────────────────────────────────────────                   │
        │  - Orquesta los casos de uso                                │
        │  - Coordina entre puertos                                   │
        │  - Maneja transacciones                                     │
        └──────────────┬─────────────────────────────────────┬────────┘
                       │                                     │
        ┌──────────────▼──────────┐         ┌───────────────▼──────┐
        │   DOMAIN LAYER          │         │   DOMAIN LAYER       │
        │   ───────────────       │         │   ──────────────     │
        │   - Transaction         │         │   - Customer         │
        │   - Customer            │         │   - TransactionStatus│
        │   - Validaciones        │         │   - Business Rules   │
        │   - Lógica de negocio   │         │   - Exceptions       │
        └──────────────┬──────────┘         └───────────────┬──────┘
                       │                                     │
        ═══════════════════════════════════════════════════════════════════
        
        CAPA EXTERNA (ADAPTADORES DE SALIDA)
        ═════════════════════════════════════════════════════════════════
        
        ┌──────────────────────┐         ┌───────────────────┐
        │ PUERTO DE SALIDA     │         │ PUERTO DE SALIDA  │
        │ Repository Port      │         │ Payment Provider  │
        └──────────────┬───────┘         └─────────┬─────────┘
                       │                           │
        ┌──────────────▼──────────────┐  ┌────────▼──────────────┐
        │   PERSISTENCE ADAPTER       │  │ PAYMENT PROVIDER      │
        │   (JPA/Hibernate)           │  │ ADAPTER (FACTORY)     │
        │   ────────────────────      │  │ ──────────────────    │
        │   - TransactionEntity       │  │ - PayuAdapter         │
        │   - CustomerEntity          │  │ - StripeAdapter       │
        │   - TransactionRepository   │  │ - Future providers... │
        │   - Database Access         │  │                       │
        └──────────────┬──────────────┘  └────────┬──────────────┘
                       │                          │
        ═══════════════════════════════════════════════════════════════════
        
        SISTEMAS EXTERNOS
        ══════════════════
        
        ┌──────────────────┐         ┌──────────────────┐
        │  PostgreSQL BD   │         │  PAYU / STRIPE   │
        │  (Persistencia)  │         │  (Pagos)         │
        └──────────────────┘         └──────────────────┘
```

---

## 2. Flujo de Datos - Crear Transacción

```
CLIENTE REST
     │
     │ POST /api/v1/transactions
     │ {JSON con datos de transacción}
     │
     ▼
┌─────────────────────────────┐
│ TransactionController       │
│ - Recibe HTTP Request       │
│ - Valida estructura JSON    │
└──────────┬──────────────────┘
           │
           │ Mapeo DTO → Dominio
           │
           ▼
┌─────────────────────────────┐
│ TransactionMapper           │
│ - Convierte CreateTransactionRequest
│ - Convierte a Transaction (Dominio)
└──────────┬──────────────────┘
           │
           │ Transaction Object
           │
           ▼
┌─────────────────────────────┐
│ TransactionApplicationService│
│ execute(transaction)        │
└──────────┬──────────────────┘
           │
           ├─ 1. Validar (domain.validate())
           │    - transaction_id no nulo
           │    - amount_in_cents > 0
           │    - currency_code ISO 4217
           │    - Datos cliente válidos
           │
           ├─ 2. Cambiar estado → PROCESSING
           │
           ├─ 3. Guardar en BD (ANTES de procesar)
           │
           ├─ 4. Procesar Pago
           │    ▼
           │    ┌──────────────────────┐
           │    │ PaymentProviderPort  │
           │    │ Factory Pattern      │
           │    └──────────┬───────────┘
           │               │
           │         ┌─────┴──────┐
           │         ▼            ▼
           │    ┌─────────┐   ┌────────┐
           │    │ PAYU    │   │ STRIPE │
           │    │Adapter  │   │Adapter │
           │    └──────┬──┘   └───┬────┘
           │           │          │
           │           ▼          ▼
           │      [PAYU API]   [STRIPE API]
           │
           ├─ 5. Actualizar estado
           │    - Si éxito: COMPLETED
           │    - Si error: FAILED
           │
           ├─ 6. Actualizar en BD
           │
           ▼
┌─────────────────────────────┐
│ TransactionMapper           │
│ - Convierte Transaction     │
│ - A TransactionResponseDto  │
└──────────┬──────────────────┘
           │
           │ {HTTP JSON Response}
           │
           ▼
    CLIENTE REST
    HTTP 201 Created
    {
      "response_code": "000",
      "response_message": "Successful operation",
      "data": { Transaction Info }
    }
```

---

## 3. Flujo de Datos - Consultar Transacción

```
CLIENTE REST
     │
     │ GET /api/v1/transactions/TRX-001-001
     │
     ▼
┌─────────────────────────────┐
│ TransactionController       │
│ - Recibe HTTP GET Request   │
│ - Extrae transaction_id     │
└──────────┬──────────────────┘
           │
           │
           ▼
┌─────────────────────────────┐
│ GetTransactionUseCase       │
│ execute(transactionId)      │
└──────────┬──────────────────┘
           │
           ▼
┌─────────────────────────────┐
│ TransactionRepositoryPort   │
│ findById(transactionId)     │
└──────────┬──────────────────┘
           │
           ▼
┌─────────────────────────────┐
│ TransactionPersistenceAdapter│
│ - Busca en BD usando JPA    │
│ - TransactionRepository     │
│   .findByTransaction_id()   │
└──────────┬──────────────────┘
           │
           ▼
    ┌──────────────┐
    │ PostgreSQL   │
    │ Database     │
    └──────┬───────┘
           │ Retorna TransactionEntity
           │
           ▼
┌─────────────────────────────┐
│ TransactionPersistenceAdapter│
│ - Mapea Entity → Dominio    │
│ - Retorna Transaction       │
└──────────┬──────────────────┘
           │
           ▼
┌─────────────────────────────┐
│ TransactionMapper           │
│ - Convierte Transaction     │
│ - A TransactionResponseDto  │
└──────────┬──────────────────┘
           │
           ▼
    CLIENTE REST
    HTTP 200 OK
    {
      "response_code": "000",
      "response_message": "Successful operation",
      "data": { Transaction Info }
    }
```

---

## 4. Componentes Detallados

### 4.1 Capa de Presentación (HTTP Inbound Adapter)

```
TransactionController (REST)
├── POST /api/v1/transactions
│   ├── CreateTransactionRequest (DTO entrada)
│   ├── Validación con @Valid
│   ├── GlobalExceptionHandler (manejo de errores)
│   └── ApiResponse<TransactionResponseDto> (DTO salida)
│
└── GET /api/v1/transactions/{transaction_id}
    ├── Validación de parámetro
    ├── GlobalExceptionHandler (manejo de errores)
    └── ApiResponse<TransactionResponseDto> (DTO salida)
```

### 4.2 Capa de Aplicación (Use Cases)

```
CreateTransactionUseCase (Interface)
├── execute(Transaction): Transaction
└── Implementado por: TransactionApplicationService

GetTransactionUseCase (Interface)
├── execute(String transactionId): Transaction
└── Implementado por: TransactionApplicationService
```

### 4.3 Capa de Dominio (Business Logic)

```
Transaction (Entidad)
├── Propiedades
│   ├── transaction_id: String (único)
│   ├── amount_in_cents: Long
│   ├── currency_code: String (ISO 4217)
│   ├── country_code: String (ISO 3166)
│   ├── payment_method_id: String
│   ├── status: TransactionStatus
│   └── customer: Customer
│
└── Métodos
    ├── validate(): void
    └── Estado: PENDING → PROCESSING → COMPLETED|FAILED

Customer (Entidad)
├── Propiedades
│   ├── document_type: String
│   ├── document_number: String
│   ├── country_calling_code: String
│   ├── phone_number: String
│   ├── email: String
│   └── Nombres y Apellidos
│
└── Métodos
    └── validate(): void

DomainException (Exception)
└── RuntimeException para errores de lógica de negocio
```

### 4.4 Capa de Infraestructura (Outbound Adapters)

#### Persistence Adapter (BD)
```
TransactionPersistenceAdapter
├── Implementa: TransactionRepositoryPort
├── save(Transaction): Transaction
├── findById(String): Transaction
├── update(Transaction): Transaction
│
└── Usa:
    ├── TransactionRepository (Spring Data JPA)
    ├── TransactionEntity (JPA)
    └── CustomerEntity (JPA)
```

#### Payment Provider Adapter
```
PaymentProviderAdapter (Factory + Strategy)
├── Implementa: PaymentProviderPort
├── processPayment(): String
├── refundPayment(): boolean
│
└── Delega a:
    ├── PayuProviderAdapter
    └── StripeProviderAdapter
```

---

## 5. Patrones de Diseño Aplicados

| Patrón | Ubicación | Beneficio |
|--------|-----------|----------|
| **Hexagonal** | Toda la arquitectura | Desacoplamiento |
| **Adapter** | Persistencia, HTTP, Pago | Conversión de interfaces |
| **Strategy** | Proveedores de pago | Intercambiar algoritmos |
| **Factory** | PaymentProviderAdapter | Crear estrategias dinámicamente |
| **Repository** | Spring Data JPA | Abstracción de acceso a datos |
| **DTO/Mapper** | HTTP Layer | Separación de capas |
| **Singleton** | Spring Beans | Instancia única |
| **Dependency Injection** | Constructor | Inversión de control |

---

## 6. Flujo de Errores

```
ERROR HANDLER FLOW:
═══════════════════

Validation Error (400)
├── CreateTransactionRequest con campos inválidos
├── @Valid dispara MethodArgumentNotValidException
├── GlobalExceptionHandler.handleValidationException()
└── ApiResponse {"response_code": "001", "message": "Validation error"}

Argument Error (400)
├── Datos inválidos en lógica de negocio
├── Lanza IllegalArgumentException
├── GlobalExceptionHandler.handleIllegalArgumentException()
└── ApiResponse {"response_code": "002", "message": "Invalid argument"}

Server Error (500)
├── Error inesperado
├── Lanza Exception genérica
├── GlobalExceptionHandler.handleGenericException()
└── ApiResponse {"response_code": "999", "message": "Internal server error"}
```

---

## 7. Matriz de Responsabilidades

```
Componente                  | Responsabilidad
──────────────────────────────────────────────────────────
TransactionController       | Recibir HTTP, validar entrada
TransactionMapper           | Convertir entre DTO y Dominio
TransactionAppService       | Orquestar lógica de negocio
Transaction (Domain)        | Validar reglas de negocio
TransactionRepository       | Acceso a datos
PersistenceAdapter          | Mapear Entity ↔ Domain
PaymentProviderAdapter      | Seleccionar proveedor de pago
PayuAdapter / StripeAdapter | Integrar con proveedor específico
GlobalExceptionHandler      | Centralizar manejo de errores
```

---

## 8. Beneficios de la Arquitectura Hexagonal

✅ **Testabilidad**
- Tests unitarios sin Spring
- Mocking de adaptadores
- Lógica de negocio aislada

✅ **Mantenibilidad**
- Cambios centralizados
- Fácil entender responsabilidades
- Bajo acoplamiento

✅ **Escalabilidad**
- Agregar nuevos adaptadores sin cambiar dominio
- Separación de concerns
- Preparado para microservicios

✅ **Flexibilidad**
- Cambiar proveedores de pago
- Cambiar base de datos
- Cambiar framework REST

✅ **Independencia del Framework**
- Dominio sin dependencias de Spring
- Fácil migrar entre frameworks
- Código agnóstico a tecnología

---

**Versión**: 1.0.0  
**Última actualización**: 2026-04-25

