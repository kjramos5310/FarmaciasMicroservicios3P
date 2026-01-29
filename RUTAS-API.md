# 📋 Guía de Rutas API - Sistema de Microservicios Farmacia

## 🔐 Autenticación OAuth2

**IMPORTANTE**: Todas las rutas requieren autenticación OAuth2 a través del Gateway.

### Login
1. Accede a cualquier ruta del Gateway: `http://localhost:8123/api/...`
2. Serás redirigido automáticamente a: `http://localhost:9000/login`
3. Ingresa tus credenciales de la base de datos PostgreSQL
4. Serás redirigido de vuelta con sesión activa

### Verificar Autenticación
```bash
# Ver información del usuario autenticado
GET http://localhost:8123/

# Ver token de acceso
GET http://localhost:8123/token
```

---

## 🌐 Gateway - Puerto 8123

Todas las peticiones deben pasar por el Gateway. El Gateway se encarga de:
- ✅ Autenticación OAuth2
- ✅ Enrutamiento a microservicios
- ✅ Propagación de tokens

**Base URL**: `http://localhost:8123`

---

## 📦 Servicio de Productos (Catálogo) - Puerto 8081

**Acceso directo**: `http://localhost:8081` (sin autenticación)  
**A través del Gateway**: `http://localhost:8123/api/catalog` (con autenticación)

### 🏷️ Productos
```bash
# Crear producto
POST http://localhost:8123/api/catalog/products
Content-Type: application/json
{
  "name": "Paracetamol 500mg",
  "description": "Analgésico y antipirético",
  "categoryId": 1,
  "laboratoryId": 1,
  "price": 5.99,
  "requiresPrescription": false,
  "activeIngredient": "Paracetamol",
  "presentation": "Tabletas",
  "concentration": "500mg"
}

# Listar todos los productos
GET http://localhost:8123/api/catalog/products

# Obtener producto por ID
GET http://localhost:8123/api/catalog/products/{id}

# Obtener producto por código de barras
GET http://localhost:8123/api/catalog/products/barcode/{barcode}

# Buscar productos activos
GET http://localhost:8123/api/catalog/products/active

# Buscar productos que requieren receta
GET http://localhost:8123/api/catalog/products/prescription

# Buscar productos por categoría
GET http://localhost:8123/api/catalog/products/category/{categoryId}

# Buscar productos por laboratorio
GET http://localhost:8123/api/catalog/products/laboratory/{laboratoryId}

# Buscar productos por nombre
GET http://localhost:8123/api/catalog/products/search?query=paracetamol

# Actualizar producto
PUT http://localhost:8123/api/catalog/products/{id}

# Eliminar producto
DELETE http://localhost:8123/api/catalog/products/{id}
```

### 📂 Categorías
```bash
# Crear categoría
POST http://localhost:8123/api/catalog/categories
Content-Type: application/json
{
  "name": "Analgésicos",
  "code": "ANA",
  "description": "Medicamentos para aliviar el dolor"
}

# Listar todas las categorías
GET http://localhost:8123/api/catalog/categories

# Obtener categoría por ID
GET http://localhost:8123/api/catalog/categories/{id}

# Obtener categoría por código
GET http://localhost:8123/api/catalog/categories/code/{code}

# Listar categorías activas
GET http://localhost:8123/api/catalog/categories/active

# Buscar categorías
GET http://localhost:8123/api/catalog/categories/search?query=analgesico

# Actualizar categoría
PUT http://localhost:8123/api/catalog/categories/{id}

# Eliminar categoría
DELETE http://localhost:8123/api/catalog/categories/{id}
```

### 🏭 Laboratorios
```bash
# Crear laboratorio
POST http://localhost:8123/api/catalog/laboratories
Content-Type: application/json
{
  "name": "Bayer",
  "country": "Alemania",
  "contactEmail": "contact@bayer.com"
}

# Listar todos los laboratorios
GET http://localhost:8123/api/catalog/laboratories

# Obtener laboratorio por ID
GET http://localhost:8123/api/catalog/laboratories/{id}

# Obtener laboratorio por nombre
GET http://localhost:8123/api/catalog/laboratories/name/{name}

# Listar laboratorios activos
GET http://localhost:8123/api/catalog/laboratories/active

# Buscar laboratorios
GET http://localhost:8123/api/catalog/laboratories/search?query=bayer

# Actualizar laboratorio
PUT http://localhost:8123/api/catalog/laboratories/{id}

# Eliminar laboratorio
DELETE http://localhost:8123/api/catalog/laboratories/{id}
```

---

## 📊 Servicio de Inventario (Almacén) - Puerto 8082

**Acceso directo**: `http://localhost:8082` (sin autenticación)  
**A través del Gateway**: `http://localhost:8123/api/inventory` (con autenticación)

### 🏪 Sucursales
```bash
# Crear sucursal
POST http://localhost:8123/api/inventory/branches
Content-Type: application/json
{
  "name": "Farmacia Norte",
  "address": "Av. Principal 123",
  "phone": "123456789",
  "email": "norte@farmacia.com"
}

# Listar todas las sucursales
GET http://localhost:8123/api/inventory/branches

# Obtener sucursal por ID
GET http://localhost:8123/api/inventory/branches/{id}

# Actualizar sucursal
PUT http://localhost:8123/api/inventory/branches/{id}

# Eliminar sucursal
DELETE http://localhost:8123/api/inventory/branches/{id}
```

### 📦 Stock
```bash
# Crear registro de stock
POST http://localhost:8123/api/inventory/stock
Content-Type: application/json
{
  "branchId": 1,
  "productId": 1,
  "quantity": 100,
  "minStock": 20,
  "maxStock": 200
}

# Listar todo el stock
GET http://localhost:8123/api/inventory/stock

# Stock por sucursal
GET http://localhost:8123/api/inventory/stock/{branchId}

# Stock de un producto en una sucursal
GET http://localhost:8123/api/inventory/stock/{branchId}/{productId}

# Alertas de stock bajo
GET http://localhost:8123/api/inventory/stock/alerts
```

### 📋 Lotes
```bash
# Crear lote
POST http://localhost:8123/api/inventory/batches
Content-Type: application/json
{
  "branchId": 1,
  "productId": 1,
  "batchNumber": "LOTE001",
  "quantity": 50,
  "expirationDate": "2026-12-31",
  "purchasePrice": 3.50,
  "supplier": "Distribuidor XYZ"
}

# Listar todos los lotes
GET http://localhost:8123/api/inventory/batches

# Lotes por sucursal
GET http://localhost:8123/api/inventory/batches/branch/{branchId}

# Lotes próximos a vencer
GET http://localhost:8123/api/inventory/batches/expiring?days=30
```

### 🔄 Movimientos de Stock
```bash
# Registrar movimiento
POST http://localhost:8123/api/inventory/movements
Content-Type: application/json
{
  "branchId": 1,
  "productId": 1,
  "quantity": 10,
  "type": "ENTRADA",
  "reason": "Compra a proveedor",
  "userId": "user123"
}

# Movimientos por sucursal
GET http://localhost:8123/api/inventory/movements/branch/{branchId}

# Movimientos por producto
GET http://localhost:8123/api/inventory/movements/product/{productId}
```

---

## 💰 Servicio de Ventas - Puerto 8083

**Acceso directo**: `http://localhost:8083` (sin autenticación)  
**A través del Gateway**: `http://localhost:8123/api/sales` (con autenticación)

### 👥 Clientes
```bash
# Crear cliente
POST http://localhost:8123/api/sales/customers
Content-Type: application/json
{
  "firstName": "Juan",
  "lastName": "Pérez",
  "email": "juan.perez@email.com",
  "phone": "123456789",
  "address": "Calle Principal 456",
  "loyaltyPoints": 0
}

# Listar todos los clientes
GET http://localhost:8123/api/sales/customers

# Obtener cliente por ID
GET http://localhost:8123/api/sales/customers/{id}

# Actualizar cliente
PUT http://localhost:8123/api/sales/customers/{id}

# Eliminar cliente
DELETE http://localhost:8123/api/sales/customers/{id}

# Historial de compras del cliente
GET http://localhost:8123/api/sales/customers/{id}/history

# Actualizar puntos de lealtad
PUT http://localhost:8123/api/sales/customers/{id}/loyalty?points=50
```

### 🛒 Ventas
```bash
# Crear venta
POST http://localhost:8123/api/sales/sales
Content-Type: application/json
{
  "branchId": 1,
  "customerId": 1,
  "paymentMethod": "EFECTIVO",
  "items": [
    {
      "productId": 1,
      "quantity": 2,
      "unitPrice": 5.99,
      "discount": 0.0
    }
  ],
  "discount": 0.0,
  "tax": 1.20
}

# Listar todas las ventas
GET http://localhost:8123/api/sales/sales

# Obtener venta por ID
GET http://localhost:8123/api/sales/sales/{id}

# Ventas por sucursal
GET http://localhost:8123/api/sales/sales/branch/{branchId}

# Ventas por cliente
GET http://localhost:8123/api/sales/sales/customer/{customerId}

# Ventas por rango de fechas
GET http://localhost:8123/api/sales/sales/date-range?start=2026-01-01&end=2026-01-31

# Actualizar estado de venta
PUT http://localhost:8123/api/sales/sales/{id}/status?status=COMPLETADA

# Cancelar venta
PUT http://localhost:8123/api/sales/sales/{id}/cancel
```

### 📝 Recetas Médicas
```bash
# Crear receta
POST http://localhost:8123/api/sales/prescriptions
Content-Type: application/json
{
  "customerId": 1,
  "doctorName": "Dr. García",
  "doctorLicense": "MED12345",
  "issueDate": "2026-01-29",
  "expirationDate": "2026-02-28",
  "diagnosis": "Infección respiratoria"
}

# Obtener receta por ID
GET http://localhost:8123/api/sales/prescriptions/{id}

# Recetas por cliente
GET http://localhost:8123/api/sales/prescriptions/customer/{customerId}

# Actualizar estado de receta
PUT http://localhost:8123/api/sales/prescriptions/{id}/status?status=DISPENSADA
```

---

## 📈 Servicio de Reportes - Puerto 8084

**Acceso directo**: `http://localhost:8084` (sin autenticación)  
**A través del Gateway**: `http://localhost:8123/api/reporting` (con autenticación)

### 📊 Reportes de Ventas
```bash
# Resumen de ventas
GET http://localhost:8123/api/reporting/reports/sales/summary?startDate=2026-01-01&endDate=2026-01-31

# Ventas por producto
GET http://localhost:8123/api/reporting/reports/sales/by-product?startDate=2026-01-01&endDate=2026-01-31

# Top productos más vendidos
GET http://localhost:8123/api/reporting/reports/sales/top-products?limit=10

# Ventas por sucursal
GET http://localhost:8123/api/reporting/reports/sales/by-branch?startDate=2026-01-01&endDate=2026-01-31
```

### 📦 Reportes de Inventario
```bash
# Resumen de inventario
GET http://localhost:8123/api/reporting/reports/inventory/summary

# Stock bajo
GET http://localhost:8123/api/reporting/reports/inventory/low-stock

# Productos próximos a vencer
GET http://localhost:8123/api/reporting/reports/inventory/expiring?days=30

# Valor del inventario
GET http://localhost:8123/api/reporting/reports/inventory/value
```

### 🎯 Dashboard
```bash
# Dashboard general
GET http://localhost:8123/api/reporting/reports/dashboard

# Health check del servicio
GET http://localhost:8123/api/reporting/reports/health
```

### 📸 Snapshots
```bash
# Generar snapshot
POST http://localhost:8123/api/reporting/snapshots/generate

# Snapshots de ventas
GET http://localhost:8123/api/reporting/snapshots/sales?startDate=2026-01-01&endDate=2026-01-31

# Snapshots de inventario
GET http://localhost:8123/api/reporting/snapshots/inventory?startDate=2026-01-01&endDate=2026-01-31
```

---

## 🧪 Ejemplos de Prueba Completa

### 1. Flujo Completo: Crear Producto y Vender

```bash
# 1. Login (en el navegador)
Abre: http://localhost:8123/api/catalog/products
Serás redirigido a: http://localhost:9000/login
Login con tus credenciales

# 2. Crear categoría
POST http://localhost:8123/api/catalog/categories
{
  "name": "Analgésicos",
  "code": "ANA",
  "description": "Medicamentos para aliviar el dolor"
}

# 3. Crear laboratorio
POST http://localhost:8123/api/catalog/laboratories
{
  "name": "Bayer",
  "country": "Alemania"
}

# 4. Crear producto
POST http://localhost:8123/api/catalog/products
{
  "name": "Aspirina 500mg",
  "categoryId": 1,
  "laboratoryId": 1,
  "price": 8.50,
  "requiresPrescription": false
}

# 5. Crear sucursal
POST http://localhost:8123/api/inventory/branches
{
  "name": "Farmacia Central",
  "address": "Av. Principal 100"
}

# 6. Agregar stock
POST http://localhost:8123/api/inventory/stock
{
  "branchId": 1,
  "productId": 1,
  "quantity": 100,
  "minStock": 10
}

# 7. Crear cliente
POST http://localhost:8123/api/sales/customers
{
  "firstName": "María",
  "lastName": "González",
  "email": "maria@email.com"
}

# 8. Realizar venta
POST http://localhost:8123/api/sales/sales
{
  "branchId": 1,
  "customerId": 1,
  "paymentMethod": "TARJETA",
  "items": [{
    "productId": 1,
    "quantity": 2,
    "unitPrice": 8.50
  }]
}

# 9. Ver reporte de ventas
GET http://localhost:8123/api/reporting/reports/sales/summary?startDate=2026-01-01&endDate=2026-01-31
```

---

## 🔧 Herramientas para Probar

### Postman/Insomnia
1. Importa las colecciones desde los archivos en cada microservicio
2. Configura la variable `baseUrl`: `http://localhost:8123`
3. La cookie de sesión se guardará automáticamente después del login

### cURL
```bash
# Login primero en el navegador para obtener la cookie JSESSIONID
# Luego usa la cookie en tus peticiones

curl -X GET http://localhost:8123/api/catalog/products \
  -H "Cookie: JSESSIONID=tu-session-id"
```

### Navegador
Simplemente abre: `http://localhost:8123/api/catalog/products`
Serás redirigido al login automáticamente.

---

## ✅ Verificación de Servicios

```bash
# Ver estado de los contenedores
docker-compose ps

# Ver logs del gateway
docker-compose logs -f gateway

# Ver logs de todos los servicios
docker-compose logs -f

# Reiniciar un servicio específico
docker-compose restart gateway

# Ver health checks
GET http://localhost:8123/actuator/health
GET http://localhost:8081/actuator/health
GET http://localhost:8082/actuator/health
GET http://localhost:8083/actuator/health
GET http://localhost:8084/actuator/health
```

---

## 🚨 Troubleshooting

### Error 401 Unauthorized
- Asegúrate de estar autenticado (accede primero a cualquier ruta en el navegador)
- Verifica que el OAuth Server esté corriendo en el puerto 9000

### Error 404 Not Found
- Verifica que el microservicio esté corriendo: `docker-compose ps`
- Revisa los logs: `docker-compose logs -f [servicio]`

### Gateway no redirige al login
- Verifica que el OAuth Server esté corriendo: `http://localhost:9000`
- Revisa los logs del gateway: `docker-compose logs -f gateway`

---

## 📝 Notas Importantes

1. **Todas las rutas requieren autenticación** a través del Gateway
2. **El OAuth Server debe estar corriendo** en `http://localhost:9000` (fuera de Docker)
3. **Los microservicios están en Docker** en la red `pharmacy-network`
4. **El Gateway traduce las rutas**: `/api/catalog` → `productos-service:8081/api`
5. **Los tokens JWT se propagan automáticamente** a los microservicios downstream
