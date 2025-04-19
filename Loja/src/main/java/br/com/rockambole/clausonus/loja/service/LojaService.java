package br.com.rockambole.clausonus.loja.service;

import java.util.List;
import java.util.Optional;

import br.com.rockambole.clausonus.loja.dto.LojaDTO;
import br.com.rockambole.clausonus.loja.entity.Loja;
import br.com.rockambole.clausonus.loja.exception.NegocioException;
import br.com.rockambole.clausonus.loja.mapper.LojaMapper;
import br.com.rockambole.clausonus.loja.repository.LojaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class LojaService {

    @Inject
    LojaRepository repository;
    
    @Inject
    LojaMapper mapper;
    
    public List<LojaDTO> listarTodas() {
        List<Loja> lojas = repository.listarTodas();
        return mapper.toDTOList(lojas);
    }
    
    public LojaDTO buscarPorId(Long id) {
        Loja loja = repository.buscarPorId(id)
                .orElseThrow(() -> new NegocioException("Loja não encontrada com o ID: " + id));
        return mapper.toDTO(loja);
    }
    
    public List<LojaDTO> buscarPorNome(String nome) {
        List<Loja> lojas = repository.buscarPorNome(nome);
        return mapper.toDTOList(lojas);
    }
    
    @Transactional
    public LojaDTO salvar(LojaDTO lojaDTO) {
        // Verificar se já existe uma loja com o mesmo CNPJ
        if (lojaDTO.getId() == null) {
            Optional<Loja> existente = repository.buscarPorCnpj(lojaDTO.getCnpj());
            if (existente.isPresent()) {
                throw new NegocioException("Já existe uma loja cadastrada com o CNPJ: " + lojaDTO.getCnpj());
            }
        }
        
        Loja loja = mapper.toEntity(lojaDTO);
        repository.salvar(loja);
        return mapper.toDTO(loja);
    }
    
    @Transactional
    public LojaDTO atualizar(Long id, LojaDTO lojaDTO) {
        Loja loja = repository.buscarPorId(id)
                .orElseThrow(() -> new NegocioException("Loja não encontrada com o ID: " + id));
        
        // Verificar se o novo CNPJ já está sendo usado por outra loja
        if (!loja.getCnpj().equals(lojaDTO.getCnpj())) {
            Optional<Loja> existente = repository.buscarPorCnpj(lojaDTO.getCnpj());
            if (existente.isPresent() && !existente.get().getId().equals(id)) {
                throw new NegocioException("Já existe uma loja cadastrada com o CNPJ: " + lojaDTO.getCnpj());
            }
        }
        
        mapper.updateEntityFromDTO(lojaDTO, loja);
        repository.salvar(loja);
        return mapper.toDTO(loja);
    }
    
    @Transactional
    public void deletar(Long id) {
        boolean deletado = repository.deletar(id);
        if (!deletado) {
            throw new NegocioException("Loja não encontrada com o ID: " + id);
        }
    }
}
