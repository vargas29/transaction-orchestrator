-- ============================================================================
-- SCRIPT SQL - TRANSACTION ORCHESTRATOR DATABASE
-- ============================================================================
-- Base de datos normalizada para el microservicio de orquestación de
-- transacciones con arquitectura hexagonal
-- ============================================================================

-- ============================================================================
-- 1. TABLA CUSTOMERS (CLIENTES)
-- ============================================================================
-- Almacena la información de los clientes
-- Campo: id (PK) - Clave primaria autoincremental
-- Campo: document_type - Tipo de documento (CC, CI, DNI, etc.)
-- Campo: document_number - Número de documento único
-- Campo: country_calling_code - Código de país para teléfono
-- Campo: phone_number - Número de teléfono
-- Campo: email - Correo electrónico único
-- Campo: first_name - Primer nombre
-- Campo: second_name - Segundo nombre (opcional)
-- Campo: first_surname - Primer apellido
-- Campo: second_surname - Segundo apellido (opcional)

CREATE TABLE customers (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    document_type VARCHAR(50) NOT NULL,
    document_number VARCHAR(50) NOT NULL,
    country_calling_code VARCHAR(5) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    email VARCHAR(100) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    second_name VARCHAR(100),
    first_surname VARCHAR(100) NOT NULL,
    second_surname VARCHAR(100),
    CONSTRAINT uk_customer_email UNIQUE (email),
    CONSTRAINT uk_customer_document UNIQUE (document_type, document_number),
    INDEX idx_customer_email (email),
    INDEX idx_customer_document (document_number)
);

-- ============================================================================
-- 2. TABLA TRANSACTIONS (TRANSACCIONES)
-- ============================================================================
-- Almacena la información de las transacciones procesadas
-- Relación con CUSTOMERS: Cada transacción pertenece a un cliente

CREATE TABLE transactions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    transaction_id VARCHAR(100) NOT NULL UNIQUE,
    customer_id BIGINT NOT NULL,
    amount_in_cents BIGINT NOT NULL,
    currency_code VARCHAR(3) NOT NULL,
    country_code VARCHAR(2) NOT NULL,
    payment_method_id VARCHAR(100) NOT NULL,
    transaction_description VARCHAR(500),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    processing_date TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    webhook_url VARCHAR(500) NOT NULL,
    redirect_url VARCHAR(500) NOT NULL,
    link_expiration_time BIGINT,
    CONSTRAINT fk_transaction_customer FOREIGN KEY (customer_id)
        REFERENCES customers(id) ON DELETE RESTRICT,
    CONSTRAINT ck_transaction_status CHECK (
        status IN ('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED', 'CANCELLED', 'REFUNDED')
    ),
    CONSTRAINT ck_amount_positive CHECK (amount_in_cents > 0),
    INDEX idx_transaction_id (transaction_id),
    INDEX idx_customer_id (customer_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at),
    INDEX idx_currency_code (currency_code),
    INDEX idx_country_code (country_code),
    INDEX idx_payment_method (payment_method_id)
);

-- ============================================================================
-- 3. ÍNDICES ADICIONALES PARA OPTIMIZACIÓN
-- ============================================================================

-- Índice compuesto para búsquedas comunes
CREATE INDEX idx_trans_customer_status ON transactions(customer_id, status);
CREATE INDEX idx_trans_currency_country ON transactions(currency_code, country_code);
CREATE INDEX idx_trans_payment_status ON transactions(payment_method_id, status);

-- ============================================================================
-- 4. DATOS DE PRUEBA
-- ============================================================================

-- Insertar cliente de prueba
INSERT INTO customers (
    document_type, document_number, country_calling_code, phone_number,
    email, first_name, second_name, first_surname, second_surname
) VALUES (
    'CC', '1234567890', '+57', '3001234567',
    'juan.perez@example.com', 'Juan', 'Carlos', 'Pérez', 'García'
);

-- Insertar transacción de prueba
INSERT INTO transactions (
    transaction_id, customer_id, amount_in_cents, currency_code, country_code,
    payment_method_id, transaction_description, status, processing_date,
    webhook_url, redirect_url
) VALUES (
    'TRX-001-001', 1, 100000, 'USD', 'CO',
    'PAYU_CREDIT_CARD', 'Compra de prueba', 'COMPLETED', NOW(),
    'https://example.com/webhook', 'https://example.com/success'
);

-- ============================================================================
-- 5. VISTAS ÚTILES PARA ANÁLISIS
-- ============================================================================

-- Vista de transacciones con información del cliente
CREATE VIEW v_transactions_with_customer AS
SELECT
    t.id,
    t.transaction_id,
    t.amount_in_cents,
    t.currency_code,
    t.country_code,
    t.status,
    t.created_at,
    t.processing_date,
    c.email,
    c.first_name,
    c.first_surname,
    c.phone_number
FROM transactions t
INNER JOIN customers c ON t.customer_id = c.id;

-- ============================================================================
-- 6. COMENTARIOS SOBRE DISEÑO NORMALIZADO
-- ============================================================================
/*
NORMALIZACIÓN (3NF):
1. Primera Forma Normal (1NF):
   - Todos los atributos contienen valores atómicos
   - No hay atributos multivaluados

2. Segunda Forma Normal (2NF):
   - Cumple 1NF
   - Todos los atributos no-clave son totalmente dependientes de la clave primaria
   - Separación de CUSTOMERS y TRANSACTIONS

3. Tercera Forma Normal (3NF):
   - Cumple 2NF
   - No existen dependencias transitivas
   - Cada tabla tiene una única responsabilidad

VENTAJAS:
✓ Eliminación de redundancia
✓ Integridad referencial
✓ Facilidad de mantenimiento
✓ Optimización de consultas
✓ Escalabilidad

ÍNDICES CREADOS PARA OPTIMIZAR:
✓ Búsquedas por transaction_id
✓ Búsquedas por customer_id
✓ Filtros por estado de transacción
✓ Búsquedas por rango de fechas
✓ Filtros combinados (customer + status, etc.)
*/

