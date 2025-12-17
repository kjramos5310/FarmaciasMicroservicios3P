# Sistema de Microservicios de Farmacia - Docker

Sistema completo de microservicios para gestión de farmacia con 4 servicios independientes y sus respectivas bases de datos PostgreSQL.

## 🏗️ Arquitectura

```
pharmacy-microservices/
├── catalog-service (Puerto 8081) → catalog-db (Puerto 5432)
├── inventory-service (Puerto 8082) → inventory-db (Puerto 5433)
├── sales-service (Puerto 8083) → sales-db (Puerto 5434)
└── reporting-service (Puerto 8084) → reporting-db (Puerto 5435)
```

## 📋 Requisitos Previos

- Docker Desktop instalado y ejecutándose
- Docker Compose v3.8 o superior
- Puertos disponibles: 5432-5435 (bases de datos) y 8081-8084 (servicios)
- Al menos 4GB de RAM disponible

## 🚀 Inicio Rápido

### 1. Construir e iniciar todos los servicios

```bash
docker-compose up -d --build
```

Este comando:
- Construye las imágenes Docker de los 4 microservicios
- Descarga las imágenes de PostgreSQL 15
- Inicia todos los contenedores en segundo plano
- Crea la red `pharmacy-network`
- Crea los volúmenes para persistencia de datos

### 2. Verificar el estado de los servicios

```bash
docker-compose ps
```

Deberías ver 8 contenedores en estado "Up" (4 bases de datos + 4 microservicios).

### 3. Ver logs de todos los servicios

```bash
docker-compose logs -f
```

Ver logs de un servicio específico:
```bash
docker-compose logs -f catalog-service
docker-compose logs -f sales-service
```

### 4. Verificar salud de los servicios

```bash
# Catalog Service
curl http://localhost:8081/actuator/health

# Inventory Service
curl http://localhost:8082/actuator/health

# Sales Service
curl http://localhost:8083/actuator/health

# Reporting Service
curl http://localhost:8084/actuator/health
```

## 🛑 Detener los Servicios

### Detener sin eliminar contenedores
```bash
docker-compose stop
```

### Detener y eliminar contenedores (los datos persisten en volúmenes)
```bash
docker-compose down
```

### Detener, eliminar contenedores Y volúmenes (¡CUIDADO: elimina todos los datos!)
```bash
docker-compose down -v
```

## 🔄 Reiniciar Servicios

```bash
# Reiniciar todos los servicios
docker-compose restart

# Reiniciar un servicio específico
docker-compose restart sales-service
```

## 🔧 Reconstruir un Servicio

Si haces cambios en el código de un servicio:

```bash
# Reconstruir un servicio específico
docker-compose up -d --build catalog-service

# Reconstruir todos los servicios
docker-compose up -d --build
```

## 📊 Acceso a las Bases de Datos

Puedes conectarte a las bases de datos usando cualquier cliente PostgreSQL:

### Catalog DB
- Host: localhost
- Puerto: 5432
- Database: catalog_db
- Usuario: postgres
- Contraseña: postgres

### Inventory DB
- Host: localhost
- Puerto: 5433
- Database: inventory_db
- Usuario: postgres
- Contraseña: postgres

### Sales DB
- Host: localhost
- Puerto: 5434
- Database: sales_db
- Usuario: postgres
- Contraseña: postgres

### Reporting DB
- Host: localhost
- Puerto: 5435
- Database: reporting_db
- Usuario: postgres
- Contraseña: postgres

## 🐛 Troubleshooting

### Los servicios no inician
```bash
# Ver logs detallados
docker-compose logs

# Verificar que no haya conflictos de puertos
netstat -ano | findstr "8081 8082 8083 8084 5432 5433 5434 5435"
```

### Error de conexión entre servicios
```bash
# Verificar que todos los contenedores estén en la misma red
docker network inspect pharmacy-network

# Verificar conectividad entre contenedores
docker exec sales-service ping catalog-service
```

### Base de datos no responde
```bash
# Verificar logs de la base de datos
docker-compose logs catalog-db

# Reiniciar la base de datos específica
docker-compose restart catalog-db
```

### Limpiar y empezar de cero
```bash
# Eliminar todo (contenedores, redes, volúmenes)
docker-compose down -v

# Limpiar imágenes antiguas
docker system prune -a

# Volver a construir e iniciar
docker-compose up -d --build
```

## 📝 Endpoints de los Servicios

### Catalog Service (8081)
- Health: `GET http://localhost:8081/actuator/health`
- API: `http://localhost:8081/api/...`

### Inventory Service (8082)
- Health: `GET http://localhost:8082/actuator/health`
- API: `http://localhost:8082/api/...`

### Sales Service (8083)
- Health: `GET http://localhost:8083/actuator/health`
- API: `http://localhost:8083/api/...`
- Consume: catalog-service, inventory-service

### Reporting Service (8084)
- Health: `GET http://localhost:8084/actuator/health`
- API: `http://localhost:8084/api/...`
- Consume: sales-service, inventory-service

## 🔐 Variables de Entorno

Cada servicio está configurado con variables de entorno que puedes modificar en el `docker-compose.yml`:

- `SPRING_DATASOURCE_URL`: URL de conexión a la base de datos
- `SPRING_DATASOURCE_USERNAME`: Usuario de la base de datos
- `SPRING_DATASOURCE_PASSWORD`: Contraseña de la base de datos
- `CATALOG_SERVICE_URL`: URL del servicio de catálogo (sales-service)
- `INVENTORY_SERVICE_URL`: URL del servicio de inventario (sales-service, reporting-service)
- `SALES_SERVICE_URL`: URL del servicio de ventas (reporting-service)

## 📦 Volúmenes de Datos

Los datos de PostgreSQL se persisten en volúmenes Docker:

- `catalog-db-data`
- `inventory-db-data`
- `sales-db-data`
- `reporting-db-data`

Para hacer backup de los datos:
```bash
docker run --rm -v catalog-db-data:/data -v $(pwd):/backup ubuntu tar czf /backup/catalog-backup.tar.gz -C /data .
```

## 🌐 Red Docker

Todos los servicios se comunican a través de la red `pharmacy-network`:
- Tipo: bridge
- Los servicios se pueden comunicar usando sus nombres de contenedor como DNS
- Ejemplo: `sales-service` puede acceder a `catalog-service` usando `http://catalog-service:8081`

## 📈 Monitoreo

Los servicios incluyen health checks que Docker ejecuta automáticamente:
- Intervalo: 30 segundos
- Timeout: 10 segundos
- Reintentos: 3
- Período de inicio: 40 segundos

Ver estado de health checks:
```bash
docker-compose ps
```

## 🚀 Producción

Para producción, considera:

1. **Cambiar credenciales**: Usa variables de entorno seguras
2. **Configurar límites de recursos**: Añade `mem_limit` y `cpus` a cada servicio
3. **Configurar logging**: Usa un driver de logging apropiado
4. **Usar secrets**: Para información sensible
5. **Configurar backups automáticos**: Para las bases de datos
6. **Usar reverse proxy**: Como Nginx o Traefik
7. **Implementar monitoreo**: Como Prometheus + Grafana

## 📞 Soporte

Para más información sobre cada servicio individual, consulta los README en cada carpeta de servicio.
