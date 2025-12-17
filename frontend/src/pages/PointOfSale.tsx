import React, { useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import { Modal, LoadingSpinner } from '../components';
import { catalogService, inventoryService, salesService } from '../services';
import {
  Product,
  Branch,
  Customer,
  CreateSaleRequest,
  CreateSaleItemRequest,
  CreateCustomerRequest,
  PaymentMethod,
} from '../types';
import { toast } from 'react-toastify';

interface CustomerFormData {
  identificationNumber: string;
  firstName: string;
  lastName: string;
  email?: string;
  phone?: string;
}

const customerSchema = yup.object().shape({
  identificationNumber: yup.string().required('CI es requerida'),
  firstName: yup.string().required('Nombres es requerido'),
  lastName: yup.string().required('Apellidos es requerido'),
  email: yup.string().email('Email inválido').optional(),
  phone: yup.string().optional(),
});

interface CartItem {
  product: Product;
  quantity: number;
  prescriptionFile?: string;
}

const PointOfSale: React.FC = () => {
  const [products, setProducts] = useState<Product[]>([]);
  const [branches, setBranches] = useState<Branch[]>([]);
  const [customers, setCustomers] = useState<Customer[]>([]);
  const [cart, setCart] = useState<CartItem[]>([]);
  const [selectedCustomer, setSelectedCustomer] = useState<Customer | null>(null);
  const [selectedBranch, setSelectedBranch] = useState<number | ''>('');
  const [searchTerm, setSearchTerm] = useState('');
  const [discount, setDiscount] = useState(0);
  const [paymentMethod, setPaymentMethod] = useState<PaymentMethod>(PaymentMethod.EFECTIVO);
  const [loading, setLoading] = useState(true);
  const [isCustomerModalOpen, setIsCustomerModalOpen] = useState(false);
  const [isSuccessModalOpen, setIsSuccessModalOpen] = useState(false);
  const [saleNumber, setSaleNumber] = useState('');
  const [processing, setProcessing] = useState(false);

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors, isSubmitting },
  } = useForm<CustomerFormData>({
    resolver: yupResolver(customerSchema) as any,
  });

  useEffect(() => {
    loadInitialData();
  }, []);

  const loadInitialData = async () => {
    try {
      setLoading(true);
      const [productsData, branchesData, customersData] = await Promise.all([
        catalogService.getAllProducts(),
        inventoryService.getAllBranches(),
        salesService.getAllCustomers(),
      ]);
      setProducts(productsData.filter((p) => p.status === 'ACTIVE'));
      setBranches(branchesData.filter((b) => b.active));
      setCustomers(customersData.filter((c) => c.active));
    } catch (error) {
      toast.error('Error al cargar datos');
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  const addToCart = async (product: Product) => {
    if (!selectedBranch) {
      toast.error('Seleccione una sucursal primero');
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
        setCart([...cart, { product, quantity: 1 }]);
      }
      toast.success('Producto agregado');
    } catch (error) {
      toast.error('Error al verificar stock');
    }
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
        firstName: data.firstName,
        lastName: data.lastName,
        email: data.email,
        phone: data.phone,
      };
      const newCustomer = await salesService.createCustomer(customerData);
      setCustomers([...customers, newCustomer]);
      setSelectedCustomer(newCustomer);
      toast.success('Cliente creado exitosamente');
      setIsCustomerModalOpen(false);
      reset();
    } catch (error) {
      toast.error('Error al crear cliente');
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

    // Validar recetas
    const requiresPrescription = cart.some(
      (item) => item.product.requiresPrescription && !item.prescriptionFile
    );
    if (requiresPrescription) {
      toast.error('Algunos productos requieren receta médica');
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
        prescriptionFile: item.prescriptionFile,
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
        items,
      };

      const sale = await salesService.createSale(saleData);
      setSaleNumber(sale.saleNumber);
      setIsSuccessModalOpen(true);

      // Limpiar carrito
      setCart([]);
      setDiscount(0);
      setSelectedCustomer(null);
      toast.success('Venta procesada exitosamente');
    } catch (error) {
      toast.error('Error al procesar la venta');
      console.error(error);
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
              {Object.values(PaymentMethod).map((method) => (
                <label key={method} className="flex items-center space-x-2">
                  <input
                    type="radio"
                    name="paymentMethod"
                    value={method}
                    checked={paymentMethod === method}
                    onChange={(e) => setPaymentMethod(e.target.value as PaymentMethod)}
                    className="w-4 h-4 text-primary-600"
                  />
                  <span className="text-sm">{method}</span>
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
          <p className="text-gray-600 mb-4">Número de venta:</p>
          <p className="text-2xl font-bold text-primary-600 mb-6">{saleNumber}</p>
          <button
            onClick={() => setIsSuccessModalOpen(false)}
            className="bg-primary-600 text-white px-6 py-2 rounded-lg hover:bg-primary-700"
          >
            Cerrar
          </button>
        </div>
      </Modal>
    </div>
  );
};

export default PointOfSale;
