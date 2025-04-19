# Documentação do Módulo de Loja

## Visão Geral

O módulo de Loja é parte de um sistema maior de Fechamento de Caixa, projetado como uma aplicação RESTful em Java utilizando o framework Quarkus. Este módulo é responsável por gerenciar todas as operações relacionadas às lojas da empresa, permitindo operações CRUD (Criar, Ler, Atualizar e Deletar) completas, garantindo a integridade dos dados e fornecendo uma API para integração com outros microsserviços do sistema.

## Arquitetura

O módulo segue a arquitetura em camadas com separação clara de responsabilidades:

1. **Camada de API (REST)**: Recebe e responde às requisições HTTP
2. **Camada de Serviço**: Contém a lógica de negócios  
3. **Camada de Repositório**: Interage com o banco de dados
4. **Camada de Modelo**: Define as entidades e DTOs

A aplicação é construída seguindo os princípios de API RESTful, com endpoints bem definidos e uso apropriado dos métodos HTTP para cada operação.

## Tecnologias Utilizadas

- **Java 17**: Linguagem de programação
- **Quarkus 3.7.0**: Framework Java leve e rápido para microserviços
- **RESTEasy**: Implementação JAX-RS para criar endpoints REST
- **Hibernate ORM com Panache**: Framework de ORM para persistência
- **PostgreSQL**: Sistema de gerenciamento de banco de dados relacional
- **JUnit e RestAssured**: Frameworks para testes de integração
- **OpenAPI e Swagger-UI**: Documentação de API
- **Maven**: Gerenciamento de dependências e build

## Modelo de Dados

A entidade principal deste módulo é a **Loja**, com os seguintes atributos:

- `id_loja`: Identificador único da loja (chave primária)
- `nome`: Nome da loja
- `endereco`: Endereço completo da loja
- `cnpj`: CNPJ da loja (valor único)
- `telefone`: Telefone de contato da loja

## Endpoints da API

| Método | Endpoint | Descrição | Parâmetros | Retorno |
|--------|----------|-----------|------------|---------|
| GET | `/api/lojas` | Lista todas as lojas | - | Lista de lojas |
| GET | `/api/lojas/{id}` | Busca loja por ID | `id`: ID da loja | Dados da loja |
| GET | `/api/lojas/buscar` | Busca lojas por nome | `nome`: Nome parcial da loja | Lista de lojas filtradas |
| POST | `/api/lojas` | Cria uma nova loja | Corpo JSON com dados da loja | Loja criada |
| PUT | `/api/lojas/{id}` | Atualiza uma loja | `id`: ID da loja; Corpo JSON com dados da loja | Loja atualizada |
| DELETE | `/api/lojas/{id}` | Remove uma loja | `id`: ID da loja | - |

## Configuração

O módulo usa o arquivo `application.properties` para configurações, incluindo:

- Configurações de banco de dados (PostgreSQL)
- Configurações do Hibernate ORM
- Configurações do OpenAPI e Swagger-UI
- Configurações CORS
- Configurações de segurança
- Configurações de log

Para ambiente de desenvolvimento, o módulo utiliza o recurso de DevServices do Quarkus, que sobe um container Docker com PostgreSQL automaticamente.

## Validações e Tratamento de Erros

O módulo implementa validações para garantir a integridade e consistência dos dados:

- Campos obrigatórios (nome, endereço, CNPJ)
- Tamanhos máximos para campos de texto
- CNPJ único (não permite cadastrar lojas com CNPJ duplicado)

Erros são tratados com respostas HTTP apropriadas e mensagens descritivas através do `ExceptionHandler`.

## Descrição dos Arquivos Java

### Entidades e DTOs

- **`Loja.java`**: Define a entidade Loja mapeada para a tabela no banco de dados, utilizando anotações JPA e estendendo PanacheEntityBase para facilitar a persistência.

- **`LojaDTO.java`**: Objeto de Transferência de Dados utilizado para expor dados através da API, contendo validações usando Bean Validation e separando a camada de API da camada de persistência.

### Repositório e Mapper

- **`LojaRepository.java`**: Fornece métodos para acessar o banco de dados utilizando Panache, com operações de CRUD e consultas personalizadas.

- **`LojaMapper.java`**: Converte objetos entre a entidade Loja e o DTO, facilitando a transferência de dados entre as camadas.

### Serviço e Exceções

- **`LojaService.java`**: Implementa a lógica de negócios, como validações específicas (CNPJ único) e orquestra as operações entre o controlador e o repositório.

- **`NegocioException.java`**: Exceção personalizada para erros de regras de negócio.

- **`ExceptionHandler.java`**: Centraliza o tratamento de exceções, convertendo-as em respostas HTTP adequadas.

### API e Testes

- **`LojaResource.java`**: Controlador REST que expõe os endpoints da API, utilizando anotações JAX-RS e documentados com OpenAPI.

- **`LojaResourceTest.java`**: Testes de integração para os endpoints da API, utilizando RestAssured e JUnit.

## Configuração e Scripts

- **`application.properties`**: Configurações gerais do Quarkus para o ambiente de produção.

- **`application-test.properties`**: Configurações específicas para testes.

- **`import.sql`**: Script para inserir dados iniciais no banco de dados.

- **`import-test.sql`**: Script para inserir dados de teste.

- **`pom.xml`**: Definição de dependências e configuração de build do Maven.

## Como executar

Para executar o módulo em modo de desenvolvimento:

```bash
./mvnw quarkus:dev
```

Para construir o projeto:

```bash
./mvnw clean package
```

Para executar testes:

```bash
./mvnw test
```

## Integração com outros Módulos

O módulo de Loja pode ser integrado com outros microsserviços do sistema, como:

- **Módulo de Fechamento de Caixa**: Relaciona os fechamentos com as lojas onde ocorreram
- **Módulo de Funcionários**: Associa funcionários às lojas onde trabalham
- **Módulo de Vendas**: Relaciona vendas às lojas onde foram realizadas

A integração acontece através da API REST, seguindo uma arquitetura de microsserviços conforme o diagrama fornecido.