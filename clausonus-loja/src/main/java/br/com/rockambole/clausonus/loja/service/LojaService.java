package br.com.rockambole.clausonus.loja.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import br.com.rockambole.clausonus.loja.dto.LojaDTO;
import br.com.rockambole.clausonus.loja.entity.Loja;
import br.com.rockambole.clausonus.loja.util.LojaConverter;
import lombok.extern.slf4j.Slf4j;

/**
 * Serviço para gerenciamento de Lojas
 */
@Slf4j
@ApplicationScoped
public class LojaService {
    
    private final LojaConverter lojaConverter;
    
    @Inject
    public LojaService(LojaConverter lojaConverter) {
        this.lojaConverter = lojaConverter;
    }
    
    /**
     * Lista todas as lojas cadastradas
     * 
     * @return Lista de LojaDTO
     */
    public List<LojaDTO> listarTodas() {
        log.info("Listando todas as lojas");
        return Loja.listarTodas().stream()
                .map(lojaConverter::toDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Busca uma loja pelo seu ID
     * 
     * @param id ID da loja
     * @return LojaDTO
     * @throws NotFoundException se a loja não for encontrada
     */
    public LojaDTO buscarPorId(Long id) {
        log.info("Buscando loja pelo ID: {}", id);
        return Loja.buscarPorId(id)
                .map(lojaConverter::toDto)
                .orElseThrow(() -> new NotFoundException("Loja não encontrada com o ID: " + id));
    }
    
    /**
     * Busca lojas pelo nome (busca parcial)
     * 
     * @param nome Nome ou parte do nome da loja
     * @return Lista de LojaDTO
     */
    public List<LojaDTO> buscarPorNome(String nome) {
        log.info("Buscando lojas pelo nome: {}", nome);
        return Loja.buscarPorNome(nome).stream()
                .map(lojaConverter::toDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Busca uma loja pelo CNPJ
     * 
     * @param cnpj CNPJ da loja
     * @return LojaDTO ou null se não encontrada
     */
    public Optional<LojaDTO> buscarPorCnpj(String cnpj) {
        log.info("Buscando loja pelo CNPJ: {}", cnpj);
        return Loja.buscarPorCnpj(cnpj)
                .map(lojaConverter::toDto);
    }
    
    /**
     * Salva uma nova loja
     * 
     * @param lojaDTO Dados da loja
     * @return LojaDTO com o ID gerado
     */
    @Transactional
    public LojaDTO salvar(LojaDTO lojaDTO) {
        log.info("Salvando loja: {}", lojaDTO);
        
        // Verifica se já existe loja com o mesmo CNPJ
        Optional<Loja> existente = Loja.buscarPorCnpj(lojaDTO.getCnpj());
        if (existente.isPresent()) {
            throw new IllegalArgumentException("Já existe uma loja cadastrada com o CNPJ: " + lojaDTO.getCnpj());
        }
        
        Loja loja = lojaConverter.toEntity(lojaDTO);
        loja.persist();
        
        return lojaConverter.toDto(loja);
    }
    
    /**
     * Atualiza os dados de uma loja existente
     * 
     * @param id ID da loja a ser atualizada
     * @param lojaDTO Novos dados da loja
     * @return LojaDTO atualizada
     * @throws NotFoundException se a loja não for encontrada
     */
    @Transactional
    public LojaDTO atualizar(Long id, LojaDTO lojaDTO) {
        log.info("Atualizando loja com ID {}: {}", id, lojaDTO);
        
        Loja loja = Loja.buscarPorId(id)
                .orElseThrow(() -> new NotFoundException("Loja não encontrada com o ID: " + id));
        
        // Verifica se o CNPJ já está sendo usado por outra loja
        if (!loja.getCnpj().equals(lojaDTO.getCnpj())) {
            Optional<Loja> existente = Loja.buscarPorCnpj(lojaDTO.getCnpj());
            if (existente.isPresent() && !existente.get().id.equals(id)) {
                throw new IllegalArgumentException("Já existe uma loja cadastrada com o CNPJ: " + lojaDTO.getCnpj());
            }
        }
        
        // Atualiza os campos
        lojaConverter.updateEntityFromDto(loja, lojaDTO);
        loja.persist();
        
        return lojaConverter.toDto(loja);
    }
    
    /**
     * Exclui uma loja pelo seu ID
     * 
     * @param id ID da loja a ser excluída
     * @return true se a loja foi excluída com sucesso
     * @throws NotFoundException se a loja não for encontrada
     */
    @Transactional
    public boolean excluir(Long id) {
        log.info("Excluindo loja com ID: {}", id);
        
        if (!Loja.buscarPorId(id).isPresent()) {
            throw new NotFoundException("Loja não encontrada com o ID: " + id);
        }
        
        return Loja.deleteById(id);
    }
}