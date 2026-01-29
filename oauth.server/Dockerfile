# ==============================================================================
# MULTI-STAGE DOCKERFILE - OAUTH SERVER
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
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copiar JAR desde stage de build
COPY --from=build /app/target/*.jar app.jar

# Exponer puerto
EXPOSE 9000

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s \
  CMD wget --no-verbose --tries=1 --spider http://localhost:9000/actuator/health || exit 1

# Punto de entrada
ENTRYPOINT ["java", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", \
  "app.jar"]
