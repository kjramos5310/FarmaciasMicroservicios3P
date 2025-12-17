import { inventoryApi, handleApiError } from './api.config';
import {
  Branch,
  Stock,
  StockMovement,
  CreateBranchRequest,
  CreateStockMovementRequest,
  CreateStockRequest,
} from '../types/inventory.types';

export const inventoryService = {
  // Branches
  getAllBranches: async (): Promise<Branch[]> => {
    try {
      const response = await inventoryApi.get('/branches');
      return response.data.data || response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  getBranchById: async (id: number): Promise<Branch> => {
    try {
      const response = await inventoryApi.get(`/branches/${id}`);
      return response.data.data || response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  createBranch: async (branch: CreateBranchRequest): Promise<Branch> => {
    try {
      const response = await inventoryApi.post('/branches', branch);
      return response.data.data || response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  updateBranch: async (id: number, branch: CreateBranchRequest): Promise<Branch> => {
    try {
      const response = await inventoryApi.put(`/branches/${id}`, branch);
      return response.data.data || response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  deleteBranch: async (id: number): Promise<void> => {
    try {
      await inventoryApi.delete(`/branches/${id}`);
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  // Stock
  getStockByBranch: async (branchId: number): Promise<Stock[]> => {
    try {
      const response = await inventoryApi.get(`/stock/${branchId}`);
      return response.data.data || response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  getStockByProduct: async (branchId: number, productId: number): Promise<Stock> => {
    try {
      const response = await inventoryApi.get(`/stock/${branchId}/${productId}`);
      return response.data.data || response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  getAllStock: async (): Promise<Stock[]> => {
    try {
      const response = await inventoryApi.get('/stock');
      return response.data.data || response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  getLowStock: async (): Promise<Stock[]> => {
    try {
      const response = await inventoryApi.get('/stock/low-stock');
      return response.data.data || response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  // Stock Movements
  getMovementsByBranch: async (branchId: number): Promise<StockMovement[]> => {
    try {
      const response = await inventoryApi.get(`/movements/branch/${branchId}`);
      return response.data.data || response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  getMovementsByProduct: async (productId: number): Promise<StockMovement[]> => {
    try {
      const response = await inventoryApi.get(`/movements/product/${productId}`);
      return response.data.data || response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  createMovement: async (movement: CreateStockMovementRequest): Promise<StockMovement> => {
    try {
      console.log('Creating movement:', movement);
      const response = await inventoryApi.post('/movements', movement);
      console.log('Movement response:', response.data);
      return response.data.data || response.data;
    } catch (error: any) {
      console.error('Movement creation error:', error.response?.data || error.message);
      throw new Error(handleApiError(error));
    }
  },

  createStock: async (stock: CreateStockRequest): Promise<Stock> => {
    try {
      console.log('Creating stock:', stock);
      const response = await inventoryApi.post('/stock', stock);
      console.log('Stock response:', response.data);
      return response.data.data || response.data;
    } catch (error: any) {
      console.error('Stock creation error:', error.response?.data || error.message);
      throw new Error(handleApiError(error));
    }
  },
};
