# Diagrama de Normalización de Base de Datos

Se incluye un diagrama ER simplificado y la explicación de la normalización aplicada.

Archivos:
- `docs/db_normalization.drawio.xml`  — diagrama en formato draw.io (editable)
- `docs/db_normalization.svg`        — versión SVG para visualización

Tablas principales y columnas:

1) customers
- id : UUID (PK)
- document_type
- document_number
- country_calling_code
- phone_number
- email
- first_name
- second_name
- first_surname
- second_surname

2) transactions
- id : UUID (PK)
- transaction_id : VARCHAR (unique)
- amount_in_cents : BIGINT
- currency_code
- country_code
- payment_method_id
- transaction_description
- link_expiration_time
- processing_date
- created_at
- updated_at
- webhook_url
- redirect_url
- status
- customer_id : UUID (FK -> customers.id)

Nivel de normalización y justificación
- 1NF (Primera Forma Normal): cada campo contiene valores atómicos; no hay listas o grupos repetidos en una columna.
- 2NF (Segunda Forma Normal): las tablas están organizadas por entidad (customers y transactions); no hay dependencias parciales en claves compuestas (no se usan claves compuestas aquí).
- 3NF (Tercera Forma Normal): no hay dependencias transitivas — datos específicos del cliente se mantienen en `customers`, evitando redundancia en `transactions`.

Notas:
- Las FK y las constraints están definidas en los scripts Liquibase (`src/main/resources/db/changelog/v1.0.0/002-create-transactions-table.sql`) como `customer_id` que referencia `customers.id`.
- Las claves `UUID` permiten identificar registros de forma única y coherente entre servicios.

Cómo abrir el diagrama editable
1. Ir a https://app.diagrams.net/
2. Archivo → Importar desde dispositivo → seleccionar `docs/db_normalization.drawio.xml`

Si deseas, puedo:
- Añadir otras tablas auxiliares (por ejemplo `providers`, `provider_responses`, `webhook_events`) si piensas ampliar el modelo.
- Generar el script de migración adicional para `providers` o `webhook_events`.

