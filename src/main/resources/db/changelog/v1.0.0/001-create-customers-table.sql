--liquibase formatted sql

--changeset fer:001-create-customers-table
CREATE TABLE customers (
    id              UUID PRIMARY KEY,
    doc_type        VARCHAR(20) NOT NULL,
    doc_number      VARCHAR(50) NOT NULL,
    country_code    CHAR(3),
    phone           VARCHAR(20),
    email           VARCHAR(150) NOT NULL,
    first_name      VARCHAR(80) NOT NULL,
    middle_name     VARCHAR(80),
    last_name       VARCHAR(80) NOT NULL,
    second_last_name VARCHAR(80)
);