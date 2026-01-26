# ==============================================================================
# MULTI-STAGE DOCKERFILE - GATEWAY SERVICE
# ==============================================================================
# Stage 1: Build
# Stage 2: Runtime
# ==============================================================================

# ==============================================================================
# STAGE 1: BUILD
# ==============================================================================
FROM maven:3.9.5-eclipse-temurin-17 AS build

WORKDIR /app

# Copiar archivos de configuración de Maven
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Descargar dependencias (capa cacheada)
RUN mvn dependency:go-offline -B

# Copiar código fuente
COPY src ./src

# Compilar la aplicación (saltar tests para build más rápido)
RUN mvn clean package -DskipTests

# ==============================================================================
# STAGE 2: RUNTIME
# ==============================================================================
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Crear usuario no-root para seguridad
RUN addgroup -g 1001 spring && \
    adduser -D -u 1001 -G spring spring

# Copiar el JAR compilado desde el stage de build
COPY --from=build /app/target/*.jar app.jar

# Cambiar ownership del JAR
RUN chown spring:spring app.jar

# Cambiar a usuario no-root
USER spring

# Exponer puerto del servicio
EXPOSE 8123

# Healthcheck
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8123/actuator/health || exit 1

# Variables de entorno por defecto
ENV SPRING_PROFILES_ACTIVE=prod

# Ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
