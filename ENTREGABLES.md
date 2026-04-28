# Lista de entregables

Este documento lista los entregables solicitados para la prueba IT-TEST-001 y dónde encontrarlos en el repositorio.

1. Código fuente del componente
   - Ruta raíz: `.` (todo el proyecto en este repositorio)

2. Patrones de diseño aplicados (documentado)
   - README: `README.md` (sección "Patrones de Diseño")
   - Código: puertos/adapters (packages `application.port.*`, `infrastructure.adapter.*`)

3. Modelo de integración continua
   - Workflow GitHub Actions: `.github/workflows/ci.yml` (si existe)
   - README (sección CI/CD)

4. Scripts SQL del modelo normalizado
   - Liquibase SQLs:
     - `src/main/resources/db/changelog/v1.0.0/001-create-customers-table.sql`
     - `src/main/resources/db/changelog/v1.0.0/002-create-transactions-table.sql`
     - `src/main/resources/db/changelog/v1.0.0/003-create-indexes.sql`

12. Diagrama de normalización de base de datos
    - Draw.io (editable): `docs/db_normalization.drawio.xml`
    - SVG (visual): `docs/db_normalization.svg`
    - Documentación: `docs/DB_NORMALIZATION.md`

5. README con decisiones, suposiciones y riesgos
   - `README.md`

6. Diagrama simple del componente
   - Draw.io: `docs/architecture.drawio.xml`
   - SVG: `docs/architecture.svg`
   - XML descriptivo: `docs/architecture.xml`
   - Versión Markdown/mermaid también disponible: `docs/arquitectura_hexagonal.md`

7. Arquitectura implementada y patrones
   - Paquetes y archivos: `src/main/java/co/com/tumipay/...` (puedes revisar `architecture` en README para descripción)

8. Métricas
   - Código: `src/main/java/co/com/tumipay/application/service/TransactionApplicationService.java`
   - Config: `src/main/resources/application.yml`
   - Docs: `docs/metrics.md` (si existe)

9. Eventos (Kafka-ready)
   - Código: `src/main/java/co/com/tumipay/application/event/*`
   - Adapter Kafka: `src/main/java/co/com/tumipay/infrastructure/adapter/out/event/KafkaEventPublisher.java`
   - Config: `src/main/java/co/com/tumipay/infrastructure/adapter/out/event/KafkaConfig.java`
   - Docs: `docs/events.md` (si existe)

10. Tests
    - Tests de integración del controlador: `src/test/java/co/com/tumipay/infrastructure/adapter/in/http/TransactionControllerTest.java`

11. Cómo compilar / probar localmente (resumen)
    - Descargar dependencias y compilar:

```powershell
cd 'C:\Users\Mafe\Documents\transaction-orchestrator'
mvn -DskipTests clean package
```

    - Ejecutar la aplicación:

```powershell
mvn spring-boot:run
```

    - Ejecutar tests:

```powershell
mvn test
```

---

Si quieres que prepare un archivo ZIP con los entregables listos para envío o una sección final en `README` con instrucciones para ejecutar un entorno local (docker-compose con Postgres + Kafka), lo genero y lo agrego al repo.

