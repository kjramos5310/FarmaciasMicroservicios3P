# Sales Service - Guía de Inicio Rápido

## Requisitos Previos

- Java 17+
- Maven 3.6+
- Docker y Docker Compose (opcional)
- PostgreSQL 15+ (si no se usa Docker)

## Opción 1: Ejecutar con Docker Compose (Recomendado)

1. Compilar el proyecto:
```bash
./mvnw clean package -DskipTests
```

2. Iniciar servicios con Docker Compose:
```bash
docker-compose up -d
```

3. Ver logs:
```bash
docker-compose logs -f sales-service
```

4. Detener servicios:
```bash
docker-compose down
```

## Opción 2: Ejecutar Localmente

1. Iniciar PostgreSQL:
```bash
docker run -d \
  --name sales-postgres \
  -e POSTGRES_DB=sales_db \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5434:5432 \
  postgres:15-alpine
```

2. Compilar y ejecutar la aplicación:
```bash
./mvnw clean spring-boot:run
```

O con Maven instalado:
```bash
mvn clean spring-boot:run
```

## Opción 3: Ejecutar el JAR

1. Compilar el proyecto:
```bash
./mvnw clean package
```

2. Ejecutar el JAR:
```bash
java -jar target/sales-service-0.0.1-SNAPSHOT.jar
```

## Verificar que el Servicio Está Corriendo

Acceder a: http://localhost:8083/api/customers

Deberías recibir una respuesta JSON (lista vacía inicialmente):
```json
{
  "content": [],
  "pageable": {...},
  "totalElements": 0,
  ...
}
```

## Configuración de Variables de Entorno

Para cambiar la configuración de la base de datos:

```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5434/sales_db
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=postgres
```

Para configurar las URLs de otros microservicios:

```bash
export SERVICES_CATALOG_URL=http://localhost:8081
export SERVICES_INVENTORY_URL=http://localhost:8082
```

## Estructura del Proyecto

```
sales-service/
├── src/
│   ├── main/
│   │   ├── java/com/example/sales_service/
│   │   │   ├── client/          # Clientes Feign
│   │   │   ├── controller/      # Controladores REST
│   │   │   ├── dto/             # DTOs y requests
│   │   │   ├── entity/          # Entidades JPA
│   │   │   ├── enums/           # Enumeraciones
│   │   │   ├── exception/       # Excepciones personalizadas
│   │   │   ├── mapper/          # Mapeadores
│   │   │   ├── repository/      # Repositorios JPA
│   │   │   ├── service/         # Lógica de negocio
│   │   │   └── SalesServiceApplication.java
│   │   └── resources/
│   │       └── application.yml
│   └── test/
├── Dockerfile
├── docker-compose.yml
├── pom.xml
├── README.md
└── API-EXAMPLES.md
```

## Probar la API

Ver el archivo `API-EXAMPLES.md` para ejemplos detallados de requests.

### Ejemplo Rápido con cURL

1. Crear un cliente:
```bash
curl -X POST http://localhost:8083/api/customers \
  -H "Content-Type: application/json" \
  -d '{
    "identificationNumber": "1234567890",
    "identificationType": "CI",
    "firstName": "Juan",
    "lastName": "Pérez",
    "email": "juan.perez@email.com",
    "phone": "0987654321",
    "address": "Av. Principal 123",
    "city": "Quito",
    "type": "REGULAR"
  }'
```

2. Listar clientes:
```bash
curl http://localhost:8083/api/customers
```

## Problemas Comunes

### Puerto 8083 ya en uso
```bash
# Linux/Mac
lsof -i :8083
kill -9 <PID>

# Windows
netstat -ano | findstr :8083
taskkill /PID <PID> /F
```

### Puerto 5434 ya en uso (PostgreSQL)
```bash
# Linux/Mac
lsof -i :5434
kill -9 <PID>

# Windows
netstat -ano | findstr :5434
taskkill /PID <PID> /F
```

### Error de conexión a PostgreSQL
Verificar que PostgreSQL esté corriendo:
```bash
docker ps | grep postgres
```

### Error de compilación
Limpiar y recompilar:
```bash
./mvnw clean install -U
```

## Logs

Los logs se muestran en consola con nivel DEBUG para el paquete `com.example.sales_service`.

Para cambiar el nivel de logs, editar `application.yml`:
```yaml
logging:
  level:
    com.example.sales_service: INFO  # Cambiar a INFO, WARN o ERROR
```

## Monitoreo

- **Health Check**: http://localhost:8083/actuator/health (si se agrega Spring Actuator)
- **Database**: Conectarse con cualquier cliente PostgreSQL a `localhost:5434`

## Próximos Pasos

1. Implementar el microservicio `catalog-service` en el puerto 8081
2. Implementar el microservicio `inventory-service` en el puerto 8082
3. Configurar un API Gateway para enrutar las peticiones
4. Agregar autenticación y autorización
5. Implementar circuit breakers con Resilience4j
6. Agregar trazabilidad distribuida con Sleuth/Zipkin
