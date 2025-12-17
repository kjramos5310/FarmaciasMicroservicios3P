# Sales Service - Microservicio de Gestión de Ventas

Microservicio Spring Boot para gestión de ventas, clientes y prescripciones médicas.

## Características

- **Gestión de Clientes**: CRUD completo con tipos de identificación (CI/PASSPORT/RUC), puntos de lealtad y clasificación (REGULAR/VIP/CORPORATE)
- **Gestión de Ventas**: Creación de ventas con múltiples items, cálculo automático de IVA (12%), descuentos y totales
- **Gestión de Prescripciones**: Control de prescripciones médicas con validación de vigencia
- **Validación de Productos**: Verificación de productos que requieren prescripción médica
- **Comunicación con Microservicios**: Integración con catalog-service e inventory-service mediante OpenFeign

## Tecnologías

- Java 17
- Spring Boot 3.2.0
- Spring Data JPA
- Spring Cloud OpenFeign
- PostgreSQL
- Lombok
- Bean Validation

## Endpoints

### Clientes (`/api/customers`)
- `POST /api/customers` - Crear cliente
- `GET /api/customers` - Listar clientes (paginado)
- `GET /api/customers/{id}` - Obtener cliente por ID
- `PUT /api/customers/{id}` - Actualizar cliente
- `DELETE /api/customers/{id}` - Eliminar cliente
- `GET /api/customers/{id}/history` - Historial de ventas del cliente
- `PUT /api/customers/{id}/loyalty?points={puntos}` - Actualizar puntos de lealtad

### Ventas (`/api/sales`)
- `POST /api/sales` - Crear venta
- `GET /api/sales` - Listar ventas (paginado)
- `GET /api/sales/{id}` - Obtener venta por ID
- `GET /api/sales/branch/{branchId}` - Ventas por sucursal
- `GET /api/sales/customer/{customerId}` - Ventas por cliente
- `GET /api/sales/date-range?start={fecha}&end={fecha}` - Ventas por rango de fechas
- `PUT /api/sales/{id}/status?status={estado}` - Cambiar estado de venta

### Prescripciones (`/api/prescriptions`)
- `POST /api/prescriptions` - Crear prescripción
- `GET /api/prescriptions/{id}` - Obtener prescripción por ID
- `GET /api/prescriptions/customer/{customerId}` - Prescripciones por cliente
- `PUT /api/prescriptions/{id}/status?status={estado}` - Actualizar estado de prescripción

## Lógica de Negocio

### Cálculo de Totales en Ventas
```
Subtotal = Suma de (cantidad × precio_unitario - descuento_item)
IVA = Subtotal × 0.12
Total = Subtotal + IVA - Descuento_General
```

### Validación de Prescripciones
- Si un producto requiere prescripción, debe estar asociada una prescripción válida
- La prescripción debe estar en estado ACTIVE
- La fecha de expiración debe ser mayor o igual a la fecha actual
- Al completar la venta, la prescripción cambia a estado USED

### Generación de Números
- **Ventas**: `SALE-{AÑO}-{CONTADOR}` (ej: SALE-2025-000001)
- **Prescripciones**: `RX-{AÑO}-{CONTADOR}` (ej: RX-2025-000001)

## Configuración

### Base de Datos
```yaml
Host: localhost
Puerto: 5434
Database: sales_db
Usuario: postgres
Contraseña: postgres
```

### Puerto del Servicio
```
8083
```

### Servicios Externos
- **Catalog Service**: http://catalog-service:8081
- **Inventory Service**: http://inventory-service:8082

## Compilar y Ejecutar

### Con Maven
```bash
./mvnw clean package
./mvnw spring-boot:run
```

### Con Docker
```bash
# Construir imagen
docker build -t sales-service:latest .

# Ejecutar contenedor
docker run -p 8083:8083 sales-service:latest
```

## Excepciones Personalizadas

- `InvalidSaleException` (400) - Venta inválida
- `PrescriptionRequiredException` (400) - Prescripción requerida pero no proporcionada
- `ExpiredPrescriptionException` (400) - Prescripción expirada o inactiva

## Validaciones

- Email válido para clientes
- Teléfono con formato de 9 o 10 dígitos
- Fecha de expiración de prescripción debe ser futura
- Items de venta con cantidad mayor a cero
- Número de identificación único por cliente
