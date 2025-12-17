import React, { useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import { Modal, LoadingSpinner } from '../components';
import { inventoryService, catalogService } from '../services';
import { Branch, Stock, StockMovement, MovementType, Product } from '../types';
import { toast } from 'react-toastify';

const branchSchema = yup.object().shape({
  code: yup.string().required('Código es requerido').max(50),
  name: yup.string().required('Nombre es requerido').max(200),
  address: yup.string().required('Dirección es requerida'),
  city: yup.string().required('Ciudad es requerida'),
  phone: yup.string().required('Teléfono es requerido'),
  schedule: yup.string(),
});

const movementSchema = yup.object().shape({
  branchId: yup.number().required('Sucursal es requerida').positive(),
  productId: yup.number().required('Producto es requerido').positive(),
  movementType: yup.string().required('Tipo es requerido'),
  quantity: yup.number().required('Cantidad es requerida').positive(),
  destinationBranchId: yup.number().optional(),
  reason: yup.string().optional(),
});

const stockSchema = yup.object().shape({
  branchId: yup.number().required('Sucursal es requerida').positive(),
  productId: yup.number().required('Producto es requerido').positive(),
  quantity: yup.number().required('Cantidad es requerida').min(0),
  minimumStock: yup.number().required('Stock mínimo es requerido').min(0),
  maximumStock: yup.number().required('Stock máximo es requerido').positive(),
});

const Inventory: React.FC = () => {
  const [activeTab, setActiveTab] = useState<'branches' | 'stock' | 'movements'>('branches');
  const [branches, setBranches] = useState<Branch[]>([]);
  const [stock, setStock] = useState<Stock[]>([]);
  const [movements, setMovements] = useState<StockMovement[]>([]);
  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(true);
  const [selectedBranch, setSelectedBranch] = useState<number | ''>('');
  const [isBranchModalOpen, setIsBranchModalOpen] = useState(false);
  const [isMovementModalOpen, setIsMovementModalOpen] = useState(false);
  const [isStockModalOpen, setIsStockModalOpen] = useState(false);
  const [editingBranch, setEditingBranch] = useState<Branch | null>(null);

  const {
    register: registerBranch,
    handleSubmit: handleSubmitBranch,
    reset: resetBranch,
    formState: { errors: branchErrors, isSubmitting: branchSubmitting },
  } = useForm({
    resolver: yupResolver(branchSchema),
  });

  const {
    register: registerMovement,
    handleSubmit: handleSubmitMovement,
    reset: resetMovement,
    watch: watchMovement,
    formState: { errors: movementErrors, isSubmitting: movementSubmitting },
  } = useForm({
    resolver: yupResolver(movementSchema),
  });

  const {
    register: registerStock,
    handleSubmit: handleSubmitStock,
    reset: resetStock,
    formState: { errors: stockErrors, isSubmitting: stockSubmitting },
  } = useForm({
    resolver: yupResolver(stockSchema),
  });

  const movementType = watchMovement('movementType');

  useEffect(() => {
    loadInitialData();
  }, []);

  useEffect(() => {
    if (selectedBranch && activeTab === 'stock') {
      loadStockByBranch(Number(selectedBranch));
    }
  }, [selectedBranch, activeTab]);

  useEffect(() => {
    if (selectedBranch && activeTab === 'movements') {
      loadMovementsByBranch(Number(selectedBranch));
    }
  }, [selectedBranch, activeTab]);

  const loadInitialData = async () => {
    try {
      setLoading(true);
      const [branchesData, productsData] = await Promise.all([
        inventoryService.getAllBranches(),
        catalogService.getAllProducts(),
      ]);
      setBranches(Array.isArray(branchesData) ? branchesData : []);
      setProducts(Array.isArray(productsData) ? productsData : []);
      setMovements([]); // Start with empty movements
    } catch (error) {
      toast.error('Error al cargar datos');
      console.error(error);
      setBranches([]);
      setProducts([]);
      setMovements([]);
    } finally {
      setLoading(false);
    }
  };

  const loadMovementsByBranch = async (branchId: number) => {
    try {
      const movementsData = await inventoryService.getMovementsByBranch(branchId);
      setMovements(Array.isArray(movementsData) ? movementsData : []);
    } catch (error) {
      toast.error('Error al cargar movimientos');
      console.error(error);
      setMovements([]);
    }
  };

  const loadStockByBranch = async (branchId: number) => {
    try {
      const stockData = await inventoryService.getStockByBranch(branchId);
      setStock(stockData);
    } catch (error) {
      toast.error('Error al cargar stock');
      console.error(error);
    }
  };

  const onSubmitBranch = async (data: any) => {
    try {
      const branchData = {
        code: data.code,
        name: data.name,
        address: data.address,
        city: data.city,
        province: data.province || 'Pichincha',
        phone: data.phone,
        email: data.email || `${data.code.toLowerCase()}@farmacia.com`,
        managerName: data.managerName || 'Administrador',
        status: data.status || 'ACTIVE',
        openingTime: data.openingTime || '08:00:00',
        closingTime: data.closingTime || '20:00:00',
        schedule: data.schedule,
      };
      
      if (editingBranch) {
        await inventoryService.updateBranch(editingBranch.id!, branchData);
        toast.success('Sucursal actualizada');
      } else {
        await inventoryService.createBranch(branchData);
        toast.success('Sucursal creada');
      }
      setIsBranchModalOpen(false);
      resetBranch();
      setEditingBranch(null);
      loadInitialData();
    } catch (error) {
      toast.error('Error al guardar sucursal');
    }
  };

  const onSubmitMovement = async (data: any) => {
    try {
      const movementData = {
        branchId: data.branchId || Number(selectedBranch),
        productId: Number(data.productId),
        type: data.movementType,
        quantity: Number(data.quantity),
        reason: data.reason || '',
        reference: data.reference || 'WEB-FORM',
        performedBy: data.performedBy || 'usuario',
        destinationBranchId: data.destinationBranchId ? Number(data.destinationBranchId) : undefined,
      };
      
      console.log('Sending movement data:', movementData);
      await inventoryService.createMovement(movementData);
      toast.success('Movimiento registrado');
      setIsMovementModalOpen(false);
      resetMovement();
      loadInitialData();
      if (selectedBranch) {
        loadStockByBranch(Number(selectedBranch));
        loadMovementsByBranch(Number(selectedBranch));
      }
    } catch (error: any) {
      let errorMessage = error.message || 'Error al registrar movimiento';
      
      // Mejorar mensajes de error del backend
      if (errorMessage.includes('Stock no encontrado')) {
        errorMessage = 'El backend requiere que exista un registro de stock previo. Contacte al administrador del sistema para inicializar el stock de este producto en esta sucursal.';
      }
      
      toast.error(errorMessage);
      console.error('Movement submission error:', error);
    }
  };

  const onSubmitStock = async (data: any) => {
    try {
      const stockData = {
        branchId: data.branchId || Number(selectedBranch),
        productId: Number(data.productId),
        quantity: Number(data.quantity),
        minimumStock: Number(data.minimumStock),
        maximumStock: Number(data.maximumStock),
      };
      
      console.log('Creating stock:', stockData);
      await inventoryService.createStock(stockData);
      toast.success('Stock creado exitosamente');
      setIsStockModalOpen(false);
      resetStock();
      if (selectedBranch) {
        loadStockByBranch(Number(selectedBranch));
      }
    } catch (error: any) {
      const errorMessage = error.message || 'Error al crear stock';
      toast.error(errorMessage);
      console.error('Stock creation error:', error);
    }
  };

  const handleEditBranch = (branch: Branch) => {
    setEditingBranch(branch);
    resetBranch(branch);
    setIsBranchModalOpen(true);
  };

  const handleDeleteBranch = async (id: number) => {
    if (window.confirm('¿Eliminar esta sucursal?')) {
      try {
        await inventoryService.deleteBranch(id);
        toast.success('Sucursal eliminada');
        loadInitialData();
      } catch (error) {
        toast.error('Error al eliminar sucursal');
      }
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center h-96">
        <LoadingSpinner size="lg" />
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Tabs */}
      <div className="border-b border-gray-200">
        <nav className="flex space-x-8">
          <button
            onClick={() => setActiveTab('branches')}
            className={`py-4 px-1 border-b-2 font-medium text-sm ${
              activeTab === 'branches'
                ? 'border-primary-500 text-primary-600'
                : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
            }`}
          >
            Sucursales
          </button>
          <button
            onClick={() => setActiveTab('stock')}
            className={`py-4 px-1 border-b-2 font-medium text-sm ${
              activeTab === 'stock'
                ? 'border-primary-500 text-primary-600'
                : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
            }`}
          >
            Stock por Sucursal
          </button>
          <button
            onClick={() => setActiveTab('movements')}
            className={`py-4 px-1 border-b-2 font-medium text-sm ${
              activeTab === 'movements'
                ? 'border-primary-500 text-primary-600'
                : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
            }`}
          >
            Movimientos
          </button>
        </nav>
      </div>

      {/* Tab Content: Branches */}
      {activeTab === 'branches' && (
        <div className="space-y-4">
          <div className="flex justify-between items-center">
            <h3 className="text-xl font-semibold">Sucursales</h3>
            <button
              onClick={() => setIsBranchModalOpen(true)}
              className="bg-primary-600 text-white px-4 py-2 rounded-lg hover:bg-primary-700"
            >
              + Nueva Sucursal
            </button>
          </div>

          <div className="bg-white rounded-lg shadow-md overflow-hidden">
            <table className="w-full">
              <thead className="bg-gray-50 border-b">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase">
                    Código
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase">
                    Nombre
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase">
                    Ciudad
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase">
                    Teléfono
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase">
                    Estado
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase">
                    Acciones
                  </th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-200">
                {branches.map((branch) => (
                  <tr key={branch.id} className="hover:bg-gray-50">
                    <td className="px-6 py-4 text-sm">{branch.code}</td>
                    <td className="px-6 py-4 text-sm">{branch.name}</td>
                    <td className="px-6 py-4 text-sm">{branch.city}</td>
                    <td className="px-6 py-4 text-sm">{branch.phone}</td>
                    <td className="px-6 py-4 text-sm">
                      <span
                        className={`px-2 py-1 rounded-full text-xs font-medium ${
                          branch.active ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'
                        }`}
                      >
                        {branch.active ? 'Activo' : 'Inactivo'}
                      </span>
                    </td>
                    <td className="px-6 py-4 text-sm space-x-2">
                      <button
                        onClick={() => handleEditBranch(branch)}
                        className="text-blue-600 hover:text-blue-800"
                      >
                        Editar
                      </button>
                      <button
                        onClick={() => handleDeleteBranch(branch.id!)}
                        className="text-red-600 hover:text-red-800"
                      >
                        Eliminar
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* Tab Content: Stock */}
      {activeTab === 'stock' && (
        <div className="space-y-4">
          <div className="flex justify-between items-center">
            <h3 className="text-xl font-semibold">Stock por Sucursal</h3>
            <button
              onClick={() => setIsStockModalOpen(true)}
              className="bg-green-600 text-white px-4 py-2 rounded-lg hover:bg-green-700"
            >
              + Crear Stock Inicial
            </button>
          </div>

          <div className="bg-white p-4 rounded-lg shadow-md">
            <select
              value={selectedBranch}
              onChange={(e) => setSelectedBranch(e.target.value === '' ? '' : Number(e.target.value))}
              className="w-full md:w-1/3 border border-gray-300 rounded-lg px-4 py-2"
            >
              <option value="">Seleccione una sucursal</option>
              {branches.map((branch) => (
                <option key={branch.id} value={branch.id}>
                  {branch.name}
                </option>
              ))}
            </select>
          </div>

          {selectedBranch && (
            <div className="bg-white rounded-lg shadow-md overflow-hidden">
              <table className="w-full">
                <thead className="bg-gray-50 border-b">
                  <tr>
                    <th className="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase">
                      Producto
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase">
                      Cantidad
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase">
                      Stock Mín
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase">
                      Stock Máx
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase">
                      Acciones
                    </th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-200">
                  {stock.map((item) => {
                    const product = products.find((p) => p.id === item.productId);
                    const isLowStock = item.quantity < item.minimumStock;
                    return (
                      <tr key={item.id} className="hover:bg-gray-50">
                        <td className="px-6 py-4 text-sm">{product?.name || '-'}</td>
                        <td className="px-6 py-4 text-sm">
                          <span
                            className={`font-semibold ${
                              isLowStock ? 'text-red-600' : 'text-green-600'
                            }`}
                          >
                            {item.quantity}
                            {isLowStock && ' ⚠️'}
                          </span>
                        </td>
                        <td className="px-6 py-4 text-sm">{item.minimumStock}</td>
                        <td className="px-6 py-4 text-sm">{item.maximumStock}</td>
                        <td className="px-6 py-4 text-sm">
                          <button
                            onClick={() => setIsMovementModalOpen(true)}
                            className="text-blue-600 hover:text-blue-800"
                          >
                            Ajustar Stock
                          </button>
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          )}
        </div>
      )}

      {/* Tab Content: Movements */}
      {activeTab === 'movements' && (
        <div className="space-y-4">
          <div className="flex justify-between items-center">
            <h3 className="text-xl font-semibold">Movimientos de Inventario</h3>
            <button
              onClick={() => setIsMovementModalOpen(true)}
              className="bg-primary-600 text-white px-4 py-2 rounded-lg hover:bg-primary-700"
            >
              + Nuevo Movimiento
            </button>
          </div>

          {/* Branch Selector for Movements */}
          <div className="bg-white p-4 rounded-lg shadow-md">
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Seleccione una sucursal para ver sus movimientos:
            </label>
            <select
              value={selectedBranch}
              onChange={(e) => setSelectedBranch(e.target.value === '' ? '' : Number(e.target.value))}
              className="w-full md:w-1/2 border border-gray-300 rounded-lg px-3 py-2"
            >
              <option value="">Seleccione una sucursal...</option>
              {branches.map((branch) => (
                <option key={branch.id} value={branch.id}>
                  {branch.name}
                </option>
              ))}
            </select>
          </div>

          {!selectedBranch ? (
            <div className="bg-white rounded-lg shadow-md p-8 text-center text-gray-500">
              Seleccione una sucursal para ver sus movimientos de inventario
            </div>
          ) : movements.length === 0 ? (
            <div className="bg-white rounded-lg shadow-md p-8 text-center text-gray-500">
              No hay movimientos registrados para esta sucursal
            </div>
          ) : (
            <div className="bg-white rounded-lg shadow-md overflow-hidden">
              <table className="w-full">
                <thead className="bg-gray-50 border-b">
                  <tr>
                    <th className="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase">
                      Fecha
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase">
                      Producto
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase">
                      Tipo
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase">
                      Cantidad
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase">
                      Razón
                    </th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-200">
                  {movements.slice(0, 50).map((movement) => {
                    const product = products.find((p) => p.id === movement.productId);
                    return (
                      <tr key={movement.id} className="hover:bg-gray-50">
                        <td className="px-6 py-4 text-sm">
                          {movement.createdAt
                            ? new Date(movement.createdAt).toLocaleDateString()
                            : '-'}
                        </td>
                        <td className="px-6 py-4 text-sm">{product?.name || '-'}</td>
                        <td className="px-6 py-4 text-sm">
                          <span className="px-2 py-1 bg-blue-100 text-blue-800 rounded-full text-xs">
                            {movement.movementType}
                          </span>
                        </td>
                        <td className="px-6 py-4 text-sm font-semibold">{movement.quantity}</td>
                        <td className="px-6 py-4 text-sm text-gray-600">
                          {movement.reason || '-'}
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          )}
        </div>
      )}

      {/* Branch Modal */}
      <Modal
        isOpen={isBranchModalOpen}
        onClose={() => {
          setIsBranchModalOpen(false);
          resetBranch();
          setEditingBranch(null);
        }}
        title={editingBranch ? 'Editar Sucursal' : 'Nueva Sucursal'}
      >
        <form onSubmit={handleSubmitBranch(onSubmitBranch)} className="space-y-4">
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Código *</label>
              <input
                {...registerBranch('code')}
                className="w-full border border-gray-300 rounded-lg px-3 py-2"
              />
              {branchErrors.code && (
                <p className="text-red-500 text-xs mt-1">{branchErrors.code.message}</p>
              )}
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Nombre *</label>
              <input
                {...registerBranch('name')}
                className="w-full border border-gray-300 rounded-lg px-3 py-2"
              />
              {branchErrors.name && (
                <p className="text-red-500 text-xs mt-1">{branchErrors.name.message}</p>
              )}
            </div>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Dirección *</label>
            <input
              {...registerBranch('address')}
              className="w-full border border-gray-300 rounded-lg px-3 py-2"
            />
            {branchErrors.address && (
              <p className="text-red-500 text-xs mt-1">{branchErrors.address.message}</p>
            )}
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Ciudad *</label>
              <input
                {...registerBranch('city')}
                className="w-full border border-gray-300 rounded-lg px-3 py-2"
              />
              {branchErrors.city && (
                <p className="text-red-500 text-xs mt-1">{branchErrors.city.message}</p>
              )}
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Teléfono *</label>
              <input
                {...registerBranch('phone')}
                className="w-full border border-gray-300 rounded-lg px-3 py-2"
              />
              {branchErrors.phone && (
                <p className="text-red-500 text-xs mt-1">{branchErrors.phone.message}</p>
              )}
            </div>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Horarios</label>
            <input
              {...registerBranch('schedule')}
              className="w-full border border-gray-300 rounded-lg px-3 py-2"
              placeholder="Ej: Lun-Vie 9:00-18:00"
            />
          </div>

          <div className="flex justify-end space-x-3 pt-4">
            <button
              type="button"
              onClick={() => {
                setIsBranchModalOpen(false);
                resetBranch();
                setEditingBranch(null);
              }}
              className="px-4 py-2 border border-gray-300 rounded-lg"
            >
              Cancelar
            </button>
            <button
              type="submit"
              disabled={branchSubmitting}
              className="px-4 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700 disabled:opacity-50"
            >
              {branchSubmitting ? 'Guardando...' : 'Guardar'}
            </button>
          </div>
        </form>
      </Modal>

      {/* Movement Modal */}
      <Modal
        isOpen={isMovementModalOpen}
        onClose={() => {
          setIsMovementModalOpen(false);
          resetMovement();
        }}
        title="Registrar Movimiento"
      >
        <form onSubmit={handleSubmitMovement(onSubmitMovement)} className="space-y-4">
          <div className="bg-blue-50 border border-blue-300 rounded-lg p-3 mb-4">
            <p className="text-sm text-blue-800">
              <strong>ℹ️ Importante:</strong> Si el producto no tiene stock en la sucursal, primero cree el stock inicial usando el botón "Crear Stock Inicial" en la pestaña Stock.
            </p>
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Sucursal *</label>
            <select
              {...registerMovement('branchId')}
              className="w-full border border-gray-300 rounded-lg px-3 py-2"
              defaultValue={selectedBranch || ''}
            >
              <option value="">Seleccione...</option>
              {branches.map((branch) => (
                <option key={branch.id} value={branch.id}>
                  {branch.name}
                </option>
              ))}
            </select>
            {movementErrors.branchId && (
              <p className="text-red-500 text-xs mt-1">{movementErrors.branchId.message}</p>
            )}
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Tipo *</label>
            <select
              {...registerMovement('movementType')}
              className="w-full border border-gray-300 rounded-lg px-3 py-2"
            >
              <option value="">Seleccione...</option>
              {Object.values(MovementType).map((type) => (
                <option key={type} value={type}>
                  {type}
                </option>
              ))}
            </select>
            {movementErrors.movementType && (
              <p className="text-red-500 text-xs mt-1">{movementErrors.movementType.message}</p>
            )}
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Producto *</label>
            <select
              {...registerMovement('productId')}
              className="w-full border border-gray-300 rounded-lg px-3 py-2"
            >
              <option value="">Seleccione...</option>
              {products.map((product) => (
                <option key={product.id} value={product.id}>
                  {product.name}
                </option>
              ))}
            </select>
            {movementErrors.productId && (
              <p className="text-red-500 text-xs mt-1">{movementErrors.productId.message}</p>
            )}
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Cantidad *</label>
            <input
              type="number"
              {...registerMovement('quantity')}
              className="w-full border border-gray-300 rounded-lg px-3 py-2"
            />
            {movementErrors.quantity && (
              <p className="text-red-500 text-xs mt-1">{movementErrors.quantity.message}</p>
            )}
          </div>

          {movementType === 'TRANSFERENCIA' && (
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Sucursal Destino
              </label>
              <select
                {...registerMovement('destinationBranchId')}
                className="w-full border border-gray-300 rounded-lg px-3 py-2"
              >
                <option value="">Seleccione...</option>
                {branches.map((branch) => (
                  <option key={branch.id} value={branch.id}>
                    {branch.name}
                  </option>
                ))}
              </select>
            </div>
          )}

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Razón</label>
            <textarea
              {...registerMovement('reason')}
              rows={3}
              className="w-full border border-gray-300 rounded-lg px-3 py-2"
            />
          </div>

          <div className="flex justify-end space-x-3 pt-4">
            <button
              type="button"
              onClick={() => {
                setIsMovementModalOpen(false);
                resetMovement();
              }}
              className="px-4 py-2 border border-gray-300 rounded-lg"
            >
              Cancelar
            </button>
            <button
              type="submit"
              disabled={movementSubmitting}
              className="px-4 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700 disabled:opacity-50"
            >
              {movementSubmitting ? 'Guardando...' : 'Guardar'}
            </button>
          </div>
        </form>
      </Modal>

      {/* Stock Creation Modal */}
      <Modal
        isOpen={isStockModalOpen}
        onClose={() => {
          setIsStockModalOpen(false);
          resetStock();
        }}
        title="Crear Stock Inicial"
      >
        <form onSubmit={handleSubmitStock(onSubmitStock)} className="space-y-4">
          <div className="bg-green-50 border border-green-200 rounded-lg p-3 mb-4">
            <p className="text-sm text-green-800">
              <strong>✓ Solución:</strong> Use este formulario para crear el registro de stock inicial antes de hacer movimientos.
            </p>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Sucursal *</label>
            <select
              {...registerStock('branchId')}
              className="w-full border border-gray-300 rounded-lg px-3 py-2"
              defaultValue={selectedBranch || ''}
            >
              <option value="">Seleccione...</option>
              {branches.map((branch) => (
                <option key={branch.id} value={branch.id}>
                  {branch.name}
                </option>
              ))}
            </select>
            {stockErrors.branchId && (
              <p className="text-red-500 text-xs mt-1">{stockErrors.branchId.message}</p>
            )}
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Producto *</label>
            <select
              {...registerStock('productId')}
              className="w-full border border-gray-300 rounded-lg px-3 py-2"
            >
              <option value="">Seleccione...</option>
              {products.map((product) => (
                <option key={product.id} value={product.id}>
                  {product.name}
                </option>
              ))}
            </select>
            {stockErrors.productId && (
              <p className="text-red-500 text-xs mt-1">{stockErrors.productId.message}</p>
            )}
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Cantidad Inicial *</label>
            <input
              type="number"
              {...registerStock('quantity')}
              className="w-full border border-gray-300 rounded-lg px-3 py-2"
              placeholder="0"
            />
            {stockErrors.quantity && (
              <p className="text-red-500 text-xs mt-1">{stockErrors.quantity.message}</p>
            )}
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Stock Mínimo *</label>
            <input
              type="number"
              {...registerStock('minimumStock')}
              className="w-full border border-gray-300 rounded-lg px-3 py-2"
              placeholder="20"
            />
            {stockErrors.minimumStock && (
              <p className="text-red-500 text-xs mt-1">{stockErrors.minimumStock.message}</p>
            )}
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Stock Máximo *</label>
            <input
              type="number"
              {...registerStock('maximumStock')}
              className="w-full border border-gray-300 rounded-lg px-3 py-2"
              placeholder="500"
            />
            {stockErrors.maximumStock && (
              <p className="text-red-500 text-xs mt-1">{stockErrors.maximumStock.message}</p>
            )}
          </div>

          <div className="flex justify-end space-x-3 pt-4">
            <button
              type="button"
              onClick={() => {
                setIsStockModalOpen(false);
                resetStock();
              }}
              className="px-4 py-2 border border-gray-300 rounded-lg"
            >
              Cancelar
            </button>
            <button
              type="submit"
              disabled={stockSubmitting}
              className="px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 disabled:opacity-50"
            >
              {stockSubmitting ? 'Creando...' : 'Crear Stock'}
            </button>
          </div>
        </form>
      </Modal>
    </div>
  );
};

export default Inventory;
