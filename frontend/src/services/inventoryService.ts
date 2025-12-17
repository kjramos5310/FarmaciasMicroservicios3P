import { inventoryApi, handleApiError } from './api.config';
import {
  Branch,
  Stock,
  StockMovement,
  CreateBranchRequest,
  CreateStockMovementRequest,
} from '../types';

export const inventoryService = {
  // Branches
  getAllBranches: async (): Promise<Branch[]> => {
    try {
      const response = await inventoryApi.get('/branches');
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  getBranchById: async (id: number): Promise<Branch> => {
    try {
      const response = await inventoryApi.get(`/branches/${id}`);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  createBranch: async (branch: CreateBranchRequest): Promise<Branch> => {
    try {
      const response = await inventoryApi.post('/branches', branch);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  updateBranch: async (id: number, branch: CreateBranchRequest): Promise<Branch> => {
    try {
      const response = await inventoryApi.put(`/branches/${id}`, branch);
      return response.data;
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
      const response = await inventoryApi.get(`/stock/branch/${branchId}`);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  getStockByProduct: async (branchId: number, productId: number): Promise<Stock> => {
    try {
      const response = await inventoryApi.get(`/stock/${branchId}/${productId}`);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  getAllStock: async (): Promise<Stock[]> => {
    try {
      const response = await inventoryApi.get('/stock');
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  getLowStock: async (): Promise<Stock[]> => {
    try {
      const response = await inventoryApi.get('/stock/low-stock');
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  // Stock Movements
  getAllMovements: async (): Promise<StockMovement[]> => {
    try {
      const response = await inventoryApi.get('/movements');
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  getMovementsByBranch: async (branchId: number): Promise<StockMovement[]> => {
    try {
      const response = await inventoryApi.get(`/movements/branch/${branchId}`);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  getMovementsByProduct: async (productId: number): Promise<StockMovement[]> => {
    try {
      const response = await inventoryApi.get(`/movements/product/${productId}`);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  createMovement: async (movement: CreateStockMovementRequest): Promise<StockMovement> => {
    try {
      const response = await inventoryApi.post('/movements', movement);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },
};
