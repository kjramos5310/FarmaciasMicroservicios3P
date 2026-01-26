# Etapa de construcción
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copiar solo pom.xml primero para cachear dependencias
COPY pom.xml .

# Descargar dependencias
RUN mvn dependency:go-offline -B

# Copiar código fuente
COPY src ./src

# Construir la aplicación
RUN mvn clean package -DskipTests

# Etapa de ejecución
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Instalar wget para healthchecks
RUN apk add --no-cache wget

# Copiar JAR construido
COPY --from=build /app/target/*.jar app.jar

# Exponer puerto
EXPOSE 8084

# Ejecutar aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
