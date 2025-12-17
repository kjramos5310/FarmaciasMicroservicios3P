// Inventory Service Types
export interface Branch {
  id?: number;
  code: string;
  name: string;
  address: string;
  city: string;
  province?: string;
  phone: string;
  email?: string;
  managerName?: string;
  status?: string;
  openingTime?: string;
  closingTime?: string;
  schedule?: string;
  active?: boolean;
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
  ENTRY = 'ENTRY',
  EXIT = 'EXIT',
  ADJUSTMENT = 'ADJUSTMENT',
  TRANSFER = 'TRANSFER',
  RETURN = 'RETURN'
}

export interface CreateBranchRequest {
  code: string;
  name: string;
  address: string;
  city: string;
  province?: string;
  phone: string;
  email?: string;
  managerName?: string;
  status?: string;
  openingTime?: string;
  closingTime?: string;
  schedule?: string;
}

export interface CreateStockMovementRequest {
  branchId: number;
  productId: number;
  type: string;
  quantity: number;
  reason?: string;
  reference?: string;
  performedBy?: string;
  destinationBranchId?: number;
}

export interface CreateStockRequest {
  branchId: number;
  productId: number;
  quantity: number;
  minimumStock: number;
  maximumStock: number;
}
