// Sales Service Types
export interface Customer {
  id?: number;
  identificationNumber: string;
  firstName: string;
  lastName: string;
  email?: string;
  phone?: string;
  address?: string;
  birthDate?: string;
  active: boolean;
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
  EFECTIVO = 'EFECTIVO',
  TARJETA = 'TARJETA',
  TRANSFERENCIA = 'TRANSFERENCIA'
}

export enum SaleStatus {
  COMPLETADA = 'COMPLETADA',
  CANCELADA = 'CANCELADA',
  PENDIENTE = 'PENDIENTE'
}

export interface CreateCustomerRequest {
  identificationNumber: string;
  firstName: string;
  lastName: string;
  email?: string;
  phone?: string;
  address?: string;
  birthDate?: string;
}

export interface CreateSaleRequest {
  customerId: number;
  branchId: number;
  subtotal: number;
  tax: number;
  discount: number;
  total: number;
  paymentMethod: PaymentMethod;
  items: CreateSaleItemRequest[];
}

export interface CreateSaleItemRequest {
  productId: number;
  quantity: number;
  unitPrice: number;
  subtotal: number;
  prescriptionFile?: string;
}
