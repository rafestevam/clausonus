# Clausonus - Sistema de Fechamento de Loja

## Módulo de Loja

Este módulo é responsável pelo gerenciamento de lojas no sistema Clausonus, fornecendo funcionalidades para cadastro, consulta, atualização e exclusão de lojas.

## Tecnologias Utilizadas

- **Java 17**: Linguagem de programação
- **Quarkus**: Framework para desenvolvimento de aplicações Java
- **Hibernate ORM com Panache**: Mapeamento objeto-relacional
- **RESTEasy Reactive**: Implementação JAX-RS para APIs REST
- **JUnit 5 e Mockito**: Frameworks para testes unitários e de integração
- **PostgreSQL**: Banco de dados relacional (produção)
- **H2**: Banco de dados em memória (desenvolvimento e testes)
- **Maven**: Gerenciamento de dependências e build

## Estrutura do Módulo

```
clausonus-loja/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── br/com/rockambole/clausonus/loja/
│   │   │       ├── controller/       # Controladores REST
│   │   │       ├── dto/              # Objetos de transferência de dados
│   │   │       ├── entity/           # Entidades JPA
│   │   │       ├── exception/        # Exceções personalizadas
│   │   │       ├── mapper/           # Mapeadores MapStruct
│   │   │       ├── repository/       # Repositórios Panache
│   │   │       └── service/          # Serviços de negócio
│   │   └── resources/
│   │       ├── application.properties # Configurações da aplicação
│   │       └── import.sql            # Script SQL para carga inicial
│   └── test/
│       ├── java/
│       │   └── br/com/rockambole/clausonus/loja/
│       │       ├── controller/       # Testes dos controladores
│       │       ├── repository/       # Testes dos repositórios
│       │       └── service/          # Testes dos serviços
│       └── resources/
│           └── import-test.sql       # Script SQL para testes
```

## Modelo de Dados

A entidade principal deste módulo é a `Loja`, que possui os seguintes atributos:

- **id**: Identificador único da loja
- **nome**: Nome da loja
- **endereco**: Endereço completo da loja
- **cnpj**: CNPJ da loja (único)
- **telefone**: Telefone de contato da loja

## Endpoints da API

### Operações CRUD Básicas

- **GET /lojas**: Lista todas as lojas cadastradas
- **GET /lojas/{id}**: Busca uma loja pelo ID
- **POST /lojas**: Cadastra uma nova loja
- **PUT /lojas/{id}**: Atualiza uma loja existente
- **DELETE /lojas/{id}**: Exclui uma loja

### Operações Adicionais

- **GET /lojas/busca?nome={texto}**: Busca lojas pelo nome (busca parcial)
- **GET /lojas/verificar-cnpj/{cnpj}**: Verifica se já existe uma loja cadastrada com o CNPJ informado

## Compilação e Execução

### Requisitos

- Java 17 ou superior
- Maven 3.8 ou superior

### Comandos

Para compilar o módulo:

```bash
mvn clean package
```

Para executar em modo de desenvolvimento:

```bash
mvn quarkus:dev
```

Para executar os testes:

```bash
mvn test
```

## Integração com outros Módulos

Este módulo de Loja é consumido por outros módulos do sistema Clausonus, como o módulo de Fechamento de Caixa, que utiliza as informações da loja para realizar o fechamento diário.
