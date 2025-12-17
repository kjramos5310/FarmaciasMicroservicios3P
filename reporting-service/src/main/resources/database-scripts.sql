-- Creación de base de datos (ejecutar como superusuario)
CREATE DATABASE reporting_db;

-- Conectar a la base de datos
\c reporting_db;

-- Las tablas serán creadas automáticamente por Hibernate
-- Este archivo contiene consultas útiles para administración

-- ========== CONSULTAS DE ANÁLISIS ==========

-- Ver resumen de ventas por sucursal (últimos 30 días)
SELECT 
    branch_id,
    COUNT(*) as total_records,
    SUM(total_sales) as total_sales,
    SUM(total_revenue) as total_revenue,
    AVG(average_ticket) as avg_ticket
FROM sales_summary
WHERE report_date >= CURRENT_DATE - INTERVAL '30 days'
GROUP BY branch_id
ORDER BY total_revenue DESC;

-- Ver productos con ventas más altas
SELECT 
    product_name,
    product_code,
    SUM(quantity_sold) as total_quantity,
    SUM(revenue) as total_revenue
FROM product_sales_report
WHERE report_date >= CURRENT_DATE - INTERVAL '30 days'
GROUP BY product_name, product_code
ORDER BY total_revenue DESC
LIMIT 10;

-- Ver inventario bajo stock por sucursal
SELECT 
    report_date,
    branch_id,
    total_products,
    low_stock_products,
    ROUND((low_stock_products::decimal / total_products::decimal * 100), 2) as low_stock_percentage
FROM inventory_summary
WHERE low_stock_products > 0
ORDER BY report_date DESC, low_stock_percentage DESC;

-- Productos próximos a vencer
SELECT 
    report_date,
    branch_id,
    expiring_soon,
    total_products,
    ROUND((expiring_soon::decimal / total_products::decimal * 100), 2) as expiring_percentage
FROM inventory_summary
WHERE expiring_soon > 0
ORDER BY report_date DESC, expiring_percentage DESC;

-- ========== CONSULTAS DE TENDENCIAS ==========

-- Tendencia de ventas diarias (última semana)
SELECT 
    report_date,
    SUM(total_sales) as daily_sales,
    SUM(total_revenue) as daily_revenue,
    AVG(average_ticket) as avg_ticket
FROM sales_summary
WHERE report_date >= CURRENT_DATE - INTERVAL '7 days'
GROUP BY report_date
ORDER BY report_date;

-- Comparación de sucursales
SELECT 
    branch_id,
    report_date,
    total_sales,
    total_revenue,
    average_ticket,
    unique_customers
FROM sales_summary
WHERE report_date = CURRENT_DATE - INTERVAL '1 day'
ORDER BY total_revenue DESC;

-- ========== ÍNDICES PARA OPTIMIZACIÓN ==========

-- Índices en sales_summary
CREATE INDEX IF NOT EXISTS idx_sales_summary_date ON sales_summary(report_date);
CREATE INDEX IF NOT EXISTS idx_sales_summary_branch ON sales_summary(branch_id);
CREATE INDEX IF NOT EXISTS idx_sales_summary_date_branch ON sales_summary(report_date, branch_id);

-- Índices en inventory_summary
CREATE INDEX IF NOT EXISTS idx_inventory_summary_date ON inventory_summary(report_date);
CREATE INDEX IF NOT EXISTS idx_inventory_summary_branch ON inventory_summary(branch_id);
CREATE INDEX IF NOT EXISTS idx_inventory_summary_date_branch ON inventory_summary(report_date, branch_id);

-- Índices en product_sales_report
CREATE INDEX IF NOT EXISTS idx_product_sales_date ON product_sales_report(report_date);
CREATE INDEX IF NOT EXISTS idx_product_sales_product ON product_sales_report(product_id);
CREATE INDEX IF NOT EXISTS idx_product_sales_branch ON product_sales_report(branch_id);
CREATE INDEX IF NOT EXISTS idx_product_sales_date_product ON product_sales_report(report_date, product_id);

-- ========== LIMPIEZA DE DATOS ==========

-- Eliminar snapshots antiguos (más de 1 año)
DELETE FROM sales_summary 
WHERE report_date < CURRENT_DATE - INTERVAL '1 year';

DELETE FROM inventory_summary 
WHERE report_date < CURRENT_DATE - INTERVAL '1 year';

DELETE FROM product_sales_report 
WHERE report_date < CURRENT_DATE - INTERVAL '1 year';

-- ========== VISTAS ÚTILES ==========

-- Vista de resumen mensual de ventas
CREATE OR REPLACE VIEW monthly_sales_summary AS
SELECT 
    DATE_TRUNC('month', report_date) as month,
    branch_id,
    SUM(total_sales) as monthly_sales,
    SUM(total_revenue) as monthly_revenue,
    AVG(average_ticket) as avg_monthly_ticket,
    SUM(unique_customers) as total_customers
FROM sales_summary
GROUP BY DATE_TRUNC('month', report_date), branch_id
ORDER BY month DESC, monthly_revenue DESC;

-- Vista de alertas de inventario
CREATE OR REPLACE VIEW inventory_alerts AS
SELECT 
    report_date,
    branch_id,
    low_stock_products,
    expiring_soon,
    CASE 
        WHEN low_stock_products > 10 THEN 'CRITICAL'
        WHEN low_stock_products > 5 THEN 'WARNING'
        ELSE 'NORMAL'
    END as stock_alert_level,
    CASE 
        WHEN expiring_soon > 20 THEN 'URGENT'
        WHEN expiring_soon > 10 THEN 'ATTENTION'
        ELSE 'NORMAL'
    END as expiry_alert_level
FROM inventory_summary
WHERE report_date = CURRENT_DATE - INTERVAL '1 day';
