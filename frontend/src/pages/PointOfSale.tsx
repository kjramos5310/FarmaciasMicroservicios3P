import React, { useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import { Modal, LoadingSpinner } from '../components';
import { catalogService, inventoryService, salesService } from '../services';
import { useAuth } from '../hooks/useAuth';
import {
  Product,
  Branch,
  Customer,
  Sale,
  CreateSaleRequest,
  CreateSaleItemRequest,
  CreateCustomerRequest,
  PaymentMethod,
  Prescription,
  CreatePrescriptionRequest,
  PrescriptionStatus,
} from '../types';
import { toast } from 'react-toastify';

interface CustomerFormData {
  identificationNumber: string;
  firstName: string;
  lastName: string;
  email?: string;
  phone?: string;
  address?: string;
  birthDate?: string;
}

const customerSchema = yup.object().shape({
  identificationNumber: yup.string().required('CI es requerida'),
  firstName: yup.string().required('Nombres es requerido'),
  lastName: yup.string().required('Apellidos es requerido'),
  email: yup.string().email('Email inválido').optional(),
  phone: yup.string().optional(),
  address: yup.string().optional(),
  birthDate: yup.string().optional(),
});

interface PrescriptionFormData {
  doctorName: string;
  doctorLicense: string;
  doctorSpecialty: string;
  issueDate: string;
  expirationDate: string;
  diagnosis: string;
  notes?: string;
}

const prescriptionSchema = yup.object().shape({
  doctorName: yup.string().required('Nombre del médico es requerido'),
  doctorLicense: yup.string().required('Licencia del médico es requerida'),
  doctorSpecialty: yup.string().required('Especialidad es requerida'),
  issueDate: yup.string().required('Fecha de emisión es requerida'),
  expirationDate: yup.string().required('Fecha de expiración es requerida'),
  diagnosis: yup.string().required('Diagnóstico es requerido'),
  notes: yup.string().optional(),
});

interface CartItem {
  product: Product;
  quantity: number;
  prescriptionFile?: string;
  prescriptionId?: number;
}

const PointOfSale: React.FC = () => {
  const { canEdit } = useAuth();
  const [activeView, setActiveView] = useState<'pos' | 'history'>('pos');
  const [products, setProducts] = useState<Product[]>([]);
  const [branches, setBranches] = useState<Branch[]>([]);
  const [customers, setCustomers] = useState<Customer[]>([]);
  const [sales, setSales] = useState<Sale[]>([]);
  const [prescriptions, setPrescriptions] = useState<Prescription[]>([]);
  const [cart, setCart] = useState<CartItem[]>([]);
  const [selectedCustomer, setSelectedCustomer] = useState<Customer | null>(null);
  const [selectedBranch, setSelectedBranch] = useState<number | ''>('');
  const [searchTerm, setSearchTerm] = useState('');
  const [discount, setDiscount] = useState(0);
  const [paymentMethod, setPaymentMethod] = useState<PaymentMethod>(PaymentMethod.CASH);
  const [loading, setLoading] = useState(true);
  const [isCustomerModalOpen, setIsCustomerModalOpen] = useState(false);
  const [isPrescriptionModalOpen, setIsPrescriptionModalOpen] = useState(false);
  const [selectedProductForPrescription, setSelectedProductForPrescription] = useState<number | null>(null);
  const [isSuccessModalOpen, setIsSuccessModalOpen] = useState(false);
  const [saleNumber, setSaleNumber] = useState('');
  const [lastSaleDetails, setLastSaleDetails] = useState<{
    total: number;
    items: number;
    customer: string;
    paymentMethod: string;
  } | null>(null);
  const [processing, setProcessing] = useState(false);

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors, isSubmitting },
  } = useForm<CustomerFormData>({
    resolver: yupResolver(customerSchema) as any,
  });

  const {
    register: registerPrescription,
    handleSubmit: handleSubmitPrescription,
    reset: resetPrescription,
    formState: { errors: errorsPrescription, isSubmitting: isSubmittingPrescription },
  } = useForm<PrescriptionFormData>({
    resolver: yupResolver(prescriptionSchema) as any,
  });

  useEffect(() => {
    loadInitialData();
  }, []);

  useEffect(() => {
    // Cargar prescripciones cuando se seleccione un cliente
    if (selectedCustomer?.id) {
      loadCustomerPrescriptions(selectedCustomer.id);
    } else {
      setPrescriptions([]);
    }
  }, [selectedCustomer]);

  const loadInitialData = async () => {
    try {
      setLoading(true);
      const [productsData, branchesData, customersData, salesData] = await Promise.all([
        catalogService.getAllProducts(),
        inventoryService.getAllBranches(),
        salesService.getAllCustomers(),
        salesService.getAllSales(),
      ]);
      console.log('Customers data received:', customersData);
      setProducts(productsData.filter((p) => p.status === 'ACTIVE'));
      setBranches(branchesData.filter((b) => b.status === 'ACTIVE' || b.active));
      setCustomers(Array.isArray(customersData) ? customersData.filter((c) => c.active !== false) : []);
      setSales(salesData);
      console.log('Customers set:', Array.isArray(customersData) ? customersData.filter((c) => c.active !== false) : []);
    } catch (error) {
      toast.error('Error al cargar datos');
      console.error('Load data error:', error);
    } finally {
      setLoading(false);
    }
  };

  const loadCustomerPrescriptions = async (customerId: number) => {
    try {
      const prescriptionsData = await salesService.getPrescriptionsByCustomer(customerId);
      // Filtrar solo prescripciones activas y no expiradas
      const activePrescriptions = prescriptionsData.filter(
        (p) => p.status === PrescriptionStatus.ACTIVE && new Date(p.expirationDate) > new Date()
      );
      setPrescriptions(activePrescriptions);
    } catch (error) {
      console.error('Error loading prescriptions:', error);
      setPrescriptions([]);
    }
  };

  const addToCart = async (product: Product) => {
    if (!selectedBranch) {
      toast.error('Seleccione una sucursal primero');
      return;
    }

    if (!selectedCustomer) {
      toast.error('Seleccione un cliente primero');
      return;
    }

    try {
      // Validar stock
      const stock = await inventoryService.getStockByProduct(Number(selectedBranch), product.id!);
      if (stock.quantity <= 0) {
        toast.error('Producto sin stock disponible');
        return;
      }

      const existingItem = cart.find((item) => item.product.id === product.id);
      if (existingItem) {
        if (existingItem.quantity + 1 > stock.quantity) {
          toast.error('Stock insuficiente');
          return;
        }
        setCart(
          cart.map((item) =>
            item.product.id === product.id ? { ...item, quantity: item.quantity + 1 } : item
          )
        );
      } else {
        // Si el producto requiere receta, abrir modal de prescripción
        if (product.requiresPrescription) {
          setSelectedProductForPrescription(product.id!);
          setIsPrescriptionModalOpen(true);
        } else {
          setCart([...cart, { product, quantity: 1 }]);
          toast.success('Producto agregado');
        }
      }
    } catch (error) {
      toast.error('Error al verificar stock');
    }
  };

  const addToCartWithPrescription = (productId: number, prescriptionId?: number) => {
    const product = products.find(p => p.id === productId);
    if (!product) return;

    setCart([...cart, { product, quantity: 1, prescriptionId }]);
    toast.success('Producto agregado con receta');
  };

  const updateQuantity = (productId: number, newQuantity: number) => {
    if (newQuantity <= 0) {
      removeFromCart(productId);
      return;
    }
    setCart(
      cart.map((item) =>
        item.product.id === productId ? { ...item, quantity: newQuantity } : item
      )
    );
  };

  const removeFromCart = (productId: number) => {
    setCart(cart.filter((item) => item.product.id !== productId));
  };

  const calculateSubtotal = () => {
    return cart.reduce((sum, item) => sum + (item.product.basePrice ?? 0) * item.quantity, 0);
  };

  const calculateTax = () => {
    const subtotal = calculateSubtotal();
    return subtotal * 0.12; // 12% IVA
  };

  const calculateTotal = () => {
    return calculateSubtotal() + calculateTax() - discount;
  };

  const onSubmitCustomer = async (data: CustomerFormData) => {
    try {
      const customerData: CreateCustomerRequest = {
        identificationNumber: data.identificationNumber,
        identificationType: 'CI',
        firstName: data.firstName,
        lastName: data.lastName,
        email: data.email || '',
        phone: data.phone || '',
        address: data.address || 'N/A',
        city: 'Quito',
        birthDate: data.birthDate || '2000-01-01',
        type: 'REGULAR',
      };
      console.log('Creating customer:', customerData);
      const newCustomer = await salesService.createCustomer(customerData);
      console.log('Customer created:', newCustomer);
      setCustomers([...customers, newCustomer]);
      setSelectedCustomer(newCustomer);
      toast.success('Cliente creado exitosamente');
      setIsCustomerModalOpen(false);
      reset();
    } catch (error: any) {
      let errorMsg = 'Error al crear cliente';
      
      console.error('Customer creation error:', error);
      console.error('Error response data:', error.response?.data);
      
      // Mostrar detalles de validación si existen
      if (error.response?.data?.errors) {
        const validationErrors = error.response.data.errors;
        console.error('Validation errors:', validationErrors);
        const errorList = Object.entries(validationErrors)
          .map(([field, msg]) => `${field}: ${msg}`)
          .join('; ');
        errorMsg = `Errores: ${errorList}`;
      } else if (error.response?.data?.error) {
        errorMsg = error.response.data.error;
      } else if (error.response?.data?.message) {
        errorMsg = error.response.data.message;
      } else if (error.message) {
        errorMsg = error.message;
      }
      
      toast.error(errorMsg);
    }
  };

  const onSubmitPrescription = async (data: PrescriptionFormData) => {
    if (!selectedCustomer) {
      toast.error('Debe seleccionar un cliente primero');
      return;
    }

    try {
      const prescriptionData: CreatePrescriptionRequest = {
        customerId: selectedCustomer.id!,
        doctorName: data.doctorName,
        doctorLicense: data.doctorLicense,
        doctorSpecialty: data.doctorSpecialty,
        issueDate: data.issueDate,
        expirationDate: data.expirationDate,
        diagnosis: data.diagnosis,
        notes: data.notes,
        status: PrescriptionStatus.ACTIVE,
      };

      const newPrescription = await salesService.createPrescription(prescriptionData);
      setPrescriptions([...prescriptions, newPrescription]);
      
      // Agregar producto al carrito con la prescripción
      if (selectedProductForPrescription) {
        addToCartWithPrescription(selectedProductForPrescription, newPrescription.id);
      }
      
      toast.success('Prescripción creada exitosamente');
      setIsPrescriptionModalOpen(false);
      resetPrescription();
      setSelectedProductForPrescription(null);
    } catch (error: any) {
      let errorMsg = 'Error al crear prescripción';
      if (error.response?.data?.message) {
        errorMsg = error.response.data.message;
      }
      toast.error(errorMsg);
      console.error('Prescription creation error:', error);
    }
  };

  const processSale = async () => {
    if (!selectedCustomer) {
      toast.error('Seleccione un cliente');
      return;
    }
    if (!selectedBranch) {
      toast.error('Seleccione una sucursal');
      return;
    }
    if (cart.length === 0) {
      toast.error('El carrito está vacío');
      return;
    }

    // Validar que productos con receta tengan prescriptionId
    const missingPrescriptions = cart.filter(
      (item) => item.product.requiresPrescription && !item.prescriptionId
    );
    if (missingPrescriptions.length > 0) {
      toast.error(`Faltan recetas para: ${missingPrescriptions.map(i => i.product.name).join(', ')}`);
      return;
    }

    try {
      setProcessing(true);

      // Preparar items
      const items: CreateSaleItemRequest[] = cart.map((item) => ({
        productId: item.product.id!,
        quantity: item.quantity,
        unitPrice: item.product.basePrice ?? 0,
        subtotal: (item.product.basePrice ?? 0) * item.quantity,
        prescriptionId: item.prescriptionId,
      }));

      // Crear venta
      const saleData: CreateSaleRequest = {
        customerId: selectedCustomer.id!,
        branchId: Number(selectedBranch),
        subtotal: calculateSubtotal(),
        tax: calculateTax(),
        discount,
        total: calculateTotal(),
        paymentMethod,
        cashierName: 'Cajero Web',
        items,
      };

      console.log('Processing sale with data:', saleData);
      const sale = await salesService.createSale(saleData);
      console.log('Sale processed:', sale);
      setSaleNumber(sale.saleNumber);
      setLastSaleDetails({
        total: saleData.total,
        items: saleData.items.length,
        customer: `${selectedCustomer.firstName} ${selectedCustomer.lastName}`,
        paymentMethod: paymentMethod,
      });
      setIsSuccessModalOpen(true);

      // Limpiar carrito
      setCart([]);
      setDiscount(0);
      setSelectedCustomer(null);
      
      // Recargar ventas para actualizar el historial
      const updatedSales = await salesService.getAllSales();
      setSales(updatedSales);
      
      toast.success('Venta procesada exitosamente');
    } catch (error: any) {
      console.error('Sale processing error:', error);
      console.error('Error response:', error.response?.data);
      
      let errorMsg = 'Error al procesar la venta';
      if (error.response?.data?.errors) {
        const validationErrors = Object.entries(error.response.data.errors)
          .map(([field, msg]) => `${field}: ${msg}`)
          .join('; ');
        errorMsg = `Errores: ${validationErrors}`;
      } else if (error.response?.data?.error) {
        errorMsg = error.response.data.error;
      } else if (error.response?.data?.message) {
        errorMsg = error.response.data.message;
      } else if (error.message) {
        errorMsg = error.message;
      }
      
      toast.error(errorMsg);
    } finally {
      setProcessing(false);
    }
  };

  const filteredProducts = products.filter(
    (product) =>
      product.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      product.code.toLowerCase().includes(searchTerm.toLowerCase())
  );

  if (loading) {
    return (
      <div className="flex items-center justify-center h-96">
        <LoadingSpinner size="lg" />
      </div>
    );
  }

  return (
    <div className="space-y-4">
      {/* Tabs */}
      <div className="bg-white rounded-lg shadow-md">
        <div className="border-b">
          <nav className="flex -mb-px">
            <button
              onClick={() => setActiveView('pos')}
              className={`px-6 py-3 text-sm font-medium border-b-2 ${
                activeView === 'pos'
                  ? 'border-primary-600 text-primary-600'
                  : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
              }`}
            >
              Punto de Venta
            </button>
            <button
              onClick={() => setActiveView('history')}
              className={`px-6 py-3 text-sm font-medium border-b-2 ${
                activeView === 'history'
                  ? 'border-primary-600 text-primary-600'
                  : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
              }`}
            >
              Historial de Ventas ({sales.length})
            </button>
          </nav>
        </div>
      </div>

      {/* View: Punto de Venta */}
      {activeView === 'pos' && (
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6 h-full">
          {/* LEFT: Product Catalog */}
          <div className="lg:col-span-2 space-y-4">
        <div className="bg-white rounded-lg shadow-md p-4">
          <input
            type="text"
            placeholder="Buscar productos..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="w-full border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-primary-500"
          />
        </div>

        <div className="grid grid-cols-2 md:grid-cols-3 gap-4 max-h-[calc(100vh-250px)] overflow-y-auto">
          {filteredProducts.map((product) => (
            <div
              key={product.id}
              onClick={() => addToCart(product)}
              className="bg-white rounded-lg shadow-md p-4 cursor-pointer hover:shadow-lg transition-shadow"
            >
              <div className="w-full h-32 bg-gray-200 rounded-lg mb-3 flex items-center justify-center text-4xl">
                💊
              </div>
              <h3 className="font-semibold text-sm mb-1 truncate">{product.name}</h3>
              <p className="text-gray-600 text-xs mb-2 truncate">{product.code}</p>
              <p className="text-primary-600 font-bold text-lg">${(product.basePrice ?? 0).toFixed(2)}</p>
              {product.requiresPrescription && (
                <span className="inline-block mt-2 text-xs bg-yellow-100 text-yellow-800 px-2 py-1 rounded">
                  Requiere receta
                </span>
              )}
            </div>
          ))}
        </div>
      </div>

      {/* RIGHT: Shopping Cart */}
      <div className="space-y-4">
        <div className="bg-white rounded-lg shadow-md p-4 space-y-4">
          <h3 className="font-bold text-lg">Carrito de Venta</h3>

          {/* Cliente */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">Cliente *</label>
            <div className="flex gap-2">
              <select
                value={selectedCustomer?.id || ''}
                onChange={(e) => {
                  const customer = customers.find((c) => c.id === Number(e.target.value));
                  setSelectedCustomer(customer || null);
                }}
                className="flex-1 border border-gray-300 rounded-lg px-3 py-2"
              >
                <option value="">Seleccione...</option>
                {customers.map((customer) => (
                  <option key={customer.id} value={customer.id}>
                    {customer.firstName} {customer.lastName} - {customer.identificationNumber}
                  </option>
                ))}
              </select>
              <button
                onClick={() => setIsCustomerModalOpen(true)}
                className="bg-green-600 text-white px-3 py-2 rounded-lg hover:bg-green-700"
              >
                +
              </button>
            </div>
          </div>

          {/* Sucursal */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">Sucursal *</label>
            <select
              value={selectedBranch}
              onChange={(e) => setSelectedBranch(e.target.value === '' ? '' : Number(e.target.value))}
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

          {/* Items en carrito */}
          <div className="border-t pt-4 max-h-60 overflow-y-auto">
            {cart.length === 0 ? (
              <p className="text-gray-500 text-sm text-center py-8">Carrito vacío</p>
            ) : (
              cart.map((item) => (
                <div key={item.product.id} className="mb-3 pb-3 border-b last:border-0">
                  <div className="flex justify-between items-start mb-2">
                    <div className="flex-1">
                      <p className="font-medium text-sm">{item.product.name}</p>
                      <p className="text-xs text-gray-600">${(item.product.basePrice ?? 0).toFixed(2)}</p>
                      {item.product.requiresPrescription && (
                        <div className="mt-1">
                          {item.prescriptionId ? (
                            <span className="text-xs bg-green-100 text-green-800 px-2 py-1 rounded">
                              ✓ Con receta
                            </span>
                          ) : (
                            <span className="text-xs bg-red-100 text-red-800 px-2 py-1 rounded">
                              ⚠ Falta receta
                            </span>
                          )}
                        </div>
                      )}
                    </div>
                    <button
                      onClick={() => removeFromCart(item.product.id!)}
                      className="text-red-600 hover:text-red-800 text-sm"
                    >
                      ×
                    </button>
                  </div>
                  <div className="flex items-center justify-between">
                    <div className="flex items-center space-x-2">
                      <button
                        onClick={() => updateQuantity(item.product.id!, item.quantity - 1)}
                        className="w-6 h-6 bg-gray-200 rounded hover:bg-gray-300"
                      >
                        -
                      </button>
                      <span className="w-8 text-center">{item.quantity}</span>
                      <button
                        onClick={() => updateQuantity(item.product.id!, item.quantity + 1)}
                        className="w-6 h-6 bg-gray-200 rounded hover:bg-gray-300"
                      >
                        +
                      </button>
                    </div>
                    <span className="font-semibold">
                      ${((item.product.basePrice ?? 0) * item.quantity).toFixed(2)}
                    </span>
                  </div>
                </div>
              ))
            )}
          </div>

          {/* Resumen */}
          <div className="border-t pt-4 space-y-2">
            <div className="flex justify-between text-sm">
              <span>Subtotal:</span>
              <span>${calculateSubtotal().toFixed(2)}</span>
            </div>
            <div className="flex justify-between text-sm">
              <span>IVA (12%):</span>
              <span>${calculateTax().toFixed(2)}</span>
            </div>
            <div className="flex justify-between text-sm items-center">
              <span>Descuento:</span>
              <input
                type="number"
                value={discount}
                onChange={(e) => setDiscount(Number(e.target.value))}
                className="w-24 border border-gray-300 rounded px-2 py-1 text-right"
                step="0.01"
              />
            </div>
            <div className="flex justify-between text-lg font-bold border-t pt-2">
              <span>TOTAL:</span>
              <span className="text-green-600">${calculateTotal().toFixed(2)}</span>
            </div>
          </div>

          {/* Método de pago */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">Método de Pago</label>
            <div className="space-y-2">
              {Object.entries({
                [PaymentMethod.CASH]: 'Efectivo',
                [PaymentMethod.CARD]: 'Tarjeta',
                [PaymentMethod.TRANSFER]: 'Transferencia',
                [PaymentMethod.CHECK]: 'Cheque',
              }).map(([value, label]) => (
                <label key={value} className="flex items-center space-x-2">
                  <input
                    type="radio"
                    name="paymentMethod"
                    value={value}
                    checked={paymentMethod === value}
                    onChange={(e) => setPaymentMethod(e.target.value as PaymentMethod)}
                    className="w-4 h-4 text-primary-600"
                  />
                  <span className="text-sm">{label}</span>
                </label>
              ))}
            </div>
          </div>

          {/* Botón Procesar */}
          <button
            onClick={processSale}
            disabled={processing || cart.length === 0}
            className="w-full bg-green-600 text-white py-3 rounded-lg font-bold text-lg hover:bg-green-700 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            {processing ? 'PROCESANDO...' : 'PROCESAR VENTA'}
          </button>
        </div>
        </div>
      </div>
      )}

      {/* View: Historial de Ventas */}
      {activeView === 'history' && (
        <div className="bg-white rounded-lg shadow-md p-6">
          <h2 className="text-2xl font-bold mb-4">Historial de Ventas</h2>
          
          {sales.length === 0 ? (
            <div className="text-center py-12 text-gray-500">
              <p className="text-lg">No hay ventas registradas</p>
              <p className="text-sm mt-2">Las ventas aparecerán aquí una vez que las proceses</p>
            </div>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead className="bg-gray-50 border-b">
                  <tr>
                    <th className="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase">
                      N° Venta
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase">
                      Fecha
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase">
                      Cliente
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase">
                      Sucursal
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase">
                      M. Pago
                    </th>
                    <th className="px-6 py-3 text-right text-xs font-semibold text-gray-600 uppercase">
                      Total
                    </th>
                    <th className="px-6 py-3 text-center text-xs font-semibold text-gray-600 uppercase">
                      Estado
                    </th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-200">
                  {sales.map((sale) => {
                    // El backend puede incluir el objeto customer completo o solo el ID
                    const customer = sale.customer || customers.find((c) => c.id === sale.customerId);
                    const branch = sale.branch || branches.find((b) => b.id === sale.branchId);
                    
                    // Debug logging
                    if (!customer) {
                      console.log('Customer not found for sale:', sale.id, 'customerId:', sale.customerId);
                      console.log('Available customers:', customers.map(c => c.id));
                    }
                    
                    return (
                      <tr key={sale.id} className="hover:bg-gray-50">
                        <td className="px-6 py-4 text-sm font-medium text-primary-600">
                          {sale.saleNumber}
                        </td>
                        <td className="px-6 py-4 text-sm text-gray-900">
                          {new Date(sale.saleDate || sale.createdAt || '').toLocaleString('es-EC', {
                            year: 'numeric',
                            month: 'short',
                            day: 'numeric',
                            hour: '2-digit',
                            minute: '2-digit',
                          })}
                        </td>
                        <td className="px-6 py-4 text-sm text-gray-900">
                          {customer ? `${customer.firstName} ${customer.lastName}` : '-'}
                        </td>
                        <td className="px-6 py-4 text-sm text-gray-900">
                          {branch?.name || '-'}
                        </td>
                        <td className="px-6 py-4 text-sm text-gray-900">
                          {sale.paymentMethod === 'CASH' && 'Efectivo'}
                          {sale.paymentMethod === 'CARD' && 'Tarjeta'}
                          {sale.paymentMethod === 'TRANSFER' && 'Transferencia'}
                          {sale.paymentMethod === 'CHECK' && 'Cheque'}
                        </td>
                        <td className="px-6 py-4 text-sm text-right font-semibold text-gray-900">
                          ${sale.total.toFixed(2)}
                        </td>
                        <td className="px-6 py-4 text-center">
                          <span
                            className={`px-2 py-1 text-xs font-medium rounded-full ${
                              sale.status === 'COMPLETADA'
                                ? 'bg-green-100 text-green-800'
                                : sale.status === 'CANCELADA'
                                ? 'bg-red-100 text-red-800'
                                : 'bg-yellow-100 text-yellow-800'
                            }`}
                          >
                            {sale.status}
                          </span>
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

      {/* Customer Modal */}
      <Modal
        isOpen={isCustomerModalOpen}
        onClose={() => {
          setIsCustomerModalOpen(false);
          reset();
        }}
        title="Nuevo Cliente"
      >
        <form onSubmit={handleSubmit(onSubmitCustomer)} className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">CI *</label>
            <input
              {...register('identificationNumber')}
              className="w-full border border-gray-300 rounded-lg px-3 py-2"
            />
            {errors.identificationNumber && (
              <p className="text-red-500 text-xs mt-1">{errors.identificationNumber.message}</p>
            )}
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Nombres *</label>
              <input
                {...register('firstName')}
                className="w-full border border-gray-300 rounded-lg px-3 py-2"
              />
              {errors.firstName && (
                <p className="text-red-500 text-xs mt-1">{errors.firstName.message}</p>
              )}
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Apellidos *</label>
              <input
                {...register('lastName')}
                className="w-full border border-gray-300 rounded-lg px-3 py-2"
              />
              {errors.lastName && (
                <p className="text-red-500 text-xs mt-1">{errors.lastName.message}</p>
              )}
            </div>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Email</label>
            <input
              type="email"
              {...register('email')}
              className="w-full border border-gray-300 rounded-lg px-3 py-2"
            />
            {errors.email && <p className="text-red-500 text-xs mt-1">{errors.email.message}</p>}
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Teléfono</label>
            <input
              {...register('phone')}
              className="w-full border border-gray-300 rounded-lg px-3 py-2"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Dirección</label>
            <input
              {...register('address')}
              className="w-full border border-gray-300 rounded-lg px-3 py-2"
              placeholder="Opcional"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Fecha de Nacimiento</label>
            <input
              type="date"
              {...register('birthDate')}
              className="w-full border border-gray-300 rounded-lg px-3 py-2"
            />
          </div>

          <div className="flex justify-end space-x-3 pt-4">
            <button
              type="button"
              onClick={() => {
                setIsCustomerModalOpen(false);
                reset();
              }}
              className="px-4 py-2 border border-gray-300 rounded-lg"
            >
              Cancelar
            </button>
            <button
              type="submit"
              disabled={isSubmitting}
              className="px-4 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700 disabled:opacity-50"
            >
              {isSubmitting ? 'Guardando...' : 'Guardar'}
            </button>
          </div>
        </form>
      </Modal>

      {/* Prescription Modal */}
      <Modal
        isOpen={isPrescriptionModalOpen}
        onClose={() => {
          setIsPrescriptionModalOpen(false);
          resetPrescription();
          setSelectedProductForPrescription(null);
        }}
        title="Prescripción Médica Requerida"
        maxWidth="lg"
      >
        <div className="space-y-4">
          {/* Selector de prescripción existente */}
          {prescriptions.length > 0 && (
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Seleccionar prescripción existente
              </label>
              <select
                onChange={(e) => {
                  const prescriptionId = Number(e.target.value);
                  if (prescriptionId && selectedProductForPrescription) {
                    addToCartWithPrescription(selectedProductForPrescription, prescriptionId);
                    setIsPrescriptionModalOpen(false);
                    setSelectedProductForPrescription(null);
                  }
                }}
                className="w-full border border-gray-300 rounded-lg px-3 py-2 mb-4"
              >
                <option value="">Seleccione una prescripción...</option>
                {prescriptions.map((prescription) => (
                  <option key={prescription.id} value={prescription.id}>
                    {prescription.prescriptionNumber} - Dr. {prescription.doctorName} - {prescription.diagnosis}
                  </option>
                ))}
              </select>
              <div className="text-center text-gray-500 mb-4">- O -</div>
            </div>
          )}

          {/* Formulario de nueva prescripción */}
          <h4 className="font-semibold text-gray-700">Crear nueva prescripción</h4>
          <form onSubmit={handleSubmitPrescription(onSubmitPrescription)} className="space-y-4">
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Nombre del Médico *
                </label>
                <input
                  {...registerPrescription('doctorName')}
                  placeholder="Dr. Juan Pérez"
                  className="w-full border border-gray-300 rounded-lg px-3 py-2"
                />
                {errorsPrescription.doctorName && (
                  <p className="text-red-500 text-xs mt-1">{errorsPrescription.doctorName.message}</p>
                )}
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Licencia Médica *
                </label>
                <input
                  {...registerPrescription('doctorLicense')}
                  placeholder="MSP-12345"
                  className="w-full border border-gray-300 rounded-lg px-3 py-2"
                />
                {errorsPrescription.doctorLicense && (
                  <p className="text-red-500 text-xs mt-1">{errorsPrescription.doctorLicense.message}</p>
                )}
              </div>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Especialidad *
              </label>
              <input
                {...registerPrescription('doctorSpecialty')}
                placeholder="Medicina General"
                className="w-full border border-gray-300 rounded-lg px-3 py-2"
              />
              {errorsPrescription.doctorSpecialty && (
                <p className="text-red-500 text-xs mt-1">{errorsPrescription.doctorSpecialty.message}</p>
              )}
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Fecha de Emisión *
                </label>
                <input
                  type="date"
                  {...registerPrescription('issueDate')}
                  className="w-full border border-gray-300 rounded-lg px-3 py-2"
                />
                {errorsPrescription.issueDate && (
                  <p className="text-red-500 text-xs mt-1">{errorsPrescription.issueDate.message}</p>
                )}
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Fecha de Expiración *
                </label>
                <input
                  type="date"
                  {...registerPrescription('expirationDate')}
                  className="w-full border border-gray-300 rounded-lg px-3 py-2"
                />
                {errorsPrescription.expirationDate && (
                  <p className="text-red-500 text-xs mt-1">{errorsPrescription.expirationDate.message}</p>
                )}
              </div>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Diagnóstico *
              </label>
              <input
                {...registerPrescription('diagnosis')}
                placeholder="Infección respiratoria"
                className="w-full border border-gray-300 rounded-lg px-3 py-2"
              />
              {errorsPrescription.diagnosis && (
                <p className="text-red-500 text-xs mt-1">{errorsPrescription.diagnosis.message}</p>
              )}
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Notas
              </label>
              <textarea
                {...registerPrescription('notes')}
                rows={3}
                placeholder="Instrucciones adicionales..."
                className="w-full border border-gray-300 rounded-lg px-3 py-2"
              />
            </div>

            <div className="flex justify-end space-x-3 pt-4">
              <button
                type="button"
                onClick={() => {
                  setIsPrescriptionModalOpen(false);
                  resetPrescription();
                  setSelectedProductForPrescription(null);
                }}
                className="px-4 py-2 border border-gray-300 rounded-lg"
              >
                Cancelar
              </button>
              <button
                type="submit"
                disabled={isSubmittingPrescription}
                className="px-4 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700 disabled:opacity-50"
              >
                {isSubmittingPrescription ? 'Guardando...' : 'Guardar y Agregar al Carrito'}
              </button>
            </div>
          </form>
        </div>
      </Modal>

      {/* Success Modal */}
      <Modal
        isOpen={isSuccessModalOpen}
        onClose={() => setIsSuccessModalOpen(false)}
        title="¡Venta Exitosa!"
        maxWidth="md"
      >
        <div className="text-center py-6">
          <div className="text-6xl mb-4">✅</div>
          <p className="text-xl font-semibold mb-2">Venta procesada correctamente</p>
          
          <div className="bg-gray-50 rounded-lg p-4 my-6 text-left">
            <div className="space-y-2">
              <div className="flex justify-between">
                <span className="font-medium">Número de venta:</span>
                <span className="font-bold text-primary-600">{saleNumber}</span>
              </div>
              {lastSaleDetails && (
                <>
                  <div className="flex justify-between">
                    <span className="font-medium">Cliente:</span>
                    <span>{lastSaleDetails.customer}</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="font-medium">Productos:</span>
                    <span>{lastSaleDetails.items} items</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="font-medium">Método de pago:</span>
                    <span>
                      {lastSaleDetails.paymentMethod === 'CASH' && 'Efectivo'}
                      {lastSaleDetails.paymentMethod === 'CARD' && 'Tarjeta'}
                      {lastSaleDetails.paymentMethod === 'TRANSFER' && 'Transferencia'}
                      {lastSaleDetails.paymentMethod === 'CHECK' && 'Cheque'}
                    </span>
                  </div>
                  <div className="flex justify-between border-t pt-2 mt-2">
                    <span className="font-bold text-lg">Total:</span>
                    <span className="font-bold text-lg text-primary-600">
                      ${lastSaleDetails.total.toFixed(2)}
                    </span>
                  </div>
                </>
              )}
            </div>
          </div>
          
          <div className="bg-blue-50 border border-blue-200 rounded-lg p-3 mb-4">
            <p className="text-sm text-blue-800">
              💡 Puedes ver esta venta reflejada en el <strong>Dashboard</strong> (métricas actualizadas) y en el <strong>Inventario</strong> (stock actualizado).
            </p>
          </div>

          <div className="flex gap-3 justify-center">
            <button
              onClick={() => {
                setIsSuccessModalOpen(false);
                window.location.href = '/dashboard';
              }}
              className="bg-blue-600 text-white px-6 py-2 rounded-lg hover:bg-blue-700"
            >
              Ver Dashboard
            </button>
            <button
              onClick={() => setIsSuccessModalOpen(false)}
              className="bg-primary-600 text-white px-6 py-2 rounded-lg hover:bg-primary-700"
            >
              Nueva Venta
            </button>
          </div>
        </div>
      </Modal>
    </div>
  );
};

export default PointOfSale;
