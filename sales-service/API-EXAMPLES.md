# Ejemplos de Requests para Sales Service

## 1. Crear Cliente

```json
POST http://localhost:8083/api/customers
Content-Type: application/json

{
  "identificationNumber": "1234567890",
  "identificationType": "CI",
  "firstName": "Juan",
  "lastName": "Pérez",
  "email": "juan.perez@email.com",
  "phone": "0987654321",
  "address": "Av. Principal 123",
  "city": "Quito",
  "birthDate": "1990-05-15",
  "type": "REGULAR"
}
```

## 2. Crear Prescripción

```json
POST http://localhost:8083/api/prescriptions
Content-Type: application/json

{
  "customerId": 1,
  "doctorName": "Dr. María González",
  "doctorLicense": "MED-12345",
  "doctorSpecialty": "Medicina General",
  "issueDate": "2025-12-15",
  "expirationDate": "2026-06-15",
  "diagnosis": "Infección respiratoria",
  "notes": "Tomar el medicamento cada 8 horas",
  "status": "ACTIVE"
}
```

## 3. Crear Venta Simple (sin prescripción)

```json
POST http://localhost:8083/api/sales
Content-Type: application/json

{
  "customerId": 1,
  "branchId": 1,
  "items": [
    {
      "productId": 1,
      "quantity": 2,
      "discount": 0,
      "requiresPrescription": false
    },
    {
      "productId": 2,
      "quantity": 1,
      "discount": 5.00,
      "requiresPrescription": false
    }
  ],
  "discount": 0,
  "paymentMethod": "CASH",
  "cashierName": "Carlos Ruiz",
  "notes": "Venta de mostrador"
}
```

## 4. Crear Venta con Prescripción

```json
POST http://localhost:8083/api/sales
Content-Type: application/json

{
  "customerId": 1,
  "branchId": 1,
  "items": [
    {
      "productId": 5,
      "quantity": 1,
      "requiresPrescription": true,
      "prescriptionId": 1
    },
    {
      "productId": 3,
      "quantity": 2,
      "requiresPrescription": false
    }
  ],
  "discount": 10.00,
  "paymentMethod": "CARD",
  "cashierName": "Ana Torres",
  "notes": "Venta con prescripción médica"
}
```

## 5. Obtener Historial de Cliente

```
GET http://localhost:8083/api/customers/1/history?page=0&size=10
```

## 6. Obtener Ventas por Sucursal

```
GET http://localhost:8083/api/sales/branch/1?page=0&size=20
```

## 7. Obtener Ventas por Rango de Fechas

```
GET http://localhost:8083/api/sales/date-range?start=2025-12-01T00:00:00&end=2025-12-31T23:59:59&page=0&size=50
```

## 8. Actualizar Estado de Venta a COMPLETED

```
PUT http://localhost:8083/api/sales/1/status?status=COMPLETED
```

Nota: Al completar la venta, las prescripciones asociadas cambiarán automáticamente a estado USED.

## 9. Actualizar Puntos de Lealtad

```
PUT http://localhost:8083/api/customers/1/loyalty?points=150
```

## 10. Obtener Prescripciones de Cliente

```
GET http://localhost:8083/api/prescriptions/customer/1?page=0&size=10
```

## 11. Actualizar Cliente

```json
PUT http://localhost:8083/api/customers/1
Content-Type: application/json

{
  "identificationNumber": "1234567890",
  "identificationType": "CI",
  "firstName": "Juan Carlos",
  "lastName": "Pérez Gómez",
  "email": "juan.perez@email.com",
  "phone": "0987654321",
  "address": "Av. Principal 123, Edificio B",
  "city": "Quito",
  "birthDate": "1990-05-15",
  "type": "VIP"
}
```

## 12. Cambiar Estado de Prescripción

```
PUT http://localhost:8083/api/prescriptions/1/status?status=EXPIRED
```

## Respuestas de Error

### Error 400 - Prescripción Requerida

```json
{
  "timestamp": "2025-12-16T10:30:00",
  "status": 400,
  "error": "Prescripción Requerida",
  "message": "El producto 'Amoxicilina 500mg' requiere prescripción médica"
}
```

### Error 400 - Prescripción Expirada

```json
{
  "timestamp": "2025-12-16T10:30:00",
  "status": 400,
  "error": "Prescripción Expirada",
  "message": "La prescripción ha expirado o no está activa"
}
```

### Error 400 - Venta Inválida

```json
{
  "timestamp": "2025-12-16T10:30:00",
  "status": 400,
  "error": "Venta Inválida",
  "message": "La venta debe tener al menos un ítem"
}
```
