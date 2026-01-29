// Reporting Service Types
export interface DashboardMetrics {
  salesMetrics: {
    totalRevenue: number;
    totalSales: number;
    averageTicket: number;
    uniqueCustomers: number;
  };
  inventoryMetrics: {
    totalProducts: number;
    lowStockProducts: number;
    expiringSoon: number;
    totalInventoryValue: number;
  };
}

export interface SalesChartData {
  date: string;
  sales: number;
  amount: number;
}

export interface TopProduct {
  productId: number;
  productName: string;
  totalQuantity: number;
  totalAmount: number;
}

export interface ExpiringProduct {
  productId: number;
  productName: string;
  branchName: string;
  expiryDate: string;
  daysUntilExpiry: number;
}

export interface SalesSummary {
  startDate: string;
  endDate: string;
  totalSales: number;
  totalAmount: number;
  averageTicket: number;
  salesByPaymentMethod: PaymentMethodSummary[];
  salesByBranch: BranchSummary[];
}

export interface PaymentMethodSummary {
  paymentMethod: string;
  count: number;
  amount: number;
}

export interface BranchSummary {
  branchId: number;
  branchName: string;
  salesCount: number;
  totalAmount: number;
}
