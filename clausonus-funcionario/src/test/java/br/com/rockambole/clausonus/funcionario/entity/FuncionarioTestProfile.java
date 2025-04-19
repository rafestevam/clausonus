package br.com.rockambole.clausonus.funcionario.entity;

import java.util.Map;
import java.util.HashMap;

import io.quarkus.test.junit.QuarkusTestProfile;

/**
 * Perfil de teste para testes relacionados a Funcionario
 */
public class FuncionarioTestProfile implements QuarkusTestProfile {
    
    @Override
    public Map<String, String> getConfigOverrides() {
        Map<String, String> config = new HashMap<>();
        
        // Garante que estamos usando banco em memória para testes
        config.put("quarkus.datasource.db-kind", "h2");
        config.put("quarkus.datasource.jdbc.url", "jdbc:h2:mem:test;MODE=LEGACY");
        config.put("quarkus.hibernate-orm.database.generation", "drop-and-create");
        config.put("quarkus.hibernate-orm.sql-load-script", "import-test.sql");
        
        return config;
    }
    
    @Override
    public String getConfigProfile() {
        return "test";
    }
}