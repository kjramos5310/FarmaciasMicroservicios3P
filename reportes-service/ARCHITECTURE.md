# Arquitectura Técnica - Reporting Service

## 📐 Visión General

El **Reporting Service** es un microservicio especializado en la generación de reportes y análisis de datos para el sistema de gestión de inventario y ventas. Actúa como un **Data Warehouse** simplificado, consumiendo datos de otros microservicios y generando insights de negocio.

## 🏗️ Arquitectura en Capas

```
┌─────────────────────────────────────────────────────────┐
│                    API REST Layer                        │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │   Report     │  │   Snapshot   │  │    Health    │  │
│  │  Controller  │  │  Controller  │  │    Check     │  │
│  └──────────────┘  └──────────────┘  └──────────────┘  │
└─────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────┐
│                   Service Layer                          │
│  ┌──────────────┐  ┌──────────────┐                    │
│  │  Reporting   │  │   Snapshot   │                    │
│  │   Service    │  │   Service    │                    │
│  └──────────────┘  └──────────────┘                    │
└─────────────────────────────────────────────────────────┘
                     ↓                ↓
┌─────────────────────────────────────────────────────────┐
│              Data Access Layer                           │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │SalesSummary  │  │ Inventory    │  │ProductSales  │  │
│  │ Repository   │  │  Repository  │  │  Repository  │  │
│  └──────────────┘  └──────────────┘  └──────────────┘  │
└─────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────┐
│                  PostgreSQL Database                     │
│         (reporting_db - Puerto 5435)                     │
└─────────────────────────────────────────────────────────┘
```

## 🔄 Flujo de Datos

### 1. Generación de Reportes en Tiempo Real

```
Usuario → Controller → Service → WebClient → Servicios Externos
                          ↓
                    Agregación/Cálculos
                          ↓
                    DTO Response → Usuario
```

### 2. Generación de Snapshots (Históricos)

```
Scheduler/Manual → SnapshotService → ReportingService
                          ↓
                    WebClient → Servicios Externos
                          ↓
                    Agregación → Repository → Base de Datos
```

## 🔌 Integraciones Externas

### Sales Service (Puerto 8083)
- **Endpoint**: `/api/sales/date-range`
- **Método**: GET
- **Parámetros**: startDate, endDate, branchId (opcional)
- **Respuesta**: Lista de ventas con detalles completos

### Inventory Service (Puerto 8082)
- **Endpoint**: `/api/stock`
- **Método**: GET
- **Parámetros**: branchId (opcional)
- **Respuesta**: Lista de productos en stock con detalles

## 📊 Modelo de Datos

### Entidades Principales

#### 1. SalesSummary
```sql
CREATE TABLE sales_summary (
    id BIGSERIAL PRIMARY KEY,
    report_date DATE NOT NULL,
    branch_id BIGINT,
    total_sales INTEGER NOT NULL,
    total_revenue DECIMAL(12,2) NOT NULL,
    average_ticket DECIMAL(10,2),
    total_items INTEGER,
    unique_customers INTEGER,
    generated_at TIMESTAMP NOT NULL,
    INDEX idx_sales_date (report_date),
    INDEX idx_sales_branch (branch_id)
);
```

#### 2. InventorySummary
```sql
CREATE TABLE inventory_summary (
    id BIGSERIAL PRIMARY KEY,
    report_date DATE NOT NULL,
    branch_id BIGINT,
    total_products INTEGER NOT NULL,
    low_stock_products INTEGER,
    expiring_soon INTEGER,
    inventory_value DECIMAL(12,2),
    generated_at TIMESTAMP NOT NULL,
    INDEX idx_inventory_date (report_date),
    INDEX idx_inventory_branch (branch_id)
);
```

#### 3. ProductSalesReport
```sql
CREATE TABLE product_sales_report (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(200) NOT NULL,
    product_code VARCHAR(50),
    report_date DATE NOT NULL,
    branch_id BIGINT,
    quantity_sold INTEGER NOT NULL,
    revenue DECIMAL(12,2) NOT NULL,
    generated_at TIMESTAMP NOT NULL,
    INDEX idx_product_date (report_date),
    INDEX idx_product_id (product_id)
);
```

## 🎯 Casos de Uso Principales

### 1. Dashboard Ejecutivo
**Objetivo**: Vista rápida de métricas clave de negocio

**Métricas Incluidas**:
- Ventas totales (últimos 30 días)
- Ingresos totales
- Ticket promedio
- Clientes únicos
- Total de productos
- Productos bajo stock
- Productos próximos a vencer
- Valor total del inventario

### 2. Análisis de Ventas
**Capacidades**:
- Resumen por período (diario, semanal, mensual)
- Comparación entre sucursales
- Top productos más vendidos
- Análisis de rentabilidad por producto

### 3. Gestión de Inventario
**Capacidades**:
- Alertas de stock bajo
- Productos próximos a vencer (< 30 días)
- Valorización de inventario
- Análisis de rotación

### 4. Snapshots Históricos
**Capacidades**:
- Generación automática diaria (@Scheduled)
- Generación manual para períodos específicos
- Consulta de datos históricos almacenados
- Análisis de tendencias

## 🔐 Seguridad y Resiliencia

### Manejo de Errores
- `GlobalExceptionHandler` centralizado
- Respuestas HTTP estandarizadas
- Logging detallado de errores
- Fallback a listas vacías en caso de fallo de servicios externos

### Tolerancia a Fallos
```java
try {
    // Llamada a servicio externo
} catch (Exception e) {
    log.error("Error: {}", e.getMessage());
    return new ArrayList<>(); // Retorna lista vacía
}
```

## 📈 Optimizaciones

### Base de Datos
1. **Índices**: En campos de fecha y branch_id
2. **Vistas**: monthly_sales_summary, inventory_alerts
3. **Particionamiento**: Considerar para datos históricos extensos

### Caché (Futuro)
```java
@Cacheable("sales-summary")
public SalesSummaryResponse getSalesSummary(...) {
    // ...
}
```

### Consultas Asíncronas (Futuro)
```java
@Async
public CompletableFuture<List<SaleData>> fetchSalesDataAsync(...) {
    // ...
}
```

## 🚀 Despliegue

### Variables de Entorno Requeridas
```env
SERVER_PORT=8084
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5435/reporting_db
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
SERVICES_SALES_URL=http://sales-service:8083
SERVICES_INVENTORY_URL=http://inventory-service:8082
```

### Docker Compose
```yaml
services:
  postgres:
    image: postgres:15-alpine
    ports: ["5435:5432"]
    environment:
      POSTGRES_DB: reporting_db
  
  reporting-service:
    build: .
    ports: ["8084:8084"]
    depends_on: [postgres]
```

## 📊 Métricas y Monitoreo (Futuro)

### Spring Actuator
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

### Endpoints de Monitoreo
- `/actuator/health`
- `/actuator/metrics`
- `/actuator/prometheus` (para Grafana)

## 🔮 Roadmap Futuro

1. **Caché distribuido** (Redis) para reportes frecuentes
2. **Procesamiento asíncrono** con Spring Async
3. **Exportación de reportes** (PDF, Excel, CSV)
4. **WebSockets** para reportes en tiempo real
5. **Machine Learning** para predicciones
6. **GraphQL** para consultas flexibles
7. **Event Sourcing** para auditoría completa
8. **CQRS** para separar lecturas y escrituras

## 📚 Referencias

- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Spring WebFlux](https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html)
- [PostgreSQL Performance](https://www.postgresql.org/docs/current/performance-tips.html)
- [Microservices Patterns](https://microservices.io/patterns/)
