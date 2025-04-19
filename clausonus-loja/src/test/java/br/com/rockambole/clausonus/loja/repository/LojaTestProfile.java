package br.com.rockambole.clausonus.loja.repository;

import java.util.Map;

import io.quarkus.test.junit.QuarkusTestProfile;

/**
 * Perfil de teste para o módulo de Loja
 */
public class LojaTestProfile implements QuarkusTestProfile {
    
    @Override
    public Map<String, String> getConfigOverrides() {
        return Map.of(
            "quarkus.datasource.jdbc.url", "jdbc:h2:mem:test-loja;MODE=LEGACY",
            "quarkus.hibernate-orm.database.generation", "drop-and-create",
            "quarkus.hibernate-orm.sql-load-script", "import-test.sql"
        );
    }
    
    @Override
    public String getConfigProfile() {
        return "test";
    }
}