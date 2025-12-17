package com.example.reporting_service.service;

import com.example.reporting_service.dto.*;
import com.example.reporting_service.external.SaleData;
import com.example.reporting_service.external.StockData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.JsonNode;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportingService {
    
    @Qualifier("salesWebClient")
    private final WebClient salesWebClient;
    
    @Qualifier("inventoryWebClient")
    private final WebClient inventoryWebClient;
    
    // Sales Reports
    public SalesSummaryResponse getSalesSummary(LocalDate startDate, LocalDate endDate, Long branchId) {
        log.info("Generating sales summary for period: {} to {}, branchId: {}", startDate, endDate, branchId);
        
        List<SaleData> sales = fetchSalesData(startDate, endDate, branchId);
        
        if (sales.isEmpty()) {
            return SalesSummaryResponse.builder()
                    .reportDate(LocalDate.now())
                    .branchId(branchId)
                    .totalSales(0)
                    .totalRevenue(BigDecimal.ZERO)
                    .averageTicket(BigDecimal.ZERO)
                    .totalItems(0)
                    .uniqueCustomers(0)
                    .build();
        }
        
        BigDecimal totalRevenue = sales.stream()
                .map(SaleData::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        int totalItems = sales.stream()
                .mapToInt(SaleData::getItemCount)
                .sum();
        
        int uniqueCustomers = (int) sales.stream()
                .map(SaleData::getCustomerId)
                .distinct()
                .count();
        
        BigDecimal averageTicket = sales.size() > 0 
                ? totalRevenue.divide(BigDecimal.valueOf(sales.size()), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        
        return SalesSummaryResponse.builder()
                .reportDate(LocalDate.now())
                .branchId(branchId)
                .totalSales(sales.size())
                .totalRevenue(totalRevenue)
                .averageTicket(averageTicket)
                .totalItems(totalItems)
                .uniqueCustomers(uniqueCustomers)
                .build();
    }
    
    public List<ProductSalesResponse> getSalesByProduct(LocalDate startDate, LocalDate endDate) {
        log.info("Generating product sales report for period: {} to {}", startDate, endDate);
        
        List<SaleData> sales = fetchSalesData(startDate, endDate, null);
        
        // Group by product (simplified - in real scenario would need product details from sales)
        Map<Long, List<SaleData>> salesByBranch = sales.stream()
                .collect(Collectors.groupingBy(SaleData::getBranchId));
        
        return salesByBranch.entrySet().stream()
                .map(entry -> ProductSalesResponse.builder()
                        .branchId(entry.getKey())
                        .quantitySold(entry.getValue().size())
                        .revenue(entry.getValue().stream()
                                .map(SaleData::getTotalAmount)
                                .reduce(BigDecimal.ZERO, BigDecimal::add))
                        .build())
                .collect(Collectors.toList());
    }
    
    public List<TopProductsResponse> getTopProducts(LocalDate startDate, LocalDate endDate, int limit) {
        log.info("Generating top {} products report", limit);
        
        List<SaleData> sales = fetchSalesData(startDate, endDate, null);
        
        // Group and aggregate (simplified)
        Map<Long, List<SaleData>> salesByBranch = sales.stream()
                .collect(Collectors.groupingBy(SaleData::getBranchId));
        
        List<TopProductsResponse> topProducts = new ArrayList<>();
        int rank = 1;
        
        for (Map.Entry<Long, List<SaleData>> entry : salesByBranch.entrySet()) {
            BigDecimal revenue = entry.getValue().stream()
                    .map(SaleData::getTotalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            topProducts.add(TopProductsResponse.builder()
                    .productId(entry.getKey())
                    .productName("Product " + entry.getKey())
                    .quantitySold(entry.getValue().size())
                    .revenue(revenue)
                    .rank(rank++)
                    .build());
        }
        
        return topProducts.stream()
                .sorted(Comparator.comparing(TopProductsResponse::getRevenue).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    public List<BranchSalesResponse> getSalesByBranch(LocalDate startDate, LocalDate endDate) {
        log.info("Generating branch comparison report for period: {} to {}", startDate, endDate);
        
        List<SaleData> sales = fetchSalesData(startDate, endDate, null);
        
        Map<Long, List<SaleData>> salesByBranch = sales.stream()
                .collect(Collectors.groupingBy(SaleData::getBranchId));
        
        return salesByBranch.entrySet().stream()
                .map(entry -> {
                    List<SaleData> branchSales = entry.getValue();
                    BigDecimal totalRevenue = branchSales.stream()
                            .map(SaleData::getTotalAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    
                    int totalItems = branchSales.stream()
                            .mapToInt(SaleData::getItemCount)
                            .sum();
                    
                    BigDecimal avgTicket = branchSales.size() > 0
                            ? totalRevenue.divide(BigDecimal.valueOf(branchSales.size()), 2, RoundingMode.HALF_UP)
                            : BigDecimal.ZERO;
                    
                    return BranchSalesResponse.builder()
                            .branchId(entry.getKey())
                            .branchName("Sucursal " + entry.getKey())
                            .totalSales(branchSales.size())
                            .totalRevenue(totalRevenue)
                            .averageTicket(avgTicket)
                            .totalItems(totalItems)
                            .build();
                })
                .collect(Collectors.toList());
    }
    
    // Inventory Reports
    public InventorySummaryResponse getInventorySummary(Long branchId) {
        log.info("Generating inventory summary for branchId: {}", branchId);
        
        List<StockData> stocks = fetchInventoryData(branchId);
        
        if (stocks.isEmpty()) {
            return InventorySummaryResponse.builder()
                    .reportDate(LocalDate.now())
                    .branchId(branchId)
                    .totalProducts(0)
                    .lowStockProducts(0)
                    .expiringSoon(0)
                    .inventoryValue(BigDecimal.ZERO)
                    .build();
        }
        
        int lowStockCount = (int) stocks.stream()
                .filter(s -> s.getQuantity() <= s.getMinStock())
                .count();
        
        int expiringSoonCount = (int) stocks.stream()
                .filter(s -> s.getExpiryDate() != null)
                .filter(s -> ChronoUnit.DAYS.between(LocalDate.now(), s.getExpiryDate()) <= 30)
                .count();
        
        BigDecimal inventoryValue = stocks.stream()
                .map(s -> s.getUnitPrice().multiply(BigDecimal.valueOf(s.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return InventorySummaryResponse.builder()
                .reportDate(LocalDate.now())
                .branchId(branchId)
                .totalProducts(stocks.size())
                .lowStockProducts(lowStockCount)
                .expiringSoon(expiringSoonCount)
                .inventoryValue(inventoryValue)
                .build();
    }
    
    public List<LowStockResponse> getLowStockProducts(Long branchId) {
        log.info("Generating low stock report for branchId: {}", branchId);
        
        List<StockData> stocks = fetchInventoryData(branchId);
        
        return stocks.stream()
                .filter(s -> s.getQuantity() <= s.getMinStock())
                .map(s -> new LowStockResponse(
                        s.getProductId(),
                        s.getProductName(),
                        s.getBranchId(),
                        s.getQuantity(),
                        s.getMinStock()))
                .collect(Collectors.toList());
    }
    
    public List<ExpiringProductResponse> getExpiringProducts(Long branchId) {
        log.info("Generating expiring products report for branchId: {}", branchId);
        
        List<StockData> stocks = fetchInventoryData(branchId);
        LocalDate now = LocalDate.now();
        
        return stocks.stream()
                .filter(s -> s.getExpiryDate() != null)
                .filter(s -> ChronoUnit.DAYS.between(now, s.getExpiryDate()) <= 30)
                .map(s -> new ExpiringProductResponse(
                        s.getProductId(),
                        s.getProductName(),
                        s.getBranchId(),
                        s.getExpiryDate(),
                        (int) ChronoUnit.DAYS.between(now, s.getExpiryDate()),
                        s.getQuantity()))
                .sorted(Comparator.comparing(ExpiringProductResponse::getDaysUntilExpiry))
                .collect(Collectors.toList());
    }
    
    public BigDecimal getInventoryValue(Long branchId) {
        log.info("Calculating inventory value for branchId: {}", branchId);
        
        List<StockData> stocks = fetchInventoryData(branchId);
        
        return stocks.stream()
                .map(s -> s.getUnitPrice().multiply(BigDecimal.valueOf(s.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    // Dashboard
    public DashboardResponse getDashboard() {
        log.info("Generating executive dashboard");
        
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(30);
        
        List<SaleData> sales = fetchSalesData(startDate, endDate, null);
        List<StockData> stocks = fetchInventoryData(null);
        
        // Sales Metrics
        BigDecimal totalRevenue = sales.stream()
                .map(SaleData::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        int uniqueCustomers = (int) sales.stream()
                .map(SaleData::getCustomerId)
                .distinct()
                .count();
        
        BigDecimal avgTicket = sales.size() > 0
                ? totalRevenue.divide(BigDecimal.valueOf(sales.size()), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        
        DashboardResponse.SalesMetrics salesMetrics = DashboardResponse.SalesMetrics.builder()
                .totalRevenue(totalRevenue)
                .totalSales(sales.size())
                .averageTicket(avgTicket)
                .uniqueCustomers(uniqueCustomers)
                .build();
        
        // Inventory Metrics
        int lowStockCount = (int) stocks.stream()
                .filter(s -> s.getQuantity() <= s.getMinStock())
                .count();
        
        LocalDate now = LocalDate.now();
        int expiringSoonCount = (int) stocks.stream()
                .filter(s -> s.getExpiryDate() != null)
                .filter(s -> ChronoUnit.DAYS.between(now, s.getExpiryDate()) <= 30)
                .count();
        
        BigDecimal totalInventoryValue = stocks.stream()
                .map(s -> s.getUnitPrice().multiply(BigDecimal.valueOf(s.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        DashboardResponse.InventoryMetrics inventoryMetrics = DashboardResponse.InventoryMetrics.builder()
                .totalProducts(stocks.size())
                .lowStockProducts(lowStockCount)
                .expiringSoon(expiringSoonCount)
                .totalInventoryValue(totalInventoryValue)
                .build();
        
        return DashboardResponse.builder()
                .salesMetrics(salesMetrics)
                .inventoryMetrics(inventoryMetrics)
                .build();
    }
    
    // Private methods to fetch data from external services
    private List<SaleData> fetchSalesData(LocalDate startDate, LocalDate endDate, Long branchId) {
        try {
            // Convert LocalDate to LocalDateTime (start of day and end of day)
            LocalDateTime start = startDate.atStartOfDay();
            LocalDateTime end = endDate.atTime(LocalTime.MAX);
            
            String uri = "/api/sales/date-range?start=" + start + "&end=" + end;
            if (branchId != null) {
                uri += "&branchId=" + branchId;
            }
            
            // Sales-service returns Page<Sale>, we need to extract content
            JsonNode response = salesWebClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();
            
            if (response == null || !response.has("content")) {
                return new ArrayList<>();
            }
            
            List<SaleData> sales = new ArrayList<>();
            response.get("content").forEach(saleNode -> {
                try {
                    SaleData sale = SaleData.builder()
                            .id(saleNode.get("id").asLong())
                            .branchId(saleNode.get("branchId").asLong())
                            .customerId(saleNode.get("customer").get("id").asLong())
                            .totalAmount(new BigDecimal(saleNode.get("total").asText()))
                            .itemCount(saleNode.get("items").size())
                            .saleDate(saleNode.has("saleDate") ? LocalDate.parse(saleNode.get("saleDate").asText().substring(0, 10)) : LocalDate.now())
                            .status(saleNode.has("status") ? saleNode.get("status").asText() : "UNKNOWN")
                            .build();
                    sales.add(sale);
                } catch (Exception e) {
                    log.warn("Error parsing sale node: {}", e.getMessage());
                }
            });
            
            return sales;
        } catch (Exception e) {
            log.error("Error fetching sales data: {}", e.getMessage());
            return new ArrayList<>();
        }
    }
    
    private List<StockData> fetchInventoryData(Long branchId) {
        try {
            String uri = branchId != null ? "/api/stock/" + branchId : "/api/stock";
            
            // Inventory-service returns ApiResponse<List<StockResponse>>
            JsonNode response = inventoryWebClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();
            
            if (response == null || !response.has("data")) {
                return new ArrayList<>();
            }
            
            List<StockData> stocks = new ArrayList<>();
            JsonNode dataNode = response.get("data");
            
            if (dataNode.isArray()) {
                dataNode.forEach(stockNode -> {
                    try {
                        StockData stock = StockData.builder()
                                .id(stockNode.has("id") ? stockNode.get("id").asLong() : null)
                                .productId(stockNode.get("productId").asLong())
                                .branchId(stockNode.get("branchId").asLong())
                                .quantity(stockNode.get("quantity").asInt())
                                .minStock(stockNode.has("minimumStock") ? stockNode.get("minimumStock").asInt() : 0)
                                .unitPrice(stockNode.has("unitPrice") ? new BigDecimal(stockNode.get("unitPrice").asText()) : BigDecimal.ZERO)
                                .productName(stockNode.has("productName") ? stockNode.get("productName").asText() : null)
                                .build();
                        stocks.add(stock);
                    } catch (Exception e) {
                        log.warn("Error parsing stock node: {}", e.getMessage());
                    }
                });
            }
            
            return stocks;
        } catch (Exception e) {
            log.error("Error fetching inventory data: {}", e.getMessage());
            return new ArrayList<>();
        }
    }
}
