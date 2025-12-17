# Reporting Service

Microservicio Spring Boot para generación de reportes y analítica de ventas e inventario.

## 🚀 Tecnologías

- **Java 17**
- **Spring Boot 3.2.0**
- **PostgreSQL** (puerto 5435)
- **Spring Data JPA**
- **Spring WebFlux** (WebClient para comunicación entre servicios)
- **Lombok**

## 📋 Características

### Reportes de Ventas
- ✅ Resumen de ventas por período
- ✅ Ventas por producto
- ✅ Top productos más vendidos
- ✅ Comparativo por sucursales

### Reportes de Inventario
- ✅ Resumen de inventario
- ✅ Productos con stock bajo
- ✅ Productos próximos a vencer (< 30 días)
- ✅ Valor total de inventario

### Dashboard Ejecutivo
- ✅ Métricas generales de ventas e inventario

## 🏗️ Arquitectura

```
reporting-service/
├── entity/              # Entidades JPA
│   ├── SalesSummary
│   ├── InventorySummary
│   └── ProductSalesReport
├── dto/                 # DTOs de request/response
├── repository/          # Repositorios JPA con queries personalizadas
├── service/             # Lógica de negocio y agregaciones
├── controller/          # Endpoints REST
├── mapper/              # Mappers entidad ↔ DTO
├── config/              # Configuración WebClient
└── exception/           # Manejo global de excepciones
```

## 🔌 Endpoints

### Sales Reports

```http
GET /api/reports/sales/summary?startDate={date}&endDate={date}&branchId={id}
GET /api/reports/sales/by-product?startDate={date}&endDate={date}
GET /api/reports/sales/top-products?limit=10
GET /api/reports/sales/by-branch?startDate={date}&endDate={date}
```

### Inventory Reports

```http
GET /api/reports/inventory/summary?branchId={id}
GET /api/reports/inventory/low-stock?branchId={id}
GET /api/reports/inventory/expiring?branchId={id}
GET /api/reports/inventory/value?branchId={id}
```

### Dashboard

```http
GET /api/reports/dashboard
```

### Health Check

```http
GET /api/reports/health
```

## 🗄️ Base de Datos

**PostgreSQL**
- Host: `localhost`
- Puerto: `5435`
- Base de datos: `reporting_db`
- Usuario: `postgres`
- Password: `postgres`

### Tablas

1. **sales_summary**: Resúmenes de ventas por fecha y sucursal
2. **inventory_summary**: Resúmenes de inventario por fecha y sucursal
3. **product_sales_report**: Reportes detallados de ventas por producto

## 🔗 Servicios Externos

El servicio consume datos de:

- **sales-service**: `http://sales-service:8083`
  - Endpoint: `/api/sales/date-range`
  
- **inventory-service**: `http://inventory-service:8082`
  - Endpoint: `/api/stock`

## 🚀 Ejecución

### Requisitos

- Java 17
- PostgreSQL
- Maven

### Local

```bash
# Configurar base de datos PostgreSQL en puerto 5435
# Crear base de datos 'reporting_db'

# Compilar
mvn clean package

# Ejecutar
java -jar target/reporting-service-0.0.1-SNAPSHOT.jar
```

El servicio estará disponible en `http://localhost:8084`

### Docker

```bash
# Construir imagen
docker build -t reporting-service:latest .

# Ejecutar contenedor
docker run -p 8084:8084 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5435/reporting_db \
  -e SERVICES_SALES_URL=http://sales-service:8083 \
  -e SERVICES_INVENTORY_URL=http://inventory-service:8082 \
  reporting-service:latest
```

## 📊 Ejemplos de Uso

### Resumen de ventas

```bash
curl "http://localhost:8084/api/reports/sales/summary?startDate=2025-01-01&endDate=2025-01-31&branchId=1"
```

### Top 10 productos

```bash
curl "http://localhost:8084/api/reports/sales/top-products?limit=10"
```

### Dashboard ejecutivo

```bash
curl "http://localhost:8084/api/reports/dashboard"
```

### Productos próximos a vencer

```bash
curl "http://localhost:8084/api/reports/inventory/expiring?branchId=1"
```

## 📝 Formato de Respuestas

### SalesSummaryResponse

```json
{
  "reportDate": "2025-12-16",
  "branchId": 1,
  "branchName": "Sucursal 1",
  "totalSales": 150,
  "totalRevenue": 45000.50,
  "averageTicket": 300.00,
  "totalItems": 450,
  "uniqueCustomers": 120
}
```

### DashboardResponse

```json
{
  "salesMetrics": {
    "totalRevenue": 150000.00,
    "totalSales": 500,
    "averageTicket": 300.00,
    "uniqueCustomers": 350
  },
  "inventoryMetrics": {
    "totalProducts": 1500,
    "lowStockProducts": 25,
    "expiringSoon": 10,
    "totalInventoryValue": 500000.00
  }
}
```

## 🔮 Funcionalidades Opcionales

Para implementar snapshots automáticos diarios:

1. Agregar dependencia de Scheduling
2. Crear `@Scheduled` task:

```java
@Scheduled(cron = "0 0 0 * * *") // Medianoche
public void generateDailySnapshots() {
    // Generar y guardar SalesSummary e InventorySummary
}
```

## 📄 Licencia

Proyecto educativo - Sistema Distribuido de Gestión de Inventario y Ventas
