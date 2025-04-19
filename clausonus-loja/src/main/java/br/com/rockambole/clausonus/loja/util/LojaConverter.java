package br.com.rockambole.clausonus.loja.util;

import br.com.rockambole.clausonus.loja.dto.LojaDTO;
import br.com.rockambole.clausonus.loja.entity.Loja;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Utilitário para converter entre entidade Loja e LojaDTO
 */
@ApplicationScoped
public class LojaConverter {
    
    /**
     * Converte uma entidade Loja para LojaDTO
     * 
     * @param loja Entidade a ser convertida
     * @return DTO correspondente
     */
    public LojaDTO toDto(Loja loja) {
        if (loja == null) {
            return null;
        }
        
        return new LojaDTO(
            loja.id,
            loja.getNome(),
            loja.getEndereco(),
            loja.getCnpj(),
            loja.getTelefone()
        );
    }
    
    /**
     * Converte um LojaDTO para entidade Loja
     * 
     * @param dto DTO a ser convertido
     * @return Entidade correspondente
     */
    public Loja toEntity(LojaDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Loja loja = new Loja();
        if (dto.getId() != null) {
            loja.id = dto.getId();
        }
        
        loja.setNome(dto.getNome());
        loja.setEndereco(dto.getEndereco());
        loja.setCnpj(dto.getCnpj());
        loja.setTelefone(dto.getTelefone());
        
        return loja;
    }
    
    /**
     * Atualiza uma entidade Loja existente com dados de um DTO
     * 
     * @param loja Entidade a ser atualizada
     * @param dto DTO com os novos dados
     */
    public void updateEntityFromDto(Loja loja, LojaDTO dto) {
        if (loja == null || dto == null) {
            return;
        }
        
        loja.setNome(dto.getNome());
        loja.setEndereco(dto.getEndereco());
        loja.setCnpj(dto.getCnpj());
        loja.setTelefone(dto.getTelefone());
    }
}