--liquibase formatted sql

--changeset fer:002-create-transactions-table
CREATE TABLE transactions (
    id              UUID PRIMARY KEY,
    client_txn_id   VARCHAR(100) NOT NULL UNIQUE,
    amount          BIGINT NOT NULL,
    currency_code   CHAR(3) NOT NULL,
    country_code    CHAR(2) NOT NULL,
    payment_method  VARCHAR(50) NOT NULL,
    status          VARCHAR(30) NOT NULL,
    webhook_url     TEXT NOT NULL,
    redirect_url    TEXT NOT NULL,
    description     VARCHAR(255),
    expires_at      TIMESTAMP,
    processed_at    TIMESTAMP NOT NULL,
    created_at      TIMESTAMP DEFAULT NOW(),
    customer_id     UUID,
    
    CONSTRAINT fk_transactions_customer
        FOREIGN KEY (customer_id)
        REFERENCES customers(id)
);