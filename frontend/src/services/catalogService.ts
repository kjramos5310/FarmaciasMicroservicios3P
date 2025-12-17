import { catalogApi, handleApiError } from './api.config';
import {
  Product,
  Category,
  Laboratory,
  CreateProductRequest,
  UpdateProductRequest,
  CreateCategoryRequest,
  CreateLaboratoryRequest,
} from '../types';

export const catalogService = {
  // Products
  getAllProducts: async (): Promise<Product[]> => {
    try {
      const response = await catalogApi.get('/products');
      return response.data.data || response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  getProductById: async (id: number): Promise<Product> => {
    try {
      const response = await catalogApi.get(`/products/${id}`);
      return response.data.data || response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  createProduct: async (product: CreateProductRequest): Promise<Product> => {
    try {
      const response = await catalogApi.post('/products', product);
      return response.data.data || response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  updateProduct: async (id: number, product: UpdateProductRequest): Promise<Product> => {
    try {
      const response = await catalogApi.put(`/products/${id}`, product);
      return response.data.data || response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  deleteProduct: async (id: number): Promise<void> => {
    try {
      await catalogApi.delete(`/products/${id}`);
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  searchProducts: async (query: string): Promise<Product[]> => {
    try {
      const response = await catalogApi.get(`/products/search?q=${encodeURIComponent(query)}`);
      return response.data.data || response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  // Categories
  getAllCategories: async (): Promise<Category[]> => {
    try {
      const response = await catalogApi.get('/categories');
      return response.data.data || response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  getCategoryById: async (id: number): Promise<Category> => {
    try {
      const response = await catalogApi.get(`/categories/${id}`);
      return response.data.data || response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  // Laboratories
  getAllLaboratories: async (): Promise<Laboratory[]> => {
    try {
      const response = await catalogApi.get('/laboratories');
      return response.data.data || response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  getLaboratoryById: async (id: number): Promise<Laboratory> => {
    try {
      const response = await catalogApi.get(`/laboratories/${id}`);
      return response.data.data || response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  createLaboratory: async (laboratory: CreateLaboratoryRequest): Promise<Laboratory> => {
    try {
      const response = await catalogApi.post('/laboratories', laboratory);
      return response.data.data || response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  createCategory: async (category: CreateCategoryRequest): Promise<Category> => {
    try {
      const response = await catalogApi.post('/categories', category);
      return response.data.data || response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },
};
