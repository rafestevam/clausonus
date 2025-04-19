#!/bin/bash

# Script para testar os endpoints da API de Lojas do sistema Clausonus
# Autor: Claude
# Data: 19/04/2025
#
# INSTRUÇÕES DE USO:
# 
# 1. Dê permissão de execução ao script:
#    chmod +x test-loja-api.sh
#
# 2. Execute o script:
#    ./test-loja-api.sh
#
# 3. Para especificar host e porta diferentes:
#    API_HOST=outro.servidor API_PORT=9090 ./test-loja-api.sh
#
# REQUISITOS:
# - curl: para realizar as requisições HTTP
# - jq: opcional, para formatação de JSON (a saída será legível mesmo sem jq)

# Configurações
API_HOST="localhost"
API_PORT="8080"
API_BASE_URL="http://${API_HOST}:${API_PORT}"
API_ENDPOINT="/clausonus/api/lojas"
CONTENT_TYPE="Content-Type: application/json"

# Cores para saída
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Função para imprimir mensagens formatadas
print_message() {
  local color=$1
  local message=$2
  echo -e "${color}${message}${NC}"
}

# Função para imprimir separador
print_separator() {
  echo -e "\n${BLUE}=========================================================${NC}\n"
}

# Função para verificar status HTTP
check_status() {
  local expected_status=$1
  local actual_status=$2
  local operation=$3
  
  if [ "$actual_status" -eq "$expected_status" ]; then
    print_message "${GREEN}" "✅ ${operation} - Status: ${actual_status} - OK"
  else
    if [ "$operation" == "Verificar exclusão (deve retornar 404)" ] && [ "$actual_status" -eq 404 ]; then
      # Caso especial para o teste de verificação de exclusão
      print_message "${GREEN}" "✅ ${operation} - Status: ${actual_status} - OK (404 esperado)"
    else
      print_message "${RED}" "❌ ${operation} - Status: ${actual_status} - ERRO (esperado: ${expected_status})"
    fi
  fi
}

# Dados para testes
LOJA_DATA='{
  "nome": "Loja Teste",
  "endereco": "Rua dos Testes, 123",
  "cnpj": "28668343000148",
  "telefone": "(11) 99999-9999"
}'

LOJA_UPDATE='{
  "nome": "Loja Teste Atualizada",
  "endereco": "Avenida dos Testes, 456",
  "cnpj": "28668343000148",
  "telefone": "(11) 88888-8888"
}'

# Variável para armazenar o ID da loja criada
LOJA_ID=""

# Função principal
main() {
  print_message "${YELLOW}" "🚀 Iniciando testes da API de Lojas"
  print_message "${BLUE}" "URL Base: ${API_BASE_URL}"
  print_message "${BLUE}" "Endpoint: ${API_ENDPOINT}"
  print_separator
  
  # Teste 0: Verificar se o serviço está acessível
  print_message "${BLUE}" "Teste 0: Verificar se o serviço está acessível"
  response=$(curl -s -o /dev/null -w "%{http_code}" "${API_BASE_URL}${API_ENDPOINT}")

  if [ "$response" -eq 200 ] || [ "$response" -eq 204 ]; then
    print_message "${GREEN}" "✅ Serviço disponível - Status: ${response}"
  else
    print_message "${RED}" "❌ Serviço indisponível - Status: ${response}"
    print_message "${YELLOW}" "Verifique se o servidor está rodando e se o caminho '${API_ENDPOINT}' está correto"
    
    # Tente acessar o Swagger UI para verificar os endpoints disponíveis
    print_message "${YELLOW}" "Tentando acessar a documentação Swagger para verificar endpoints disponíveis..."
    swagger_response=$(curl -s -o /dev/null -w "%{http_code}" "${API_BASE_URL}/q/swagger-ui/")
    
    if [ "$swagger_response" -eq 200 ]; then
      print_message "${GREEN}" "✅ Documentação Swagger disponível em: ${API_BASE_URL}/q/swagger-ui/"
      print_message "${YELLOW}" "Acesse a URL acima para verificar os endpoints corretos da API"
    else
      print_message "${RED}" "❌ Documentação Swagger não disponível - Status: ${swagger_response}"
    fi
    
    exit 1
  fi
  print_separator
  
  # Teste 1: Listar todas as lojas
  print_message "${BLUE}" "Teste 1: Listar todas as lojas"
  response=$(curl -s -w "\n%{http_code}" -X GET "${API_BASE_URL}${API_ENDPOINT}")
  status_code=$(echo "$response" | tail -n1)
  response_body=$(echo "$response" | sed '$d')
  
  check_status 200 "$status_code" "Listar todas as lojas"
  echo "$response_body" | jq . 2>/dev/null || echo "$response_body"
  print_separator
  
  # Teste 2: Criar uma nova loja
  print_message "${BLUE}" "Teste 2: Criar uma nova loja"
  response=$(curl -s -w "\n%{http_code}" -X POST \
    -H "${CONTENT_TYPE}" \
    -d "${LOJA_DATA}" \
    "${API_BASE_URL}${API_ENDPOINT}")
  status_code=$(echo "$response" | tail -n1)
  response_body=$(echo "$response" | sed '$d')
  
  check_status 201 "$status_code" "Criar uma nova loja"
  echo "$response_body" | jq . 2>/dev/null || echo "$response_body"
  
  # Extrair o ID da loja criada
  if [ "$status_code" -eq 201 ]; then
    LOJA_ID=$(echo "$response_body" | jq -r .id 2>/dev/null)
    print_message "${GREEN}" "ID da loja criada: ${LOJA_ID}"
  fi
  print_separator
  
  # Teste 3: Buscar loja por ID
  if [ -n "$LOJA_ID" ]; then
    print_message "${BLUE}" "Teste 3: Buscar loja por ID ${LOJA_ID}"
    response=$(curl -s -w "\n%{http_code}" -X GET "${API_BASE_URL}${API_ENDPOINT}/${LOJA_ID}")
    status_code=$(echo "$response" | tail -n1)
    response_body=$(echo "$response" | sed '$d')
    
    check_status 200 "$status_code" "Buscar loja por ID"
    echo "$response_body" | jq . 2>/dev/null || echo "$response_body"
    print_separator
  else
    print_message "${RED}" "Pulando teste de busca por ID: ID da loja não disponível"
  fi
  
  # Teste 4: Buscar lojas por nome
  print_message "${BLUE}" "Teste 4: Buscar lojas por nome 'Teste'"
  response=$(curl -s -w "\n%{http_code}" -X GET "${API_BASE_URL}${API_ENDPOINT}/buscar?nome=Teste")
  status_code=$(echo "$response" | tail -n1)
  response_body=$(echo "$response" | sed '$d')
  
  check_status 200 "$status_code" "Buscar lojas por nome"
  echo "$response_body" | jq . 2>/dev/null || echo "$response_body"
  print_separator
  
  # Teste 5: Buscar loja por CNPJ
  print_message "${BLUE}" "Teste 5: Buscar loja por CNPJ '12345678901234'"
  response=$(curl -s -w "\n%{http_code}" -X GET "${API_BASE_URL}${API_ENDPOINT}/cnpj/12345678901234")
  status_code=$(echo "$response" | tail -n1)
  response_body=$(echo "$response" | sed '$d')
  
  check_status 200 "$status_code" "Buscar loja por CNPJ"
  echo "$response_body" | jq . 2>/dev/null || echo "$response_body"
  print_separator
  
  # Teste 6: Atualizar loja
  if [ -n "$LOJA_ID" ]; then
    print_message "${BLUE}" "Teste 6: Atualizar loja com ID ${LOJA_ID}"
    response=$(curl -s -w "\n%{http_code}" -X PUT \
      -H "${CONTENT_TYPE}" \
      -d "${LOJA_UPDATE}" \
      "${API_BASE_URL}${API_ENDPOINT}/${LOJA_ID}")
    status_code=$(echo "$response" | tail -n1)
    response_body=$(echo "$response" | sed '$d')
    
    check_status 200 "$status_code" "Atualizar loja"
    echo "$response_body" | jq . 2>/dev/null || echo "$response_body"
    print_separator
  else
    print_message "${RED}" "Pulando teste de atualização: ID da loja não disponível"
  fi
  
  # Teste 7: Excluir loja
  if [ -n "$LOJA_ID" ]; then
    print_message "${BLUE}" "Teste 7: Excluir loja com ID ${LOJA_ID}"
    response=$(curl -s -w "\n%{http_code}" -X DELETE "${API_BASE_URL}${API_ENDPOINT}/${LOJA_ID}")
    status_code=$(echo "$response" | tail -n1)
    response_body=$(echo "$response" | sed '$d')
    
    check_status 204 "$status_code" "Excluir loja"
    if [ -n "$response_body" ]; then
      echo "$response_body"
    else
      print_message "${GREEN}" "Loja excluída com sucesso"
    fi
    print_separator
  else
    print_message "${RED}" "Pulando teste de exclusão: ID da loja não disponível"
  fi
  
  # Teste 8: Confirmar se a loja foi excluída tentando buscar por ID
  if [ -n "$LOJA_ID" ]; then
    print_message "${BLUE}" "Teste 8: Verificar se a loja com ID ${LOJA_ID} foi realmente excluída"
    print_message "${YELLOW}" "Nota: Espera-se um erro 404 aqui (Loja não encontrada com o ID: ${LOJA_ID})"
    response=$(curl -s -w "\n%{http_code}" -X GET "${API_BASE_URL}${API_ENDPOINT}/${LOJA_ID}")
    status_code=$(echo "$response" | tail -n1)
    response_body=$(echo "$response" | sed '$d')
    
    check_status 404 "$status_code" "Verificar exclusão (deve retornar 404)"
    if [ "$status_code" -eq 404 ]; then
      print_message "${GREEN}" "✅ Loja não encontrada, confirmando exclusão bem-sucedida"
      print_message "${YELLOW}" "  Isso gerará um log de erro no servidor: 'NotFoundException: Loja não encontrada com o ID: ${LOJA_ID}'"
      print_message "${YELLOW}" "  Este é um comportamento esperado e não indica um problema real."
    else
      print_message "${RED}" "❌ Erro: Loja ainda existe após tentativa de exclusão"
      echo "$response_body" | jq . 2>/dev/null || echo "$response_body"
    fi
    print_separator
  else
    print_message "${RED}" "Pulando verificação de exclusão: ID da loja não disponível"
  fi
  
  print_message "${YELLOW}" "🏁 Testes concluídos!"
}

# Executar a função principal
main