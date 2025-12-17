# Guía de Contribución - Reporting Service

## 🎯 Convenciones de Código

### Nomenclatura

#### Clases
```java
// Entidades: sustantivos en singular
public class SalesSummary { }

// DTOs: sufijo "Request" o "Response"
public class SalesSummaryResponse { }

// Services: sufijo "Service"
public class ReportingService { }

// Repositories: sufijo "Repository"
public interface SalesSummaryRepository { }
```

#### Métodos
```java
// Obtener datos: prefijo "get"
public SalesSummaryResponse getSalesSummary() { }

// Consultas: prefijo "find"
public List<SalesSummary> findByDateRange() { }

// Cálculos: prefijo "calculate"
public BigDecimal calculateTotalRevenue() { }

// Generación: prefijo "generate"
public void generateDailySnapshots() { }
```

#### Variables
```java
// camelCase
LocalDate startDate;
BigDecimal totalRevenue;

// Constantes: UPPER_SNAKE_CASE
public static final String DEFAULT_BRANCH = "ALL";
```

### Estructura de Paquetes

```
com.example.reporting_service/
├── config/          # Configuraciones (WebClient, etc.)
├── controller/      # Controladores REST
├── dto/            # Data Transfer Objects
├── entity/         # Entidades JPA
├── exception/      # Excepciones personalizadas
├── external/       # DTOs de servicios externos
├── mapper/         # Mappers (Entity ↔ DTO)
├── repository/     # Repositorios JPA
└── service/        # Lógica de negocio
```

## 📝 Estándares de Código

### Annotations

```java
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor  // Lombok: inyección por constructor
@Slf4j                    // Lombok: logging
@CrossOrigin(origins = "*")
public class ReportController { }
```

### Logging

```java
// Niveles de log apropiados
log.debug("Processing request with params: {}", params);
log.info("Report generated successfully for branch: {}", branchId);
log.warn("No data found for date range: {} to {}", start, end);
log.error("Error fetching data from external service: {}", e.getMessage());
```

### Manejo de Excepciones

```java
// Service layer
try {
    // Operación riesgosa
} catch (WebClientResponseException e) {
    log.error("Error communicating with external service: {}", e.getMessage());
    throw new ExternalServiceException("Sales service unavailable", e);
} catch (Exception e) {
    log.error("Unexpected error: {}", e.getMessage(), e);
    throw new InternalServerException("Error generating report", e);
}
```

### Validaciones

```java
// Controller
@GetMapping("/sales/summary")
public ResponseEntity<SalesSummaryResponse> getSalesSummary(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) 
        @NotNull LocalDate startDate,
        
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        @NotNull LocalDate endDate) {
    // ...
}
```

## 🧪 Testing

### Estructura de Tests

```java
@SpringBootTest
@AutoConfigureMockMvc
class ReportControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private ReportingService reportingService;
    
    @Test
    @DisplayName("Should return sales summary for valid date range")
    void testGetSalesSummary_Success() {
        // Given
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 1, 31);
        
        // When & Then
        mockMvc.perform(get("/api/reports/sales/summary")
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString()))
                .andExpect(status().isOk());
    }
}
```

### Coverage Mínimo
- Unit Tests: **80%**
- Integration Tests: **60%**

## 🔄 Flujo de Trabajo Git

### Branches

```
main                    # Producción
├── develop            # Desarrollo
    ├── feature/ABC-123-add-report   # Nueva funcionalidad
    ├── bugfix/ABC-456-fix-date      # Corrección de bug
    └── hotfix/ABC-789-security      # Corrección urgente
```

### Commits

#### Formato
```
tipo(alcance): descripción corta

Descripción detallada (opcional)

Refs: #123
```

#### Tipos
- `feat`: Nueva funcionalidad
- `fix`: Corrección de bug
- `docs`: Documentación
- `style`: Formato de código
- `refactor`: Refactorización
- `test`: Tests
- `chore`: Tareas de mantenimiento

#### Ejemplos
```bash
git commit -m "feat(reports): add inventory value endpoint"
git commit -m "fix(service): correct average ticket calculation"
git commit -m "docs(readme): update deployment instructions"
```

### Pull Requests

#### Template
```markdown
## Descripción
Breve descripción del cambio

## Tipo de Cambio
- [ ] Nueva funcionalidad
- [ ] Corrección de bug
- [ ] Documentación
- [ ] Refactorización

## ¿Cómo se ha probado?
Descripción de tests realizados

## Checklist
- [ ] Tests agregados/actualizados
- [ ] Documentación actualizada
- [ ] Build exitoso localmente
- [ ] No hay warnings de linting
```

## 📦 Agregar Nueva Funcionalidad

### Ejemplo: Agregar nuevo reporte

#### 1. Crear DTO
```java
// dto/NewReportResponse.java
@Data
@Builder
public class NewReportResponse {
    private LocalDate reportDate;
    private String metric;
    private BigDecimal value;
}
```

#### 2. Agregar método en Service
```java
// service/ReportingService.java
public NewReportResponse getNewReport(LocalDate date) {
    log.info("Generating new report for date: {}", date);
    // Implementación
}
```

#### 3. Agregar endpoint en Controller
```java
// controller/ReportController.java
@GetMapping("/new-report")
public ResponseEntity<NewReportResponse> getNewReport(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
    
    log.info("GET /api/reports/new-report - date: {}", date);
    NewReportResponse response = reportingService.getNewReport(date);
    return ResponseEntity.ok(response);
}
```

#### 4. Agregar test
```java
// controller/ReportControllerTest.java
@Test
void testGetNewReport() {
    // Test implementation
}
```

#### 5. Actualizar documentación
- README.md: agregar endpoint
- ARCHITECTURE.md: agregar caso de uso
- Postman collection: agregar request

## 🐛 Reportar Bugs

### Template de Issue
```markdown
**Descripción del Bug**
Descripción clara del problema

**Pasos para Reproducir**
1. Ir a '...'
2. Ejecutar '...'
3. Ver error

**Comportamiento Esperado**
Lo que debería ocurrir

**Comportamiento Actual**
Lo que está ocurriendo

**Logs**
```
Logs relevantes
```

**Entorno**
- OS: [Windows/Linux/Mac]
- Java: [17]
- Spring Boot: [3.2.0]
```

## 🚀 Despliegue

### Checklist Pre-Despliegue
- [ ] Tests pasando
- [ ] Build exitoso
- [ ] Variables de entorno configuradas
- [ ] Base de datos migrada
- [ ] Documentación actualizada
- [ ] Changelog actualizado

### Versionado Semántico
```
MAJOR.MINOR.PATCH
1.0.0 → 1.0.1 (patch: bug fix)
1.0.1 → 1.1.0 (minor: nueva funcionalidad)
1.1.0 → 2.0.0 (major: cambio incompatible)
```

## 📚 Recursos

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [PostgreSQL Best Practices](https://www.postgresql.org/docs/current/index.html)
- [Clean Code](https://github.com/ryanmcdermott/clean-code-javascript)
- [Conventional Commits](https://www.conventionalcommits.org/)

## 💬 Contacto

Para preguntas o sugerencias, abrir un issue en el repositorio.
