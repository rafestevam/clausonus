package br.com.rockambole.clausonus.loja.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "loja")
public class Loja extends PanacheEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_loja")  // Nome da coluna no banco de dados
    public Long id;           // Nome do campo na classe Java

    @NotBlank(message = "O nome é obrigatório")
    @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
    @Column(name = "nome", length = 100, nullable = false)
    private String nome;
    
    @NotBlank(message = "O endereço é obrigatório")
    @Size(max = 200, message = "O endereço deve ter no máximo 200 caracteres")
    @Column(name = "endereco", length = 200, nullable = false)
    private String endereco;
    
    @NotBlank(message = "O CNPJ é obrigatório")
    @Size(min = 14, max = 18, message = "CNPJ inválido")
    @Column(name = "cnpj", length = 18, nullable = false, unique = true)
    private String cnpj;
    
    @Size(max = 20, message = "O telefone deve ter no máximo 20 caracteres")
    @Column(name = "telefone", length = 20)
    private String telefone;
    
    // Construtores
    public Loja() {
    }
    
    public Loja(String nome, String endereco, String cnpj, String telefone) {
        this.nome = nome;
        this.endereco = endereco;
        this.cnpj = cnpj;
        this.telefone = telefone;
    }
    
    // Métodos de consulta usando Panache
    public static List<Loja> listarTodas() {
        return listAll();
    }
    
    public static Optional<Loja> buscarPorId(Long id) {
        return findByIdOptional(id);
    }
    
    public static Optional<Loja> buscarPorCnpj(String cnpj) {
        return find("cnpj", cnpj).firstResultOptional();
    }
    
    public static List<Loja> buscarPorNome(String nome) {
        return list("nome LIKE ?1", "%" + nome + "%");
    }
    
    // Getters e Setters
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

}