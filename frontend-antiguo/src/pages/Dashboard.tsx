import React, { useEffect, useState } from 'react';
import { Card, LoadingSpinner } from '../components';
import { reportingService } from '../services';
import { DashboardMetrics } from '../types';
import { toast } from 'react-toastify';

const Dashboard: React.FC = () => {
  const [metrics, setMetrics] = useState<DashboardMetrics | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadDashboardData();
  }, []);

  const loadDashboardData = async () => {
    try {
      setLoading(true);
      console.log('Loading dashboard data...');
      const data = await reportingService.getDashboardMetrics();
      console.log('Dashboard data received:', data);
      setMetrics(data);
    } catch (error: any) {
      console.error('Dashboard error:', error);
      console.error('Error details:', error.response?.data);
      toast.error('Error al cargar datos del dashboard');
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center h-96">
        <LoadingSpinner size="lg" />
      </div>
    );
  }

  // Usar valores predeterminados si no hay métricas
  const salesMetrics = metrics?.salesMetrics || {
    totalRevenue: 0,
    totalSales: 0,
    averageTicket: 0,
    uniqueCustomers: 0
  };

  const inventoryMetrics = metrics?.inventoryMetrics || {
    totalProducts: 0,
    lowStockProducts: 0,
    expiringSoon: 0,
    totalInventoryValue: 0
  };

  return (
    <div className="space-y-6">
      {/* Metrics Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <Card
          title="Total Productos"
          value={inventoryMetrics.totalProducts}
          icon="💊"
          color="blue"
        />
        <Card
          title="Stock Bajo"
          value={inventoryMetrics.lowStockProducts}
          icon="⚠️"
          color="red"
        />
        <Card
          title="Ingresos Totales"
          value={`$${salesMetrics.totalRevenue.toFixed(2)}`}
          icon="💰"
          color="green"
        />
        <Card
          title="Total Ventas"
          value={salesMetrics.totalSales}
          icon="🛒"
          color="yellow"
        />
      </div>

      {/* Additional Metrics */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <Card
          title="Ticket Promedio"
          value={`$${salesMetrics.averageTicket.toFixed(2)}`}
          icon="💳"
          color="blue"
        />
        <Card
          title="Clientes Únicos"
          value={salesMetrics.uniqueCustomers}
          icon="👥"
          color="green"
        />
        <Card
          title="Valor Inventario"
          value={`$${inventoryMetrics.totalInventoryValue.toFixed(2)}`}
          icon="📦"
          color="yellow"
        />
      </div>

      {/* Inventory Summary */}
      <div className="bg-white rounded-lg shadow-md p-6">
        <h3 className="text-lg font-semibold mb-4 text-gray-800">Resumen de Inventario</h3>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div className="text-center p-4 bg-blue-50 rounded-lg">
            <p className="text-sm text-gray-600">Total Productos</p>
            <p className="text-2xl font-bold text-blue-600">
              {inventoryMetrics.totalProducts}
            </p>
          </div>
          <div className="text-center p-4 bg-red-50 rounded-lg">
            <p className="text-sm text-gray-600">Stock Bajo</p>
            <p className="text-2xl font-bold text-red-600">
              {inventoryMetrics.lowStockProducts}
            </p>
          </div>
          <div className="text-center p-4 bg-yellow-50 rounded-lg">
            <p className="text-sm text-gray-600">Próximos a Vencer</p>
            <p className="text-2xl font-bold text-yellow-600">
              {inventoryMetrics.expiringSoon}
            </p>
          </div>
        </div>
      </div>

      {/* Sales Summary */}
      <div className="bg-white rounded-lg shadow-md p-6">
        <h3 className="text-lg font-semibold mb-4 text-gray-800">Resumen de Ventas</h3>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div className="text-center p-4 bg-green-50 rounded-lg">
            <p className="text-sm text-gray-600">Ingresos Totales</p>
            <p className="text-2xl font-bold text-green-600">
              ${salesMetrics.totalRevenue.toFixed(2)}
            </p>
          </div>
          <div className="text-center p-4 bg-blue-50 rounded-lg">
            <p className="text-sm text-gray-600">Total Ventas</p>
            <p className="text-2xl font-bold text-blue-600">
              {salesMetrics.totalSales}
            </p>
          </div>
          <div className="text-center p-4 bg-purple-50 rounded-lg">
            <p className="text-sm text-gray-600">Ticket Promedio</p>
            <p className="text-2xl font-bold text-purple-600">
              ${salesMetrics.averageTicket.toFixed(2)}
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
