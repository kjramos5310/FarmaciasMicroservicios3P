// Catalog Service Types
export interface Product {
  id?: number;
  code: string;
  barcode?: string;
  name: string;
  genericName?: string;
  description?: string;
  presentation?: string;
  categoryId?: number;
  category?: Category;
  laboratoryId?: number;
  laboratory?: Laboratory;
  basePrice: number;
  requiresPrescription: boolean;
  isControlled: boolean;
  activeIngredient?: string;
  contraindications?: string;
  dosage?: string;
  status?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface Category {
  id: number;
  code?: string;
  name: string;
  description?: string;
  parentCategory?: any;
  isActive: boolean;
  createdAt?: string;
}

export interface Laboratory {
  id: number;
  name: string;
  country?: string;
  contactEmail?: string;
  phone?: string;
  website?: string;
  isActive: boolean;
  createdAt?: string;
}

export interface CreateProductRequest {
  code: string;
  barcode?: string;
  name: string;
  genericName?: string;
  description?: string;
  presentation?: string;
  categoryId: number;
  laboratoryId: number;
  basePrice: number;
  requiresPrescription: boolean;
  isControlled: boolean;
  activeIngredient?: string;
  dosage?: string;
  status?: string;
}

export interface UpdateProductRequest extends CreateProductRequest {
  id: number;
  active?: boolean;
}
