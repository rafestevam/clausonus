package br.com.rockambole.clausonus.loja.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.Optional;

import jakarta.transaction.Transactional;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;

import org.junit.jupiter.api.Test;

/**
 * Testes de integração para a entidade Loja usando Panache
 */
@QuarkusTest
@TestProfile(LojaTestProfile.class)
public class LojaTest {

    @Test
    public void testListarTodas() {
        List<Loja> lojas = Loja.listAll();
        assertFalse(lojas.isEmpty(), "A lista de lojas não deveria estar vazia");
        assertEquals(3, lojas.size(), "Deveriam existir 3 lojas cadastradas");
    }

    @Test
    public void testBuscarPorId() {
        Optional<Loja> loja = Loja.findByIdOptional(1L);
        assertTrue(loja.isPresent(), "Deveria encontrar a loja com ID 1");
        assertEquals("Loja Matriz", loja.get().getNome(), "O nome da loja deveria ser 'Loja Matriz'");
    }

    @Test
    public void testBuscarPorIdInexistente() {
        Optional<Loja> loja = Loja.findByIdOptional(999L);
        assertFalse(loja.isPresent(), "Não deveria encontrar loja com ID 999");
    }

    @Test
    public void testBuscarPorCnpj() {
        Optional<Loja> loja = Loja.find("cnpj", "12345678901234").firstResultOptional();
        assertTrue(loja.isPresent(), "Deveria encontrar a loja com CNPJ 12345678901234");
        assertEquals("Loja Matriz", loja.get().getNome(), "O nome da loja deveria ser 'Loja Matriz'");
    }

    @Test
    public void testBuscarPorCnpjInexistente() {
        Optional<Loja> loja = Loja.find("cnpj", "99999999999999").firstResultOptional();
        assertFalse(loja.isPresent(), "Não deveria encontrar loja com CNPJ inexistente");
    }

    @Test
    public void testBuscarPorNome() {
        List<Loja> lojas = Loja.list("nome LIKE ?1", "%Campinas%");
        assertEquals(1, lojas.size(), "Deveria encontrar 1 loja com nome contendo 'Campinas'");
        assertEquals("Loja Campinas", lojas.get(0).getNome(), "O nome da loja deveria ser 'Loja Campinas'");
    }

    @Test
    public void testBuscarPorNomeParcial() {
        List<Loja> lojas = Loja.list("nome LIKE ?1", "%Loja%");
        assertEquals(3, lojas.size(), "Deveria encontrar 3 lojas com nome contendo 'Loja'");
    }

    @Test
    @Transactional
    public void testSalvarEDeletar() {
        // Criar uma nova loja
        Loja novaLoja = new Loja("Loja Teste", "Rua de Teste, 123", "98765432109876", "(11) 1234-5678");
        
        // Salvar a loja
        novaLoja.persist();
        
        // Verificar se a loja foi salva
        assertNotNull(novaLoja.id, "O ID da loja não deveria ser nulo após salvar");
        
        // Buscar a loja pelo ID
        Optional<Loja> lojaSalva = Loja.findByIdOptional(novaLoja.id);
        assertTrue(lojaSalva.isPresent(), "Deveria encontrar a loja recém-salva");
        assertEquals("Loja Teste", lojaSalva.get().getNome(), "O nome da loja deveria ser 'Loja Teste'");
        
        // Excluir a loja
        boolean deletado = Loja.deleteById(novaLoja.id);
        assertTrue(deletado, "A exclusão da loja deveria retornar true");
        
        // Verificar se a loja foi excluída
        Optional<Loja> lojaExcluida = Loja.findByIdOptional(novaLoja.id);
        assertFalse(lojaExcluida.isPresent(), "Não deveria encontrar a loja após exclusão");
    }
}