--liquibase formatted sql

--changeset fer:003-create-indexes

-- Índice para búsqueda por documento
CREATE INDEX idx_customers_doc
    ON customers (doc_type, doc_number);

-- Índice por email
CREATE INDEX idx_customers_email
    ON customers (email);

-- Índice para consultas por cliente
CREATE INDEX idx_transactions_customer_id
    ON transactions (customer_id);

-- Índice por estado
CREATE INDEX idx_transactions_status
    ON transactions (status);

-- Índice por fecha de creación
CREATE INDEX idx_transactions_created_at
    ON transactions (created_at);

-- Índice por client_txn_id (aunque ya es UNIQUE, ayuda explícitamente)
CREATE INDEX idx_transactions_client_txn_id
    ON transactions (client_txn_id);

