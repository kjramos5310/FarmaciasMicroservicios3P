# FarmaSystem - Frontend

Sistema de gestión de farmacia desarrollado con React + TypeScript que consume 4 microservicios.

## 🚀 Características

- **Dashboard**: Métricas en tiempo real, gráficos de ventas, productos más vendidos
- **Gestión de Medicamentos**: CRUD completo con búsqueda y filtros
- **Inventario**: Gestión de sucursales, stock y movimientos
- **Punto de Venta**: Sistema POS completo con carrito y procesamiento de ventas

## 🛠️ Tecnologías

- React 18.2
- TypeScript 4.9
- React Router DOM
- TailwindCSS
- Axios
- React Hook Form + Yup
- React Query (TanStack Query)
- Recharts
- React Toastify

## 📋 Requisitos Previos

- Node.js 16+
- npm o yarn
- Microservicios backend corriendo:
  - Catalog Service: `http://localhost:8081`
  - Inventory Service: `http://localhost:8082`
  - Sales Service: `http://localhost:8083`
  - Reporting Service: `http://localhost:8084`

## 🔧 Instalación

1. Instalar dependencias:
```bash
npm install
```

2. Iniciar el servidor de desarrollo:
```bash
npm start
```

La aplicación estará disponible en `http://localhost:3000`

## 📁 Estructura del Proyecto

```
src/
├── components/       # Componentes reutilizables (Layout, Modal, Card, etc)
├── pages/           # Páginas principales (Dashboard, Products, Inventory, POS)
├── services/        # Axios clients por microservicio
├── types/           # Interfaces TypeScript
├── hooks/           # Custom hooks (useDebounce)
├── utils/           # Helpers (formatters, validators)
└── App.tsx          # Configuración de rutas y React Query
```

## 🎯 Scripts Disponibles

- `npm start` - Inicia el servidor de desarrollo
- `npm run build` - Genera build de producción
- `npm test` - Ejecuta tests

## 🌐 Endpoints de API

### Catalog Service (8081)
- GET /api/products - Listar productos
- GET /api/categories - Listar categorías
- GET /api/laboratories - Listar laboratorios
- POST /api/products - Crear producto
- PUT /api/products/{id} - Actualizar producto
- DELETE /api/products/{id} - Eliminar producto

### Inventory Service (8082)
- GET /api/branches - Listar sucursales
- GET /api/stock/branch/{branchId} - Stock por sucursal
- POST /api/branches - Crear sucursal
- POST /api/movements - Registrar movimiento

### Sales Service (8083)
- GET /api/customers - Listar clientes
- POST /api/customers - Crear cliente
- POST /api/sales - Procesar venta

### Reporting Service (8084)
- GET /api/reports/dashboard - Métricas del dashboard

## 📱 Responsive Design

- Mobile-first approach
- Sidebar colapsable en dispositivos móviles
- Tablas con scroll horizontal en pantallas pequeñas
- Grid adaptable para catálogo de productos

## 📄 Licencia

MIT
