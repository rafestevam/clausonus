package br.com.rockambole.clausonus.loja.resource;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import br.com.rockambole.clausonus.loja.entity.Loja;
import br.com.rockambole.clausonus.loja.repository.LojaRepository;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
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
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

@Path("/api/lojas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LojaResource {
    
    private static final Logger LOGGER = Logger.getLogger(LojaResource.class);
    
    @Inject
    LojaRepository lojaRepository;
    
    @Inject
    @ConfigProperty(name = "quarkus.datasource.db-kind")
    String dbKind;
    
    @GET
    public List<Loja> listarTodas() {
        LOGGER.info("Listando todas as lojas usando banco de dados: " + dbKind);
        return lojaRepository.listarTodas();
    }
    
    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") Long id) {
        LOGGER.info("Buscando loja por ID: " + id);
        Optional<Loja> lojaOptional = lojaRepository.buscarPorId(id);
        
        if (lojaOptional.isPresent()) {
            return Response.ok(lojaOptional.get()).build();
        } else {
            return Response.status(Status.NOT_FOUND)
                    .entity("Loja não encontrada com ID: " + id)
                    .build();
        }
    }
    
    @GET
    @Path("/busca")
    public List<Loja> buscarPorNome(@QueryParam("nome") String nome) {
        LOGGER.info("Buscando lojas por nome: " + nome);
        return lojaRepository.buscarPorNome(nome);
    }
    
    @POST
    @Transactional
    public Response criar(@Valid Loja loja) {
        LOGGER.info("Criando nova loja: " + loja.getNome());
        
        // Verificar se já existe uma loja com este CNPJ
        Optional<Loja> existente = lojaRepository.buscarPorCnpj(loja.getCnpj());
        if (existente.isPresent()) {
            return Response.status(Status.CONFLICT)
                    .entity("Já existe uma loja cadastrada com este CNPJ")
                    .build();
        }
        
        lojaRepository.salvar(loja);
        return Response.created(URI.create("/api/lojas/" + loja.getId())).entity(loja).build();
    }
    
    @PUT
    @Path("/{id}")
    @Transactional
    public Response atualizar(@PathParam("id") Long id, @Valid Loja loja) {
        LOGGER.info("Atualizando loja ID: " + id);
        
        Optional<Loja> existenteOpt = lojaRepository.buscarPorId(id);
        if (existenteOpt.isEmpty()) {
            return Response.status(Status.NOT_FOUND)
                    .entity("Loja não encontrada com ID: " + id)
                    .build();
        }
        
        Loja existente = existenteOpt.get();
        
        // Verificar se o CNPJ já existe em outra loja
        if (!existente.getCnpj().equals(loja.getCnpj())) {
            Optional<Loja> lojaMesmoCnpj = lojaRepository.buscarPorCnpj(loja.getCnpj());
            if (lojaMesmoCnpj.isPresent() && !lojaMesmoCnpj.get().getId().equals(id)) {
                return Response.status(Status.CONFLICT)
                        .entity("Já existe outra loja cadastrada com este CNPJ")
                        .build();
            }
        }
        
        // Atualizar os dados
        existente.setNome(loja.getNome());
        existente.setEndereco(loja.getEndereco());
        existente.setCnpj(loja.getCnpj());
        existente.setTelefone(loja.getTelefone());
        
        return Response.ok(existente).build();
    }
    
    @DELETE
    @Path("/{id}")
    @Transactional
    public Response remover(@PathParam("id") Long id) {
        LOGGER.info("Removendo loja ID: " + id);
        
        Optional<Loja> existenteOpt = lojaRepository.buscarPorId(id);
        if (existenteOpt.isEmpty()) {
            return Response.status(Status.NOT_FOUND)
                    .entity("Loja não encontrada com ID: " + id)
                    .build();
        }
        
        // Executa a exclusão sem armazenar o resultado em uma variável
        lojaRepository.deletar(id);
        return Response.status(Status.NO_CONTENT).build();
    }
    
    @GET
    @Path("/info/database")
    public Response getDatabaseInfo() {
        LOGGER.info("Obtendo informações do banco de dados");
        return Response.ok("Tipo de banco de dados em uso: " + dbKind).build();
    }
}