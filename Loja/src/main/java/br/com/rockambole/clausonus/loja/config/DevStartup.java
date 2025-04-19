package br.com.rockambole.clausonus.loja.config;

import br.com.rockambole.clausonus.loja.entity.Loja;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.transaction.Transactional;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

/**
 * Classe responsável por carregar dados iniciais no ambiente de desenvolvimento
 */
@ApplicationScoped
public class DevStartup {
    
    private static final Logger LOGGER = Logger.getLogger(DevStartup.class);
    
    @ConfigProperty(name = "quarkus.profile", defaultValue = "dev")
    String activeProfile;
    
    /**
     * Método chamado na inicialização da aplicação
     */
    @Transactional
    public void loadTestData(@Observes StartupEvent evt) {
        // Carregar dados somente no ambiente de desenvolvimento
        if ("dev".equals(activeProfile)) {
            LOGGER.info("Carregando dados iniciais para ambiente de desenvolvimento");
            
            // Verificar se já existem dados
            if (Loja.count() == 0) {
                LOGGER.info("Inserindo dados de exemplo para lojas");
                
                // Criar algumas lojas de exemplo
                Loja loja1 = new Loja(
                    "Loja Central", 
                    "Av. Paulista, 1000, São Paulo - SP", 
                    "12.345.678/0001-01", 
                    "(11) 3456-7890"
                );
                loja1.persist();
                
                Loja loja2 = new Loja(
                    "Loja Shopping", 
                    "Shopping Center Norte, São Paulo - SP", 
                    "23.456.789/0001-02", 
                    "(11) 4567-8901"
                );
                loja2.persist();
                
                Loja loja3 = new Loja(
                    "Loja Guarulhos", 
                    "Av. Tiradentes, 2000, Guarulhos - SP", 
                    "34.567.890/0001-03", 
                    "(11) 5678-9012"
                );
                loja3.persist();
                
                LOGGER.info("Dados de exemplo inseridos com sucesso!");
            }
        }
    }
}