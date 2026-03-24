#!/bin/bash
# ============================================================
# QUICK START - INTRANET BANCARIA
# Script de setup rápido para desarrolladores
# ============================================================

set -e

echo "=================================================="
echo "🚀 INTRANET BANCARIA - QUICK START"
echo "=================================================="
echo ""

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Verificar prereq
check_java() {
    if ! command -v java &> /dev/null; then
        echo -e "${RED}❌ Java no encontrado. Instala Java 17+${NC}"
        exit 1
    fi
    echo -e "${GREEN}✅ Java found: $(java -version 2>&1 | head -1)${NC}"
}

check_node() {
    if ! command -v node &> /dev/null; then
        echo -e "${RED}❌ Node.js no encontrado. Instala Node 18+${NC}"
        exit 1
    fi
    echo -e "${GREEN}✅ Node.js found: $(node --version)${NC}"
}

check_maven() {
    if ! command -v mvn &> /dev/null; then
        echo -e "${RED}❌ Maven no encontrado. Instala Maven 3.8+${NC}"
        exit 1
    fi
    echo -e "${GREEN}✅ Maven found: $(mvn --version | head -1)${NC}"
}

setup_backend() {
    echo ""
    echo -e "${YELLOW}*** Configurando Backend ***${NC}"
    cd backend
    
    echo "Limpiando build anterior..."
    mvn clean > /dev/null 2>&1
    
    echo "Descargando dependencias..."
    mvn install > /dev/null 2>&1
    
    echo -e "${GREEN}✅ Backend configurado${NC}"
    cd ..
}

setup_frontend() {
    echo ""
    echo -e "${YELLOW}*** Configurando Frontend ***${NC}"
    cd frontend
    
    if [ -d "node_modules" ]; then
        echo "Modules existentes encontrados, skipping npm install"
    else
        echo "Instalando dependencias npm..."
        npm install
    fi
    
    echo -e "${GREEN}✅ Frontend configurado${NC}"
    cd ..
}

setup_database() {
    echo ""
    echo -e "${YELLOW}*** Base de Datos ***${NC}"
    echo ""
    echo "IMPORTANTE:"
    echo "1. SQL Server debe estar ejecutándose en localhost:1433"
    echo "2. Usuario: sa"
    echo "3. Ejecutar script: database/scripts/01_create_database.sql"
    echo ""
    read -p "¿Has ejecutado el script SQL? (s/n): " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Ss]$ ]]; then
        echo -e "${RED}❌ Script SQL no ejecutado. Hazlo en SSMS y vuelve.${NC}"
        exit 1
    fi
    echo -e "${GREEN}✅ Base de datos configurada${NC}"
}

start_services() {
    echo ""
    echo -e "${YELLOW}*** Iniciando Servicios ***${NC}"
    echo ""
    echo "Se abrirán 3 ventanas de terminal:"
    echo "1. Backend (Spring Boot)"
    echo "2. Frontend (Angular)"
    echo "3. Navegador (http://localhost:4200)"
    echo ""
    read -p "Presiona Enter para continuar..."
    
    # Backend
    echo -e "${GREEN}Iniciando Backend...${NC}"
    cd backend
    gnome-terminal --title="Backend Spring Boot" -- bash -c "mvn spring-boot:run -Dspring-boot.run.arguments='--spring.profiles.active=dev'; bash" &
    cd ..
    sleep 3
    
    # Frontend
    echo -e "${GREEN}Iniciando Frontend...${NC}"
    cd frontend
    gnome-terminal --title="Frontend Angular" -- bash -c "npm start; bash" &
    cd ..
    
    echo ""
    echo -e "${GREEN}=================================================="
    echo "✅ Servicios iniciados!"
    echo "=================================================="
    echo ""
    echo "Backend: http://localhost:8080 (API)"
    echo "Frontend: http://localhost:4200"
    echo ""
    echo "Credenciales:"
    echo "Usuario: admin@banco.local"
    echo "Contraseña: AdminPassword123!"
    echo ""
    echo "Documentación: README.md"
    echo "=================================================="
    echo ""
}

# Main execution
main() {
    echo "Verificando requisitos previos..."
    check_java
    check_maven
    check_node
    
    setup_backend
    setup_frontend
    setup_database
    start_services
    
    echo -e "${GREEN}Abriendo navegador...${NC}"
    sleep 5
    xdg-open "http://localhost:4200" &
}

# Run
main
