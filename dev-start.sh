#!/bin/bash

# Script para inicialização do ambiente de desenvolvimento

# Cores para saída
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Funções auxiliares
function print_header() {
  echo -e "${YELLOW}=======================================================${NC}"
  echo -e "${YELLOW}               $1                ${NC}"
  echo -e "${YELLOW}=======================================================${NC}"
}

function print_success() {
  echo -e "${GREEN}$1${NC}"
}

function print_error() {
  echo -e "${RED}$1${NC}"
}

# Verificar se o Maven está instalado
if ! command -v mvn &> /dev/null; then
    print_error "Maven não encontrado. Por favor, instale o Maven."
    exit 1
fi

# Verificar se o Java está instalado
if ! command -v java &> /dev/null; then
    print_error "Java não encontrado. Por favor, instale o Java 17 ou superior."
    exit 1
fi

# Verificar versão do Java
java_version=$(java -version 2>&1 | head -1 | cut -d'"' -f2 | sed 's/^1\.//' | cut -d'.' -f1)
if [ "$java_version" -lt "17" ]; then
    print_error "Java $java_version detectado. Este projeto requer Java 17 ou superior."
    exit 1
fi

print_header "Iniciando ambiente de desenvolvimento Clausonus - Módulo Loja"

# Compilar o projeto
print_header "Compilando o projeto"
mvn clean compile
if [ $? -ne 0 ]; then
    print_error "Falha na compilação do projeto."
    exit 1
fi
print_success "Projeto compilado com sucesso!"

# Verificar se o usuário deseja iniciar em modo Docker ou local
read -p "Deseja iniciar o projeto usando Docker? (s/n): " use_docker

if [ "$use_docker" = "s" ] || [ "$use_docker" = "S" ]; then
    # Iniciar com Docker Compose
    print_header "Iniciando com Docker Compose"
    
    # Verificar se o Docker está instalado
    if ! command -v docker &> /dev/null; then
        print_error "Docker não encontrado. Por favor, instale o Docker."
        exit 1
    fi
    
    # Verificar se o Docker Compose está instalado
    if ! command -v docker-compose &> /dev/null; then
        print_error "Docker Compose não encontrado. Por favor, instale o Docker Compose."
        exit 1
    fi
    
    # Construir e iniciar os containers
    docker-compose up --build -d
    
    if [ $? -ne 0 ]; then
        print_error "Falha ao iniciar os containers."
        exit 1
    fi
    
    print_success "Containers iniciados com sucesso!"
    echo ""
    echo "Serviços disponíveis:"
    echo "- API Loja: http://localhost:8080"
    echo "- Swagger UI: http://localhost:8080/swagger-ui/"
    echo "- PgAdmin: http://localhost:5050"
    echo "  - Email: admin@clausonus.com.br"
    echo "  - Senha: admin"
    
else
    # Iniciar em modo de desenvolvimento local
    print_header "Iniciando Quarkus em modo dev"
    
    echo "Iniciando o aplicativo..."
    mvn quarkus:dev
fi