-- This file allow to write SQL commands that will be emitted in test and dev.
-- The commands are commented as their support depends of the database
-- insert into myentity (id, field) values(1, 'field-1');
-- insert into myentity (id, field) values(2, 'field-2');
-- insert into myentity (id, field) values(3, 'field-3');
-- alter sequence myentity_seq restart with 4;

-- Este script será executado durante a inicialização para popular o banco com dados iniciais (somente se quarkus.hibernate-orm.database.generation=drop-and-create)

-- Dados de exemplo para ambiente de teste
INSERT INTO loja(id_loja, nome, endereco, cnpj, telefone) VALUES (1, 'Matriz - Centro', 'Av. Paulista, 1000, Centro, São Paulo - SP', '12.345.678/0001-01', '(11) 3333-4444');
INSERT INTO loja(id_loja, nome, endereco, cnpj, telefone) VALUES (2, 'Filial - Shopping', 'Shopping Vila Olímpia, Loja 42, São Paulo - SP', '12.345.678/0002-02', '(11) 4444-5555');
INSERT INTO loja(id_loja, nome, endereco, cnpj, telefone) VALUES (3, 'Filial - Norte', 'Av. Engenheiro Caetano Álvares, 2200, Santana, São Paulo - SP', '12.345.678/0003-03', '(11) 5555-6666');