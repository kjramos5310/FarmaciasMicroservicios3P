#!/bin/bash

# Script de inicio rápido para Reporting Service
# Autor: Sistema Distribuido
# Fecha: $(date +%Y-%m-%d)

echo "=========================================="
echo "   REPORTING SERVICE - Quick Start"
echo "=========================================="
echo ""

# Colores para mensajes
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Verificar Java
echo -e "${YELLOW}Verificando Java...${NC}"
if ! command -v java &> /dev/null; then
    echo -e "${RED}Java no está instalado. Por favor instale Java 17 o superior.${NC}"
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')
echo -e "${GREEN}Java version: $JAVA_VERSION${NC}"
echo ""

# Verificar Maven
echo -e "${YELLOW}Verificando Maven...${NC}"
if ! command -v mvn &> /dev/null; then
    echo -e "${YELLOW}Maven no encontrado. Usando Maven Wrapper...${NC}"
    MVN_CMD="./mvnw"
    chmod +x mvnw
else
    MVN_CMD="mvn"
    MVN_VERSION=$(mvn -version | head -n 1)
    echo -e "${GREEN}$MVN_VERSION${NC}"
fi
echo ""

# Verificar PostgreSQL
echo -e "${YELLOW}Verificando PostgreSQL...${NC}"
if ! command -v psql &> /dev/null; then
    echo -e "${YELLOW}PostgreSQL client no encontrado en PATH${NC}"
    echo -e "${YELLOW}Asegúrate de tener PostgreSQL corriendo en localhost:5435${NC}"
else
    echo -e "${GREEN}PostgreSQL client encontrado${NC}"
fi
echo ""

# Menú de opciones
echo "Seleccione una opción:"
echo "1) Compilar proyecto"
echo "2) Ejecutar tests"
echo "3) Empaquetar aplicación"
echo "4) Ejecutar aplicación"
echo "5) Ejecutar con Docker Compose"
echo "6) Limpiar y compilar todo"
echo "7) Salir"
echo ""

read -p "Opción: " option

case $option in
    1)
        echo -e "${YELLOW}Compilando proyecto...${NC}"
        $MVN_CMD clean compile
        ;;
    2)
        echo -e "${YELLOW}Ejecutando tests...${NC}"
        $MVN_CMD test
        ;;
    3)
        echo -e "${YELLOW}Empaquetando aplicación...${NC}"
        $MVN_CMD clean package -DskipTests
        echo -e "${GREEN}JAR generado en: target/reporting-service-0.0.1-SNAPSHOT.jar${NC}"
        ;;
    4)
        echo -e "${YELLOW}Ejecutando aplicación...${NC}"
        if [ ! -f "target/reporting-service-0.0.1-SNAPSHOT.jar" ]; then
            echo -e "${YELLOW}JAR no encontrado. Compilando primero...${NC}"
            $MVN_CMD clean package -DskipTests
        fi
        echo -e "${GREEN}Iniciando Reporting Service en puerto 8084...${NC}"
        java -jar target/reporting-service-0.0.1-SNAPSHOT.jar
        ;;
    5)
        echo -e "${YELLOW}Ejecutando con Docker Compose...${NC}"
        if ! command -v docker &> /dev/null; then
            echo -e "${RED}Docker no está instalado${NC}"
            exit 1
        fi
        docker-compose up --build
        ;;
    6)
        echo -e "${YELLOW}Limpiando y compilando todo...${NC}"
        $MVN_CMD clean install
        echo -e "${GREEN}Build completo exitoso!${NC}"
        ;;
    7)
        echo -e "${GREEN}Saliendo...${NC}"
        exit 0
        ;;
    *)
        echo -e "${RED}Opción inválida${NC}"
        exit 1
        ;;
esac

echo ""
echo -e "${GREEN}=========================================="
echo -e "   Proceso completado"
echo -e "==========================================${NC}"
