# Inventory Service - Microservicio de Gestión de Inventario

Microservicio Spring Boot para la gestión de inventario y sucursales, desarrollado con arquitectura REST y base de datos PostgreSQL.

## 🚀 Características

- **Gestión de Sucursales**: CRUD completo para sucursales con estados (ACTIVA/CERRADA/MANTENIMIENTO)
- **Control de Stock**: Seguimiento de inventario por sucursal y producto con alertas de stock mínimo
- **Gestión de Lotes**: Control de lotes con fechas de vencimiento y alertas
- **Movimientos de Stock**: Registro de entradas, salidas, transferencias, ajustes y devoluciones
- **Validaciones**: Validaciones exhaustivas con mensajes en español
- **Manejo de Errores**: Sistema centralizado de manejo de excepciones
- **Logging**: Sistema de logs con Slf4j

## 📋 Requisitos Previos

- Java 17+
- Maven 3.6+
- PostgreSQL 12+
- Docker (opcional)

## ⚙️ Configuración

### Base de Datos

Crear la base de datos en PostgreSQL:

```sql
CREATE DATABASE inventory_db;
```

### Variables de Entorno

El servicio usa las siguientes configuraciones (definidas en `application.yml`):

- **Puerto**: 8082
- **Base de datos**: PostgreSQL en localhost:5433
- **Usuario**: postgres
- **Contraseña**: postgres
- **Base de datos**: inventory_db

## 🏗️ Estructura del Proyecto

```
src/main/java/com/example/inventory_service/
├── controller/          # Controladores REST
│   ├── BranchController.java
│   ├── StockController.java
│   ├── BatchController.java
│   └── StockMovementController.java
├── service/            # Lógica de negocio
│   ├── BranchService.java
│   ├── StockService.java
│   ├── BatchService.java
│   └── StockMovementService.java
├── repository/         # Acceso a datos
│   ├── BranchRepository.java
│   ├── StockRepository.java
│   ├── BatchRepository.java
│   └── StockMovementRepository.java
├── entity/            # Entidades JPA
│   ├── Branch.java
│   ├── Stock.java
│   ├── Batch.java
│   ├── StockMovement.java
│   └── enums/
│       ├── BranchStatus.java
│       ├── BatchStatus.java
│       └── MovementType.java
├── dto/               # Data Transfer Objects
│   ├── request/
│   └── response/
├── mapper/            # Mappers Entity <-> DTO
├── exception/         # Excepciones personalizadas
└── InventoryServiceApplication.java
```

## 🔌 Endpoints API

### Sucursales (`/api/branches`)

- `GET /api/branches` - Obtener todas las sucursales
- `GET /api/branches/{id}` - Obtener sucursal por ID
- `POST /api/branches` - Crear nueva sucursal
- `PUT /api/branches/{id}` - Actualizar sucursal
- `DELETE /api/branches/{id}` - Eliminar sucursal

### Stock (`/api/stock`)

- `GET /api/stock` - Obtener todo el stock
- `GET /api/stock/{branchId}` - Obtener stock por sucursal
- `GET /api/stock/{branchId}/{productId}` - Obtener stock específico
- `POST /api/stock` - Crear o actualizar stock
- `GET /api/stock/alerts` - Obtener alertas de stock bajo

### Lotes (`/api/batches`)

- `POST /api/batches` - Crear nuevo lote
- `GET /api/batches` - Obtener todos los lotes
- `GET /api/batches/branch/{branchId}` - Obtener lotes por sucursal
- `GET /api/batches/expiring` - Obtener lotes por vencer (30 días)

### Movimientos (`/api/movements`)

- `POST /api/movements` - Registrar nuevo movimiento
- `GET /api/movements/branch/{branchId}` - Obtener movimientos por sucursal
- `GET /api/movements/product/{productId}` - Obtener movimientos por producto

## 📝 Ejemplos de Uso

### Crear Sucursal

```json
POST /api/branches
{
  "code": "SUC001",
  "name": "Sucursal Centro",
  "address": "Av. Principal 123",
  "city": "Lima",
  "province": "Lima",
  "phone": "+51-999-888-777",
  "email": "centro@empresa.com",
  "managerName": "Juan Pérez",
  "status": "ACTIVE",
  "openingTime": "08:00:00",
  "closingTime": "20:00:00"
}
```

### Crear Stock

```json
POST /api/stock
{
  "branchId": 1,
  "productId": 100,
  "quantity": 50,
  "minimumStock": 10,
  "maximumStock": 100
}
```

### Registrar Movimiento de Salida

```json
POST /api/movements
{
  "branchId": 1,
  "productId": 100,
  "type": "EXIT",
  "quantity": 5,
  "reason": "Venta al cliente",
  "performedBy": "María González"
}
```

### Registrar Transferencia

```json
POST /api/movements
{
  "branchId": 1,
  "productId": 100,
  "type": "TRANSFER",
  "quantity": 10,
  "destinationBranchId": 2,
  "reason": "Reposición de stock",
  "performedBy": "Carlos López"
}
```

## 🐳 Docker

### Construir Imagen

```bash
docker build -t inventory-service:latest .
```

### Ejecutar Contenedor

```bash
docker run -d \
  -p 8082:8082 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5433/inventory_db \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=postgres \
  --name inventory-service \
  inventory-service:latest
```

## 🚀 Ejecución Local

### Compilar

```bash
mvn clean install
```

### Ejecutar

```bash
mvn spring-boot:run
```

O ejecutar el JAR directamente:

```bash
java -jar target/inventory-service-0.0.1-SNAPSHOT.jar
```

## 🧪 Validaciones Implementadas

- **@NotNull**: Campos obligatorios
- **@NotBlank**: Cadenas no vacías
- **@Positive**: Números positivos
- **@PositiveOrZero**: Números no negativos
- **@Size**: Longitud de cadenas
- **@Email**: Formato de email válido
- **@Pattern**: Patrones regex (teléfonos)
- **@Future**: Fechas futuras (vencimiento)
- **@PastOrPresent**: Fechas pasadas o presentes (fabricación)

## ⚠️ Lógica Especial

### Movimientos de Stock

1. **ENTRY**: Incrementa stock automáticamente
2. **EXIT**: Decrementa stock (valida disponibilidad)
3. **TRANSFER**: Decrementa origen e incrementa destino (valida disponibilidad y sucursal diferente)
4. **ADJUSTMENT**: Ajusta stock (puede ser positivo o negativo)
5. **RETURN**: Incrementa stock (devoluciones)

### Validaciones de Negocio

- Todas las cantidades deben ser mayores a cero
- Stock máximo debe ser mayor al mínimo
- No se permiten operaciones EXIT/TRANSFER sin stock suficiente
- En TRANSFER, la sucursal destino debe ser diferente a la origen
- Los códigos de sucursal deben ser únicos

## 📊 Códigos de Estado HTTP

- **200 OK**: Operación exitosa
- **201 CREATED**: Recurso creado exitosamente
- **400 BAD REQUEST**: Error de validación o stock insuficiente
- **404 NOT FOUND**: Recurso no encontrado
- **409 CONFLICT**: Recurso duplicado
- **500 INTERNAL SERVER ERROR**: Error interno del servidor

## 📦 Dependencias Principales

- Spring Boot 3.2.0
- Spring Data JPA
- Spring Web
- Spring Validation
- PostgreSQL Driver
- Lombok

## 👥 Autor

Desarrollado para el curso de Sistemas Distribuidos - Semestre 2025-2026

## 📄 Licencia

Este proyecto es de código abierto y está disponible bajo la licencia MIT.
