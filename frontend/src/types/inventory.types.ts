// Inventory Service Types
export interface Branch {
  id?: number;
  code: string;
  name: string;
  address: string;
  city: string;
  phone: string;
  schedule?: string;
  active: boolean;
  createdAt?: string;
  updatedAt?: string;
}

export interface Stock {
  id?: number;
  branchId: number;
  branch?: Branch;
  productId: number;
  product?: any;
  quantity: number;
  minimumStock: number;
  maximumStock: number;
  lastUpdated?: string;
}

export interface StockMovement {
  id?: number;
  productId: number;
  product?: any;
  movementType: MovementType;
  quantity: number;
  sourceBranchId?: number;
  sourceBranch?: Branch;
  destinationBranchId?: number;
  destinationBranch?: Branch;
  reason?: string;
  userId?: number;
  createdAt?: string;
}

export enum MovementType {
  ENTRADA = 'ENTRADA',
  SALIDA = 'SALIDA',
  AJUSTE = 'AJUSTE',
  TRANSFERENCIA = 'TRANSFERENCIA',
  DEVOLUCION = 'DEVOLUCION'
}

export interface CreateBranchRequest {
  code: string;
  name: string;
  address: string;
  city: string;
  phone: string;
  schedule?: string;
}

export interface CreateStockMovementRequest {
  productId: number;
  movementType: MovementType;
  quantity: number;
  sourceBranchId?: number;
  destinationBranchId?: number;
  reason?: string;
}
