package br.com.rockambole.clausonus.loja.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

import br.com.rockambole.clausonus.loja.dto.LojaDTO;
import br.com.rockambole.clausonus.loja.entity.Loja;
import br.com.rockambole.clausonus.loja.mapper.LojaMapper;
import br.com.rockambole.clausonus.loja.repository.LojaRepository;

/**
 * Testes unitários para o LojaService
 */
@ExtendWith(MockitoExtension.class)
public class LojaServiceTest {

    @Mock
    private LojaRepository lojaRepository;
    
    @Mock
    private LojaMapper lojaMapper;
    
    @InjectMocks
    private LojaService lojaService;
    
    private Loja loja1;
    private Loja loja2;
    private LojaDTO lojaDTO1;
    private LojaDTO lojaDTO2;
    
    @BeforeEach
    public void setup() {
        // Configuração das entidades de teste
        loja1 = new Loja("Loja Teste 1", "Endereço Teste 1", "11111111111111", "11111111");
        loja1.setId(1L);
        
        loja2 = new Loja("Loja Teste 2", "Endereço Teste 2", "22222222222222", "22222222");
        loja2.setId(2L);
        
        // Configuração dos DTOs de teste
        lojaDTO1 = new LojaDTO();
        lojaDTO1.setId(1L);
        lojaDTO1.setNome("Loja Teste 1");
        lojaDTO1.setEndereco("Endereço Teste 1");
        lojaDTO1.setCnpj("11111111111111");
        lojaDTO1.setTelefone("11111111");
        
        lojaDTO2 = new LojaDTO();
        lojaDTO2.setId(2L);
        lojaDTO2.setNome("Loja Teste 2");
        lojaDTO2.setEndereco("Endereço Teste 2");
        lojaDTO2.setCnpj("22222222222222");
        lojaDTO2.setTelefone("22222222");
    }
    
    @Test
    public void testListarTodas() {
        // Configuração do mock
        when(lojaRepository.listarTodas()).thenReturn(Arrays.asList(loja1, loja2));
        when(lojaMapper.toDto(loja1)).thenReturn(lojaDTO1);
        when(lojaMapper.toDto(loja2)).thenReturn(lojaDTO2);
        
        // Execução do método
        List<LojaDTO> resultado = lojaService.listarTodas();
        
        // Verificações
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Loja Teste 1", resultado.get(0).getNome());
        assertEquals("Loja Teste 2", resultado.get(1).getNome());
        verify(lojaRepository, times(1)).listarTodas();
    }
    
    @Test
    public void testBuscarPorIdExistente() {
        // Configuração do mock
        when(lojaRepository.buscarPorId(1L)).thenReturn(Optional.of(loja1));
        when(lojaMapper.toDto(loja1)).thenReturn(lojaDTO1);
        
        // Execução do método
        LojaDTO resultado = lojaService.buscarPorId(1L);
        
        // Verificações
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Loja Teste 1", resultado.getNome());
        verify(lojaRepository, times(1)).buscarPorId(1L);
    }
    
    @Test
    public void testBuscarPorIdInexistente() {
        // Configuração do mock
        when(lojaRepository.buscarPorId(999L)).thenReturn(Optional.empty());
        
        // Execução do método e verificação da exceção
        assertThrows(NotFoundException.class, () -> {
            lojaService.buscarPorId(999L);
        });
        
        verify(lojaRepository, times(1)).buscarPorId(999L);
    }
    
    @Test
    public void testBuscarPorNome() {
        // Configuração do mock
        when(lojaRepository.buscarPorNome("Teste")).thenReturn(Arrays.asList(loja1, loja2));
        when(lojaMapper.toDto(loja1)).thenReturn(lojaDTO1);
        when(lojaMapper.toDto(loja2)).thenReturn(lojaDTO2);
        
        // Execução do método
        List<LojaDTO> resultado = lojaService.buscarPorNome("Teste");
        
        // Verificações
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(lojaRepository, times(1)).buscarPorNome("Teste");
    }
    
    @Test
    public void testSalvarNovo() {
        // Configuração do mock
        when(lojaRepository.buscarPorCnpj(anyString())).thenReturn(Optional.empty());
        when(lojaMapper.toEntity(lojaDTO1)).thenReturn(loja1);
        when(lojaMapper.toDto(loja1)).thenReturn(lojaDTO1);
        
        // Execução do método
        LojaDTO resultado = lojaService.salvar(lojaDTO1);
        
        // Verificações
        assertNotNull(resultado);
        assertEquals("Loja Teste 1", resultado.getNome());
        verify(lojaRepository, times(1)).buscarPorCnpj(lojaDTO1.getCnpj());
        verify(lojaRepository, times(1)).salvar(loja1);
    }
    
    @Test
    public void testSalvarCnpjExistente() {
        // Configuração do mock
        when(lojaRepository.buscarPorCnpj(lojaDTO1.getCnpj())).thenReturn(Optional.of(loja1));
        
        // Execução do método e verificação da exceção
        assertThrows(IllegalArgumentException.class, () -> {
            lojaService.salvar(lojaDTO1);
        });
        
        verify(lojaRepository, times(1)).buscarPorCnpj(lojaDTO1.getCnpj());
        verify(lojaRepository, times(0)).salvar(any());
    }
    
    @Test
    public void testAtualizarExistente() {
        // Loja para atualização
        LojaDTO lojaAtualizadaDTO = new LojaDTO();
        lojaAtualizadaDTO.setId(1L);
        lojaAtualizadaDTO.setNome("Loja Atualizada");
        lojaAtualizadaDTO.setEndereco("Endereço Atualizado");
        lojaAtualizadaDTO.setCnpj("11111111111111"); // Mesmo CNPJ
        lojaAtualizadaDTO.setTelefone("99999999"); // Novo telefone
        
        // Configuração do mock
        when(lojaRepository.buscarPorId(1L)).thenReturn(Optional.of(loja1));
        when(lojaMapper.toDto(any(Loja.class))).thenReturn(lojaAtualizadaDTO);
        
        // Execução do método
        LojaDTO resultado = lojaService.atualizar(1L, lojaAtualizadaDTO);
        
        // Verificações
        assertNotNull(resultado);
        assertEquals("Loja Atualizada", resultado.getNome());
        verify(lojaRepository, times(1)).buscarPorId(1L);
        verify(lojaRepository, times(1)).salvar(any(Loja.class));
    }
    
    @Test
    public void testAtualizarNovoCnpj() {
        // Loja para atualização com novo CNPJ
        LojaDTO lojaAtualizadaDTO = new LojaDTO();
        lojaAtualizadaDTO.setId(1L);
        lojaAtualizadaDTO.setNome("Loja Atualizada");
        lojaAtualizadaDTO.setEndereco("Endereço Atualizado");
        lojaAtualizadaDTO.setCnpj("33333333333333"); // Novo CNPJ
        lojaAtualizadaDTO.setTelefone("99999999");
        
        // Configuração do mock
        when(lojaRepository.buscarPorId(1L)).thenReturn(Optional.of(loja1));
        when(lojaRepository.buscarPorCnpj("33333333333333")).thenReturn(Optional.empty());
        when(lojaMapper.toDto(any(Loja.class))).thenReturn(lojaAtualizadaDTO);
        
        // Execução do método
        LojaDTO resultado = lojaService.atualizar(1L, lojaAtualizadaDTO);
        
        // Verificações
        assertNotNull(resultado);
        assertEquals("Loja Atualizada", resultado.getNome());
        verify(lojaRepository, times(1)).buscarPorId(1L);
        verify(lojaRepository, times(1)).buscarPorCnpj("33333333333333");
        verify(lojaRepository, times(1)).salvar(any(Loja.class));
    }
    
    @Test
    public void testAtualizarCnpjExistente() {
        // Loja para atualização com CNPJ de outra loja
        LojaDTO lojaAtualizadaDTO = new LojaDTO();
        lojaAtualizadaDTO.setId(1L);
        lojaAtualizadaDTO.setNome("Loja Atualizada");
        lojaAtualizadaDTO.setEndereco("Endereço Atualizado");
        lojaAtualizadaDTO.setCnpj("22222222222222"); // CNPJ da loja2
        lojaAtualizadaDTO.setTelefone("99999999");
        
        // Configuração do mock
        when(lojaRepository.buscarPorId(1L)).thenReturn(Optional.of(loja1));
        when(lojaRepository.buscarPorCnpj("22222222222222")).thenReturn(Optional.of(loja2));
        
        // Execução do método e verificação da exceção
        assertThrows(IllegalArgumentException.class, () -> {
            lojaService.atualizar(1L, lojaAtualizadaDTO);
        });
        
        verify(lojaRepository, times(1)).buscarPorId(1L);
        verify(lojaRepository, times(1)).buscarPorCnpj("22222222222222");
        verify(lojaRepository, times(0)).salvar(any(Loja.class));
    }
    
    @Test
    public void testExcluirExistente() {
        // Configuração do mock
        when(lojaRepository.buscarPorId(1L)).thenReturn(Optional.of(loja1));
        when(lojaRepository.deletar(1L)).thenReturn(true);
        
        // Execução do método
        boolean resultado = lojaService.excluir(1L);
        
        // Verificações
        assertTrue(resultado);
        verify(lojaRepository, times(1)).buscarPorId(1L);
        verify(lojaRepository, times(1)).deletar(1L);
    }
    
    @Test
    public void testExcluirInexistente() {
        // Configuração do mock
        when(lojaRepository.buscarPorId(999L)).thenReturn(Optional.empty());
        
        // Execução do método e verificação da exceção
        assertThrows(NotFoundException.class, () -> {
            lojaService.excluir(999L);
        });
        
        verify(lojaRepository, times(1)).buscarPorId(999L);
        verify(lojaRepository, times(0)).deletar(anyLong());
    }
}