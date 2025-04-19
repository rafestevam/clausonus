package br.com.rockambole.clausonus.funcionario.entity;

import java.util.List;
import java.util.Optional;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Entidade Funcionário implementada com Panache
 * Utiliza o padrão Active Record para operações de banco de dados
 */
@Entity
@Table(name = "funcionario")
public class Funcionario extends PanacheEntity {
    
    @NotBlank(message = "O nome é obrigatório")
    @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
    @Column(name = "nome", length = 100, nullable = false)
    private String nome;
    
    @NotBlank(message = "O CPF é obrigatório")
    @Size(min = 11, max = 14, message = "CPF inválido")
    @Column(name = "cpf", length = 14, nullable = false, unique = true)
    private String cpf;
    
    @NotBlank(message = "O cargo é obrigatório")
    @Size(max = 50, message = "O cargo deve ter no máximo 50 caracteres")
    @Column(name = "cargo", length = 50, nullable = false)
    private String cargo;
    
    @NotBlank(message = "O login é obrigatório")
    @Size(min = 3, max = 20, message = "O login deve ter entre 3 e 20 caracteres")
    @Column(name = "login", length = 20, nullable = false, unique = true)
    private String login;
    
    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 6, max = 100, message = "A senha deve ter no mínimo 6 caracteres")
    @Column(name = "senha", length = 100, nullable = false)
    private String senha;
    
    @Column(name = "ativo", nullable = false)
    private boolean ativo = true;
    
    // Construtores
    public Funcionario() {
    }
    
    public Funcionario(String nome, String cpf, String cargo, String login, String senha, boolean ativo) {
        this.nome = nome;
        this.cpf = cpf;
        this.cargo = cargo;
        this.login = login;
        this.senha = senha;
        this.ativo = ativo;
    }
    
    // Métodos de consulta (usando Panache)
    
    /**
     * Lista todos os funcionários ativos
     */
    public static List<Funcionario> listarAtivos() {
        return list("ativo", true);
    }
    
    /**
     * Busca funcionário por CPF
     */
    public static Optional<Funcionario> buscarPorCpf(String cpf) {
        return find("cpf", cpf).firstResultOptional();
    }
    
    /**
     * Busca funcionário por login
     */
    public static Optional<Funcionario> buscarPorLogin(String login) {
        return find("login", login).firstResultOptional();
    }
    
    /**
     * Busca funcionários por nome (busca parcial)
     */
    public static List<Funcionario> buscarPorNome(String nome) {
        return list("nome LIKE ?1", "%" + nome + "%");
    }
    
    /**
     * Busca funcionários por cargo
     */
    public static List<Funcionario> buscarPorCargo(String cargo) {
        return list("cargo", cargo);
    }
    
    // Getters e Setters
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
    
    // Métodos para conversão entre Entity e DTO
    
    /**
     * Converte esta entidade para DTO
     */
    public br.com.rockambole.clausonus.funcionario.dto.FuncionarioDTO toDTO() {
        br.com.rockambole.clausonus.funcionario.dto.FuncionarioDTO dto = new br.com.rockambole.clausonus.funcionario.dto.FuncionarioDTO();
        dto.setId(this.id);
        dto.setNome(this.nome);
        dto.setCpf(this.cpf);
        dto.setCargo(this.cargo);
        dto.setLogin(this.login);
        // Não transferimos a senha para o DTO por segurança
        dto.setAtivo(this.ativo);
        return dto;
    }
    
    /**
     * Atualiza esta entidade com dados do DTO
     */
    public Funcionario fromDTO(br.com.rockambole.clausonus.funcionario.dto.FuncionarioDTO dto) {
        this.nome = dto.getNome();
        this.cpf = dto.getCpf();
        this.cargo = dto.getCargo();
        this.login = dto.getLogin();
        // A senha só é atualizada se for fornecida
        if (dto.getSenha() != null && !dto.getSenha().isEmpty()) {
            this.senha = dto.getSenha(); // Na implementação real, deve usar SenhaService para criptografar
        }
        this.ativo = dto.isAtivo();
        return this;
    }
    
    /**
     * Cria uma nova entidade a partir de um DTO
     */
    public static Funcionario fromDTO(br.com.rockambole.clausonus.funcionario.dto.FuncionarioDTO dto, String senhaCriptografada) {
        Funcionario funcionario = new Funcionario();
        funcionario.nome = dto.getNome();
        funcionario.cpf = dto.getCpf();
        funcionario.cargo = dto.getCargo();
        funcionario.login = dto.getLogin();
        funcionario.senha = senhaCriptografada;
        funcionario.ativo = dto.isAtivo();
        return funcionario;
    }
}