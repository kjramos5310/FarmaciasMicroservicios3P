import { reportingApi, handleApiError } from './api.config';
import {
  DashboardMetrics,
  SalesSummary,
} from '../types';

export const reportingService = {
  getDashboardMetrics: async (): Promise<DashboardMetrics> => {
    try {
      const response = await reportingApi.get('/reports/dashboard');
      return response.data.data || response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  getSalesSummary: async (startDate: string, endDate: string): Promise<SalesSummary> => {
    try {
      const response = await reportingApi.get('/reports/sales-summary', {
        params: { startDate, endDate },
      });
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  getInventoryReport: async (branchId?: number): Promise<any> => {
    try {
      const params = branchId ? { branchId } : {};
      const response = await reportingApi.get('/reports/inventory', { params });
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },

  getTopProducts: async (limit: number = 10): Promise<any> => {
    try {
      const response = await reportingApi.get('/reports/top-products', {
        params: { limit },
      });
      return response.data;
    } catch (error) {
      throw new Error(handleApiError(error));
    }
  },
};
