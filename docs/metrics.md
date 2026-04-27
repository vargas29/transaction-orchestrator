Métricas expuestas por Transaction Orchestrator

Endpoints
- /actuator/prometheus -> Métricas en formato Prometheus
- /actuator/health -> Estado de salud

Métricas implementadas (Micrometer)
- transactions_total (counter): total de transacciones recibidas
- transactions_success_total (counter): total de transacciones exitosas
- transactions_failed_total (counter): total de transacciones fallidas
- transactions_processing_time_seconds (timer): tiempo de procesamiento de transacciones
- transactions_by_provider{provider="<provider_id>"} (counter): número de transacciones por proveedor

Cómo probar localmente
1. Levanta la app:
   mvn spring-boot:run
2. Accede a las métricas:
   curl http://localhost:8080/actuator/prometheus

Recomendaciones
- Configurar Prometheus para raspar /actuator/prometheus
- Añadir labels adicionales si necesitas segmentar por país, currency, status, etc.

