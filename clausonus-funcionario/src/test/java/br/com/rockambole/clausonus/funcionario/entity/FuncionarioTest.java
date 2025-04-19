package br.com.rockambole.clausonus.funcionario.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.Optional;

import jakarta.transaction.Transactional;

import br.com.rockambole.clausonus.funcionario.dto.FuncionarioDTO;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;

import org.junit.jupiter.api.Test;

/**
 * Testes de integração para a entidade Funcionario usando Panache
 */
@QuarkusTest
@TestProfile(FuncionarioTestProfile.class)
public class FuncionarioTest {

    @Test
    public void testListarTodos() {
        List<Funcionario> funcionarios = Funcionario.listAll();
        assertFalse(funcionarios.isEmpty(), "A lista de funcionários não deveria estar vazia");
        assertEquals(2, funcionarios.size(), "Deveriam existir 2 funcionários cadastrados no teste");
    }

    @Test
    public void testListarAtivos() {
        List<Funcionario> funcionarios = Funcionario.listarAtivos();
        assertFalse(funcionarios.isEmpty(), "A lista de funcionários ativos não deveria estar vazia");
        assertEquals(1, funcionarios.size(), "Deveria existir 1 funcionário ativo");
        assertEquals("Funcionário Teste", funcionarios.get(0).getNome(), "O funcionário ativo deveria ser 'Funcionário Teste'");
    }

    @Test
    public void testBuscarPorId() {
        Optional<Funcionario> funcionario = Funcionario.findByIdOptional(1L);
        assertTrue(funcionario.isPresent(), "Deveria encontrar o funcionário com ID 1");
        assertEquals("Funcionário Teste", funcionario.get().getNome(), "O nome do funcionário deveria ser 'Funcionário Teste'");
    }

    @Test
    public void testBuscarPorIdInexistente() {
        Optional<Funcionario> funcionario = Funcionario.findByIdOptional(999L);
        assertFalse(funcionario.isPresent(), "Não deveria encontrar funcionário com ID 999");
    }

    @Test
    public void testBuscarPorCpf() {
        Optional<Funcionario> funcionario = Funcionario.buscarPorCpf("00011122233");
        assertTrue(funcionario.isPresent(), "Deveria encontrar o funcionário com CPF 00011122233");
        assertEquals("Funcionário Teste", funcionario.get().getNome(), "O nome do funcionário deveria ser 'Funcionário Teste'");
    }

    @Test
    public void testBuscarPorCpfInexistente() {
        Optional<Funcionario> funcionario = Funcionario.buscarPorCpf("99999999999");
        assertFalse(funcionario.isPresent(), "Não deveria encontrar funcionário com CPF inexistente");
    }

    @Test
    public void testBuscarPorLogin() {
        Optional<Funcionario> funcionario = Funcionario.buscarPorLogin("testuser");
        assertTrue(funcionario.isPresent(), "Deveria encontrar o funcionário com login 'testuser'");
        assertEquals("Funcionário Teste", funcionario.get().getNome(), "O nome do funcionário deveria ser 'Funcionário Teste'");
    }

    @Test
    public void testBuscarPorLoginInexistente() {
        Optional<Funcionario> funcionario = Funcionario.buscarPorLogin("usuarioinexistente");
        assertFalse(funcionario.isPresent(), "Não deveria encontrar funcionário com login inexistente");
    }

    @Test
    public void testBuscarPorNome() {
        List<Funcionario> funcionarios = Funcionario.buscarPorNome("Teste");
        assertEquals(1, funcionarios.size(), "Deveria encontrar 1 funcionário com nome contendo 'Teste'");
        assertEquals("Funcionário Teste", funcionarios.get(0).getNome(), "O nome do funcionário deveria ser 'Funcionário Teste'");
    }

    @Test
    public void testBuscarPorCargo() {
        List<Funcionario> funcionarios = Funcionario.buscarPorCargo("Tester");
        assertEquals(2, funcionarios.size(), "Deveria encontrar 2 funcionários com cargo 'Tester'");
    }

    @Test
    @Transactional
    public void testSalvarEDeletar() {
        // Criar um novo funcionário
        Funcionario novoFuncionario = new Funcionario("Funcionário Novo", "12345678900", "Gerente", "gerente.novo", "senha123", true);
        
        // Salvar o funcionário
        novoFuncionario.persist();
        
        // Verificar se o funcionário foi salvo
        assertNotNull(novoFuncionario.id, "O ID do funcionário não deveria ser nulo após salvar");
        
        // Buscar o funcionário pelo ID
        Optional<Funcionario> funcionarioSalvo = Funcionario.findByIdOptional(novoFuncionario.id);
        assertTrue(funcionarioSalvo.isPresent(), "Deveria encontrar o funcionário recém-salvo");
        assertEquals("Funcionário Novo", funcionarioSalvo.get().getNome(), "O nome do funcionário deveria ser 'Funcionário Novo'");
        
        // Excluir o funcionário
        boolean deletado = Funcionario.deleteById(novoFuncionario.id);
        assertTrue(deletado, "A exclusão do funcionário deveria retornar true");
        
        // Verificar se o funcionário foi excluído
        Optional<Funcionario> funcionarioExcluido = Funcionario.findByIdOptional(novoFuncionario.id);
        assertFalse(funcionarioExcluido.isPresent(), "Não deveria encontrar o funcionário após exclusão");
    }
    
    @Test
    public void testToDTO() {
        // Obter um funcionário existente
        Optional<Funcionario> funcionarioOpt = Funcionario.findByIdOptional(1L);
        assertTrue(funcionarioOpt.isPresent(), "Deveria encontrar o funcionário para teste");
        
        Funcionario funcionario = funcionarioOpt.get();
        
        // Converter para DTO
        FuncionarioDTO dto = funcionario.toDTO();
        
        // Verificar valores
        assertEquals(funcionario.id, dto.getId(), "O ID deveria ser preservado");
        assertEquals(funcionario.getNome(), dto.getNome(), "O nome deveria ser preservado");
        assertEquals(funcionario.getCpf(), dto.getCpf(), "O CPF deveria ser preservado");
        assertEquals(funcionario.getCargo(), dto.getCargo(), "O cargo deveria ser preservado");
        assertEquals(funcionario.getLogin(), dto.getLogin(), "O login deveria ser preservado");
        assertEquals(funcionario.isAtivo(), dto.isAtivo(), "O status ativo deveria ser preservado");
        // A senha não deve ser incluída no DTO por segurança
        assertEquals(null, dto.getSenha(), "A senha não deveria estar presente no DTO");
    }
    
    @Test
    @Transactional
    public void testFromDTO() {
        // Criar um DTO
        FuncionarioDTO dto = new FuncionarioDTO();
        dto.setNome("Funcionário From DTO");
        dto.setCpf("11122233344");
        dto.setCargo("Analista");
        dto.setLogin("analista.novo");
        dto.setSenha("senhaDTO");  // Esta senha será usada diretamente no teste, sem criptografia
        dto.setAtivo(true);
        
        // Criar uma entidade a partir do DTO
        Funcionario funcionario = Funcionario.fromDTO(dto, "senhaCriptografada");
        
        // Verificar valores
        assertEquals(dto.getNome(), funcionario.getNome(), "O nome deveria ser preservado");
        assertEquals(dto.getCpf(), funcionario.getCpf(), "O CPF deveria ser preservado");
        assertEquals(dto.getCargo(), funcionario.getCargo(), "O cargo deveria ser preservado");
        assertEquals(dto.getLogin(), funcionario.getLogin(), "O login deveria ser preservado");
        assertEquals("senhaCriptografada", funcionario.getSenha(), "A senha deveria ser a versão criptografada");
        assertEquals(dto.isAtivo(), funcionario.isAtivo(), "O status ativo deveria ser preservado");
    }
    
    @Test
    @Transactional
    public void testUpdateFromDTO() {
        // Criar um funcionário
        Funcionario funcionario = new Funcionario("Funcionário Original", "22233344455", "Desenvolvedor", "dev.original", "senha123", true);
        funcionario.persist();
        
        // Criar um DTO com novos valores
        FuncionarioDTO dto = new FuncionarioDTO();
        dto.setNome("Funcionário Atualizado");
        dto.setCpf("22233344455");  // Mesmo CPF para evitar conflitos
        dto.setCargo("Desenvolvedor Senior");
        dto.setLogin("dev.original");  // Mesmo login para evitar conflitos
        dto.setSenha("novaSenha");
        dto.setAtivo(true);
        
        // Atualizar o funcionário a partir do DTO
        funcionario.fromDTO(dto);
        funcionario.persist();
        
        // Verificar valores
        assertEquals(dto.getNome(), funcionario.getNome(), "O nome deveria ser atualizado");
        assertEquals(dto.getCpf(), funcionario.getCpf(), "O CPF deveria ser preservado");
        assertEquals(dto.getCargo(), funcionario.getCargo(), "O cargo deveria ser atualizado");
        assertEquals(dto.getLogin(), funcionario.getLogin(), "O login deveria ser preservado");
        assertEquals(dto.getSenha(), funcionario.getSenha(), "A senha deveria ser atualizada");
        assertEquals(dto.isAtivo(), funcionario.isAtivo(), "O status ativo deveria ser preservado");
        
        // Limpar depois do teste
        Funcionario.deleteById(funcionario.id);
    }
}