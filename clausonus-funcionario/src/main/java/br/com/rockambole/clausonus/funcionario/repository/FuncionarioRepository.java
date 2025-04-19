package br.com.rockambole.clausonus.funcionario.repository;

import java.util.List;
import java.util.Optional;

import br.com.rockambole.clausonus.funcionario.entity.Funcionario;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FuncionarioRepository implements PanacheRepository<Funcionario> {
    
    public List<Funcionario> listarTodos() {
        return listAll();
    }
    
    public List<Funcionario> listarAtivos() {
        return list("ativo", true);
    }
    
    public Optional<Funcionario> buscarPorId(Long id) {
        return findByIdOptional(id);
    }
    
    public Optional<Funcionario> buscarPorCpf(String cpf) {
        return find("cpf", cpf).firstResultOptional();
    }
    
    public Optional<Funcionario> buscarPorLogin(String login) {
        return find("login", login).firstResultOptional();
    }
    
    public List<Funcionario> buscarPorNome(String nome) {
        return list("nome LIKE ?1", "%" + nome + "%");
    }
    
    public List<Funcionario> buscarPorCargo(String cargo) {
        return list("cargo", cargo);
    }
    
    public void salvar(Funcionario funcionario) {
        persist(funcionario);
    }
    
    public boolean deletar(Long id) {
        return deleteById(id);
    }
}