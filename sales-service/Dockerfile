# Usar imagen base de Java 17
FROM openjdk:17-jdk-slim

# Crear directorio de trabajo
WORKDIR /app

# Copiar el jar de la aplicación
COPY target/sales-service-0.0.1-SNAPSHOT.jar app.jar

# Exponer el puerto 8083
EXPOSE 8083

# Variables de entorno por defecto
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Ejecutar la aplicación
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
