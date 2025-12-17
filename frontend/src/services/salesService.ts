import { salesApi, handleApiError } from './api.config';
import {
  Customer,
  Sale,
  CreateCustomerRequest,
  CreateSaleRequest,
} from '../types';

export const salesService = {
  // Customers
  getAllCustomers: async (): Promise<Customer[]> => {
    try {
      const response = await salesApi.get('/customers');
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  getCustomerById: async (id: number): Promise<Customer> => {
    try {
      const response = await salesApi.get(`/customers/${id}`);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  searchCustomerByIdentification: async (identification: string): Promise<Customer | null> => {
    try {
      const response = await salesApi.get(`/customers/search?identification=${identification}`);
      return response.data;
    } catch (error) {
      if ((error as any)?.response?.status === 404) {
        return null;
      }
      throw new Error(handleApiError(error));
    }
  },

  createCustomer: async (customer: CreateCustomerRequest): Promise<Customer> => {
    try {
      const response = await salesApi.post('/customers', customer);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  updateCustomer: async (id: number, customer: CreateCustomerRequest): Promise<Customer> => {
    try {
      const response = await salesApi.put(`/customers/${id}`, customer);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  // Sales
  getAllSales: async (): Promise<Sale[]> => {
    try {
      const response = await salesApi.get('/sales');
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  getSaleById: async (id: number): Promise<Sale> => {
    try {
      const response = await salesApi.get(`/sales/${id}`);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  getSalesByCustomer: async (customerId: number): Promise<Sale[]> => {
    try {
      const response = await salesApi.get(`/sales/customer/${customerId}`);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  getSalesByBranch: async (branchId: number): Promise<Sale[]> => {
    try {
      const response = await salesApi.get(`/sales/branch/${branchId}`);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  createSale: async (sale: CreateSaleRequest): Promise<Sale> => {
    try {
      const response = await salesApi.post('/sales', sale);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  cancelSale: async (id: number): Promise<Sale> => {
    try {
      const response = await salesApi.put(`/sales/${id}/cancel`);
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },
};
