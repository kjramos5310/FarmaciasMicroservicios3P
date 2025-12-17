# Changelog

Todos los cambios notables en este proyecto serán documentados en este archivo.

El formato está basado en [Keep a Changelog](https://keepachangelog.com/es-ES/1.0.0/),
y este proyecto adhiere a [Semantic Versioning](https://semver.org/lang/es/).

## [0.0.1] - 2025-12-16

### Agregado
- ✨ Configuración inicial del proyecto Spring Boot 3.2.0
- ✨ Entidades JPA: SalesSummary, InventorySummary, ProductSalesReport
- ✨ Repositorios con queries personalizadas (@Query)
- ✨ DTOs para requests y responses
- ✨ Configuración de WebClient para comunicación con servicios externos
- ✨ ReportingService con lógica de agregación y cálculos
- ✨ Endpoints REST para reportes de ventas:
  - GET `/api/reports/sales/summary` - Resumen de ventas por período
  - GET `/api/reports/sales/by-product` - Ventas por producto
  - GET `/api/reports/sales/top-products` - Top productos más vendidos
  - GET `/api/reports/sales/by-branch` - Comparativo por sucursales
- ✨ Endpoints REST para reportes de inventario:
  - GET `/api/reports/inventory/summary` - Resumen de inventario
  - GET `/api/reports/inventory/low-stock` - Productos bajo stock
  - GET `/api/reports/inventory/expiring` - Productos próximos a vencer
  - GET `/api/reports/inventory/value` - Valor total de inventario
- ✨ Endpoint de dashboard ejecutivo:
  - GET `/api/reports/dashboard` - Métricas generales
- ✨ SnapshotService para generación automática de snapshots diarios
- ✨ SnapshotController para gestión de snapshots históricos:
  - POST `/api/snapshots/generate` - Generar snapshots históricos
  - GET `/api/snapshots/sales` - Obtener snapshots de ventas
  - GET `/api/snapshots/inventory` - Obtener snapshots de inventario
- ✨ GlobalExceptionHandler para manejo centralizado de errores
- ✨ Mappers para conversión Entity ↔ DTO
- ✨ Configuración de PostgreSQL (puerto 5435)
- ✨ Dockerfile multi-stage para optimización de imagen
- ✨ docker-compose.yml con PostgreSQL y reporting-service
- ✨ Scripts de inicio rápido (start.sh y start.bat)
- ✨ Perfiles de configuración (dev y prod)
- ✨ Scripts SQL con índices, vistas y consultas útiles
- ✨ Colección Postman con todos los endpoints
- 📚 Documentación completa:
  - README.md con guía de uso
  - ARCHITECTURE.md con arquitectura técnica
  - CONTRIBUTING.md con guía de contribución
  - Este CHANGELOG.md

### Características Técnicas
- Java 17
- Spring Boot 3.2.0
- Spring Data JPA con PostgreSQL
- Spring WebFlux (WebClient) para comunicación entre servicios
- Lombok para reducir boilerplate
- Queries personalizadas con agregaciones (SUM, COUNT, AVG)
- Logging con SLF4J
- CORS habilitado
- Scheduled tasks preparado (comentado por defecto)

### Base de Datos
- PostgreSQL 15
- 3 tablas principales: sales_summary, inventory_summary, product_sales_report
- Índices optimizados en campos de fecha y sucursal
- Vistas para análisis: monthly_sales_summary, inventory_alerts

### Integraciones Externas
- Sales Service (puerto 8083): `/api/sales/date-range`
- Inventory Service (puerto 8082): `/api/stock`

### Documentación
- README completo con ejemplos de uso
- Arquitectura técnica detallada
- Guía de contribución con convenciones de código
- Scripts SQL para administración
- Colección Postman para testing

### Configuración
- Perfiles: dev (desarrollo) y prod (producción)
- Variables de entorno configurables
- Logging configurado por perfil
- Connection pooling con HikariCP

### Docker
- Dockerfile optimizado multi-stage
- docker-compose.yml con servicios completos
- Healthcheck de PostgreSQL
- Redes Docker configuradas

## [Próximas Versiones]

### [0.1.0] - Planificado
- [ ] Agregar caché con Redis
- [ ] Implementar procesamiento asíncrono
- [ ] Agregar exportación de reportes (PDF, Excel)
- [ ] Integrar Spring Actuator para métricas
- [ ] Agregar autenticación/autorización
- [ ] Implementar rate limiting

### [0.2.0] - Planificado
- [ ] WebSockets para reportes en tiempo real
- [ ] Soporte para múltiples idiomas (i18n)
- [ ] Dashboard web con gráficos
- [ ] Notificaciones automáticas por email
- [ ] API Gateway integration

### [1.0.0] - Planificado
- [ ] Machine Learning para predicciones
- [ ] GraphQL API
- [ ] Event Sourcing para auditoría
- [ ] CQRS pattern
- [ ] Kubernetes deployment files

---

## Tipos de Cambios

- `Agregado` - para nuevas funcionalidades
- `Cambiado` - para cambios en funcionalidades existentes
- `Deprecado` - para funcionalidades que serán eliminadas
- `Eliminado` - para funcionalidades eliminadas
- `Corregido` - para corrección de bugs
- `Seguridad` - para vulnerabilidades corregidas
