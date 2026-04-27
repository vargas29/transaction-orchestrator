# Diagrama simple de arquitectura hexagonal para Transaction Orchestrator

```mermaid
graph TD
    A[Cliente/API REST] -->|HTTP| B[Adaptador Inbound (Controller)]
    B -->|DTO a Dominio| C[Aplicación (Servicio/Casos de Uso)]
    C -->|Puerto de salida| D[Adaptador Outbound (Repositorio JPA)]
    C -->|Puerto de salida| E[Adaptador Outbound (Proveedor de Pagos)]
    D -->|Entidad JPA| F[(Base de Datos)]
    E -->|HTTP/SDK| G[Proveedor Externo]
    C --> H[Dominio (Entidades, Validaciones)]
```

- **Inbound:** Controller REST expone endpoints.
- **Aplicación:** Orquesta lógica, usa puertos.
- **Dominio:** Entidades, reglas, validaciones.
- **Outbound:** Persistencia y proveedores externos.

