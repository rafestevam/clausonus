package br.com.rockambole.clausonus.loja.repository;

import java.util.List;
import java.util.Optional;

import br.com.rockambole.clausonus.loja.entity.Loja;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Repositório para operações de persistência da entidade Loja
 */
@ApplicationScoped
public class LojaRepository implements PanacheRepository<Loja> {
    
    /**
     * Lista todas as lojas cadastradas
     * 
     * @return Lista de lojas
     */
    public List<Loja> listarTodas() {
        return listAll();
    }
    
    /**
     * Busca uma loja pelo seu ID
     * 
     * @param id ID da loja
     * @return Optional contendo a loja, se encontrada
     */
    public Optional<Loja> buscarPorId(Long id) {
        return findByIdOptional(id);
    }
    
    /**
     * Busca uma loja pelo CNPJ
     * 
     * @param cnpj CNPJ da loja
     * @return Optional contendo a loja, se encontrada
     */
    public Optional<Loja> buscarPorCnpj(String cnpj) {
        return find("cnpj", cnpj).firstResultOptional();
    }
    
    /**
     * Busca lojas cujo nome contenha o texto informado
     * 
     * @param nome Texto a ser buscado no nome das lojas
     * @return Lista de lojas que contêm o nome informado
     */
    public List<Loja> buscarPorNome(String nome) {
        return list("nome LIKE ?1", "%" + nome + "%");
    }
    
    /**
     * Salva ou atualiza uma loja
     * 
     * @param loja Loja a ser salva ou atualizada
     */
    public void salvar(Loja loja) {
        persist(loja);
    }
    
    /**
     * Exclui uma loja pelo seu ID
     * 
     * @param id ID da loja a ser excluída
     * @return true se a loja foi excluída, false caso contrário
     */
    public boolean deletar(Long id) {
        return deleteById(id);
    }
}