package br.com.rockambole.clausonus.funcionario.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import jakarta.ws.rs.NotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.rockambole.clausonus.funcionario.dto.FuncionarioDTO;
import br.com.rockambole.clausonus.funcionario.entity.Funcionario;
import br.com.rockambole.clausonus.funcionario.mapper.FuncionarioMapper;
import br.com.rockambole.clausonus.funcionario.repository.FuncionarioRepository;

@ExtendWith(MockitoExtension.class)
public class FuncionarioServiceTest {

    @Mock
    private FuncionarioRepository funcionarioRepository;
    
    @Mock
    private FuncionarioMapper funcionarioMapper;
    
    @Mock
    private SenhaService senhaService;
    
    @InjectMocks
    private FuncionarioService funcionarioService;
    
    private Funcionario funcionario;
    private FuncionarioDTO funcionarioDTO;
    
    @BeforeEach
    void setUp() {
        // Configuração de dados para testes
        funcionario = new Funcionario();
        funcionario.setId(1L);
        funcionario.setNome("Funcionário Teste");
        funcionario.setCpf("12345678900");
        funcionario.setCargo("Tester");
        funcionario.setLogin("testuser");
        funcionario.setSenha("hash_senha");
        funcionario.setAtivo(true);
        
        funcionarioDTO = new FuncionarioDTO();
        funcionarioDTO.setId(1L);
        funcionarioDTO.setNome("Funcionário Teste");
        funcionarioDTO.setCpf("12345678900");
        funcionarioDTO.setCargo("Tester");
        funcionarioDTO.setLogin("testuser");
        funcionarioDTO.setSenha("senha123");
        funcionarioDTO.setAtivo(true);
    }
    
    @Test
    void testListarTodos() {
        // Arrange
        List<Funcionario> funcionarios = Arrays.asList(funcionario);
        when(funcionarioRepository.listarTodos()).thenReturn(funcionarios);
        when(funcionarioMapper.toDto(funcionario)).thenReturn(funcionarioDTO);
        
        // Act
        List<FuncionarioDTO> resultado = funcionarioService.listarTodos();
        
        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(funcionarioDTO, resultado.get(0));
        verify(funcionarioRepository).listarTodos();
        verify(funcionarioMapper).toDto(funcionario);
    }
    
    @Test
    void testBuscarPorId_Sucesso() {
        // Arrange
        when(funcionarioRepository.buscarPorId(1L)).thenReturn(Optional.of(funcionario));
        when(funcionarioMapper.toDto(funcionario)).thenReturn(funcionarioDTO);
        
        // Act
        FuncionarioDTO resultado = funcionarioService.buscarPorId(1L);
        
        // Assert
        assertNotNull(resultado);
        assertEquals(funcionarioDTO, resultado);
        verify(funcionarioRepository).buscarPorId(1L);
        verify(funcionarioMapper).toDto(funcionario);
    }
    
    @Test
    void testBuscarPorId_NaoEncontrado() {
        // Arrange
        when(funcionarioRepository.buscarPorId(99L)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            funcionarioService.buscarPorId(99L);
        });
        verify(funcionarioRepository).buscarPorId(99L);
    }
    
    @Test
    void testSalvar_Sucesso() {
        // Arrange
        when(funcionarioRepository.buscarPorCpf(funcionarioDTO.getCpf())).thenReturn(Optional.empty());
        when(funcionarioRepository.buscarPorLogin(funcionarioDTO.getLogin())).thenReturn(Optional.empty());
        when(funcionarioMapper.toEntity(funcionarioDTO)).thenReturn(funcionario);
        when(senhaService.criptografar(funcionarioDTO.getSenha())).thenReturn("hash_senha");
        when(funcionarioMapper.toDto(funcionario)).thenReturn(funcionarioDTO);
        
        // Act
        FuncionarioDTO resultado = funcionarioService.salvar(funcionarioDTO);
        
        // Assert
        assertNotNull(resultado);
        assertEquals(funcionarioDTO, resultado);
        verify(funcionarioRepository).buscarPorCpf(funcionarioDTO.getCpf());
        verify(funcionarioRepository).buscarPorLogin(funcionarioDTO.getLogin());
        verify(funcionarioMapper).toEntity(funcionarioDTO);
        verify(senhaService).criptografar(funcionarioDTO.getSenha());
        verify(funcionarioRepository).salvar(funcionario);
        verify(funcionarioMapper).toDto(funcionario);
    }
    
    @Test
    void testSalvar_CpfJaExiste() {
        // Arrange
        when(funcionarioRepository.buscarPorCpf(funcionarioDTO.getCpf())).thenReturn(Optional.of(funcionario));
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            funcionarioService.salvar(funcionarioDTO);
        });
        verify(funcionarioRepository).buscarPorCpf(funcionarioDTO.getCpf());
        verify(funcionarioRepository, never()).salvar(any());
    }
    
    @Test
    void testSalvar_LoginJaExiste() {
        // Arrange
        when(funcionarioRepository.buscarPorCpf(funcionarioDTO.getCpf())).thenReturn(Optional.empty());
        when(funcionarioRepository.buscarPorLogin(funcionarioDTO.getLogin())).thenReturn(Optional.of(funcionario));
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            funcionarioService.salvar(funcionarioDTO);
        });
        verify(funcionarioRepository).buscarPorCpf(funcionarioDTO.getCpf());
        verify(funcionarioRepository).buscarPorLogin(funcionarioDTO.getLogin());
        verify(funcionarioRepository, never()).salvar(any());
    }
    
    @Test
    void testAtualizar_Sucesso() {
        // Arrange
        when(funcionarioRepository.buscarPorId(1L)).thenReturn(Optional.of(funcionario));
        when(funcionarioMapper.toDto(funcionario)).thenReturn(funcionarioDTO);
        
        // Act
        FuncionarioDTO resultado = funcionarioService.atualizar(1L, funcionarioDTO);
        
        // Assert
        assertNotNull(resultado);
        assertEquals(funcionarioDTO, resultado);
        assertEquals("Funcionário Teste", funcionario.getNome());
        assertEquals("12345678900", funcionario.getCpf());
        assertEquals("Tester", funcionario.getCargo());
        assertEquals("testuser", funcionario.getLogin());
        assertEquals(true, funcionario.isAtivo());
        verify(funcionarioRepository).buscarPorId(1L);
        verify(funcionarioRepository).salvar(funcionario);
        verify(funcionarioMapper).toDto(funcionario);
    }
    
    @Test
    void testAtualizar_NaoEncontrado() {
        // Arrange
        when(funcionarioRepository.buscarPorId(99L)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            funcionarioService.atualizar(99L, funcionarioDTO);
        });
        verify(funcionarioRepository).buscarPorId(99L);
        verify(funcionarioRepository, never()).salvar(any());
    }
    
    @Test
    void testExcluir_Sucesso() {
        // Arrange
        when(funcionarioRepository.buscarPorId(1L)).thenReturn(Optional.of(funcionario));
        when(funcionarioRepository.deletar(1L)).thenReturn(true);
        
        // Act
        boolean resultado = funcionarioService.excluir(1L);
        
        // Assert
        assertTrue(resultado);
        verify(funcionarioRepository).buscarPorId(1L);
        verify(funcionarioRepository).deletar(1L);
    }
    
    @Test
    void testExcluir_NaoEncontrado() {
        // Arrange
        when(funcionarioRepository.buscarPorId(99L)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            funcionarioService.excluir(99L);
        });
        verify(funcionarioRepository).buscarPorId(99L);
        verify(funcionarioRepository, never()).deletar(anyLong());
    }
}