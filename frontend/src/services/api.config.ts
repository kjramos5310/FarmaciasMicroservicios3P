import axios, { AxiosError } from 'axios';

// Use proxy paths in development to avoid CORS issues
// The setupProxy.js file will route these to the correct backend services
const API_BASE_URLS = {
  catalog: '/api/catalog',
  inventory: '/api/inventory',
  sales: '/api/sales',
  reporting: '/api/reporting',
};

export const catalogApi = axios.create({
  baseURL: API_BASE_URLS.catalog,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const inventoryApi = axios.create({
  baseURL: API_BASE_URLS.inventory,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const salesApi = axios.create({
  baseURL: API_BASE_URLS.sales,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const reportingApi = axios.create({
  baseURL: API_BASE_URLS.reporting,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Error handler helper
export const handleApiError = (error: unknown): string => {
  if (axios.isAxiosError(error)) {
    const axiosError = error as AxiosError<any>;
    
    // Log full error details for debugging
    console.error('API Error Details:', {
      status: axiosError.response?.status,
      data: axiosError.response?.data,
      message: axiosError.message,
    });
    
    if (axiosError.response?.data?.message) {
      return axiosError.response.data.message;
    }
    if (axiosError.response?.data?.error) {
      return axiosError.response.data.error;
    }
    if (axiosError.message) {
      return axiosError.message;
    }
  }
  return 'Ha ocurrido un error inesperado';
};
