package br.com.rockambole.clausonus.funcionario.resource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Test;

import br.com.rockambole.clausonus.funcionario.dto.FuncionarioDTO;
import br.com.rockambole.clausonus.funcionario.dto.SenhaDTO;
import br.com.rockambole.clausonus.funcionario.service.FuncionarioService;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;  // Importação correta
import io.restassured.http.ContentType;
import jakarta.ws.rs.NotFoundException;

@QuarkusTest
public class FuncionarioResourceTest {

    @InjectMock  // Substituído @Mock por @InjectMock
    FuncionarioService funcionarioService;

    @Test
    public void testListarTodos() {
        // Configurar mocks
        FuncionarioDTO funcionario1 = new FuncionarioDTO(1L, "Funcionário Um", "12345678900", "Analista", "analista1", true);
        FuncionarioDTO funcionario2 = new FuncionarioDTO(2L, "Funcionário Dois", "98765432100", "Gerente", "gerente1", true);
        when(funcionarioService.listarTodos()).thenReturn(Arrays.asList(funcionario1, funcionario2));

        // Executar e verificar
        given()
            .when().get("/clausonus/api/funcionarios")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("", hasSize(2))
                .body("[0].id", is(1))
                .body("[0].nome", is("Funcionário Um"))
                .body("[1].id", is(2))
                .body("[1].nome", is("Funcionário Dois"));
    }

    @Test
    public void testListarTodosComFiltroAtivos() {
        // Configurar mocks
        FuncionarioDTO funcionario1 = new FuncionarioDTO(1L, "Funcionário Um", "12345678900", "Analista", "analista1", true);
        when(funcionarioService.listarAtivos()).thenReturn(Collections.singletonList(funcionario1));

        // Executar e verificar
        given()
            .queryParam("ativos", true)
            .when().get("/clausonus/api/funcionarios")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("", hasSize(1))
                .body("[0].id", is(1))
                .body("[0].nome", is("Funcionário Um"))
                .body("[0].ativo", is(true));
    }

    @Test
    public void testBuscarPorId_Existente() {
        // Configurar mock
        FuncionarioDTO funcionario = new FuncionarioDTO(1L, "Funcionário Um", "12345678900", "Analista", "analista1", true);
        when(funcionarioService.buscarPorId(1L)).thenReturn(funcionario);

        // Executar e verificar
        given()
            .pathParam("id", 1)
            .when().get("/clausonus/api/funcionarios/{id}")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", is(1))
                .body("nome", is("Funcionário Um"))
                .body("cpf", is("12345678900"))
                .body("cargo", is("Analista"))
                .body("login", is("analista1"))
                .body("ativo", is(true));
    }

    @Test
    public void testBuscarPorId_NaoExistente() {
        // Configurar mock
        when(funcionarioService.buscarPorId(999L)).thenThrow(new NotFoundException("Funcionário não encontrado com o ID: 999"));

        // Executar e verificar
        given()
            .pathParam("id", 999)
            .when().get("/clausonus/api/funcionarios/{id}")
            .then()
                .statusCode(404);
    }

    @Test
    public void testBuscarPorNome() {
        // Configurar mock
        FuncionarioDTO funcionario = new FuncionarioDTO(1L, "Funcionário Um", "12345678900", "Analista", "analista1", true);
        when(funcionarioService.buscarPorNome("Funcionário")).thenReturn(Collections.singletonList(funcionario));

        // Executar e verificar
        given()
            .queryParam("nome", "Funcionário")
            .when().get("/clausonus/api/funcionarios/busca")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("", hasSize(1))
                .body("[0].id", is(1))
                .body("[0].nome", is("Funcionário Um"));
    }

    @Test
    public void testBuscarPorCargo() {
        // Configurar mock
        FuncionarioDTO funcionario = new FuncionarioDTO(1L, "Funcionário Um", "12345678900", "Analista", "analista1", true);
        when(funcionarioService.buscarPorCargo("Analista")).thenReturn(Collections.singletonList(funcionario));

        // Executar e verificar
        given()
            .pathParam("cargo", "Analista")
            .when().get("/clausonus/api/funcionarios/cargo/{cargo}")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("", hasSize(1))
                .body("[0].id", is(1))
                .body("[0].nome", is("Funcionário Um"))
                .body("[0].cargo", is("Analista"));
    }

    @Test
    public void testSalvar_Sucesso() {
        // Configurar DTO e mock
        FuncionarioDTO funcionarioDTO = new FuncionarioDTO();
        funcionarioDTO.setNome("Novo Funcionário");
        funcionarioDTO.setCpf("11122233344");
        funcionarioDTO.setCargo("Desenvolvedor");
        funcionarioDTO.setLogin("dev1");
        funcionarioDTO.setSenha("senha123");
        funcionarioDTO.setAtivo(true);

        FuncionarioDTO funcionarioSalvo = new FuncionarioDTO(1L, "Novo Funcionário", "11122233344", "Desenvolvedor", "dev1", true);
        when(funcionarioService.salvar(any(FuncionarioDTO.class))).thenReturn(funcionarioSalvo);

        // Executar e verificar
        given()
            .contentType(ContentType.JSON)
            .body(funcionarioDTO)
            .when().post("/clausonus/api/funcionarios")
            .then()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .body("id", is(1))
                .body("nome", is("Novo Funcionário"))
                .body("cpf", is("11122233344"))
                .body("cargo", is("Desenvolvedor"))
                .body("login", is("dev1"))
                .body("ativo", is(true))
                .header("Location", notNullValue());
    }
    
    @Test
    public void testSalvar_DadosInvalidos() {
        // Configurar DTO e mock com dados inválidos (sem senha)
        FuncionarioDTO funcionarioDTO = new FuncionarioDTO();
        funcionarioDTO.setNome("Novo Funcionário");
        funcionarioDTO.setCpf("11122233344");
        funcionarioDTO.setCargo("Desenvolvedor");
        funcionarioDTO.setLogin("dev1");
        // Faltando senha
        funcionarioDTO.setAtivo(true);

        // Executar e verificar
        given()
            .contentType(ContentType.JSON)
            .body(funcionarioDTO)
            .when().post("/clausonus/api/funcionarios")
            .then()
                .statusCode(400); // Bad Request devido à validação
    }

    @Test
    public void testSalvar_CpfOuLoginJaExistente() {
        // Configurar DTO e mock
        FuncionarioDTO funcionarioDTO = new FuncionarioDTO();
        funcionarioDTO.setNome("Novo Funcionário");
        funcionarioDTO.setCpf("11122233344");
        funcionarioDTO.setCargo("Desenvolvedor");
        funcionarioDTO.setLogin("dev1");
        funcionarioDTO.setSenha("senha123");
        funcionarioDTO.setAtivo(true);

        when(funcionarioService.salvar(any(FuncionarioDTO.class)))
            .thenThrow(new IllegalArgumentException("Já existe um funcionário cadastrado com o CPF: 11122233344"));

        // Executar e verificar
        given()
            .contentType(ContentType.JSON)
            .body(funcionarioDTO)
            .when().post("/clausonus/api/funcionarios")
            .then()
                .statusCode(400);
    }

    @Test
    public void testAtualizar_Sucesso() {
        // Configurar DTO e mock
        FuncionarioDTO funcionarioDTO = new FuncionarioDTO();
        funcionarioDTO.setId(1L);
        funcionarioDTO.setNome("Funcionário Atualizado");
        funcionarioDTO.setCpf("12345678900");
        funcionarioDTO.setCargo("Analista Senior");
        funcionarioDTO.setLogin("analista1");
        funcionarioDTO.setSenha("nova_senha");
        funcionarioDTO.setAtivo(true);

        FuncionarioDTO funcionarioAtualizado = new FuncionarioDTO(1L, "Funcionário Atualizado", "12345678900", "Analista Senior", "analista1", true);
        when(funcionarioService.atualizar(anyLong(), any(FuncionarioDTO.class))).thenReturn(funcionarioAtualizado);

        // Executar e verificar
        given()
            .contentType(ContentType.JSON)
            .pathParam("id", 1)
            .body(funcionarioDTO)
            .when().put("/clausonus/api/funcionarios/{id}")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", is(1))
                .body("nome", is("Funcionário Atualizado"))
                .body("cargo", is("Analista Senior"));
    }

    @Test
    public void testAtualizar_FuncionarioNaoEncontrado() {
        // Configurar DTO e mock
        FuncionarioDTO funcionarioDTO = new FuncionarioDTO();
        funcionarioDTO.setId(999L);
        funcionarioDTO.setNome("Funcionário Inexistente");
        funcionarioDTO.setCpf("99988877766");
        funcionarioDTO.setCargo("Teste");
        funcionarioDTO.setLogin("teste999");
        funcionarioDTO.setSenha("senha999");
        funcionarioDTO.setAtivo(true);

        when(funcionarioService.atualizar(anyLong(), any(FuncionarioDTO.class)))
            .thenThrow(new NotFoundException("Funcionário não encontrado com o ID: 999"));

        // Executar e verificar
        given()
            .contentType(ContentType.JSON)
            .pathParam("id", 999)
            .body(funcionarioDTO)
            .when().put("/clausonus/api/funcionarios/{id}")
            .then()
                .statusCode(404);
    }

    @Test
    public void testAtualizarSenha_Sucesso() {
        // Configurar DTO e mock
        SenhaDTO senhaDTO = new SenhaDTO("senha_atual", "nova_senha");
        doNothing().when(funcionarioService).atualizarSenha(anyLong(), anyString(), anyString());

        // Executar e verificar
        given()
            .contentType(ContentType.JSON)
            .pathParam("id", 1)
            .body(senhaDTO)
            .when().put("/clausonus/api/funcionarios/{id}/senha")
            .then()
                .statusCode(204);

        verify(funcionarioService).atualizarSenha(1L, "senha_atual", "nova_senha");
    }

    @Test
    public void testAtualizarSenha_FuncionarioNaoEncontrado() {
        // Configurar DTO e mock
        SenhaDTO senhaDTO = new SenhaDTO("senha_atual", "nova_senha");
        doThrow(new NotFoundException("Funcionário não encontrado com o ID: 999"))
            .when(funcionarioService).atualizarSenha(anyLong(), anyString(), anyString());

        // Executar e verificar
        given()
            .contentType(ContentType.JSON)
            .pathParam("id", 999)
            .body(senhaDTO)
            .when().put("/clausonus/api/funcionarios/{id}/senha")
            .then()
                .statusCode(404);
    }

    @Test
    public void testAlterarStatus_Sucesso() {
        // Configurar mock
        FuncionarioDTO funcionarioAtualizado = new FuncionarioDTO(1L, "Funcionário Um", "12345678900", "Analista", "analista1", false);
        when(funcionarioService.alterarStatus(1L, false)).thenReturn(funcionarioAtualizado);

        // Executar e verificar
        given()
            .pathParam("id", 1)
            .queryParam("ativo", false)
            .when().put("/clausonus/api/funcionarios/{id}/status")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", is(1))
                .body("ativo", is(false));
    }

    @Test
    public void testExcluir_Sucesso() {
        // Configurar mock
        when(funcionarioService.excluir(1L)).thenReturn(true);

        // Executar e verificar
        given()
            .pathParam("id", 1)
            .when().delete("/clausonus/api/funcionarios/{id}")
            .then()
                .statusCode(204);
    }

    @Test
    public void testExcluir_FuncionarioNaoEncontrado() {
        // Configurar mock
        when(funcionarioService.excluir(999L))
            .thenThrow(new NotFoundException("Funcionário não encontrado com o ID: 999"));

        // Executar e verificar
        given()
            .pathParam("id", 999)
            .when().delete("/clausonus/api/funcionarios/{id}")
            .then()
                .statusCode(404);
    }
}