package br.com.rockambole.clausonus.loja.mapper;

import java.util.List;
import java.util.stream.Collectors;

import br.com.rockambole.clausonus.loja.dto.LojaDTO;
import br.com.rockambole.clausonus.loja.entity.Loja;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LojaMapper {
    
    public LojaDTO toDTO(Loja loja) {
        if (loja == null) {
            return null;
        }
        
        return new LojaDTO(
            loja.getId(),
            loja.getNome(),
            loja.getEndereco(),
            loja.getCnpj(),
            loja.getTelefone()
        );
    }
    
    public List<LojaDTO> toDTOList(List<Loja> lojas) {
        if (lojas == null) {
            return List.of();
        }
        
        return lojas.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    public Loja toEntity(LojaDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Loja loja = new Loja();
        loja.setId(dto.getId());
        loja.setNome(dto.getNome());
        loja.setEndereco(dto.getEndereco());
        loja.setCnpj(dto.getCnpj());
        loja.setTelefone(dto.getTelefone());
        
        return loja;
    }
    
    public void updateEntityFromDTO(LojaDTO dto, Loja loja) {
        if (dto == null || loja == null) {
            return;
        }
        
        loja.setNome(dto.getNome());
        loja.setEndereco(dto.getEndereco());
        loja.setCnpj(dto.getCnpj());
        loja.setTelefone(dto.getTelefone());
    }
}
