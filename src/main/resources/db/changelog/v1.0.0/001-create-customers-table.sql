--liquibase formatted sql

--changeset fer:001-create-customers-table
CREATE TABLE customers (
    id                      UUID PRIMARY KEY,
    document_type           VARCHAR(20) NOT NULL,
    document_number         VARCHAR(50) NOT NULL,
    country_calling_code    VARCHAR(10),
    phone_number            VARCHAR(20),
    email                   VARCHAR(150) NOT NULL,
    first_name              VARCHAR(80) NOT NULL,
    second_name             VARCHAR(80),
    first_surname           VARCHAR(80) NOT NULL,
    second_surname          VARCHAR(80)
);