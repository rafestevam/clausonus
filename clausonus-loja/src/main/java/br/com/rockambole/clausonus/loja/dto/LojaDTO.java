package br.com.rockambole.clausonus.loja.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para transferência de dados da entidade Loja
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LojaDTO {
    
    private Long id;
    
    @NotBlank(message = "O nome é obrigatório")
    @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
    private String nome;
    
    @NotBlank(message = "O endereço é obrigatório")
    @Size(max = 200, message = "O endereço deve ter no máximo 200 caracteres")
    private String endereco;
    
    @NotBlank(message = "O CNPJ é obrigatório")
    @Size(min = 14, max = 18, message = "CNPJ inválido")
    private String cnpj;
    
    @Size(max = 20, message = "O telefone deve ter no máximo 20 caracteres")
    private String telefone;
}