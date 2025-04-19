package br.com.rockambole.clausonus.loja.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import br.com.rockambole.clausonus.loja.dto.LojaDTO;
import br.com.rockambole.clausonus.loja.entity.Loja;

/**
 * Interface para mapeamento entre a entidade Loja e seu DTO
 */
@Mapper(
    componentModel = "cdi",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface LojaMapper {
    
    /**
     * Converte uma entidade Loja para LojaDTO
     * 
     * @param loja Entidade a ser convertida
     * @return LojaDTO
     */
    LojaDTO toDto(Loja loja);
    
    /**
     * Converte um LojaDTO para a entidade Loja
     * 
     * @param lojaDTO DTO a ser convertido
     * @return Entidade Loja
     */
    Loja toEntity(LojaDTO lojaDTO);
}