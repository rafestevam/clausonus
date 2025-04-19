package br.com.rockambole.clausonus.loja.repository;

import java.util.List;
import java.util.Optional;

import br.com.rockambole.clausonus.loja.entity.Loja;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LojaRepository implements PanacheRepository<Loja> {
    
    public List<Loja> listarTodas() {
        return listAll();
    }
    
    public Optional<Loja> buscarPorId(Long id) {
        return findByIdOptional(id);
    }
    
    public Optional<Loja> buscarPorCnpj(String cnpj) {
        return find("cnpj", cnpj).firstResultOptional();
    }
    
    public List<Loja> buscarPorNome(String nome) {
        return list("nome LIKE ?1", "%" + nome + "%");
    }
    
    public void salvar(Loja loja) {
        persist(loja);
    }
    
    public boolean deletar(Long id) {
        return deleteById(id);
    }
}