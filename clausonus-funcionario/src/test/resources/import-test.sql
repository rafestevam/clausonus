-- Script para carga inicial de dados no ambiente de testes

-- Funcionários
INSERT INTO funcionario (id, nome, cpf, cargo, login, senha, ativo) VALUES (1, 'Funcionário Teste', '00011122233', 'Tester', 'testuser', 'test_password_hash', true);
INSERT INTO funcionario (id, nome, cpf, cargo, login, senha, ativo) VALUES (2, 'Funcionário Inativo', '44455566677', 'Tester', 'inativo', 'test_password_hash', false);

-- Sequência para autoincremento
ALTER SEQUENCE funcionario_seq RESTART WITH 3;