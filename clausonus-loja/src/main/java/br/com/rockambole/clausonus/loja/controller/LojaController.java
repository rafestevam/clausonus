package br.com.rockambole.clausonus.loja.controller;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import br.com.rockambole.clausonus.loja.dto.LojaDTO;
import br.com.rockambole.clausonus.loja.service.LojaService;
import lombok.extern.slf4j.Slf4j;

/**
 * Endpoints REST para gerenciamento de Lojas
 */
@Slf4j
@Path("/lojas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Loja", description = "Operações relacionadas às lojas")
public class LojaController {
    
    private final LojaService lojaService;
    
    @Inject
    public LojaController(LojaService lojaService) {
        this.lojaService = lojaService;
    }
    
    /**
     * Lista todas as lojas cadastradas
     * 
     * @return Lista de lojas
     */
    @GET
    @Operation(summary = "Lista todas as lojas", description = "Retorna uma lista com todas as lojas cadastradas")
    @APIResponse(
        responseCode = "200",
        description = "Lista de lojas",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = LojaDTO.class, type = SchemaType.ARRAY))
    )
    public List<LojaDTO> listarTodas() {
        log.info("Requisição para listar todas as lojas");
        return lojaService.listarTodas();
    }
    
    /**
     * Busca uma loja pelo ID
     * 
     * @param id ID da loja
     * @return Loja encontrada
     */
    @GET
    @Path("/{id}")
    @Operation(summary = "Busca loja por ID", description = "Retorna uma loja específica pelo seu ID")
    @APIResponse(
        responseCode = "200",
        description = "Loja encontrada",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = LojaDTO.class))
    )
    @APIResponse(responseCode = "404", description = "Loja não encontrada")
    public LojaDTO buscarPorId(
            @Parameter(description = "ID da loja", required = true)
            @PathParam("id") Long id) {
        log.info("Requisição para buscar loja com ID: {}", id);
        return lojaService.buscarPorId(id);
    }
    
    /**
     * Busca lojas pelo nome (busca parcial)
     * 
     * @param nome Nome ou parte do nome da loja
     * @return Lista de lojas que contêm o nome informado
     */
    @GET
    @Path("/busca")
    @Operation(summary = "Busca lojas por nome", description = "Retorna uma lista de lojas cujo nome contenha o texto informado")
    @APIResponse(
        responseCode = "200",
        description = "Lista de lojas",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = LojaDTO.class, type = SchemaType.ARRAY))
    )
    public List<LojaDTO> buscarPorNome(
            @Parameter(description = "Nome ou parte do nome da loja", required = true)
            @QueryParam("nome") String nome) {
        log.info("Requisição para buscar lojas pelo nome: {}", nome);
        return lojaService.buscarPorNome(nome);
    }
    
    /**
     * Cadastra uma nova loja
     * 
     * @param lojaDTO Dados da loja
     * @param uriInfo Informações da URI
     * @return Resposta com a loja cadastrada e URI de acesso
     */
    @POST
    @Operation(summary = "Cadastra uma nova loja", description = "Cadastra uma nova loja no sistema")
    @APIResponse(
        responseCode = "201",
        description = "Loja cadastrada com sucesso",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = LojaDTO.class))
    )
    @APIResponse(responseCode = "400", description = "Dados inválidos ou CNPJ já cadastrado")
    public Response salvar(
            @Parameter(description = "Dados da loja", required = true)
            @Valid LojaDTO lojaDTO,
            @Context UriInfo uriInfo) {
        log.info("Requisição para cadastrar nova loja: {}", lojaDTO);
        
        LojaDTO lojaSalva = lojaService.salvar(lojaDTO);
        
        URI location = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(lojaSalva.getId()))
                .build();
                
        return Response.created(location).entity(lojaSalva).build();
    }
    
    /**
     * Atualiza os dados de uma loja existente
     * 
     * @param id ID da loja a ser atualizada
     * @param lojaDTO Novos dados da loja
     * @return Loja atualizada
     */
    @PUT
    @Path("/{id}")
    @Operation(summary = "Atualiza uma loja", description = "Atualiza os dados de uma loja existente")
    @APIResponse(
        responseCode = "200",
        description = "Loja atualizada com sucesso",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = LojaDTO.class))
    )
    @APIResponse(responseCode = "404", description = "Loja não encontrada")
    @APIResponse(responseCode = "400", description = "Dados inválidos ou CNPJ já cadastrado")
    public LojaDTO atualizar(
            @Parameter(description = "ID da loja", required = true)
            @PathParam("id") Long id,
            @Parameter(description = "Novos dados da loja", required = true)
            @Valid LojaDTO lojaDTO) {
        log.info("Requisição para atualizar loja com ID {}: {}", id, lojaDTO);
        return lojaService.atualizar(id, lojaDTO);
    }
    
    /**
     * Exclui uma loja
     * 
     * @param id ID da loja a ser excluída
     * @return Resposta de sucesso sem conteúdo
     */
    @DELETE
    @Path("/{id}")
    @Operation(summary = "Exclui uma loja", description = "Remove uma loja do sistema")
    @APIResponse(responseCode = "204", description = "Loja excluída com sucesso")
    @APIResponse(responseCode = "404", description = "Loja não encontrada")
    public Response excluir(
            @Parameter(description = "ID da loja", required = true)
            @PathParam("id") Long id) {
        log.info("Requisição para excluir loja com ID: {}", id);
        
        lojaService.excluir(id);
        
        return Response.noContent().build();
    }
    
    /**
     * Verifica se existe uma loja com o CNPJ informado
     * 
     * @param cnpj CNPJ da loja
     * @return Resposta indicando se o CNPJ já está cadastrado
     */
    @GET
    @Path("/verificar-cnpj/{cnpj}")
    @Operation(summary = "Verifica CNPJ", description = "Verifica se já existe uma loja cadastrada com o CNPJ informado")
    @APIResponse(
        responseCode = "200",
        description = "CNPJ verificado",
        content = @Content(mediaType = "application/json")
    )
    public Response verificarCnpj(
            @Parameter(description = "CNPJ a ser verificado", required = true)
            @PathParam("cnpj") String cnpj) {
        log.info("Requisição para verificar CNPJ: {}", cnpj);
        
        Optional<LojaDTO> loja = lojaService.buscarPorCnpj(cnpj);
        
        return Response.ok()
                .entity(loja.isPresent() ? 
                        new CnpjCheckResponse(true, "CNPJ já cadastrado para a loja: " + loja.get().getNome()) : 
                        new CnpjCheckResponse(false, "CNPJ disponível para cadastro"))
                .build();
    }
    
    /**
     * Classe para resposta da verificação de CNPJ
     */
    private static class CnpjCheckResponse {
        private final boolean exists;
        private final String message;
        
        public CnpjCheckResponse(boolean exists, String message) {
            this.exists = exists;
            this.message = message;
        }
        
        public boolean isExists() {
            return exists;
        }
        
        public String getMessage() {
            return message;
        }
    }
}