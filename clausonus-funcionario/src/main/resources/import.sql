-- Dados iniciais para tabela de funcionários
-- Senhas estão em formato hash simulado, na implementação real utilize a classe SenhaService
INSERT INTO funcionario (id_funcionario, nome, cpf, cargo, login, senha, ativo) VALUES (1, 'Administrador', '12345678900', 'Administrador', 'admin', 'hashed_password_123', true);
INSERT INTO funcionario (id_funcionario, nome, cpf, cargo, login, senha, ativo) VALUES (2, 'Gerente', '98765432100', 'Gerente', 'gerente', 'hashed_password_456', true);
INSERT INTO funcionario (id_funcionario, nome, cpf, cargo, login, senha, ativo) VALUES (3, 'Vendedor', '11122233344', 'Vendedor', 'vendedor', 'hashed_password_789', true);
INSERT INTO funcionario (id_funcionario, nome, cpf, cargo, login, senha, ativo) VALUES (4, 'Caixa', '55566677788', 'Caixa', 'caixa', 'hashed_password_012', true);
INSERT INTO funcionario (id_funcionario, nome, cpf, cargo, login, senha, ativo) VALUES (5, 'Estoquista', '99988877766', 'Estoquista', 'estoque', 'hashed_password_345', true);