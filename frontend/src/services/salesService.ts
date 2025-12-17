import { salesApi, handleApiError } from './api.config';
import {
  Customer,
  Sale,
  CreateCustomerRequest,
  CreateSaleRequest,
  Prescription,
  CreatePrescriptionRequest,
} from '../types';

export const salesService = {
  // Customers
  getAllCustomers: async (): Promise<Customer[]> => {
    try {
      const response = await salesApi.get('/customers');
      console.log('Raw customers response:', response.data);
      
      // El backend devuelve formato paginado con "content"
      let data = response.data.data || response.data;
      
      // Si tiene propiedad content, extraer el array de ahí
      if (data && data.content && Array.isArray(data.content)) {
        data = data.content;
      }
      
      console.log('Extracted customers data:', data);
      const result = Array.isArray(data) ? data : [];
      console.log('Final customers array:', result);
      return result;
    } catch (error) {
      console.error('Get customers error:', error);
      throw new Error(handleApiError(error));
    }
  },

  getCustomerById: async (id: number): Promise<Customer> => {
    try {
      const response = await salesApi.get(`/customers/${id}`);
      return response.data.data || response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  searchCustomerByIdentification: async (identification: string): Promise<Customer | null> => {
    try {
      const response = await salesApi.get(`/customers/search?identification=${identification}`);
      return response.data.data || response.data;
    } catch (error) {
      if ((error as any)?.response?.status === 404) {
        return null;
      }
      throw new Error(handleApiError(error));
    }
  },

  createCustomer: async (customer: CreateCustomerRequest): Promise<Customer> => {
    try {
      console.log('Sending customer data:', customer);
      const response = await salesApi.post('/customers', customer);
      console.log('Customer created response:', response.data);
      return response.data.data || response.data;
    } catch (error: any) {
      console.error('Customer creation failed:', error.response?.data);
      // Propagar el error original de Axios para mantener response
      throw error;
    }
  },

  updateCustomer: async (id: number, customer: CreateCustomerRequest): Promise<Customer> => {
    try {
      const response = await salesApi.put(`/customers/${id}`, customer);
      return response.data.data || response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  // Sales
  getAllSales: async (): Promise<Sale[]> => {
    try {
      const response = await salesApi.get('/sales');
      console.log('Raw sales response:', response.data);
      
      // El backend puede devolver formato paginado con "content"
      let data = response.data.data || response.data;
      
      // Si tiene propiedad content, extraer el array de ahí
      if (data && data.content && Array.isArray(data.content)) {
        data = data.content;
      }
      
      console.log('Extracted sales data:', data);
      const result = Array.isArray(data) ? data : [];
      console.log('Final sales array:', result);
      return result;
    } catch (error) {
      console.error('Get sales error:', error);
      throw new Error(handleApiError(error));
    }
  },

  getSaleById: async (id: number): Promise<Sale> => {
    try {
      const response = await salesApi.get(`/sales/${id}`);
      return response.data.data || response.data;
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
      const data = response.data.data || response.data;
      return Array.isArray(data) ? data : [];
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  createSale: async (sale: CreateSaleRequest): Promise<Sale> => {
    try {
      console.log('Creating sale with data:', sale);
      const response = await salesApi.post('/sales', sale);
      console.log('Sale created response:', response.data);
      return response.data.data || response.data;
    } catch (error: any) {
      console.error('Sale creation failed:', error.response?.data || error.message);
      throw error;
    }
  },

  cancelSale: async (id: number): Promise<Sale> => {
    try {
      const response = await salesApi.put(`/sales/${id}/cancel`);
      return response.data.data || response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  // Prescriptions
  getAllPrescriptions: async (): Promise<Prescription[]> => {
    try {
      const response = await salesApi.get('/prescriptions');
      let data = response.data.data || response.data;
      
      // Manejar respuesta paginada
      if (data && data.content && Array.isArray(data.content)) {
        data = data.content;
      }
      
      return Array.isArray(data) ? data : [];
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  getPrescriptionById: async (id: number): Promise<Prescription> => {
    try {
      const response = await salesApi.get(`/prescriptions/${id}`);
      return response.data.data || response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  getPrescriptionsByCustomer: async (customerId: number): Promise<Prescription[]> => {
    try {
      const response = await salesApi.get(`/prescriptions/customer/${customerId}`);
      let data = response.data.data || response.data;
      
      // Manejar respuesta paginada
      if (data && data.content && Array.isArray(data.content)) {
        data = data.content;
      }
      
      return Array.isArray(data) ? data : [];
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  createPrescription: async (prescription: CreatePrescriptionRequest): Promise<Prescription> => {
    try {
      console.log('Creating prescription with data:', prescription);
      const response = await salesApi.post('/prescriptions', prescription);
      console.log('Prescription created response:', response.data);
      return response.data.data || response.data;
    } catch (error: any) {
      console.error('Prescription creation failed:', error.response?.data || error.message);
      throw error;
    }
  },
};
