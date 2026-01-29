# Guía de Endpoints para Postman - Sistema de Farmacias

## Autenticación (OAuth Server)

### 1. Obtener Token de Acceso
```
POST http://localhost:9000/api/auth/login
Content-Type: application/json

Body:
{
  "username": "admin",
  "password": "admin123"
}

Respuesta:
{
  "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "token_type": "Bearer",
  "expires_in": 3600,
  "username": "admin",
  "email": "admin@example.com",
  "userId": 1,
  "roles": ["ADMIN", "USER"]
}
```

**IMPORTANTE**: Copia el `access_token` y úsalo en el header `Authorization: Bearer <token>` para todas las peticiones siguientes.

---

## 📦 CATÁLOGO - Productos Service (Puerto 8081 → Gateway 8123)

### Productos

#### GET Todos los Productos
```
GET http://localhost:8123/api/catalog/products
Authorization: Bearer <token>
```

#### GET Producto por ID
```
GET http://localhost:8123/api/catalog/products/1
Authorization: Bearer <token>
```

#### GET Producto por Código
```
GET http://localhost:8123/api/catalog/products/code/PROD001
Authorization: Bearer <token>
```

#### GET Buscar Productos
```
GET http://localhost:8123/api/catalog/products/search?keyword=paracetamol
Authorization: Bearer <token>
```

#### POST Crear Producto
```
POST http://localhost:8123/api/catalog/products
Authorization: Bearer <token>
Content-Type: application/json

Body:
{
  "code": "PROD999",
  "name": "Paracetamol 500mg",
  "description": "Analgésico y antipirético",
  "categoryId": 1,
  "laboratoryId": 1,
  "price": 15.50,
  "prescriptionRequired": false,
  "activeIngredient": "Paracetamol"
}
```

#### PUT Actualizar Producto
```
PUT http://localhost:8123/api/catalog/products/1
Authorization: Bearer <token>
Content-Type: application/json

Body:
{
  "code": "PROD001",
  "name": "Paracetamol 500mg Updated",
  "description": "Descripción actualizada",
  "categoryId": 1,
  "laboratoryId": 1,
  "price": 16.00,
  "prescriptionRequired": false,
  "activeIngredient": "Paracetamol"
}
```

#### DELETE Eliminar Producto
```
DELETE http://localhost:8123/api/catalog/products/1
Authorization: Bearer <token>
```

---

### Categorías

#### GET Todas las Categorías
```
GET http://localhost:8123/api/catalog/categories
Authorization: Bearer <token>
```

#### GET Categoría por ID
```
GET http://localhost:8123/api/catalog/categories/1
Authorization: Bearer <token>
```

#### GET Categoría por Código
```
GET http://localhost:8123/api/catalog/categories/code/CAT001
Authorization: Bearer <token>
```

#### GET Categorías Activas
```
GET http://localhost:8123/api/catalog/categories/active
Authorization: Bearer <token>
```

#### GET Buscar Categorías
```
GET http://localhost:8123/api/catalog/categories/search?keyword=analgesico
Authorization: Bearer <token>
```

#### POST Crear Categoría
```
POST http://localhost:8123/api/catalog/categories
Authorization: Bearer <token>
Content-Type: application/json

Body:
{
  "code": "CAT999",
  "name": "Analgésicos",
  "description": "Medicamentos para el dolor",
  "active": true
}
```

#### PUT Actualizar Categoría
```
PUT http://localhost:8123/api/catalog/categories/1
Authorization: Bearer <token>
Content-Type: application/json

Body:
{
  "code": "CAT001",
  "name": "Analgésicos Updated",
  "description": "Descripción actualizada",
  "active": true
}
```

#### DELETE Eliminar Categoría
```
DELETE http://localhost:8123/api/catalog/categories/1
Authorization: Bearer <token>
```

---

### Laboratorios

#### GET Todos los Laboratorios
```
GET http://localhost:8123/api/catalog/laboratories
Authorization: Bearer <token>
```

#### GET Laboratorio por ID
```
GET http://localhost:8123/api/catalog/laboratories/1
Authorization: Bearer <token>
```

#### GET Laboratorio por Nombre
```
GET http://localhost:8123/api/catalog/laboratories/name/Bayer
Authorization: Bearer <token>
```

#### GET Laboratorios Activos
```
GET http://localhost:8123/api/catalog/laboratories/active
Authorization: Bearer <token>
```

#### GET Buscar Laboratorios
```
GET http://localhost:8123/api/catalog/laboratories/search?keyword=bayer
Authorization: Bearer <token>
```

#### POST Crear Laboratorio
```
POST http://localhost:8123/api/catalog/laboratories
Authorization: Bearer <token>
Content-Type: application/json

Body:
{
  "name": "Laboratorios XYZ",
  "country": "Ecuador",
  "description": "Laboratorio farmacéutico",
  "active": true
}
```

#### PUT Actualizar Laboratorio
```
PUT http://localhost:8123/api/catalog/laboratories/1
Authorization: Bearer <token>
Content-Type: application/json

Body:
{
  "name": "Bayer Updated",
  "country": "Alemania",
  "description": "Descripción actualizada",
  "active": true
}
```

#### DELETE Eliminar Laboratorio
```
DELETE http://localhost:8123/api/catalog/laboratories/1
Authorization: Bearer <token>
```

---

## 📊 INVENTARIO - Almacén Service (Puerto 8082 → Gateway 8123)

### Sucursales

#### GET Todas las Sucursales
```
GET http://localhost:8123/api/inventory/branches
Authorization: Bearer <token>
```

#### GET Sucursal por ID
```
GET http://localhost:8123/api/inventory/branches/1
Authorization: Bearer <token>
```

#### POST Crear Sucursal
```
POST http://localhost:8123/api/inventory/branches
Authorization: Bearer <token>
Content-Type: application/json

Body:
{
  "code": "SUC999",
  "name": "Sucursal Centro",
  "address": "Av. Principal 123",
  "phone": "0987654321",
  "city": "Quito",
  "active": true
}
```

#### PUT Actualizar Sucursal
```
PUT http://localhost:8123/api/inventory/branches/1
Authorization: Bearer <token>
Content-Type: application/json

Body:
{
  "code": "SUC001",
  "name": "Sucursal Centro Updated",
  "address": "Nueva dirección",
  "phone": "0987654321",
  "city": "Quito",
  "active": true
}
```

#### DELETE Eliminar Sucursal
```
DELETE http://localhost:8123/api/inventory/branches/1
Authorization: Bearer <token>
```

---

### Stock

#### GET Todo el Stock
```
GET http://localhost:8123/api/inventory/stock
Authorization: Bearer <token>
```

#### GET Stock por Sucursal
```
GET http://localhost:8123/api/inventory/stock/1
Authorization: Bearer <token>
```

#### GET Stock por Sucursal y Producto
```
GET http://localhost:8123/api/inventory/stock/1/1
Authorization: Bearer <token>
```

#### GET Alertas de Stock Bajo
```
GET http://localhost:8123/api/inventory/stock/alerts
Authorization: Bearer <token>
```

#### POST Crear/Actualizar Stock
```
POST http://localhost:8123/api/inventory/stock
Authorization: Bearer <token>
Content-Type: application/json

Body:
{
  "branchId": 1,
  "productId": 1,
  "quantity": 100,
  "minimumStock": 20,
  "maximumStock": 500
}
```

---

### Movimientos de Stock

#### POST Registrar Movimiento
```
POST http://localhost:8123/api/inventory/movements
Authorization: Bearer <token>
Content-Type: application/json

Body:
{
  "branchId": 1,
  "productId": 1,
  "movementType": "ENTRADA",
  "quantity": 50,
  "reason": "Compra a proveedor",
  "reference": "PO-2024-001"
}
```

#### GET Movimientos por Sucursal
```
GET http://localhost:8123/api/inventory/movements/branch/1
Authorization: Bearer <token>
```

#### GET Movimientos por Producto
```
GET http://localhost:8123/api/inventory/movements/product/1
Authorization: Bearer <token>
```

---

## 💰 VENTAS - Ventas Service (Puerto 8083 → Gateway 8123)

### Ventas

#### POST Crear Venta
```
POST http://localhost:8123/api/sales/sales
Authorization: Bearer <token>
Content-Type: application/json

Body:
{
  "branchId": 1,
  "customerId": 1,
  "items": [
    {
      "productId": 1,
      "quantity": 2,
      "unitPrice": 15.50
    }
  ],
  "paymentMethod": "EFECTIVO",
  "prescriptionId": null
}
```

#### GET Todas las Ventas (Paginado)
```
GET http://localhost:8123/api/sales/sales?page=0&size=10
Authorization: Bearer <token>
```

#### GET Venta por ID
```
GET http://localhost:8123/api/sales/sales/1
Authorization: Bearer <token>
```

#### GET Ventas por Sucursal
```
GET http://localhost:8123/api/sales/sales/branch/1?page=0&size=10
Authorization: Bearer <token>
```

#### GET Ventas por Cliente
```
GET http://localhost:8123/api/sales/sales/customer/1?page=0&size=10
Authorization: Bearer <token>
```

#### GET Ventas por Rango de Fechas
```
GET http://localhost:8123/api/sales/sales/date-range?start=2024-01-01T00:00:00&end=2024-12-31T23:59:59&page=0&size=10
Authorization: Bearer <token>
```

#### PUT Actualizar Estado de Venta
```
PUT http://localhost:8123/api/sales/sales/1/status?status=COMPLETADA
Authorization: Bearer <token>
```

---

### Clientes

#### POST Crear Cliente
```
POST http://localhost:8123/api/sales/customers
Authorization: Bearer <token>
Content-Type: application/json

Body:
{
  "identificationType": "CEDULA",
  "identificationNumber": "1234567890",
  "firstName": "Juan",
  "lastName": "Pérez",
  "email": "juan.perez@example.com",
  "phone": "0987654321",
  "address": "Calle Principal 123",
  "dateOfBirth": "1990-01-15",
  "loyaltyPoints": 0
}
```

#### GET Todos los Clientes (Paginado)
```
GET http://localhost:8123/api/sales/customers?page=0&size=10
Authorization: Bearer <token>
```

#### GET Cliente por ID
```
GET http://localhost:8123/api/sales/customers/1
Authorization: Bearer <token>
```

#### PUT Actualizar Cliente
```
PUT http://localhost:8123/api/sales/customers/1
Authorization: Bearer <token>
Content-Type: application/json

Body:
{
  "identificationType": "CEDULA",
  "identificationNumber": "1234567890",
  "firstName": "Juan Carlos",
  "lastName": "Pérez López",
  "email": "juan.perez@example.com",
  "phone": "0987654321",
  "address": "Nueva dirección",
  "dateOfBirth": "1990-01-15",
  "loyaltyPoints": 100
}
```

#### DELETE Eliminar Cliente
```
DELETE http://localhost:8123/api/sales/customers/1
Authorization: Bearer <token>
```

#### GET Historial de Compras del Cliente
```
GET http://localhost:8123/api/sales/customers/1/history?page=0&size=10
Authorization: Bearer <token>
```

#### PUT Actualizar Puntos de Lealtad
```
PUT http://localhost:8123/api/sales/customers/1/loyalty?points=150
Authorization: Bearer <token>
```

---

## 📈 REPORTES - Reportes Service (Puerto 8084 → Gateway 8123)

### Dashboard

#### GET Dashboard General
```
GET http://localhost:8123/api/reporting/reports/dashboard
Authorization: Bearer <token>
```

---

### Reportes de Ventas

#### GET Resumen de Ventas
```
GET http://localhost:8123/api/reporting/reports/sales/summary?startDate=2024-01-01&endDate=2024-12-31&branchId=1
Authorization: Bearer <token>
```

#### GET Ventas por Producto
```
GET http://localhost:8123/api/reporting/reports/sales/by-product?startDate=2024-01-01&endDate=2024-12-31
Authorization: Bearer <token>
```

#### GET Top Productos Más Vendidos
```
GET http://localhost:8123/api/reporting/reports/sales/top-products?limit=10&startDate=2024-01-01&endDate=2024-12-31
Authorization: Bearer <token>
```

#### GET Ventas por Sucursal
```
GET http://localhost:8123/api/reporting/reports/sales/by-branch?startDate=2024-01-01&endDate=2024-12-31
Authorization: Bearer <token>
```

---

### Reportes de Inventario

#### GET Resumen de Inventario
```
GET http://localhost:8123/api/reporting/reports/inventory/summary?branchId=1
Authorization: Bearer <token>
```

#### GET Productos con Stock Bajo
```
GET http://localhost:8123/api/reporting/reports/inventory/low-stock?branchId=1
Authorization: Bearer <token>
```

#### GET Productos Próximos a Vencer
```
GET http://localhost:8123/api/reporting/reports/inventory/expiring?branchId=1
Authorization: Bearer <token>
```

#### GET Valor Total del Inventario
```
GET http://localhost:8123/api/reporting/reports/inventory/value?branchId=1
Authorization: Bearer <token>
```

#### GET Health Check
```
GET http://localhost:8123/api/reporting/reports/health
Authorization: Bearer <token>
```

---

## 🔧 Configuración de Postman

### Variables de Entorno Sugeridas

1. Crea un Environment llamado "Farmacias Dev"
2. Agrega estas variables:

```
gateway_url = http://localhost:8123
oauth_url = http://localhost:9000
access_token = (se llenará después del login)
```

### Pre-request Script para Auto-login (Opcional)

Puedes agregar este script en una colección para obtener el token automáticamente:

```javascript
// Pre-request Script
if (!pm.environment.get("access_token")) {
    pm.sendRequest({
        url: pm.environment.get("oauth_url") + "/api/auth/login",
        method: 'POST',
        header: {
            'Content-Type': 'application/json'
        },
        body: {
            mode: 'raw',
            raw: JSON.stringify({
                username: "admin",
                password: "admin123"
            })
        }
    }, function (err, response) {
        if (!err) {
            var jsonData = response.json();
            pm.environment.set("access_token", jsonData.access_token);
        }
    });
}
```

### Headers Comunes

Para todas las peticiones (excepto login), agrega estos headers:

```
Authorization: Bearer {{access_token}}
Content-Type: application/json (solo para POST/PUT)
```

---

## 📝 Notas Importantes

1. **Credenciales válidas**:
   - `admin` / `admin123`
   - `test` / `test123`

2. **Tokens**: Los tokens expiran en 3600 segundos (1 hora). Si obtienes error 401, obtén un nuevo token.

3. **Rutas del Gateway**: 
   - `/api/catalog/**` → productos-service (8081)
   - `/api/inventory/**` → almacen-service (8082)
   - `/api/sales/**` → ventas-service (8083)
   - `/api/reporting/**` → reportes-service (8084)

4. **CORS**: Todos los servicios aceptan peticiones desde cualquier origen.

5. **Formato de Fechas**:
   - Para fechas: `YYYY-MM-DD` (ejemplo: `2024-01-15`)
   - Para fecha-hora: `YYYY-MM-DDTHH:mm:ss` (ejemplo: `2024-01-15T14:30:00`)

6. **Paginación**: Los endpoints con paginación usan los parámetros:
   - `page`: número de página (empieza en 0)
   - `size`: elementos por página (default: 10)

---

## ✅ Orden Recomendado de Pruebas

1. **Autenticación**: Obtén el token
2. **Catálogo**: Crea categorías → laboratorios → productos
3. **Inventario**: Crea sucursales → stock → movimientos
4. **Ventas**: Crea clientes → ventas
5. **Reportes**: Consulta dashboard y reportes

