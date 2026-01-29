# Guía de Despliegue en Google Cloud Platform (GCP)

## 📋 Índice
1. [Opciones de Arquitectura en GCP](#opciones-de-arquitectura)
2. [Preparativos antes del Despliegue](#preparativos)
3. [Configuración de Producción](#configuración-de-producción)
4. [Despliegue con Cloud Run (Recomendado)](#despliegue-cloud-run)
5. [Despliegue con Google Kubernetes Engine](#despliegue-gke)
6. [Base de Datos en la Nube](#base-de-datos)
7. [CI/CD con Cloud Build](#cicd)
8. [Seguridad y Secretos](#seguridad)
9. [Monitoreo y Logging](#monitoreo)
10. [Estimación de Costos](#costos)

---

## 🏗️ Opciones de Arquitectura en GCP {#opciones-de-arquitectura}

### Opción 1: Cloud Run (🌟 RECOMENDADO para empezar)
**Ventajas:**
- ✅ Más fácil de configurar y mantener
- ✅ Serverless (paga solo por uso)
- ✅ Escala automáticamente a 0 cuando no hay tráfico
- ✅ Integración nativa con Docker
- ✅ HTTPS automático
- ✅ Ideal para microservicios con tráfico variable

**Servicios a usar:**
- Cloud Run: Frontend + 6 microservicios
- Cloud SQL (PostgreSQL): 5 bases de datos
- Cloud Build: CI/CD
- Artifact Registry: Imágenes Docker
- Secret Manager: Credenciales

**Costo estimado:** $30-80/mes (tráfico bajo-medio)

### Opción 2: Google Kubernetes Engine (GKE)
**Ventajas:**
- ✅ Control total sobre la orquestación
- ✅ Mejor para alta demanda y escala compleja
- ✅ Service mesh avanzado (Istio)

**Desventajas:**
- ❌ Más complejo de configurar
- ❌ Requiere conocimientos de Kubernetes
- ❌ Costo mínimo ~$70/mes (cluster siempre activo)

### Opción 3: Compute Engine (VMs)
**Solo si necesitas:** Control total del SO o software legacy
**Costo:** Similar a tener servidores dedicados

---

## 🎯 Preparativos antes del Despliegue {#preparativos}

### 1. Crear cuenta y proyecto en GCP
```bash
# Instalar Google Cloud SDK
# Windows: https://cloud.google.com/sdk/docs/install

# Iniciar sesión
gcloud auth login

# Crear proyecto
gcloud projects create farmacia-microservicios-prod --name="Farmacia Microservicios"

# Configurar proyecto activo
gcloud config set project farmacia-microservicios-prod

# Habilitar APIs necesarias
gcloud services enable \
  run.googleapis.com \
  sqladmin.googleapis.com \
  cloudbuild.googleapis.com \
  artifactregistry.googleapis.com \
  secretmanager.googleapis.com \
  logging.googleapis.com \
  monitoring.googleapis.com
```

### 2. Configurar Artifact Registry (para imágenes Docker)
```bash
# Crear repositorio de imágenes
gcloud artifacts repositories create farmacia-services \
  --repository-format=docker \
  --location=us-central1 \
  --description="Microservicios de Farmacia"

# Configurar autenticación de Docker
gcloud auth configure-docker us-central1-docker.pkg.dev
```

### 3. Crear archivo `.gcloudignore` en la raíz del proyecto
```gitignore
# .gcloudignore
.git
.gitignore
node_modules/
target/
*/target/
*.log
.env
.env.local
README*.md
*.md
.vscode/
.idea/
```

---

## ⚙️ Configuración de Producción {#configuración-de-producción}

### Cambios necesarios en el código:

#### 1. Variables de entorno externalizadas
Crear archivos de configuración por ambiente:

**`application-prod.yml`** (para cada microservicio):
```yaml
spring:
  datasource:
    url: ${DATABASE_URL}
    username: ${DATABASE_USER}
    password: ${DATABASE_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: validate  # NUNCA 'update' en producción
    show-sql: false       # Desactivar en producción
  
server:
  port: ${PORT:8080}      # Cloud Run usa variable PORT

logging:
  level:
    root: INFO
    com.example: INFO
```

#### 2. Dockerfile optimizado para producción
Ya tienes buenos Dockerfiles, pero asegúrate de:

**Frontend Dockerfile** - Agregar variables de entorno:
```dockerfile
# En tu frontend/Dockerfile, agregar:
ENV REACT_APP_API_URL=${REACT_APP_API_URL:-/api}
ENV REACT_APP_OAUTH_URL=${REACT_APP_OAUTH_URL:-/oauth2}
```

#### 3. Health checks mejorados
Asegúrate de que todos los microservicios tengan:
```java
// Ya tienes Spring Boot Actuator, solo verificar en application.yml
management:
  endpoints:
    web:
      exposure:
        include: health,info
  endpoint:
    health:
      show-details: when-authorized
```

#### 4. CORS para producción
En `SecurityConfig.java` de cada microservicio:
```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList(
        "https://tu-dominio.com",
        "https://farmacia-frontend-xxxxxxx.run.app"
    ));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(Arrays.asList("*"));
    configuration.setAllowCredentials(true);
    
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}
```

---

## 🚀 Despliegue con Cloud Run (Paso a Paso) {#despliegue-cloud-run}

### Paso 1: Crear Cloud SQL (PostgreSQL)

```bash
# Crear instancia de Cloud SQL
gcloud sql instances create farmacia-db \
  --database-version=POSTGRES_15 \
  --tier=db-f1-micro \
  --region=us-central1 \
  --root-password=TU_PASSWORD_SEGURO

# Crear las 5 bases de datos
gcloud sql databases create catalog_db --instance=farmacia-db
gcloud sql databases create inventory_db --instance=farmacia-db
gcloud sql databases create sales_db --instance=farmacia-db
gcloud sql databases create reporting_db --instance=farmacia-db
gcloud sql databases create oauth_db --instance=farmacia-db

# Crear usuario de aplicación
gcloud sql users create farmacia_user \
  --instance=farmacia-db \
  --password=PASSWORD_SEGURO
```

### Paso 2: Guardar secretos en Secret Manager

```bash
# Crear secretos
echo -n "TU_PASSWORD_DB" | gcloud secrets create db-password --data-file=-
echo -n "jdbc:postgresql://CLOUD_SQL_CONNECTION_NAME/catalog_db" | gcloud secrets create catalog-db-url --data-file=-
echo -n "jdbc:postgresql://CLOUD_SQL_CONNECTION_NAME/inventory_db" | gcloud secrets create inventory-db-url --data-file=-
echo -n "jdbc:postgresql://CLOUD_SQL_CONNECTION_NAME/sales_db" | gcloud secrets create sales-db-url --data-file=-
echo -n "jdbc:postgresql://CLOUD_SQL_CONNECTION_NAME/reporting_db" | gcloud secrets create reporting-db-url --data-file=-
echo -n "jdbc:postgresql://CLOUD_SQL_CONNECTION_NAME/oauth_db" | gcloud secrets create oauth-db-url --data-file=-

# Obtener el CLOUD_SQL_CONNECTION_NAME
gcloud sql instances describe farmacia-db --format="value(connectionName)"
# Formato: proyecto:region:instance-name
```

### Paso 3: Build y Push de imágenes Docker

Crear script `deploy-to-gcp.sh`:
```bash
#!/bin/bash

PROJECT_ID="farmacia-microservicios-prod"
REGION="us-central1"
REGISTRY="us-central1-docker.pkg.dev"

# Array de servicios
services=("productos-service" "almacen-service" "ventas-service" "reportes-service" "gateway" "oauth.server" "frontend")

for service in "${services[@]}"
do
  echo "Building and pushing $service..."
  
  # Build
  docker build -t $REGISTRY/$PROJECT_ID/farmacia-services/$service:latest ./$service
  
  # Push
  docker push $REGISTRY/$PROJECT_ID/farmacia-services/$service:latest
  
  echo "$service pushed successfully!"
done
```

### Paso 4: Deploy a Cloud Run

```bash
# 1. Deploy productos-service
gcloud run deploy productos-service \
  --image=us-central1-docker.pkg.dev/farmacia-microservicios-prod/farmacia-services/productos-service:latest \
  --region=us-central1 \
  --platform=managed \
  --allow-unauthenticated \
  --add-cloudsql-instances=farmacia-db \
  --set-env-vars="SPRING_PROFILES_ACTIVE=prod,DATABASE_USER=farmacia_user" \
  --set-secrets="DATABASE_PASSWORD=db-password:latest,DATABASE_URL=catalog-db-url:latest" \
  --memory=512Mi \
  --cpu=1 \
  --max-instances=10

# 2. Deploy almacen-service
gcloud run deploy almacen-service \
  --image=us-central1-docker.pkg.dev/farmacia-microservicios-prod/farmacia-services/almacen-service:latest \
  --region=us-central1 \
  --platform=managed \
  --allow-unauthenticated \
  --add-cloudsql-instances=farmacia-db \
  --set-env-vars="SPRING_PROFILES_ACTIVE=prod,DATABASE_USER=farmacia_user" \
  --set-secrets="DATABASE_PASSWORD=db-password:latest,DATABASE_URL=inventory-db-url:latest" \
  --memory=512Mi \
  --cpu=1

# 3. Deploy ventas-service
gcloud run deploy ventas-service \
  --image=us-central1-docker.pkg.dev/farmacia-microservicios-prod/farmacia-services/ventas-service:latest \
  --region=us-central1 \
  --platform=managed \
  --allow-unauthenticated \
  --add-cloudsql-instances=farmacia-db \
  --set-env-vars="SPRING_PROFILES_ACTIVE=prod,DATABASE_USER=farmacia_user,CATALOG_SERVICE_URL=https://productos-service-xxxxx.run.app,INVENTORY_SERVICE_URL=https://almacen-service-xxxxx.run.app" \
  --set-secrets="DATABASE_PASSWORD=db-password:latest,DATABASE_URL=sales-db-url:latest" \
  --memory=512Mi

# 4. Deploy reportes-service
gcloud run deploy reportes-service \
  --image=us-central1-docker.pkg.dev/farmacia-microservicios-prod/farmacia-services/reportes-service:latest \
  --region=us-central1 \
  --platform=managed \
  --allow-unauthenticated \
  --add-cloudsql-instances=farmacia-db \
  --set-env-vars="SPRING_PROFILES_ACTIVE=prod,DATABASE_USER=farmacia_user,SALES_SERVICE_URL=https://ventas-service-xxxxx.run.app,INVENTORY_SERVICE_URL=https://almacen-service-xxxxx.run.app" \
  --set-secrets="DATABASE_PASSWORD=db-password:latest,DATABASE_URL=reporting-db-url:latest" \
  --memory=512Mi

# 5. Deploy oauth-server
gcloud run deploy oauth-server \
  --image=us-central1-docker.pkg.dev/farmacia-microservicios-prod/farmacia-services/oauth.server:latest \
  --region=us-central1 \
  --platform=managed \
  --allow-unauthenticated \
  --add-cloudsql-instances=farmacia-db \
  --set-env-vars="SPRING_PROFILES_ACTIVE=prod,DATABASE_USER=farmacia_user" \
  --set-secrets="DATABASE_PASSWORD=db-password:latest,DATABASE_URL=oauth-db-url:latest" \
  --memory=512Mi

# 6. Deploy gateway
gcloud run deploy gateway \
  --image=us-central1-docker.pkg.dev/farmacia-microservicios-prod/farmacia-services/gateway:latest \
  --region=us-central1 \
  --platform=managed \
  --allow-unauthenticated \
  --set-env-vars="SPRING_PROFILES_ACTIVE=prod,\
SPRING_SECURITY_OAUTH2_CLIENT_PROVIDER_GATEWAY-CLIENT_AUTHORIZATION-URI=https://oauth-server-xxxxx.run.app/oauth2/authorize,\
SPRING_SECURITY_OAUTH2_CLIENT_PROVIDER_GATEWAY-CLIENT_TOKEN-URI=https://oauth-server-xxxxx.run.app/oauth2/token,\
SPRING_SECURITY_OAUTH2_CLIENT_PROVIDER_GATEWAY-CLIENT_JWK-SET-URI=https://oauth-server-xxxxx.run.app/oauth2/jwks" \
  --memory=512Mi

# 7. Deploy frontend
gcloud run deploy frontend \
  --image=us-central1-docker.pkg.dev/farmacia-microservicios-prod/farmacia-services/frontend:latest \
  --region=us-central1 \
  --platform=managed \
  --allow-unauthenticated \
  --set-env-vars="REACT_APP_API_URL=https://gateway-xxxxx.run.app/api" \
  --memory=256Mi
```

### Paso 5: Configurar dominio personalizado (Opcional)

```bash
# Mapear dominio
gcloud run domain-mappings create \
  --service=frontend \
  --domain=farmacia.tu-dominio.com \
  --region=us-central1
```

---

## 🔒 Seguridad y Secretos {#seguridad}

### 1. Usar Secret Manager en el código

Agregar dependencia en `pom.xml`:
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-gcp-starter-secretmanager</artifactId>
</dependency>
```

En `application-prod.yml`:
```yaml
spring:
  cloud:
    gcp:
      secretmanager:
        enabled: true
  datasource:
    url: ${sm://catalog-db-url}
    password: ${sm://db-password}
```

### 2. IAM y Service Accounts

```bash
# Crear service account para cada servicio
gcloud iam service-accounts create productos-service-sa

# Dar permisos necesarios
gcloud projects add-iam-policy-binding farmacia-microservicios-prod \
  --member="serviceAccount:productos-service-sa@farmacia-microservicios-prod.iam.gserviceaccount.com" \
  --role="roles/cloudsql.client"

# Usar en Cloud Run
gcloud run services update productos-service \
  --service-account=productos-service-sa@farmacia-microservicios-prod.iam.gserviceaccount.com
```

### 3. HTTPS/SSL
Cloud Run proporciona HTTPS automáticamente. Para dominio personalizado:
- GCP genera certificado SSL gratis
- Renovación automática

---

## 🔄 CI/CD con Cloud Build {#cicd}

Crear `cloudbuild.yaml` en la raíz:
```yaml
steps:
  # Build productos-service
  - name: 'gcr.io/cloud-builders/docker'
    args: ['build', '-t', 'us-central1-docker.pkg.dev/$PROJECT_ID/farmacia-services/productos-service:$SHORT_SHA', './productos-service']
  
  # Push productos-service
  - name: 'gcr.io/cloud-builders/docker'
    args: ['push', 'us-central1-docker.pkg.dev/$PROJECT_ID/farmacia-services/productos-service:$SHORT_SHA']
  
  # Deploy productos-service
  - name: 'gcr.io/google.com/cloudsdktool/cloud-sdk'
    entrypoint: gcloud
    args:
      - 'run'
      - 'deploy'
      - 'productos-service'
      - '--image=us-central1-docker.pkg.dev/$PROJECT_ID/farmacia-services/productos-service:$SHORT_SHA'
      - '--region=us-central1'
      - '--platform=managed'

  # Repetir para otros servicios...

options:
  machineType: 'N1_HIGHCPU_8'
  
timeout: '1800s'
```

Configurar trigger en GitHub:
```bash
gcloud builds triggers create github \
  --repo-name=FarmaciasMicroservicios3P \
  --repo-owner=TU_USUARIO \
  --branch-pattern="^main$" \
  --build-config=cloudbuild.yaml
```

---

## 📊 Monitoreo y Logging {#monitoreo}

### 1. Cloud Logging (logs automáticos)
```bash
# Ver logs en tiempo real
gcloud logging read "resource.type=cloud_run_revision AND resource.labels.service_name=productos-service" --limit 50 --format json

# Crear alertas
gcloud alpha monitoring policies create \
  --notification-channels=CHANNEL_ID \
  --display-name="Error en productos-service" \
  --condition-display-name="Error rate > 5%" \
  --condition-threshold-value=0.05
```

### 2. Cloud Monitoring (métricas)
- CPU, memoria, latencia: automáticos
- Custom metrics con Micrometer:

```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-stackdriver</artifactId>
</dependency>
```

### 3. Cloud Trace (trazabilidad distribuida)
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-gcp-starter-trace</artifactId>
</dependency>
```

---

## 💰 Estimación de Costos {#costos}

### Cloud Run (Tráfico bajo-medio: ~1000 req/día)

| Servicio | Instancias | RAM | Costo/mes |
|----------|------------|-----|-----------|
| 4 Microservicios | 1-2 cada uno | 512Mi | ~$15 |
| Gateway | 1-2 | 512Mi | ~$5 |
| OAuth Server | 1 | 512Mi | ~$3 |
| Frontend | 1 | 256Mi | ~$2 |
| **Subtotal Compute** | | | **~$25** |

### Cloud SQL
| Recurso | Tier | Costo/mes |
|---------|------|-----------|
| PostgreSQL (db-f1-micro) | 1 vCPU, 614MB RAM | ~$7 |
| Storage (10GB) | | ~$2 |
| **Subtotal DB** | | **~$9** |

### Otros servicios
- Artifact Registry: ~$1
- Secret Manager: ~$0.50
- Logging/Monitoring: ~$5 (capa gratuita cubre mucho)
- Networking: ~$2

### **TOTAL ESTIMADO: $40-60/mes**

Para reducir costos:
- Usar tier `db-f1-micro` o `db-g1-small`
- Configurar `--min-instances=0` en Cloud Run
- Usar capa gratuita de Firebase Hosting para frontend estático

---

## 📝 Checklist de Producción

### Antes del despliegue:
- [ ] Cambiar `ddl-auto: validate` (no `update`)
- [ ] Desactivar `show-sql: false`
- [ ] Configurar CORS con dominios específicos
- [ ] Cambiar passwords por defecto
- [ ] Configurar variables de entorno
- [ ] Probar health checks
- [ ] Crear backups de BD

### Durante el despliegue:
- [ ] Crear proyecto GCP
- [ ] Habilitar APIs necesarias
- [ ] Configurar Cloud SQL
- [ ] Crear secretos en Secret Manager
- [ ] Build y push de imágenes
- [ ] Deploy de servicios en orden correcto
- [ ] Configurar IAM y service accounts

### Después del despliegue:
- [ ] Verificar health de todos los servicios
- [ ] Probar flujo completo de autenticación
- [ ] Configurar alertas de monitoreo
- [ ] Configurar backups automáticos de BD
- [ ] Documentar URLs de producción
- [ ] Configurar CI/CD

---

## 🚨 Problemas Comunes

### Cloud SQL Connection Issues
```bash
# Verificar que Cloud SQL esté agregado al servicio
gcloud run services describe productos-service --region=us-central1 --format="value(spec.template.spec.containers[0].cloudsql_instances)"

# Verificar permisos
gcloud projects get-iam-policy farmacia-microservicios-prod \
  --flatten="bindings[].members" \
  --filter="bindings.role:roles/cloudsql.client"
```

### Memory Issues
```bash
# Aumentar memoria
gcloud run services update productos-service \
  --memory=1Gi \
  --region=us-central1
```

### Timeout en requests
```bash
# Aumentar timeout
gcloud run services update productos-service \
  --timeout=300 \
  --region=us-central1
```

---

## 📚 Recursos Adicionales

- [Cloud Run Documentation](https://cloud.google.com/run/docs)
- [Cloud SQL Best Practices](https://cloud.google.com/sql/docs/postgres/best-practices)
- [Spring Boot on GCP](https://spring.io/guides/gs/spring-boot-for-gcp/)
- [GCP Free Tier](https://cloud.google.com/free)

---

## 🎯 Próximos Pasos Recomendados

1. **Crear proyecto GCP** y habilitar billing
2. **Empezar con Cloud Run** (más fácil)
3. **Configurar Cloud SQL** para las bases de datos
4. **Deploy manual** de un servicio primero (productos-service)
5. **Automatizar** con Cloud Build después
6. **Agregar dominio personalizado** cuando todo funcione
7. **Configurar monitoreo** y alertas

¿Necesitas ayuda con algún paso específico?
