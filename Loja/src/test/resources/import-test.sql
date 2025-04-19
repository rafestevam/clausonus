-- Script para popular o banco de dados de teste

-- Inserindo lojas para teste
INSERT INTO loja(id_loja, nome, endereco, cnpj, telefone) VALUES (1, 'Matriz - Centro (Teste)', 'Av. Paulista, 1000, Centro, São Paulo - SP', '12.345.678/0001-91', '(11) 3333-4444');
INSERT INTO loja(id_loja, nome, endereco, cnpj, telefone) VALUES (2, 'Filial - Shopping (Teste)', 'Shopping Vila Olímpia, Loja 42, São Paulo - SP', '12.345.678/0002-92', '(11) 4444-5555');
INSERT INTO loja(id_loja, nome, endereco, cnpj, telefone) VALUES (3, 'Filial - Norte (Teste)', 'Av. Engenheiro Caetano Álvares, 2200, Santana, São Paulo - SP', '12.345.678/0003-93', '(11) 5555-6666');

-- Reset da sequência (somente necessário para PostgreSQL)
ALTER SEQUENCE loja_id_loja_seq RESTART WITH 4;