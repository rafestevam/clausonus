package br.com.rockambole.clausonus.loja.resource;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import br.com.rockambole.clausonus.loja.dto.LojaDTO;
import br.com.rockambole.clausonus.loja.service.LojaService;

@Path("/lojas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Lojas", description = "Operações relacionadas a Lojas")
public class LojaResource {
    
    private final LojaService lojaService;
    
    @Inject
    public LojaResource(LojaService lojaService) {
        this.lojaService = lojaService;
    }
    
    @GET
    @Operation(summary = "Listar todas as lojas", description = "Retorna todas as lojas cadastradas no sistema")
    @APIResponse(responseCode = "200", description = "Lista de lojas",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = LojaDTO.class)))
    public List<LojaDTO> listarTodas() {
        return lojaService.listarTodas();
    }
    
    @GET
    @Path("/{id}")
    @Operation(summary = "Buscar loja por ID", description = "Retorna uma loja específica pelo seu ID")
    @APIResponse(responseCode = "200", description = "Loja encontrada",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = LojaDTO.class)))
    @APIResponse(responseCode = "404", description = "Loja não encontrada")
    public LojaDTO buscarPorId(
            @Parameter(description = "ID da loja", required = true) @PathParam("id") Long id) {
        return lojaService.buscarPorId(id);
    }
    
    @GET
    @Path("/busca")
    @Operation(summary = "Buscar lojas por nome", description = "Retorna lojas que contenham o nome informado")
    @APIResponse(responseCode = "200", description = "Lista de lojas que correspondem ao nome",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = LojaDTO.class)))
    public List<LojaDTO> buscarPorNome(
            @Parameter(description = "Nome ou parte do nome da loja", required = true) @QueryParam("nome") String nome) {
        return lojaService.buscarPorNome(nome);
    }
    
    @GET
    @Path("/cnpj/{cnpj}")
    @Operation(summary = "Buscar loja por CNPJ", description = "Retorna uma loja pelo seu CNPJ")
    @APIResponse(responseCode = "200", description = "Loja encontrada",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = LojaDTO.class)))
    @APIResponse(responseCode = "404", description = "Loja não encontrada")
    public Response buscarPorCnpj(
            @Parameter(description = "CNPJ da loja", required = true) @PathParam("cnpj") String cnpj) {
        Optional<LojaDTO> loja = lojaService.buscarPorCnpj(cnpj);
        return loja.map(Response::ok)
                .orElse(Response.status(Response.Status.NOT_FOUND))
                .build();
    }
    
    @POST
    @Operation(summary = "Criar nova loja", description = "Cria uma nova loja com os dados informados")
    @APIResponse(responseCode = "201", description = "Loja criada com sucesso",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = LojaDTO.class)))
    @APIResponse(responseCode = "400", description = "Dados inválidos")
    public Response criar(@Valid LojaDTO lojaDTO) {
        try {
            LojaDTO lojaCriada = lojaService.salvar(lojaDTO);
            return Response.created(URI.create("/lojas/" + lojaCriada.getId()))
                    .entity(lojaCriada)
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }
    
    @PUT
    @Path("/{id}")
    @Operation(summary = "Atualizar loja", description = "Atualiza os dados de uma loja existente")
    @APIResponse(responseCode = "200", description = "Loja atualizada com sucesso",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = LojaDTO.class)))
    @APIResponse(responseCode = "404", description = "Loja não encontrada")
    @APIResponse(responseCode = "400", description = "Dados inválidos")
    public Response atualizar(
            @Parameter(description = "ID da loja", required = true) @PathParam("id") Long id,
            @Valid LojaDTO lojaDTO) {
        try {
            LojaDTO lojaAtualizada = lojaService.atualizar(id, lojaDTO);
            return Response.ok(lojaAtualizada).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }
    
    @DELETE
    @Path("/{id}")
    @Operation(summary = "Excluir loja", description = "Exclui uma loja pelo seu ID")
    @APIResponse(responseCode = "204", description = "Loja excluída com sucesso")
    @APIResponse(responseCode = "404", description = "Loja não encontrada")
    public Response excluir(
            @Parameter(description = "ID da loja", required = true) @PathParam("id") Long id) {
        try {
            lojaService.excluir(id);
            return Response.noContent().build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .build();
        }
    }
}