--liquibase formatted sql

--changeset fer:002-create-transactions-table
CREATE TABLE transactions (
    id                      UUID PRIMARY KEY,
    transaction_id          VARCHAR(100) NOT NULL UNIQUE,
    amount_in_cents         BIGINT NOT NULL,
    currency_code           CHAR(3) NOT NULL,
    country_code            CHAR(2) NOT NULL,
    payment_method_id       VARCHAR(50) NOT NULL,
    status                  VARCHAR(30) NOT NULL,
    webhook_url             TEXT NOT NULL,
    redirect_url            TEXT NOT NULL,
    transaction_description VARCHAR(255),
    link_expiration_time    BIGINT,
    processing_date         TIMESTAMP NOT NULL,
    created_at              TIMESTAMP DEFAULT NOW(),
    updated_at              TIMESTAMP,
    customer_id             UUID,

    CONSTRAINT fk_transactions_customer
        FOREIGN KEY (customer_id)
        REFERENCES customers(id)
);