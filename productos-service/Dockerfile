# Etapa de construcción
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copiar archivo de configuración de Maven con mirror más rápido
COPY ../maven-settings.xml /root/.m2/settings.xml

# Copiar solo pom.xml primero para cachear dependencias
COPY pom.xml .

# Descargar dependencias con cache mount para acelerar builds
RUN --mount=type=cache,target=/root/.m2 \
    mvn dependency:go-offline -B

# Copiar código fuente
COPY src ./src

# Construir la aplicación usando cache de Maven
RUN --mount=type=cache,target=/root/.m2 \
    mvn clean package -DskipTests

# Etapa de ejecución
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Instalar wget para healthchecks
RUN apk add --no-cache wget

# Copiar JAR construido
COPY --from=build /app/target/*.jar app.jar

# Exponer puerto
EXPOSE 8081

# Ejecutar aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
