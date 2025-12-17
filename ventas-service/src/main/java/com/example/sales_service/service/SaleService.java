package com.example.sales_service.service;

import com.example.sales_service.client.CatalogClient;
import com.example.sales_service.dto.CreateSaleRequest;
import com.example.sales_service.dto.ProductDTO;
import com.example.sales_service.dto.SaleItemRequest;
import com.example.sales_service.entity.*;
import com.example.sales_service.enums.PrescriptionStatus;
import com.example.sales_service.enums.SaleStatus;
import com.example.sales_service.exception.ExpiredPrescriptionException;
import com.example.sales_service.exception.InvalidSaleException;
import com.example.sales_service.exception.PrescriptionRequiredException;
import com.example.sales_service.repository.CustomerRepository;
import com.example.sales_service.repository.PrescriptionRepository;
import com.example.sales_service.repository.SaleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SaleService {
    
    private final SaleRepository saleRepository;
    private final CustomerRepository customerRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final CatalogClient catalogClient;
    private final PrescriptionService prescriptionService;
    
    private static final BigDecimal TAX_RATE = new BigDecimal("0.12"); // 12% IVA
    
    @Transactional
    public Sale createSale(CreateSaleRequest request) {
        log.info("Creando nueva venta para sucursal: {}", request.getBranchId());
        
        // Validar items
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new InvalidSaleException("La venta debe tener al menos un ítem");
        }
        
        // Crear venta
        Sale sale = new Sale();
        sale.setSaleNumber(generateSaleNumber());
        sale.setBranchId(request.getBranchId());
        sale.setPaymentMethod(request.getPaymentMethod());
        sale.setCashierName(request.getCashierName());
        sale.setNotes(request.getNotes());
        sale.setSaleDate(LocalDateTime.now());
        sale.setStatus(SaleStatus.PENDING);
        
        // Asociar cliente si existe
        if (request.getCustomerId() != null) {
            Customer customer = customerRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new InvalidSaleException("Cliente no encontrado con ID: " + request.getCustomerId()));
            sale.setCustomer(customer);
        }
        
        // Procesar items
        List<SaleItem> saleItems = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        
        for (SaleItemRequest itemRequest : request.getItems()) {
            SaleItem saleItem = processSaleItem(itemRequest, sale);
            saleItems.add(saleItem);
            subtotal = subtotal.add(saleItem.getSubtotal());
        }
        
        sale.setItems(saleItems);
        
        // Calcular totales
        sale.setSubtotal(subtotal);
        
        BigDecimal tax = subtotal.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);
        sale.setTax(tax);
        
        BigDecimal discount = request.getDiscount() != null ? request.getDiscount() : BigDecimal.ZERO;
        sale.setDiscount(discount);
        
        BigDecimal total = subtotal.add(tax).subtract(discount).setScale(2, RoundingMode.HALF_UP);
        sale.setTotal(total);
        
        Sale saved = saleRepository.save(sale);
        log.info("Venta creada exitosamente con número: {}", saved.getSaleNumber());
        log.info("Totales - Subtotal: {}, IVA: {}, Descuento: {}, Total: {}", 
                 subtotal, tax, discount, total);
        
        return saved;
    }
    
    private SaleItem processSaleItem(SaleItemRequest itemRequest, Sale sale) {
        log.debug("Procesando item - ProductoID: {}", itemRequest.getProductId());
        
        // Obtener información del producto desde catalog-service
        ProductDTO product = null;
        try {
            product = catalogClient.getProductById(itemRequest.getProductId());
        } catch (Exception e) {
            log.warn("No se pudo obtener información del catálogo para producto {}: {}. Usando datos del request.", 
                    itemRequest.getProductId(), e.getMessage());
        }
        
        // Determinar precio unitario
        BigDecimal unitPrice;
        if (itemRequest.getUnitPrice() != null) {
            unitPrice = itemRequest.getUnitPrice();
            log.debug("Usando precio del request: {}", unitPrice);
        } else if (product != null && product.getPrice() != null) {
            unitPrice = product.getPrice();
            log.debug("Usando precio del catálogo: {}", unitPrice);
        } else {
            throw new InvalidSaleException("No se pudo determinar el precio del producto ID: " + itemRequest.getProductId() + 
                    ". Proporcione el unitPrice en el request o asegúrese de que catalog-service esté disponible.");
        }
        
        // Determinar nombre y código del producto
        String productName = product != null && product.getName() != null ? 
                product.getName() : "Producto " + itemRequest.getProductId();
        String productCode = product != null && product.getCode() != null ? 
                product.getCode() : "PROD-" + itemRequest.getProductId();
        
        // Verificar si requiere prescripción
        boolean requiresPrescription = itemRequest.getRequiresPrescription() != null ? 
                itemRequest.getRequiresPrescription() : 
                (product != null && product.getRequiresPrescription() != null && product.getRequiresPrescription());
        
        if (requiresPrescription) {
            if (itemRequest.getPrescriptionId() == null) {
                throw new PrescriptionRequiredException(
                        "El producto '" + productName + "' requiere prescripción médica");
            }
            
            Prescription prescription = prescriptionRepository.findById(itemRequest.getPrescriptionId())
                    .orElseThrow(() -> new PrescriptionRequiredException(
                            "Prescripción no encontrada con ID: " + itemRequest.getPrescriptionId()));
            
            if (!prescriptionService.isPrescriptionValid(prescription)) {
                throw new ExpiredPrescriptionException(
                        "La prescripción ha expirado o no está activa");
            }
        }
        
        // Crear SaleItem con desnormalización
        SaleItem saleItem = new SaleItem();
        saleItem.setSale(sale);
        saleItem.setProductId(itemRequest.getProductId());
        saleItem.setProductName(productName); // Desnormalizado
        saleItem.setProductCode(productCode); // Desnormalizado
        saleItem.setQuantity(itemRequest.getQuantity());
        saleItem.setUnitPrice(unitPrice);
        saleItem.setRequiresPrescription(requiresPrescription);
        saleItem.setBatchId(itemRequest.getBatchId());
        
        if (itemRequest.getPrescriptionId() != null) {
            Prescription prescription = prescriptionRepository.findById(itemRequest.getPrescriptionId()).orElse(null);
            saleItem.setPrescription(prescription);
        }
        
        BigDecimal itemDiscount = itemRequest.getDiscount() != null ? itemRequest.getDiscount() : BigDecimal.ZERO;
        saleItem.setDiscount(itemDiscount);
        
        BigDecimal itemSubtotal = unitPrice
                .multiply(BigDecimal.valueOf(itemRequest.getQuantity()))
                .subtract(itemDiscount)
                .setScale(2, RoundingMode.HALF_UP);
        saleItem.setSubtotal(itemSubtotal);
        
        log.debug("Item procesado - {}: {} x {} = {}", 
                  productName, itemRequest.getQuantity(), unitPrice, itemSubtotal);
        
        return saleItem;
    }
    
    @Transactional(readOnly = true)
    public Page<Sale> getAllSales(Pageable pageable) {
        log.info("Obteniendo todas las ventas - página: {}", pageable.getPageNumber());
        return saleRepository.findAll(pageable);
    }
    
    @Transactional(readOnly = true)
    public Sale getSaleById(Long id) {
        log.info("Buscando venta con ID: {}", id);
        return saleRepository.findById(id)
                .orElseThrow(() -> new InvalidSaleException("Venta no encontrada con ID: " + id));
    }
    
    @Transactional(readOnly = true)
    public Page<Sale> getSalesByBranch(Long branchId, Pageable pageable) {
        log.info("Obteniendo ventas de la sucursal: {}", branchId);
        return saleRepository.findByBranchId(branchId, pageable);
    }
    
    @Transactional(readOnly = true)
    public Page<Sale> getSalesByCustomer(Long customerId, Pageable pageable) {
        log.info("Obteniendo ventas del cliente: {}", customerId);
        return saleRepository.findByCustomerId(customerId, pageable);
    }
    
    @Transactional(readOnly = true)
    public Page<Sale> getSalesByDateRange(LocalDateTime start, LocalDateTime end, Pageable pageable) {
        log.info("Obteniendo ventas entre {} y {}", start, end);
        return saleRepository.findBySaleDateBetween(start, end, pageable);
    }
    
    @Transactional
    public Sale updateSaleStatus(Long id, SaleStatus status) {
        log.info("Actualizando estado de venta ID: {} a {}", id, status);
        Sale sale = getSaleById(id);
        
        SaleStatus oldStatus = sale.getStatus();
        sale.setStatus(status);
        
        // Si la venta se completa, marcar prescripciones como usadas
        if (status == SaleStatus.COMPLETED && oldStatus != SaleStatus.COMPLETED) {
            for (SaleItem item : sale.getItems()) {
                if (item.getRequiresPrescription() && item.getPrescription() != null) {
                    Prescription prescription = item.getPrescription();
                    prescription.setStatus(PrescriptionStatus.USED);
                    prescriptionRepository.save(prescription);
                    log.info("Prescripción {} marcada como usada", prescription.getPrescriptionNumber());
                }
            }
        }
        
        Sale updated = saleRepository.save(sale);
        log.info("Estado de venta actualizado exitosamente de {} a {}", oldStatus, status);
        return updated;
    }
    
    private String generateSaleNumber() {
        Year currentYear = Year.now();
        LocalDateTime startOfYear = LocalDateTime.of(currentYear.getValue(), 1, 1, 0, 0);
        LocalDateTime endOfYear = LocalDateTime.of(currentYear.getValue(), 12, 31, 23, 59, 59);
        
        Long countThisYear = saleRepository.countBySaleDateBetween(startOfYear, endOfYear);
        String number = String.format("SALE-%d-%06d", currentYear.getValue(), countThisYear + 1);
        
        while (saleRepository.findBySaleNumber(number).isPresent()) {
            countThisYear++;
            number = String.format("SALE-%d-%06d", currentYear.getValue(), countThisYear + 1);
        }
        
        log.debug("Número de venta generado: {}", number);
        return number;
    }
}
