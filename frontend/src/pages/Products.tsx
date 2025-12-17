import React, { useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import { Modal, LoadingSpinner } from '../components';
import { catalogService } from '../services';
import { Product, Category, Laboratory, CreateProductRequest, CreateCategoryRequest, CreateLaboratoryRequest } from '../types';
import { toast } from 'react-toastify';

interface ProductFormData {
  code: string;
  name: string;
  description?: string;
  categoryId: number;
  laboratoryId: number;
  basePrice: number;
  requiresPrescription?: boolean;
  isControlled?: boolean;
}

const productSchema = yup.object().shape({
  code: yup.string().required('Código es requerido').max(50, 'Máximo 50 caracteres'),
  name: yup.string().required('Nombre es requerido').max(200, 'Máximo 200 caracteres'),
  description: yup.string().optional(),
  categoryId: yup.number().required('Categoría es requerida').positive(),
  laboratoryId: yup.number().required('Laboratorio es requerido').positive(),
  basePrice: yup
    .number()
    .required('Precio es requerido')
    .positive('Debe ser positivo')
    .test('decimal', 'Máximo 2 decimales', (value) => {
      if (value === undefined) return true;
      return /^\d+(\.\d{1,2})?$/.test(value.toString());
    }),
  requiresPrescription: yup.boolean().optional().default(false),
  isControlled: yup.boolean().optional().default(false),
});

const categorySchema = yup.object().shape({
  code: yup.string().required('Código es requerido').max(20, 'Máximo 20 caracteres'),
  name: yup.string().required('Nombre es requerido').max(100, 'Máximo 100 caracteres'),
  description: yup.string().optional(),
});

const laboratorySchema = yup.object().shape({
  name: yup.string().required('Nombre es requerido').max(200, 'Máximo 200 caracteres'),
  country: yup.string().optional().max(100, 'Máximo 100 caracteres'),
  contactEmail: yup.string().optional().email('Email inválido'),
  phone: yup.string().optional().max(20, 'Máximo 20 caracteres'),
  website: yup.string().optional().url('URL inválida'),
});

const Products: React.FC = () => {
  const [products, setProducts] = useState<Product[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [laboratories, setLaboratories] = useState<Laboratory[]>([]);
  const [loading, setLoading] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isCategoryModalOpen, setIsCategoryModalOpen] = useState(false);
  const [isLaboratoryModalOpen, setIsLaboratoryModalOpen] = useState(false);
  const [editingProduct, setEditingProduct] = useState<Product | null>(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [filterCategory, setFilterCategory] = useState<number | ''>('');
  const [filterLaboratory, setFilterLaboratory] = useState<number | ''>('');

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors, isSubmitting },
  } = useForm<ProductFormData>({
    resolver: yupResolver(productSchema) as any,
  });

  const {
    register: registerCategory,
    handleSubmit: handleSubmitCategory,
    reset: resetCategory,
    formState: { errors: errorsCategory, isSubmitting: isSubmittingCategory },
  } = useForm<CreateCategoryRequest>({
    resolver: yupResolver(categorySchema) as any,
  });

  const {
    register: registerLaboratory,
    handleSubmit: handleSubmitLaboratory,
    reset: resetLaboratory,
    formState: { errors: errorsLaboratory, isSubmitting: isSubmittingLaboratory },
  } = useForm<CreateLaboratoryRequest>({
    resolver: yupResolver(laboratorySchema) as any,
  });

  useEffect(() => {
    loadInitialData();
  }, []);

  const loadInitialData = async () => {
    try {
      setLoading(true);
      const [productsData, categoriesData, laboratoriesData] = await Promise.all([
        catalogService.getAllProducts(),
        catalogService.getAllCategories(),
        catalogService.getAllLaboratories(),
      ]);
      setProducts(Array.isArray(productsData) ? productsData : []);
      setCategories(Array.isArray(categoriesData) ? categoriesData : []);
      setLaboratories(Array.isArray(laboratoriesData) ? laboratoriesData : []);
    } catch (error) {
      toast.error('Error al cargar datos');
      console.error(error);
      // Ensure arrays remain empty on error
      setProducts([]);
      setCategories([]);
      setLaboratories([]);
    } finally {
      setLoading(false);
    }
  };

  const onSubmit = async (data: ProductFormData) => {
    try {
      const productData: CreateProductRequest = {
        code: data.code,
        barcode: data.code, // Use code as barcode if not provided
        name: data.name,
        genericName: data.name, // Use name as genericName if not provided
        description: data.description,
        presentation: 'Unidad', // Default presentation
        categoryId: data.categoryId,
        laboratoryId: data.laboratoryId,
        basePrice: data.basePrice,
        requiresPrescription: data.requiresPrescription || false,
        isControlled: data.isControlled || false,
        activeIngredient: data.name, // Default to product name
        dosage: 'Según indicación médica', // Default dosage
        status: 'ACTIVE',
      };

      if (editingProduct) {
        await catalogService.updateProduct(editingProduct.id!, { ...productData, id: editingProduct.id! });
        toast.success('Producto actualizado exitosamente');
      } else {
        await catalogService.createProduct(productData);
        toast.success('Producto creado exitosamente');
      }
      setIsModalOpen(false);
      reset();
      setEditingProduct(null);
      loadInitialData();
    } catch (error) {
      toast.error('Error al guardar producto');
      console.error(error);
    }
  };

  const onSubmitCategory = async (data: CreateCategoryRequest) => {
    try {
      await catalogService.createCategory({
        ...data,
        isActive: true,
      });
      toast.success('Categoría creada exitosamente');
      setIsCategoryModalOpen(false);
      resetCategory();
      // Reload categories
      const categoriesData = await catalogService.getAllCategories();
      setCategories(Array.isArray(categoriesData) ? categoriesData : []);
    } catch (error) {
      toast.error('Error al crear categoría');
      console.error(error);
    }
  };

  const onSubmitLaboratory = async (data: CreateLaboratoryRequest) => {
    try {
      await catalogService.createLaboratory({
        ...data,
        isActive: true,
      });
      toast.success('Laboratorio creado exitosamente');
      setIsLaboratoryModalOpen(false);
      resetLaboratory();
      // Reload laboratories
      const laboratoriesData = await catalogService.getAllLaboratories();
      setLaboratories(Array.isArray(laboratoriesData) ? laboratoriesData : []);
    } catch (error) {
      toast.error('Error al crear laboratorio');
      console.error(error);
    }
  };

  const handleEdit = (product: Product) => {
    setEditingProduct(product);
    reset({
      code: product.code,
      name: product.name,
      description: product.description,
      categoryId: product.categoryId || product.category?.id,
      laboratoryId: product.laboratoryId || product.laboratory?.id,
      basePrice: product.basePrice,
      requiresPrescription: product.requiresPrescription,
      isControlled: product.isControlled,
    });
    setIsModalOpen(true);
  };

  const handleDelete = async (id: number) => {
    if (window.confirm('¿Está seguro de eliminar este producto?')) {
      try {
        await catalogService.deleteProduct(id);
        toast.success('Producto eliminado exitosamente');
        loadInitialData();
      } catch (error) {
        toast.error('Error al eliminar producto');
        console.error(error);
      }
    }
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
    setEditingProduct(null);
    reset();
  };

  const filteredProducts = (products || []).filter((product) => {
    const matchesSearch =
      product.name?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      product.code?.toLowerCase().includes(searchTerm.toLowerCase());
    
    // Handle both nested objects and direct IDs
    const productCategoryId = product.categoryId || product.category?.id;
    const productLaboratoryId = product.laboratoryId || product.laboratory?.id;
    
    const matchesCategory = filterCategory === '' || productCategoryId === filterCategory;
    const matchesLaboratory = filterLaboratory === '' || productLaboratoryId === filterLaboratory;
    
    return matchesSearch && matchesCategory && matchesLaboratory;
  });

  if (loading) {
    return (
      <div className="flex items-center justify-center h-96">
        <LoadingSpinner size="lg" />
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex justify-between items-center">
        <h2 className="text-2xl font-bold text-gray-800">Gestión de Medicamentos</h2>
        <button
          onClick={() => setIsModalOpen(true)}
          className="bg-primary-600 text-white px-4 py-2 rounded-lg hover:bg-primary-700 transition-colors"
        >
          + Nuevo Medicamento
        </button>
      </div>

      {/* Filters */}
      <div className="bg-white p-4 rounded-lg shadow-md">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <input
            type="text"
            placeholder="Buscar por nombre o código..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-primary-500"
          />
          <select
            value={filterCategory}
            onChange={(e) => setFilterCategory(e.target.value === '' ? '' : Number(e.target.value))}
            className="border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-primary-500"
          >
            <option value="">Todas las categorías</option>
            {categories.map((cat) => (
              <option key={cat.id} value={cat.id}>
                {cat.name}
              </option>
            ))}
          </select>
          <select
            value={filterLaboratory}
            onChange={(e) =>
              setFilterLaboratory(e.target.value === '' ? '' : Number(e.target.value))
            }
            className="border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-primary-500"
          >
            <option value="">Todos los laboratorios</option>
            {laboratories.map((lab) => (
              <option key={lab.id} value={lab.id}>
                {lab.name}
              </option>
            ))}
          </select>
        </div>
      </div>

      {/* Table */}
      <div className="bg-white rounded-lg shadow-md overflow-hidden">
        <div className="overflow-x-auto">
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
                  Categoría
                </th>
                <th className="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase">
                  Laboratorio
                </th>
                <th className="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase">
                  Precio
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
              {filteredProducts.map((product) => (
                <tr key={product.id} className="hover:bg-gray-50">
                  <td className="px-6 py-4 text-sm text-gray-900">{product.code}</td>
                  <td className="px-6 py-4 text-sm text-gray-900">{product.name}</td>
                  <td className="px-6 py-4 text-sm text-gray-900">
                    {product.category?.name || categories.find((c) => c.id === product.categoryId)?.name || '-'}
                  </td>
                  <td className="px-6 py-4 text-sm text-gray-900">
                    {product.laboratory?.name || laboratories.find((l) => l.id === product.laboratoryId)?.name || '-'}
                  </td>
                  <td className="px-6 py-4 text-sm text-gray-900">${(product.basePrice ?? 0).toFixed(2)}</td>
                  <td className="px-6 py-4 text-sm">
                    <span
                      className={`px-2 py-1 rounded-full text-xs font-medium ${
                        product.status === 'ACTIVE'
                          ? 'bg-green-100 text-green-800'
                          : 'bg-red-100 text-red-800'
                      }`}
                    >
                      {product.status === 'ACTIVE' ? 'Activo' : 'Inactivo'}
                    </span>
                  </td>
                  <td className="px-6 py-4 text-sm space-x-2">
                    <button
                      onClick={() => handleEdit(product)}
                      className="text-blue-600 hover:text-blue-800"
                    >
                      Editar
                    </button>
                    <button
                      onClick={() => handleDelete(product.id!)}
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

      {/* Modal */}
      <Modal
        isOpen={isModalOpen}
        onClose={handleCloseModal}
        title={editingProduct ? 'Editar Medicamento' : 'Nuevo Medicamento'}
        maxWidth="xl"
      >
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Código *
              </label>
              <input
                {...register('code')}
                className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-primary-500"
              />
              {errors.code && (
                <p className="text-red-500 text-xs mt-1">{errors.code.message}</p>
              )}
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Nombre *
              </label>
              <input
                {...register('name')}
                className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-primary-500"
              />
              {errors.name && (
                <p className="text-red-500 text-xs mt-1">{errors.name.message}</p>
              )}
            </div>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Descripción
            </label>
            <textarea
              {...register('description')}
              rows={3}
              className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-primary-500"
            />
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Categoría *
              </label>
              <div className="flex gap-2">
                <select
                  {...register('categoryId')}
                  className="flex-1 border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-primary-500"
                >
                  <option value="">Seleccione...</option>
                  {categories.map((cat) => (
                    <option key={cat.id} value={cat.id}>
                      {cat.name}
                    </option>
                  ))}
                </select>
                <button
                  type="button"
                  onClick={() => setIsCategoryModalOpen(true)}
                  className="px-3 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition-colors"
                  title="Agregar nueva categoría"
                >
                  +
                </button>
              </div>
              {errors.categoryId && (
                <p className="text-red-500 text-xs mt-1">{errors.categoryId.message}</p>
              )}
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Laboratorio *
              </label>
              <div className="flex gap-2">
                <select
                  {...register('laboratoryId')}
                  className="flex-1 border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-primary-500"
                >
                  <option value="">Seleccione...</option>
                  {laboratories.map((lab) => (
                    <option key={lab.id} value={lab.id}>
                      {lab.name}
                    </option>
                  ))}
                </select>
                <button
                  type="button"
                  onClick={() => setIsLaboratoryModalOpen(true)}
                  className="px-3 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition-colors"
                  title="Agregar nuevo laboratorio"
                >
                  +
                </button>
              </div>
              {errors.laboratoryId && (
                <p className="text-red-500 text-xs mt-1">{errors.laboratoryId.message}</p>
              )}
            </div>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Precio *
            </label>
            <input
              type="number"
              step="0.01"
              {...register('basePrice')}
              className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-primary-500"
            />
            {errors.basePrice && (
              <p className="text-red-500 text-xs mt-1">{errors.basePrice.message}</p>
            )}
          </div>

          <div className="flex gap-6">
            <label className="flex items-center space-x-2">
              <input
                type="checkbox"
                {...register('requiresPrescription')}
                className="w-4 h-4 text-primary-600 border-gray-300 rounded focus:ring-primary-500"
              />
              <span className="text-sm text-gray-700">Requiere receta</span>
            </label>

            <label className="flex items-center space-x-2">
              <input
                type="checkbox"
                {...register('isControlled')}
                className="w-4 h-4 text-primary-600 border-gray-300 rounded focus:ring-primary-500"
              />
              <span className="text-sm text-gray-700">Es controlado</span>
            </label>
          </div>

          <div className="flex justify-end space-x-3 pt-4">
            <button
              type="button"
              onClick={handleCloseModal}
              className="px-4 py-2 border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50 transition-colors"
            >
              Cancelar
            </button>
            <button
              type="submit"
              disabled={isSubmitting}
              className="px-4 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700 transition-colors disabled:opacity-50"
            >
              {isSubmitting ? 'Guardando...' : 'Guardar'}
            </button>
          </div>
        </form>
      </Modal>

      {/* Modal Crear Categoría */}
      <Modal
        isOpen={isCategoryModalOpen}
        onClose={() => {
          setIsCategoryModalOpen(false);
          resetCategory();
        }}
        title="Nueva Categoría"
      >
        <form onSubmit={handleSubmitCategory(onSubmitCategory)} className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Código *
            </label>
            <input
              {...registerCategory('code')}
              className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-primary-500"
            />
            {errorsCategory.code && (
              <p className="text-red-500 text-xs mt-1">{errorsCategory.code.message}</p>
            )}
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Nombre *
            </label>
            <input
              {...registerCategory('name')}
              className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-primary-500"
            />
            {errorsCategory.name && (
              <p className="text-red-500 text-xs mt-1">{errorsCategory.name.message}</p>
            )}
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Descripción
            </label>
            <textarea
              {...registerCategory('description')}
              rows={3}
              className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-primary-500"
            />
          </div>

          <div className="flex justify-end space-x-3 pt-4">
            <button
              type="button"
              onClick={() => {
                setIsCategoryModalOpen(false);
                resetCategory();
              }}
              className="px-4 py-2 border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50 transition-colors"
            >
              Cancelar
            </button>
            <button
              type="submit"
              disabled={isSubmittingCategory}
              className="px-4 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700 transition-colors disabled:opacity-50"
            >
              {isSubmittingCategory ? 'Guardando...' : 'Guardar'}
            </button>
          </div>
        </form>
      </Modal>

      {/* Modal Crear Laboratorio */}
      <Modal
        isOpen={isLaboratoryModalOpen}
        onClose={() => {
          setIsLaboratoryModalOpen(false);
          resetLaboratory();
        }}
        title="Nuevo Laboratorio"
      >
        <form onSubmit={handleSubmitLaboratory(onSubmitLaboratory)} className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Nombre *
            </label>
            <input
              {...registerLaboratory('name')}
              className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-primary-500"
            />
            {errorsLaboratory.name && (
              <p className="text-red-500 text-xs mt-1">{errorsLaboratory.name.message}</p>
            )}
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              País
            </label>
            <input
              {...registerLaboratory('country')}
              className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-primary-500"
            />
            {errorsLaboratory.country && (
              <p className="text-red-500 text-xs mt-1">{errorsLaboratory.country.message}</p>
            )}
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Email de Contacto
            </label>
            <input
              type="email"
              {...registerLaboratory('contactEmail')}
              className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-primary-500"
            />
            {errorsLaboratory.contactEmail && (
              <p className="text-red-500 text-xs mt-1">{errorsLaboratory.contactEmail.message}</p>
            )}
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Teléfono
            </label>
            <input
              {...registerLaboratory('phone')}
              className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-primary-500"
            />
            {errorsLaboratory.phone && (
              <p className="text-red-500 text-xs mt-1">{errorsLaboratory.phone.message}</p>
            )}
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Sitio Web
            </label>
            <input
              type="url"
              {...registerLaboratory('website')}
              placeholder="https://ejemplo.com"
              className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-primary-500"
            />
            {errorsLaboratory.website && (
              <p className="text-red-500 text-xs mt-1">{errorsLaboratory.website.message}</p>
            )}
          </div>

          <div className="flex justify-end space-x-3 pt-4">
            <button
              type="button"
              onClick={() => {
                setIsLaboratoryModalOpen(false);
                resetLaboratory();
              }}
              className="px-4 py-2 border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50 transition-colors"
            >
              Cancelar
            </button>
            <button
              type="submit"
              disabled={isSubmittingLaboratory}
              className="px-4 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700 transition-colors disabled:opacity-50"
            >
              {isSubmittingLaboratory ? 'Guardando...' : 'Guardar'}
            </button>
          </div>
        </form>
      </Modal>
    </div>
  );
};

export default Products;
