package br.com.rockambole.clausonus.funcionario.config;

import io.quarkus.test.junit.QuarkusTestProfile;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuração para testes de integração da API REST
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RestTestConfig {

    @BeforeAll
    public static void setupAll() {
        // Configurar RestAssured para testes
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }
    
    @BeforeEach
    public void setup() {
        // Configurar base path para cada teste
        RestAssured.basePath = "/api";
    }
    
    /**
     * Perfil de teste para configurar propriedades específicas
     */
    public static class RestAPITestProfile implements QuarkusTestProfile {
        @Override
        public Map<String, String> getConfigOverrides() {
            Map<String, String> config = new HashMap<>();
            
            // Configurações para o ambiente de teste
            config.put("quarkus.http.root-path", "/clausonus");
            config.put("quarkus.resteasy-reactive.path", "/api");
            
            return config;
        }
    }
}