package br.com.rockambole.clausonus.funcionario.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

/**
 * Testes para o serviço de senha
 */
@QuarkusTest
public class SenhaServiceTest {

    @Inject
    SenhaService senhaService;
    
    @Test
    public void testCriptografar() {
        // Senha em texto plano
        String senhaPlana = "senha123";
        
        // Criptografar a senha
        String senhaCriptografada = senhaService.criptografar(senhaPlana);
        
        // Verificar que a senha criptografada não é nula e é diferente da senha original
        assertNotNull(senhaCriptografada, "A senha criptografada não deveria ser nula");
        assertNotEquals(senhaPlana, senhaCriptografada, "A senha criptografada deveria ser diferente da senha original");
    }
    
    @Test
    public void testCriptografarSenhasDiferentes() {
        // Mesmo texto plano criptografado duas vezes deveria gerar hashes diferentes (devido ao salt)
        String senhaPlana = "minhasenha";
        
        String hash1 = senhaService.criptografar(senhaPlana);
        String hash2 = senhaService.criptografar(senhaPlana);
        
        assertNotEquals(hash1, hash2, "Duas criptografias da mesma senha deveriam produzir resultados diferentes devido ao salt");
    }
    
    @Test
    public void testVerificarSenha_Correta() {
        // Senha em texto plano
        String senhaPlana = "senhaCorreta123";
        
        // Criptografar a senha
        String senhaCriptografada = senhaService.criptografar(senhaPlana);
        
        // Verificar que a senha correta é validada com sucesso
        boolean resultado = senhaService.verificar(senhaPlana, senhaCriptografada);
        
        assertTrue(resultado, "A senha correta deveria ser validada com sucesso");
    }
    
    @Test
    public void testVerificarSenha_Incorreta() {
        // Senha em texto plano
        String senhaPlana = "senhaCorreta123";
        String senhaIncorreta = "senhaErrada456";
        
        // Criptografar a senha correta
        String senhaCriptografada = senhaService.criptografar(senhaPlana);
        
        // Verificar que a senha incorreta é rejeitada
        boolean resultado = senhaService.verificar(senhaIncorreta, senhaCriptografada);
        
        assertFalse(resultado, "A senha incorreta deveria ser rejeitada");
    }
    
    @Test
    public void testVerificarSenha_HashInvalido() {
        // Tentar verificar com um hash inválido
        boolean resultado = senhaService.verificar("qualquerSenha", "hashInvalido");
        
        assertFalse(resultado, "Um hash inválido deveria ser rejeitado");
    }
}
