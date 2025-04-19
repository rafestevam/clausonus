package br.com.rockambole.clausonus.funcionario.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import jakarta.ws.rs.NotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.rockambole.clausonus.funcionario.dto.FuncionarioDTO;
import br.com.rockambole.clausonus.funcionario.entity.Funcionario;
import br.com.rockambole.clausonus.funcionario.repository.FuncionarioRepository;

public class FuncionarioServiceTest {

    @Mock
    private FuncionarioRepository funcionarioRepository;
    
    @Mock
    private SenhaService senhaService;
    
    @InjectMocks
    private FuncionarioService funcionarioService;
    
    private Funcionario funcionario1;
    private Funcionario funcionario2;
    
    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        
        // Configurar funcionários de teste
        funcionario1 = new Funcionario("Funcionário Um", "12345678900", "Analista", "analista1", "senha123", true);
        funcionario1.id = 1L;
        
        funcionario2 = new Funcionario("Funcionário Dois", "98765432100", "Gerente", "gerente1", "senha456", false);
        funcionario2.id = 2L;
    }
    
    @Test
    public void testListarTodos() {
        // Configurar mock
        when(funcionarioRepository.listarTodos()).thenReturn(Arrays.asList(funcionario1, funcionario2));
        
        // Executar
        List<FuncionarioDTO> resultado = funcionarioService.listarTodos();
        
        // Verificar
        assertEquals(2, resultado.size(), "Deveria retornar 2 funcionários");
        assertEquals("Funcionário Um", resultado.get(0).getNome(), "O nome do primeiro funcionário está incorreto");
        assertEquals("Funcionário Dois", resultado.get(1).getNome(), "O nome do segundo funcionário está incorreto");
        verify(funcionarioRepository, times(1)).listarTodos();
    }
    
    @Test
    public void testListarAtivos() {
        // Configurar mock
        when(funcionarioRepository.listarAtivos()).thenReturn(Arrays.asList(funcionario1));
        
        // Executar
        List<FuncionarioDTO> resultado = funcionarioService.listarAtivos();
        
        // Verificar
        assertEquals(1, resultado.size(), "Deveria retornar 1 funcionário ativo");
        assertEquals("Funcionário Um", resultado.get(0).getNome(), "O nome do funcionário ativo está incorreto");
        verify(funcionarioRepository, times(1)).listarAtivos();
    }
    
    @Test
    public void testBuscarPorId_Existente() {
        // Configurar mock
        when(funcionarioRepository.buscarPorId(1L)).thenReturn(Optional.of(funcionario1));
        
        // Executar
        FuncionarioDTO resultado = funcionarioService.buscarPorId(1L);
        
        // Verificar
        assertNotNull(resultado, "O resultado não deveria ser nulo");
        assertEquals("Funcionário Um", resultado.getNome(), "O nome do funcionário está incorreto");
        verify(funcionarioRepository, times(1)).buscarPorId(1L);
    }
    
    @Test
    public void testBuscarPorId_NaoExistente() {
        // Configurar mock
        when(funcionarioRepository.buscarPorId(999L)).thenReturn(Optional.empty());
        
        // Executar e verificar
        assertThrows(NotFoundException.class, () -> {
            funcionarioService.buscarPorId(999L);
        }, "Deveria lançar NotFoundException para ID inexistente");
        
        verify(funcionarioRepository, times(1)).buscarPorId(999L);
    }
    
    @Test
    public void testBuscarPorNome() {
        // Configurar mock
        when(funcionarioRepository.buscarPorNome("Funcionário")).thenReturn(Arrays.asList(funcionario1, funcionario2));
        
        // Executar
        List<FuncionarioDTO> resultado = funcionarioService.buscarPorNome("Funcionário");
        
        // Verificar
        assertEquals(2, resultado.size(), "Deveria retornar 2 funcionários");
        verify(funcionarioRepository, times(1)).buscarPorNome("Funcionário");
    }
    
    @Test
    public void testBuscarPorCargo() {
        // Configurar mock
        when(funcionarioRepository.buscarPorCargo("Analista")).thenReturn(Arrays.asList(funcionario1));
        
        // Executar
        List<FuncionarioDTO> resultado = funcionarioService.buscarPorCargo("Analista");
        
        // Verificar
        assertEquals(1, resultado.size(), "Deveria retornar 1 funcionário");
        assertEquals("Funcionário Um", resultado.get(0).getNome(), "O nome do funcionário está incorreto");
        verify(funcionarioRepository, times(1)).buscarPorCargo("Analista");
    }
    
    @Test
    public void testSalvar_Sucesso() {
        // Configurar DTO
        FuncionarioDTO dto = new FuncionarioDTO();
        dto.setNome("Novo Funcionário");
        dto.setCpf("11122233344");
        dto.setCargo("Desenvolvedor");
        dto.setLogin("dev1");
        dto.setSenha("senha123");
        dto.setAtivo(true);
        
        // Configurar mocks
        when(funcionarioRepository.buscarPorCpf(anyString())).thenReturn(Optional.empty());
        when(funcionarioRepository.buscarPorLogin(anyString())).thenReturn(Optional.empty());
        when(senhaService.criptografar(anyString())).thenReturn("senha_criptografada");
        doNothing().when(funcionarioRepository).salvar(any(Funcionario.class));
        
        // Executar
        FuncionarioDTO resultado = funcionarioService.salvar(dto);
        
        // Verificar
        assertNotNull(resultado, "O resultado não deveria ser nulo");
        assertEquals("Novo Funcionário", resultado.getNome(), "O nome do funcionário está incorreto");
        verify(funcionarioRepository, times(1)).buscarPorCpf(dto.getCpf());
        verify(funcionarioRepository, times(1)).buscarPorLogin(dto.getLogin());
        verify(senhaService, times(1)).criptografar(dto.getSenha());
        verify(funcionarioRepository, times(1)).salvar(any(Funcionario.class));
    }
    
    @Test
    public void testSalvar_CpfJaExistente() {
        // Configurar DTO
        FuncionarioDTO dto = new FuncionarioDTO();
        dto.setNome("Novo Funcionário");
        dto.setCpf("12345678900");  // CPF já existente
        dto.setCargo("Desenvolvedor");
        dto.setLogin("dev1");
        dto.setSenha("senha123");
        dto.setAtivo(true);
        
        // Configurar mock
        when(funcionarioRepository.buscarPorCpf("12345678900")).thenReturn(Optional.of(funcionario1));
        
        // Executar e verificar
        assertThrows(IllegalArgumentException.class, () -> {
            funcionarioService.salvar(dto);
        }, "Deveria lançar IllegalArgumentException para CPF já existente");
        
        verify(funcionarioRepository, times(1)).buscarPorCpf(dto.getCpf());
        verify(funcionarioRepository, times(0)).salvar(any(Funcionario.class));
    }
    
    @Test
    public void testSalvar_LoginJaExistente() {
        // Configurar DTO
        FuncionarioDTO dto = new FuncionarioDTO();
        dto.setNome("Novo Funcionário");
        dto.setCpf("11122233344");
        dto.setCargo("Desenvolvedor");
        dto.setLogin("analista1");  // Login já existente
        dto.setSenha("senha123");
        dto.setAtivo(true);
        
        // Configurar mocks
        when(funcionarioRepository.buscarPorCpf(anyString())).thenReturn(Optional.empty());
        when(funcionarioRepository.buscarPorLogin("analista1")).thenReturn(Optional.of(funcionario1));
        
        // Executar e verificar
        assertThrows(IllegalArgumentException.class, () -> {
            funcionarioService.salvar(dto);
        }, "Deveria lançar IllegalArgumentException para login já existente");
        
        verify(funcionarioRepository, times(1)).buscarPorCpf(dto.getCpf());
        verify(funcionarioRepository, times(1)).buscarPorLogin(dto.getLogin());
        verify(funcionarioRepository, times(0)).salvar(any(Funcionario.class));
    }
    
    @Test
    public void testAtualizar_Sucesso() {
        // Configurar DTO
        FuncionarioDTO dto = new FuncionarioDTO();
        dto.setId(1L);
        dto.setNome("Funcionário Um Atualizado");
        dto.setCpf("12345678900");  // Mesmo CPF
        dto.setCargo("Analista Senior");  // Cargo alterado
        dto.setLogin("analista1");  // Mesmo login
        dto.setSenha("nova_senha");
        dto.setAtivo(true);
        
        // Configurar mocks
        when(funcionarioRepository.buscarPorId(1L)).thenReturn(Optional.of(funcionario1));
        when(funcionarioRepository.buscarPorCpf(dto.getCpf())).thenReturn(Optional.of(funcionario1));
        when(funcionarioRepository.buscarPorLogin(dto.getLogin())).thenReturn(Optional.of(funcionario1));
        when(senhaService.criptografar("nova_senha")).thenReturn("nova_senha_criptografada");
        doNothing().when(funcionarioRepository).salvar(any(Funcionario.class));
        
        // Executar
        FuncionarioDTO resultado = funcionarioService.atualizar(1L, dto);
        
        // Verificar
        assertNotNull(resultado, "O resultado não deveria ser nulo");
        assertEquals("Funcionário Um Atualizado", resultado.getNome(), "O nome atualizado está incorreto");
        assertEquals("Analista Senior", resultado.getCargo(), "O cargo atualizado está incorreto");
        verify(funcionarioRepository, times(1)).buscarPorId(1L);
        verify(senhaService, times(1)).criptografar(dto.getSenha());
        verify(funcionarioRepository, times(1)).salvar(any(Funcionario.class));
    }
    
    @Test
    public void testAtualizar_FuncionarioNaoEncontrado() {
        // Configurar DTO
        FuncionarioDTO dto = new FuncionarioDTO();
        dto.setId(999L);
        dto.setNome("Funcionário Inexistente");
        
        // Configurar mock
        when(funcionarioRepository.buscarPorId(999L)).thenReturn(Optional.empty());
        
        // Executar e verificar
        assertThrows(NotFoundException.class, () -> {
            funcionarioService.atualizar(999L, dto);
        }, "Deveria lançar NotFoundException para funcionário inexistente");
        
        verify(funcionarioRepository, times(1)).buscarPorId(999L);
        verify(funcionarioRepository, times(0)).salvar(any(Funcionario.class));
    }
    
    @Test
    public void testAtualizarSenha_Sucesso() {
        // Configurar mocks
        when(funcionarioRepository.buscarPorId(1L)).thenReturn(Optional.of(funcionario1));
        when(senhaService.verificar("senha_atual", funcionario1.getSenha())).thenReturn(true);
        when(senhaService.criptografar("nova_senha")).thenReturn("nova_senha_criptografada");
        doNothing().when(funcionarioRepository).salvar(funcionario1);
        
        // Executar
        funcionarioService.atualizarSenha(1L, "senha_atual", "nova_senha");
        
        // Verificar
        verify(funcionarioRepository, times(1)).buscarPorId(1L);
        verify(senhaService, times(1)).verificar("senha_atual", funcionario1.getSenha());
        verify(senhaService, times(1)).criptografar("nova_senha");
        verify(funcionarioRepository, times(1)).salvar(funcionario1);
    }
    
    @Test
    public void testAtualizarSenha_FuncionarioNaoEncontrado() {
        // Configurar mock
        when(funcionarioRepository.buscarPorId(999L)).thenReturn(Optional.empty());
        
        // Executar e verificar
        assertThrows(NotFoundException.class, () -> {
            funcionarioService.atualizarSenha(999L, "senha_atual", "nova_senha");
        }, "Deveria lançar NotFoundException para funcionário inexistente");
        
        verify(funcionarioRepository, times(1)).buscarPorId(999L);
        verify(senhaService, times(0)).verificar(anyString(), anyString());
        verify(senhaService, times(0)).criptografar(anyString());
    }
    
    @Test
    public void testAtualizarSenha_SenhaAtualIncorreta() {
        // Configurar mocks
        when(funcionarioRepository.buscarPorId(1L)).thenReturn(Optional.of(funcionario1));
        when(senhaService.verificar("senha_incorreta", funcionario1.getSenha())).thenReturn(false);
        
        // Executar e verificar
        assertThrows(IllegalArgumentException.class, () -> {
            funcionarioService.atualizarSenha(1L, "senha_incorreta", "nova_senha");
        }, "Deveria lançar IllegalArgumentException para senha atual incorreta");
        
        verify(funcionarioRepository, times(1)).buscarPorId(1L);
        verify(senhaService, times(1)).verificar("senha_incorreta", funcionario1.getSenha());
        verify(senhaService, times(0)).criptografar(anyString());
    }
    
    @Test
    public void testAlterarStatus_Sucesso() {
        // Configurar mock
        when(funcionarioRepository.buscarPorId(1L)).thenReturn(Optional.of(funcionario1));
        doNothing().when(funcionarioRepository).salvar(funcionario1);
        
        // Executar
        FuncionarioDTO resultado = funcionarioService.alterarStatus(1L, false);
        
        // Verificar
        assertNotNull(resultado, "O resultado não deveria ser nulo");
        assertFalse(resultado.isAtivo(), "O funcionário deveria estar inativo");
        verify(funcionarioRepository, times(1)).buscarPorId(1L);
        verify(funcionarioRepository, times(1)).salvar(funcionario1);
    }
    
    @Test
    public void testAlterarStatus_FuncionarioNaoEncontrado() {
        // Configurar mock
        when(funcionarioRepository.buscarPorId(999L)).thenReturn(Optional.empty());
        
        // Executar e verificar
        assertThrows(NotFoundException.class, () -> {
            funcionarioService.alterarStatus(999L, false);
        }, "Deveria lançar NotFoundException para funcionário inexistente");
        
        verify(funcionarioRepository, times(1)).buscarPorId(999L);
        verify(funcionarioRepository, times(0)).salvar(any(Funcionario.class));
    }
    
    @Test
    public void testExcluir_Sucesso() {
        // Configurar mocks
        when(funcionarioRepository.buscarPorId(1L)).thenReturn(Optional.of(funcionario1));
        when(funcionarioRepository.deletar(1L)).thenReturn(true);
        
        // Executar
        boolean resultado = funcionarioService.excluir(1L);
        
        // Verificar
        assertTrue(resultado, "A exclusão deveria retornar true");
        verify(funcionarioRepository, times(1)).buscarPorId(1L);
        verify(funcionarioRepository, times(1)).deletar(1L);
    }
    
    @Test
    public void testExcluir_FuncionarioNaoEncontrado() {
        // Configurar mock
        when(funcionarioRepository.buscarPorId(999L)).thenReturn(Optional.empty());
        
        // Executar e verificar
        assertThrows(NotFoundException.class, () -> {
            funcionarioService.excluir(999L);
        }, "Deveria lançar NotFoundException para funcionário inexistente");
        
        verify(funcionarioRepository, times(1)).buscarPorId(999L);
        verify(funcionarioRepository, times(0)).deletar(anyLong());
    }
}