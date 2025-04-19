package br.com.rockambole.clausonus.funcionario.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para transferência de dados de Funcionário
 */
public class FuncionarioDTO {
    
    private Long id;
    
    @NotBlank(message = "O nome é obrigatório")
    @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
    private String nome;
    
    @NotBlank(message = "O CPF é obrigatório")
    @Size(min = 11, max = 14, message = "CPF inválido")
    private String cpf;
    
    @NotBlank(message = "O cargo é obrigatório")
    @Size(max = 50, message = "O cargo deve ter no máximo 50 caracteres")
    private String cargo;
    
    @NotBlank(message = "O login é obrigatório")
    @Size(min = 3, max = 20, message = "O login deve ter entre 3 e 20 caracteres")
    private String login;
    
    @Size(min = 6, max = 100, message = "A senha deve ter no mínimo 6 caracteres")
    private String senha;
    
    private boolean ativo = true;
    
    // Construtores
    public FuncionarioDTO() {
    }
    
    public FuncionarioDTO(Long id, String nome, String cpf, String cargo, String login, boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.cargo = cargo;
        this.login = login;
        this.ativo = ativo;
    }
    
    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
    
    @Override
    public String toString() {
        return "FuncionarioDTO [id=" + id + ", nome=" + nome + ", cpf=" + cpf + ", cargo=" + cargo + ", login=" + login
                + ", ativo=" + ativo + "]";
    }
}