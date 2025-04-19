package br.com.rockambole.clausonus.loja.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.restassured.http.ContentType;

import br.com.rockambole.clausonus.loja.repository.LojaTestProfile;

/**
 * Testes de integração para o LojaController
 */
@QuarkusTest
@TestHTTPEndpoint(LojaController.class)
@TestProfile(LojaTestProfile.class)
public class LojaControllerTest {

    @Test
    public void testListarTodas() {
        given()
            .when()
            .get()
            .then()
            .statusCode(200)
            .body("size()", is(3));
    }
    
    @Test
    public void testBuscarPorIdExistente() {
        given()
            .when()
            .get("/1")
            .then()
            .statusCode(200)
            .body("id", is(1))
            .body("nome", equalTo("Loja Matriz"));
    }
    
    @Test
    public void testBuscarPorIdInexistente() {
        given()
            .when()
            .get("/999")
            .then()
            .statusCode(404);
    }
    
    @Test
    public void testBuscarPorNome() {
        given()
            .when()
            .get("/busca?nome=Campinas")
            .then()
            .statusCode(200)
            .body("size()", is(1))
            .body("[0].nome", equalTo("Loja Campinas"));
    }
    
    @Test
    public void testVerificarCnpjExistente() {
        given()
            .when()
            .get("/verificar-cnpj/12345678901234")
            .then()
            .statusCode(200)
            .body("exists", is(true));
    }
    
    @Test
    public void testVerificarCnpjInexistente() {
        given()
            .when()
            .get("/verificar-cnpj/99999999999999")
            .then()
            .statusCode(200)
            .body("exists", is(false));
    }
    
    @Test
    public void testSalvarNovo() {
        Map<String, Object> lojaNova = new HashMap<>();
        lojaNova.put("nome", "Loja Nova");
        lojaNova.put("endereco", "Endereço Novo, 123");
        lojaNova.put("cnpj", "45678901234567");
        lojaNova.put("telefone", "(21) 9876-5432");
        
        given()
            .contentType(ContentType.JSON)
            .body(lojaNova)
            .when()
            .post()
            .then()
            .statusCode(201)
            .header("Location", notNullValue())
            .body("id", notNullValue())
            .body("nome", equalTo("Loja Nova"))
            .body("cnpj", equalTo("45678901234567"));
    }
    
    @Test
    public void testSalvarComCnpjExistente() {
        Map<String, Object> lojaExistente = new HashMap<>();
        lojaExistente.put("nome", "Loja Duplicada");
        lojaExistente.put("endereco", "Endereço Duplicado, 123");
        lojaExistente.put("cnpj", "12345678901234"); // CNPJ já existente
        lojaExistente.put("telefone", "(21) 9876-5432");
        
        given()
            .contentType(ContentType.JSON)
            .body(lojaExistente)
            .when()
            .post()
            .then()
            .statusCode(400); // Bad Request
    }
    
    @Test
    public void testAtualizar() {
        Map<String, Object> lojaAtualizada = new HashMap<>();
        lojaAtualizada.put("nome", "Loja Matriz Atualizada");
        lojaAtualizada.put("endereco", "Av. Paulista, 1000 - Atualizado");
        lojaAtualizada.put("cnpj", "12345678901234"); // Mesmo CNPJ
        lojaAtualizada.put("telefone", "(11) 9999-9999");
        
        given()
            .contentType(ContentType.JSON)
            .body(lojaAtualizada)
            .when()
            .put("/1")
            .then()
            .statusCode(200)
            .body("id", is(1))
            .body("nome", equalTo("Loja Matriz Atualizada"))
            .body("telefone", equalTo("(11) 9999-9999"));
    }
    
    @Test
    public void testAtualizarInexistente() {
        Map<String, Object> lojaAtualizada = new HashMap<>();
        lojaAtualizada.put("nome", "Loja Inexistente");
        lojaAtualizada.put("endereco", "Endereço Inexistente");
        lojaAtualizada.put("cnpj", "99999999999999");
        lojaAtualizada.put("telefone", "(99) 9999-9999");
        
        given()
            .contentType(ContentType.JSON)
            .body(lojaAtualizada)
            .when()
            .put("/999")
            .then()
            .statusCode(404);
    }
    
    @Test
    public void testExcluir() {
        // Primeiro salvamos uma loja para excluir
        Map<String, Object> lojaNova = new HashMap<>();
        lojaNova.put("nome", "Loja para Excluir");
        lojaNova.put("endereco", "Endereço Excluir, 123");
        lojaNova.put("cnpj", "56789012345678");
        lojaNova.put("telefone", "(31) 1234-5678");
        
        Integer id = given()
            .contentType(ContentType.JSON)
            .body(lojaNova)
            .post()
            .then()
            .statusCode(201)
            .extract()
            .path("id");
        
        // Agora excluímos a loja
        given()
            .when()
            .delete("/" + id)
            .then()
            .statusCode(204);
        
        // Verificamos se a loja foi excluída
        given()
            .when()
            .get("/" + id)
            .then()
            .statusCode(404);
    }
}