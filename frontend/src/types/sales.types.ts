// Sales Service Types
export interface Customer {
  id?: number;
  identificationNumber: string;
  identificationType?: string;
  firstName: string;
  lastName: string;
  email?: string;
  phone?: string;
  address?: string;
  city?: string;
  birthDate?: string;
  type?: string;
  active?: boolean;
  createdAt?: string;
}

export interface Sale {
  id?: number;
  saleNumber: string;
  customerId: number;
  customer?: Customer;
  branchId: number;
  branch?: any;
  saleDate: string;
  subtotal: number;
  tax: number;
  discount: number;
  total: number;
  paymentMethod: PaymentMethod;
  status: SaleStatus;
  items?: SaleItem[];
  userId?: number;
  createdAt?: string;
}

export interface SaleItem {
  id?: number;
  saleId?: number;
  productId: number;
  product?: any;
  quantity: number;
  unitPrice: number;
  subtotal: number;
  prescriptionFile?: string;
}

export enum PaymentMethod {
  CASH = 'CASH',
  CARD = 'CARD',
  TRANSFER = 'TRANSFER',
  CHECK = 'CHECK'
}

export enum SaleStatus {
  COMPLETADA = 'COMPLETADA',
  CANCELADA = 'CANCELADA',
  PENDIENTE = 'PENDIENTE'
}

export interface CreateCustomerRequest {
  identificationNumber: string;
  identificationType?: string;
  firstName: string;
  lastName: string;
  email?: string;
  phone?: string;
  address?: string;
  city?: string;
  birthDate?: string;
  type?: string;
}

export interface CreateSaleRequest {
  customerId: number;
  branchId: number;
  subtotal: number;
  tax: number;
  discount: number;
  total: number;
  paymentMethod: PaymentMethod;
  cashierName?: string;
  items: CreateSaleItemRequest[];
}

export interface CreateSaleItemRequest {
  productId: number;
  quantity: number;
  unitPrice: number;
  subtotal: number;
  prescriptionFile?: string;
}
