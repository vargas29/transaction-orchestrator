# 🏗️ Transaction Orchestrator - Microservicio de Orquestación de Transacciones

## 📋 Tabla de Contenidos
- [Descripción](#descripción)
- [Requisitos](#requisitos)
- [Instalación](#instalación)
- [Uso](#uso)
- [Arquitectura](#arquitectura)
- [API Documentation](#api-documentation)
- [Decisiones Arquitectónicas](#decisiones-arquitectónicas)
- [Patrones de Diseño](#patrones-de-diseño)
- [Suposiciones](#suposiciones)
- [Riesgos Identificados](#riesgos-identificados)
- [Calidad del Código](#calidad-del-código)
- [CI/CD](#cicd)

---

## 📝 Descripción

**Transaction Orchestrator** es un microservicio construido con **arquitectura hexagonal** que permite la orquestación de transacciones de pago a través de múltiples proveedores (Payu, Stripe, etc.).

### Características Principales
✅ **Arquitectura Hexagonal**: Separación clara entre capas  
✅ **API RESTful**: Endpoints siguiendo estándares REST  
✅ **Validación Robusta**: Campos obligatorios y opcionales validados  
✅ **Persistencia en BD**: PostgreSQL/H2 con JPA/Hibernate  
✅ **Proveedores Múltiples**: Factory + Strategy para cambiar proveedores  
✅ **Manejo de Excepciones**: Global exception handler estandarizado  
✅ **Logging Estructurado**: Trazabilidad completa de operaciones  
✅ **Versionamiento de API**: `/api/v1/` para futuras versiones  

---

## 📦 Requisitos

- **Java 17+**
- **Spring Boot 3.2.0+**
- **Maven 3.8.0+**
- **PostgreSQL 13+** (o H2 para desarrollo)
- **Git**

---

## 🚀 Instalación

### 1. Clonar el repositorio
```bash
git clone https://github.com/tu-usuario/transaction-orchestrator.git
cd transaction-orchestrator
```

### 2. Compilar el proyecto
```bash
mvn clean install
```

### 3. Ejecutar la aplicación
```bash
mvn spring-boot:run
```

La aplicación estará disponible en: **http://localhost:8080**

---

## 💡 Uso

### Crear una Transacción (POST)

**Endpoint:**
```
POST /api/v1/transactions
Content-Type: application/json
```

**Request:**
```json
{
  "transaction_id": "TRX-001-001",
  "amount_in_cents": 100000,
  "currency_code": "USD",
  "country_code": "CO",
  "payment_method_id": "PAYU_CREDIT_CARD",
  "webhook_url": "https://example.com/webhook",
  "redirect_url": "https://example.com/success",
  "transaction_description": "Compra de producto",
  "link_expiration_time": 3600,
  "customer": {
    "document_type": "CC",
    "document_number": "1234567890",
    "country_calling_code": "+57",
    "phone_number": "3001234567",
    "email": "juan.perez@example.com",
    "first_name": "Juan",
    "second_name": "Carlos",
    "first_surname": "Pérez",
    "second_surname": "García"
  }
}
```

**Response (201 Created):**
```json
{
  "response_code": "000",
  "response_message": "Successful operation",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "transaction_id": "TRX-001-001",
    "amount_in_cents": 100000,
    "currency_code": "USD",
    "country_code": "CO",
    "payment_method_id": "PAYU_CREDIT_CARD",
    "transaction_description": "Compra de producto",
    "processing_date": "2026-04-25T15:30:45.123456",
    "status": "COMPLETED"
  }
}
```

### Consultar una Transacción (GET)

**Endpoint:**
```
GET /api/v1/transactions/{transaction_id}
```

**Response (200 OK):**
```json
{
  "response_code": "000",
  "response_message": "Successful operation",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "transaction_id": "TRX-001-001",
    "amount_in_cents": 100000,
    "currency_code": "USD",
    "country_code": "CO",
    "payment_method_id": "PAYU_CREDIT_CARD",
    "transaction_description": "Compra de producto",
    "processing_date": "2026-04-25T15:30:45.123456",
    "status": "COMPLETED"
  }
}
```

### Códigos de Respuesta

| Código | Significado | HTTP Status |
|--------|-------------|------------|
| 000 | Operación exitosa | 200/201 |
| 001 | Error de validación | 400 |
| 002 | Argumento inválido | 400 |
| 999 | Error interno del servidor | 500 |

---

## 🏗️ Arquitectura

### Modelo de Arquitectura Hexagonal (Puertos y Adaptadores)

```
┌─────────────────────────────────────────────────────┐
│                   PRESENTACIÓN                       │
│  TransactionController (REST API - Adaptador IN)    │
└────────────────────┬────────────────────────────────┘
                     │
        ┌────────────▼────────────┐
        │   PUERTO DE ENTRADA      │
        │ CreateTransactionUseCase │
        │ GetTransactionUseCase    │
        └────────────┬────────────┘
                     │
        ┌────────────▼────────────────────┐
        │   LÓGICA DE NEGOCIO (DOMINIO)   │
        │  TransactionApplicationService  │
        │                                  │
        │  Transaction (Entidad)           │
        │  Customer (Entidad)              │
        │  TransactionStatus (Value Obj)   │
        └────────────┬────────────────────┘
                     │
      ┌──────────────┴──────────────┐
      │                             │
┌─────▼──────────────┐    ┌────────▼─────────┐
│ PUERTO DE SALIDA   │    │ PUERTO DE SALIDA │
│ TransactionRepo    │    │ PaymentProvider  │
└─────┬──────────────┘    └────────┬─────────┘
      │                            │
┌─────▼──────────────────┐  ┌──────▼─────────────────┐
│ ADAPTADOR OUT          │  │ ADAPTADOR OUT          │
│ Persistence Adapter    │  │ PaymentProviderAdapter │
│ (JPA/Hibernate)        │  │                        │
│                        │  │ ├─ PayuAdapter         │
│ ├─ Database H2/PgSQL   │  │ └─ StripeAdapter       │
│ └─ TransactionEntity   │  │                        │
└────────────────────────┘  └────────────────────────┘
```

### Estructura de Carpetas

```
transaction-orchestrator/
├── src/
│   ├── main/
│   │   ├── java/org/example/
│   │   │   ├── TransactionOrchestratorApplication.java (Spring Boot)
│   │   │   ├── application/                    # Casos de uso
│   │   │   │   ├── port/in/                   # Puertos de entrada
│   │   │   │   ├── port/out/                  # Puertos de salida
│   │   │   │   └── service/                   # Servicios de aplicación
│   │   │   ├── domain/                        # Lógica de negocio
│   │   │   │   ├── model/                     # Entidades
│   │   │   │   └── exception/                 # Excepciones
│   │   │   └── infrastructure/                # Adaptadores
│   │   │       ├── adapter/in/http/           # Adaptador HTTP
│   │   │       │   ├── dto/
│   │   │       │   ├── mapper/
│   │   │       │   ├── exception/
│   │   │       │   └── TransactionController
│   │   │       ├── adapter/out/persistence/   # Adaptador BD
│   │   │       ├── adapter/out/provider/      # Adaptadores de pago
│   │   │       └── config/                    # Configuración
│   │   └── resources/
│   │       └── application.yml
│   └── test/
│       └── java/
├── database/
│   └── schema.sql                             # Script SQL
├── pom.xml
├── README.md
└── .gitignore
```

---

## 📡 API Documentation

### Base URL
```
http://localhost:8080/api/v1
```

### Endpoints

#### 1. Crear Transacción
```
POST /transactions
Content-Type: application/json
```
- **Validaciones**: Todos los campos obligatorios
- **Response**: 201 Created o 400 Bad Request

#### 2. Consultar Transacción
```
GET /transactions/{transaction_id}
```
- **Validaciones**: transaction_id requerido
- **Response**: 200 OK o 404 Not Found

#### 3. Health Check
```
GET /transactions/health
```
- **Validaciones**: Ninguna
- **Response**: 200 OK

---

## 🎯 Decisiones Arquitectónicas

### 1. **Arquitectura Hexagonal (Puertos y Adaptadores)**
**Justificación:**
- ✅ Desacoplamiento entre capas
- ✅ Facilita testing (mocks de adaptadores)
- ✅ Independencia de frameworks
- ✅ Facilita cambios de proveedores de pago

### 2. **Spring Boot 3.2.0**
**Justificación:**
- ✅ Framework maduro y ampliamente usado
- ✅ Soporte para Jakarta EE (Java 17+)
- ✅ Excelente ecosistema (Spring Data, Spring Validation)
- ✅ Comunidad activa y buena documentación

### 3. **JPA/Hibernate + Spring Data**
**Justificación:**
- ✅ ORM robusto y probado
- ✅ Abstracción de BD (intercambiable H2, PostgreSQL, MySQL)
- ✅ Consultas tipadas con Spring Data
- ✅ Soporte para migraciones (Flyway/Liquibase)

### 4. **DTOs con snake_case**
**Justificación:**
- ✅ Cumple especificación del cliente
- ✅ Compatibilidad con APIs estándar
- ✅ Uso de `@JsonProperty` para mapeo

### 5. **Validación de Entrada**
**Justificación:**
- ✅ Anotaciones `@Valid`, `@NotNull`, `@Pattern`, `@Email`
- ✅ Excepciones centralizadas con `GlobalExceptionHandler`
- ✅ Mensajes de error estandarizados

### 6. **Múltiples Proveedores (Factory + Strategy)**
**Justificación:**
- ✅ Patrón Strategy para intercambiar algoritmos
- ✅ Patrón Factory para instanciar proveedores
- ✅ Facilita agregar nuevos proveedores sin cambiar código existente

---

## 🎨 Patrones de Diseño

| Patrón | Ubicación | Justificación |
|--------|-----------|--------------|
| **Hexagonal** | Arquitectura completa | Desacoplamiento y testabilidad |
| **Factory** | `PaymentProviderAdapter` | Creación dinámica de proveedores |
| **Strategy** | Adaptadores de pago | Intercambio de algoritmos de pago |
| **Adapter** | `TransactionController`, `TransactionPersistenceAdapter` | Convertir interfaces |
| **Repository** | `TransactionRepository` | Acceso a datos abstracto |
| **Mapper/DTO** | `TransactionMapper` | Separación entre capas |
| **Singleton** | `@Service`, `@Component` | Beans de Spring |
| **Dependency Injection** | Constructor `@RequiredArgsConstructor` | Inversión de control |
| **Builder** | `@Builder` en DTOs | Construcción segura de objetos |
| **Value Object** | `TransactionStatus` | Objetos inmutables |

---

## 🤔 Suposiciones

1. **Base de Datos**: Se usa H2 en memoria para desarrollo, PostgreSQL para producción
2. **Autenticación**: No implementada (fuera del scope actual)
3. **Autorizaci**: No implementada (fuera del scope actual)
4. **Rate Limiting**: No implementado (se recomienda agregar)
5. **Webhooks**: Se almacenan URLs pero no se envían automáticamente
6. **Transacciones ACID**: Manejadas por JPA/Hibernate
7. **Logs**: Se confía en framework de logging de Spring
8. **Caché**: No implementado (se recomienda agregar para consultas frecuentes)

---

## ⚠️ Riesgos Identificados

### 1. **Riesgo: Pérdida de Transacciones en Proveedores**
- **Impacto**: Alto
- **Probabilidad**: Media
- **Mitigación**: 
  - Guardar en BD ANTES de enviar a proveedor
  - Implementar reintentos exponenciales
  - Implementar reconciliación periódica

### 2. **Riesgo: Inconsistencia de Datos**
- **Impacto**: Alto
- **Probabilidad**: Media
- **Mitigación**:
  - Usar transacciones ACID
  - Implementar validaciones en dominio
  - Usar optimistic locking

### 3. **Riesgo: Performance bajo alta concurrencia**
- **Impacto**: Medio
- **Probabilidad**: Media
- **Mitigación**:
  - Agregar caché distribuido (Redis)
  - Implementar índices en BD
  - Usar conexión pooling
  - Considerar CQRS para lectas

### 4. **Riesgo: Exposición de datos sensibles**
- **Impacto**: Alto
- **Probabilidad**: Baja
- **Mitigación**:
  - Implementar HTTPS
  - Encriptar datos sensibles (email, teléfono)
  - Implementar autenticación y autorización
  - Auditoría de accesos

### 5. **Riesgo: Falta de documentación de cambios**
- **Impacto**: Medio
- **Probabilidad**: Alta
- **Mitigación**:
  - Mantener changelog.md
  - Usar versionamiento semántico
  - Documentar cambios de API

---

## ✅ Calidad del Código

### Métricas de Calidad

#### 1. **SonarQube**
```bash
mvn sonar:sonar \
  -Dsonar.projectKey=transaction-orchestrator \
  -Dsonar.sources=src/main/java \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.login=your_token
```

#### 2. **Code Coverage con JaCoCo**
```bash
mvn clean install jacoco:report
# Reporte en: target/site/jacoco/index.html
```

#### 3. **Linting con Checkstyle**
```bash
mvn checkstyle:check
```

### Herramientas Recomendadas

| Herramienta | Propósito | Cómo usar |
|------------|-----------|----------|
| **SonarQube** | Análisis estático | Dashboard web |
| **JaCoCo** | Cobertura de tests | Reportes HTML |
| **Checkstyle** | Estilo de código | `mvn checkstyle:check` |
| **SpotBugs** | Detección de bugs | `mvn spotbugs:check` |
| **IntelliJ IDEA** | IDE con inspecciones | Built-in |

### Buenas Prácticas Implementadas

✅ **Principios SOLID**
- Single Responsibility: Cada clase tiene una responsabilidad
- Open/Closed: Abierto a extensión (nuevos proveedores), cerrado a modificación
- Liskov Substitution: Adaptadores intercambiables
- Interface Segregation: Puertos específicos
- Dependency Inversion: Inyección de dependencias

✅ **Clean Code**
- Nombres descriptivos
- Métodos pequeños y enfocados
- Comentarios cuando es necesario
- Sin duplicación de código (DRY)
- Manejo robusto de excepciones

✅ **Testing**
- Tests unitarios para servicios
- Tests de integración para adaptadores
- Tests E2E para controllers
- Mocking con Mockito

✅ **Logging**
- Niveles apropiados (INFO, DEBUG, ERROR)
- Contexto claro en mensajes
- Structured logging

---

## 🔄 CI/CD

### GitHub Actions (Recomendado)

Crear archivo: `.github/workflows/ci-cd.yml`

```yaml
name: CI/CD Pipeline

on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main, develop]

jobs:
  build:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
    
    - name: Build with Maven
      run: mvn clean install
    
    - name: Run Tests
      run: mvn test
    
    - name: SonarQube Analysis
      run: mvn sonar:sonar \
        -Dsonar.projectKey=transaction-orchestrator \
        -Dsonar.host.url=http://sonarqube:9000 \
        -Dsonar.login=${{ secrets.SONAR_TOKEN }}
    
    - name: Build Docker Image
      run: docker build -t transaction-orchestrator:latest .
    
    - name: Push to Registry
      run: docker push ${{ secrets.REGISTRY }}/transaction-orchestrator:latest
```

### Jenkins (Alternativa)

```groovy
pipeline {
    agent any
    
    stages {
        stage('Checkout') {
            steps {
                git 'https://github.com/tu-usuario/transaction-orchestrator.git'
            }
        }
        
        stage('Build') {
            steps {
                sh 'mvn clean install'
            }
        }
        
        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }
        
        stage('Quality Gate') {
            steps {
                sh 'mvn sonar:sonar'
            }
        }
        
        stage('Deploy') {
            steps {
                sh 'docker build -t transaction-orchestrator:latest .'
                sh 'docker push registry.example.com/transaction-orchestrator:latest'
            }
        }
    }
}
```

### GitLab CI (Alternativa)

```yaml
stages:
  - build
  - test
  - quality
  - deploy

build:
  stage: build
  image: maven:3.8.0-jdk-17
  script:
    - mvn clean install

test:
  stage: test
  image: maven:3.8.0-jdk-17
  script:
    - mvn test

quality:
  stage: quality
  image: maven:3.8.0-jdk-17
  script:
    - mvn sonar:sonar

deploy:
  stage: deploy
  image: docker:latest
  script:
    - docker build -t transaction-orchestrator:latest .
    - docker push registry.example.com/transaction-orchestrator:latest
```

---

## 📊 Diagrama de Componentes

```
┌──────────────────────────────────────────────────────────────┐
│                        CLIENTE REST                          │
│  (Postman, curl, aplicación web, aplicación móvil)          │
└────────────────────────┬─────────────────────────────────────┘
                         │
                    HTTP/REST
                         │
         ┌───────────────▼────────────────┐
         │   API Gateway / Load Balancer  │
         └───────────────┬────────────────┘
                         │
    ┌────────────────────┴──────────────────────┐
    │                                           │
    │  ┌─────────────────────────────────────┐ │
    │  │   TRANSACTION ORCHESTRATOR          │ │
    │  │   (Spring Boot App - Puerto 8080)   │ │
    │  │                                     │ │
    │  │  ┌─────────────────────────────┐   │ │
    │  │  │ TransactionController       │   │ │
    │  │  │ (REST Adapter)              │   │ │
    │  │  └────────────┬────────────────┘   │ │
    │  │               │                     │ │
    │  │  ┌────────────▼────────────────┐   │ │
    │  │  │ ApplicationService          │   │ │
    │  │  │ (Business Logic)            │   │ │
    │  │  └────────────┬────────────────┘   │ │
    │  │               │                     │ │
    │  │   ┌───────────┴───────────┐        │ │
    │  │   │                       │        │ │
    │  │ ┌─┴──────────┐  ┌────────┴──┐    │ │
    │  │ │Persistence │  │Payment    │    │ │
    │  │ │Adapter     │  │Provider   │    │ │
    │  │ │(JPA)       │  │Adapter    │    │ │
    │  │ └─┬──────────┘  └────┬──────┘    │ │
    │  └───┼────────────────────┼──────────┘ │
    └──────┼────────────────────┼────────────┘
           │                    │
    ┌──────▼─────────┐  ┌───────▼─────────┐
    │  PostgreSQL    │  │  Payment Gateway│
    │  Database      │  │  (Payu, Stripe) │
    └────────────────┘  └─────────────────┘
```

---

## 🔐 Seguridad

### Recomendaciones Adicionales

1. **Autenticación**: Implementar OAuth2/JWT
2. **Autorización**: Role-based Access Control (RBAC)
3. **Encriptación**: TLS para comunicación, AES para datos sensibles
4. **Rate Limiting**: Implementar con Spring Cloud Gateway
5. **CORS**: Configurar adecuadamente en producción
6. **CSRF Protection**: Habilitado por defecto en Spring Security
7. **SQL Injection**: JPA previene automáticamente
8. **Validación de Entrada**: Implementada con Jakarta Validation

---

## 📚 Referencias

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Hexagonal Architecture - Domain-Driven Design](https://www.culttt.com/2014/11/24/hexagonal-architecture/)
- [Design Patterns in Java](https://refactoring.guru/design-patterns/java)
- [REST API Best Practices](https://restfulapi.net/)
- [Database Normalization](https://en.wikipedia.org/wiki/Database_normalization)

---

## 📞 Contacto y Soporte

- **Desarrollador**: Tu Nombre
- **Email**: tu.email@example.com
- **GitHub**: https://github.com/tu-usuario
- **Issues**: GitHub Issues

---

## 📄 Licencia

Este proyecto está bajo la licencia MIT. Ver archivo `LICENSE` para más detalles.

---

**Versión**: 1.0.0  
**Última actualización**: 2026-04-25  
**Estado**: Desarrollo ✅

