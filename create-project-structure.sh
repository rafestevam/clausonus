#!/bin/bash

# Script corrigido para criação da estrutura de diretórios e arquivos do projeto Clausonus

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

# Diretório base (onde o script será executado)
BASE_DIR=$(pwd)
print_header "Criando estrutura do projeto Clausonus no diretório: $BASE_DIR"

# Criar estrutura principal do projeto
mkdir -p "$BASE_DIR"

# Criar arquivos da raiz
touch "$BASE_DIR/pom.xml"
touch "$BASE_DIR/docker-compose.yml"
touch "$BASE_DIR/.gitignore"
touch "$BASE_DIR/dev-start.sh"
touch "$BASE_DIR/README.md"

# Criar módulo de loja
mkdir -p "$BASE_DIR/clausonus-loja"
touch "$BASE_DIR/clausonus-loja/pom.xml"
touch "$BASE_DIR/clausonus-loja/Dockerfile"

# Estrutura do módulo loja
# src/main/java
mkdir -p "$BASE_DIR/clausonus-loja/src/main/java/br/com/rockambole/clausonus/loja/controller"
mkdir -p "$BASE_DIR/clausonus-loja/src/main/java/br/com/rockambole/clausonus/loja/dto"
mkdir -p "$BASE_DIR/clausonus-loja/src/main/java/br/com/rockambole/clausonus/loja/entity"
mkdir -p "$BASE_DIR/clausonus-loja/src/main/java/br/com/rockambole/clausonus/loja/exception"
mkdir -p "$BASE_DIR/clausonus-loja/src/main/java/br/com/rockambole/clausonus/loja/mapper"
mkdir -p "$BASE_DIR/clausonus-loja/src/main/java/br/com/rockambole/clausonus/loja/repository"
mkdir -p "$BASE_DIR/clausonus-loja/src/main/java/br/com/rockambole/clausonus/loja/service"

# src/main/resources
mkdir -p "$BASE_DIR/clausonus-loja/src/main/resources"
touch "$BASE_DIR/clausonus-loja/src/main/resources/application.properties"
touch "$BASE_DIR/clausonus-loja/src/main/resources/import.sql"

# src/test/java
mkdir -p "$BASE_DIR/clausonus-loja/src/test/java/br/com/rockambole/clausonus/loja/controller"
mkdir -p "$BASE_DIR/clausonus-loja/src/test/java/br/com/rockambole/clausonus/loja/repository"
mkdir -p "$BASE_DIR/clausonus-loja/src/test/java/br/com/rockambole/clausonus/loja/service"

# src/test/resources
mkdir -p "$BASE_DIR/clausonus-loja/src/test/resources"
touch "$BASE_DIR/clausonus-loja/src/test/resources/import-test.sql"

# Criar arquivos Java
touch "$BASE_DIR/clausonus-loja/src/main/java/br/com/rockambole/clausonus/loja/controller/LojaController.java"
touch "$BASE_DIR/clausonus-loja/src/main/java/br/com/rockambole/clausonus/loja/dto/LojaDTO.java"
touch "$BASE_DIR/clausonus-loja/src/main/java/br/com/rockambole/clausonus/loja/entity/Loja.java"
touch "$BASE_DIR/clausonus-loja/src/main/java/br/com/rockambole/clausonus/loja/exception/LojaExceptionHandler.java"
touch "$BASE_DIR/clausonus-loja/src/main/java/br/com/rockambole/clausonus/loja/mapper/LojaMapper.java"
touch "$BASE_DIR/clausonus-loja/src/main/java/br/com/rockambole/clausonus/loja/repository/LojaRepository.java"
touch "$BASE_DIR/clausonus-loja/src/main/java/br/com/rockambole/clausonus/loja/service/LojaService.java"

# Criar arquivos de teste
touch "$BASE_DIR/clausonus-loja/src/test/java/br/com/rockambole/clausonus/loja/controller/LojaControllerTest.java"
touch "$BASE_DIR/clausonus-loja/src/test/java/br/com/rockambole/clausonus/loja/repository/LojaRepositoryTest.java"
touch "$BASE_DIR/clausonus-loja/src/test/java/br/com/rockambole/clausonus/loja/repository/LojaTestProfile.java"
touch "$BASE_DIR/clausonus-loja/src/test/java/br/com/rockambole/clausonus/loja/service/LojaServiceTest.java"

# Tornar os scripts executáveis
chmod +x "$BASE_DIR/dev-start.sh"
chmod +x "$BASE_DIR/create-project-structure-fixed.sh"

print_success "Todos os arquivos foram criados com sucesso!"

echo ""
echo "Estrutura do projeto criada corretamente para compatibilidade com Maven/IDE:"
echo ""
find "$BASE_DIR" -type f | grep -v "target/" | sort
echo ""
print_header "Próximos passos"
echo "1. Copie o conteúdo de cada arquivo para o respectivo arquivo criado"
echo "2. Execute 'mvn install' na raiz do projeto para instalar as dependências"
echo "3. Execute './dev-start.sh' para iniciar o ambiente de desenvolvimento"
echo ""
print_header "Notas importantes"
echo "- Certifique-se de que as classes Java tenham o pacote correto declarado no início:"
echo "  package br.com.rockambole.clausonus.loja.dto;  (sem 'main.java' no início)"
echo "- Verifique se o Maven está configurado para reconhecer a estrutura de diretórios padrão"
echo "- Configure sua IDE para reconhecer o projeto como um projeto Maven"