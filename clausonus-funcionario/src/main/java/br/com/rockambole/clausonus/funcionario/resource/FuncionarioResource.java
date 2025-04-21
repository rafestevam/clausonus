package br.com.rockambole.clausonus.funcionario.resource;

import java.util.List;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import br.com.rockambole.clausonus.funcionario.dto.FuncionarioDTO;
import br.com.rockambole.clausonus.funcionario.dto.SenhaDTO;
import br.com.rockambole.clausonus.funcionario.service.FuncionarioService;
import jakarta.ws.rs.core.UriBuilder;

@Path("/funcionarios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Funcionários", description = "Operações relacionadas a funcionários")
public class FuncionarioResource {
    
    @Inject
    FuncionarioService funcionarioService;
    
    @GET
    @Operation(summary = "Lista todos os funcionários", description = "Retorna uma lista com todos os funcionários cadastrados")
    @APIResponse(responseCode = "200", description = "Lista de funcionários", 
        content = @Content(mediaType = "application/json", 
        schema = @Schema(type = SchemaType.ARRAY, implementation = FuncionarioDTO.class)))
    public Response listarTodos(@QueryParam("ativos") Boolean apenasAtivos) {
        List<FuncionarioDTO> funcionarios;
        
        if (apenasAtivos != null && apenasAtivos) {
            funcionarios = funcionarioService.listarAtivos();
        } else {
            funcionarios = funcionarioService.listarTodos();
        }
        
        return Response.ok(funcionarios).build();
    }
    
    @GET
    @Path("/{id}")
    @Operation(summary = "Busca funcionário por ID", description = "Retorna um funcionário específico pelo seu ID")
    @APIResponse(responseCode = "200", description = "Funcionário encontrado", 
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = FuncionarioDTO.class)))
    @APIResponse(responseCode = "404", description = "Funcionário não encontrado")
    public Response buscarPorId(
            @Parameter(description = "ID do funcionário", required = true) 
            @PathParam("id") Long id) {
        return Response.ok(funcionarioService.buscarPorId(id)).build();
    }
    
    @GET
    @Path("/busca")
    @Operation(summary = "Busca funcionários por nome", description = "Retorna funcionários que contenham o nome informado")
    @APIResponse(responseCode = "200", description = "Funcionários encontrados", 
        content = @Content(mediaType = "application/json", 
        schema = @Schema(type = SchemaType.ARRAY, implementation = FuncionarioDTO.class)))
    public Response buscarPorNome(
            @Parameter(description = "Nome ou parte do nome do funcionário", required = true) 
            @QueryParam("nome") @NotBlank String nome) {
        return Response.ok(funcionarioService.buscarPorNome(nome)).build();
    }
    
    @GET
    @Path("/cargo/{cargo}")
    @Operation(summary = "Busca funcionários por cargo", description = "Retorna funcionários que possuem o cargo informado")
    @APIResponse(responseCode = "200", description = "Funcionários encontrados", 
        content = @Content(mediaType = "application/json", 
        schema = @Schema(type = SchemaType.ARRAY, implementation = FuncionarioDTO.class)))
    public Response buscarPorCargo(
            @Parameter(description = "Cargo dos funcionários", required = true) 
            @PathParam("cargo") @NotBlank String cargo) {
        return Response.ok(funcionarioService.buscarPorCargo(cargo)).build();
    }
    
    @POST
    @Operation(summary = "Cadastra novo funcionário", description = "Cadastra um novo funcionário no sistema")
    @APIResponse(responseCode = "201", description = "Funcionário cadastrado com sucesso", 
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = FuncionarioDTO.class)))
    @APIResponse(responseCode = "400", description = "Dados inválidos ou funcionário já existente")
    public Response salvar(@Valid FuncionarioDTO funcionarioDTO) {
        try {
            FuncionarioDTO salvo = funcionarioService.salvar(funcionarioDTO);
            
            // Verificação para evitar NullPointerException
            if (salvo == null || salvo.getId() == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Não foi possível salvar o funcionário. Dados inválidos ou conflito.")
                    .build();
            }
            
            return Response
                    .created(UriBuilder.fromResource(FuncionarioResource.class)
                            .path(String.valueOf(salvo.getId())).build())
                    .entity(salvo)
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Erro ao salvar funcionário: " + e.getMessage())
                    .build();
        }
    }
    
    @PUT
    @Path("/{id}")
    @Operation(summary = "Atualiza funcionário", description = "Atualiza os dados de um funcionário existente")
    @APIResponse(responseCode = "200", description = "Funcionário atualizado com sucesso", 
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = FuncionarioDTO.class)))
    @APIResponse(responseCode = "404", description = "Funcionário não encontrado")
    @APIResponse(responseCode = "400", description = "Dados inválidos ou conflito com funcionário existente")
    public Response atualizar(
            @Parameter(description = "ID do funcionário", required = true) 
            @PathParam("id") Long id,
            @Valid FuncionarioDTO funcionarioDTO) {
        return Response.ok(funcionarioService.atualizar(id, funcionarioDTO)).build();
    }
    
    @PUT
    @Path("/{id}/senha")
    @Operation(summary = "Atualiza senha do funcionário", description = "Atualiza a senha de um funcionário existente")
    @APIResponse(responseCode = "204", description = "Senha atualizada com sucesso")
    @APIResponse(responseCode = "404", description = "Funcionário não encontrado")
    @APIResponse(responseCode = "400", description = "Senha atual incorreta ou dados inválidos")
    public Response atualizarSenha(
            @Parameter(description = "ID do funcionário", required = true) 
            @PathParam("id") Long id,
            @Valid SenhaDTO senhaDTO) {
        funcionarioService.atualizarSenha(id, senhaDTO.getSenhaAtual(), senhaDTO.getNovaSenha());
        return Response.noContent().build();
    }
    
    @PUT
    @Path("/{id}/status")
    @Operation(summary = "Ativa/desativa funcionário", description = "Altera o status de um funcionário (ativo/inativo)")
    @APIResponse(responseCode = "200", description = "Status alterado com sucesso", 
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = FuncionarioDTO.class)))
    @APIResponse(responseCode = "404", description = "Funcionário não encontrado")
    public Response alterarStatus(
            @Parameter(description = "ID do funcionário", required = true) 
            @PathParam("id") Long id,
            @Parameter(description = "Status do funcionário (true=ativo, false=inativo)", required = true) 
            @QueryParam("ativo") boolean ativo) {
        return Response.ok(funcionarioService.alterarStatus(id, ativo)).build();
    }
    
    @DELETE
    @Path("/{id}")
    @Operation(summary = "Remove funcionário", description = "Remove um funcionário do sistema")
    @APIResponse(responseCode = "204", description = "Funcionário removido com sucesso")
    @APIResponse(responseCode = "404", description = "Funcionário não encontrado")
    public Response excluir(
            @Parameter(description = "ID do funcionário", required = true) 
            @PathParam("id") Long id) {
        funcionarioService.excluir(id);
        return Response.noContent().build();
    }
}