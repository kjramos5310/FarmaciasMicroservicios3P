# Catalog Service - Microservicio de Gestión de Catálogo de Medicamentos

Microservicio Spring Boot para gestión completa de catálogo de medicamentos con arquitectura de capas.

## 📋 Características

- **API REST** completa con operaciones CRUD
- **Validaciones** exhaustivas en todos los endpoints
- **Manejo de errores** centralizado con mensajes en español
- **Desnormalización** en respuestas para mejor rendimiento
- **Base de datos PostgreSQL** con JPA/Hibernate
- **Logging** con SLF4J/Logback
- **Dockerización** lista para producción

## 🏗️ Arquitectura

```
catalog-service/
├── entity/              # Entidades JPA (Product, Category, Laboratory)
│   └── enums/          # Enumeraciones (ProductStatus)
├── dto/
│   ├── request/        # DTOs de entrada con validaciones
│   └── response/       # DTOs de salida estandarizados
├── repository/         # Repositorios JPA con queries custom
├── service/            # Lógica de negocio con @Transactional
├── controller/         # Controladores REST
├── mapper/             # Conversión Entity <-> DTO
└── exception/          # Excepciones custom y GlobalExceptionHandler
```

## 🚀 Endpoints

### Products (`/api/products`)
- `POST /api/products` - Crear producto
- `GET /api/products` - Listar todos
- `GET /api/products/{id}` - Obtener por ID
- `GET /api/products/code/{code}` - Obtener por código
- `GET /api/products/search?keyword=` - Búsqueda por palabra clave
- `PUT /api/products/{id}` - Actualizar producto
- `DELETE /api/products/{id}` - Eliminar producto

### Categories (`/api/categories`)
- `POST /api/categories` - Crear categoría
- `GET /api/categories` - Listar todas
- `GET /api/categories/{id}` - Obtener por ID
- `GET /api/categories/code/{code}` - Obtener por código
- `GET /api/categories/active` - Listar categorías activas
- `GET /api/categories/search?keyword=` - Búsqueda
- `PUT /api/categories/{id}` - Actualizar
- `DELETE /api/categories/{id}` - Eliminar

### Laboratories (`/api/laboratories`)
- `POST /api/laboratories` - Crear laboratorio
- `GET /api/laboratories` - Listar todos
- `GET /api/laboratories/{id}` - Obtener por ID
- `GET /api/laboratories/name/{name}` - Obtener por nombre
- `GET /api/laboratories/active` - Listar laboratorios activos
- `GET /api/laboratories/search?keyword=` - Búsqueda
- `PUT /api/laboratories/{id}` - Actualizar
- `DELETE /api/laboratories/{id}` - Eliminar

## 📦 Modelo de Datos

### Product
- `id`, `code` (unique), `barcode` (unique), `name`, `genericName`
- `description`, `presentation`, `basePrice`
- `category` (ManyToOne), `laboratory` (ManyToOne)
- `requiresPrescription`, `isControlled`
- `activeIngredient`, `contraindications`, `dosage`
- `status` (ACTIVE/DISCONTINUED/OUT_OF_STOCK)
- `createdAt`, `updatedAt`

### Category
- `id`, `code` (unique), `name`, `description`
- `parentCategory` (self-reference)
- `isActive`, `createdAt`

### Laboratory
- `id`, `name` (unique), `country`
- `contactEmail`, `phone`, `website`
- `isActive`, `createdAt`

## ⚙️ Configuración

### Requisitos
- Java 17
- Maven 3.6+
- PostgreSQL 12+
- Docker (opcional)

### Base de Datos
```sql
CREATE DATABASE catalog_db;
```

### application.yml
```yaml
server:
  port: 8081

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/catalog_db
    username: postgres
    password: postgres
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
```

## 🏃 Ejecución

### Local
```bash
# Compilar
mvn clean package

# Ejecutar
mvn spring-boot:run
```

### Docker
```bash
# Construir imagen
docker build -t catalog-service:latest .

# Ejecutar contenedor
docker run -p 8081:8081 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/catalog_db \
  catalog-service:latest
```

## 📝 Ejemplo de Request

### Crear Producto
```json
POST /api/products
{
  "code": "MED001",
  "barcode": "7501234567890",
  "name": "Paracetamol 500mg",
  "genericName": "Paracetamol",
  "description": "Analgésico y antipirético",
  "presentation": "Caja con 20 tabletas",
  "categoryId": 1,
  "laboratoryId": 1,
  "basePrice": 45.50,
  "requiresPrescription": false,
  "isControlled": false,
  "activeIngredient": "Paracetamol 500mg",
  "contraindications": "Hipersensibilidad al principio activo",
  "dosage": "1 tableta cada 6-8 horas",
  "status": "ACTIVE"
}
```

### Response
```json
{
  "success": true,
  "message": "Producto creado exitosamente",
  "data": {
    "id": 1,
    "code": "MED001",
    "barcode": "7501234567890",
    "name": "Paracetamol 500mg",
    "category": {
      "id": 1,
      "code": "CAT001",
      "name": "Analgésicos",
      "isActive": true
    },
    "laboratory": {
      "id": 1,
      "name": "Laboratorios Omega",
      "country": "México"
    },
    "basePrice": 45.50,
    "status": "ACTIVE",
    "createdAt": "2025-12-16T10:30:00"
  }
}
```

## 🛡️ Manejo de Errores

### 404 - Not Found
```json
{
  "success": false,
  "message": "No se encontró el producto con ID: 999",
  "status": 404,
  "timestamp": "2025-12-16T10:30:00"
}
```

### 409 - Conflict
```json
{
  "success": false,
  "message": "Ya existe un producto con el código: MED001",
  "status": 409,
  "timestamp": "2025-12-16T10:30:00"
}
```

### 400 - Validation Error
```json
{
  "success": false,
  "message": "Error de validación en los datos proporcionados",
  "status": 400,
  "timestamp": "2025-12-16T10:30:00",
  "errors": {
    "code": "El código es obligatorio",
    "basePrice": "El precio base debe ser mayor a 0"
  }
}
```

## 🧪 Testing

El servicio está listo para recibir peticiones en: `http://localhost:8081`

Puedes probar con herramientas como:
- Postman
- cURL
- Insomnia
- Thunder Client (VS Code)

## 📄 Licencia

Este proyecto es parte de un ejercicio académico.
