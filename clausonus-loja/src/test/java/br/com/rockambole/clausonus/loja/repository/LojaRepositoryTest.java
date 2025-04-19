package br.com.rockambole.clausonus.loja.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import jakarta.inject.Inject;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;

import org.junit.jupiter.api.Test;

import br.com.rockambole.clausonus.loja.entity.Loja;

/**
 * Testes de integração para o LojaRepository
 */
@QuarkusTest
@TestProfile(LojaTestProfile.class)
public class LojaRepositoryTest {

    @Inject
    LojaRepository repository;

    @Test
    public void testListarTodas() {
        List<Loja> lojas = repository.listarTodas();
        assertFalse(lojas.isEmpty(), "A lista de lojas não deveria estar vazia");
        assertEquals(3, lojas.size(), "Deveriam existir 3 lojas cadastradas");
    }

    @Test
    public void testBuscarPorId() {
        Optional<Loja> loja = repository.buscarPorId(1L);
        assertTrue(loja.isPresent(), "Deveria encontrar a loja com ID 1");
        assertEquals("Loja Matriz", loja.get().getNome(), "O nome da loja deveria ser 'Loja Matriz'");
    }

    @Test
    public void testBuscarPorIdInexistente() {
        Optional<Loja> loja = repository.buscarPorId(999L);
        assertFalse(loja.isPresent(), "Não deveria encontrar loja com ID 999");
    }

    @Test
    public void testBuscarPorCnpj() {
        Optional<Loja> loja = repository.buscarPorCnpj("12345678901234");
        assertTrue(loja.isPresent(), "Deveria encontrar a loja com CNPJ 12345678901234");
        assertEquals("Loja Matriz", loja.get().getNome(), "O nome da loja deveria ser 'Loja Matriz'");
    }

    @Test
    public void testBuscarPorCnpjInexistente() {
        Optional<Loja> loja = repository.buscarPorCnpj("99999999999999");
        assertFalse(loja.isPresent(), "Não deveria encontrar loja com CNPJ inexistente");
    }

    @Test
    public void testBuscarPorNome() {
        List<Loja> lojas = repository.buscarPorNome("Campinas");
        assertEquals(1, lojas.size(), "Deveria encontrar 1 loja com nome contendo 'Campinas'");
        assertEquals("Loja Campinas", lojas.get(0).getNome(), "O nome da loja deveria ser 'Loja Campinas'");
    }

    @Test
    public void testBuscarPorNomeParcial() {
        List<Loja> lojas = repository.buscarPorNome("Loja");
        assertEquals(3, lojas.size(), "Deveria encontrar 3 lojas com nome contendo 'Loja'");
    }

    @Test
    public void testSalvarEDeletar() {
        // Criar uma nova loja
        Loja novaLoja = new Loja("Loja Teste", "Rua de Teste, 123", "98765432109876", "(11) 1234-5678");
        
        // Salvar a loja
        repository.salvar(novaLoja);
        
        // Verificar se a loja foi salva
        assertFalse(novaLoja.getId() == null, "O ID da loja não deveria ser nulo após salvar");
        
        // Buscar a loja pelo ID
        Optional<Loja> lojaSalva = repository.buscarPorId(novaLoja.getId());
        assertTrue(lojaSalva.isPresent(), "Deveria encontrar a loja recém-salva");
        assertEquals("Loja Teste", lojaSalva.get().getNome(), "O nome da loja deveria ser 'Loja Teste'");
        
        // Excluir a loja
        boolean deletado = repository.deletar(novaLoja.getId());
        assertTrue(deletado, "A exclusão da loja deveria retornar true");
        
        // Verificar se a loja foi excluída
        Optional<Loja> lojaExcluida = repository.buscarPorId(novaLoja.getId());
        assertFalse(lojaExcluida.isPresent(), "Não deveria encontrar a loja após exclusão");
    }
}