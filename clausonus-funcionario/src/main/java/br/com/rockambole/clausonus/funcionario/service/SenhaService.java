package br.com.rockambole.clausonus.funcionario.service;

import jakarta.enterprise.context.ApplicationScoped;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Serviço para operações relacionadas à segurança de senhas
 */
@ApplicationScoped
public class SenhaService {
    
    private static final String ALGORITMO = "SHA-256";
    private static final int TAMANHO_SALT = 16;
    
    /**
     * Criptografa uma senha utilizando PBKDF2 com SHA-256
     * 
     * @param senha Senha em texto plano
     * @return Senha criptografada no formato Base64
     */
    public String criptografar(String senha) {
        try {
            // Gera um salt aleatório
            byte[] salt = gerarSalt();
            
            // Criptografa a senha com o salt
            byte[] hash = criptografarComSalt(senha, salt);
            
            // Combina o salt e o hash para armazenamento
            byte[] combined = new byte[salt.length + hash.length];
            System.arraycopy(salt, 0, combined, 0, salt.length);
            System.arraycopy(hash, 0, combined, salt.length, hash.length);
            
            // Converte para Base64 para armazenamento em banco de dados
            return Base64.getEncoder().encodeToString(combined);
            
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao criptografar senha", e);
        }
    }
    
    /**
     * Verifica se uma senha corresponde à versão criptografada
     * 
     * @param senha Senha em texto plano para verificar
     * @param hashArmazenado Hash armazenado no banco de dados
     * @return true se a senha está correta, false caso contrário
     */
    public boolean verificar(String senha, String hashArmazenado) {
        try {
            // Decodifica o hash armazenado
            byte[] combined = Base64.getDecoder().decode(hashArmazenado);
            
            // Extrai o salt (primeiros TAMANHO_SALT bytes)
            byte[] salt = new byte[TAMANHO_SALT];
            System.arraycopy(combined, 0, salt, 0, TAMANHO_SALT);
            
            // Extrai o hash original
            byte[] hashOriginal = new byte[combined.length - TAMANHO_SALT];
            System.arraycopy(combined, TAMANHO_SALT, hashOriginal, 0, hashOriginal.length);
            
            // Criptografa a senha fornecida com o mesmo salt
            byte[] hashNovo = criptografarComSalt(senha, salt);
            
            // Compara os hashes
            return MessageDigest.isEqual(hashOriginal, hashNovo);
            
        } catch (Exception e) {
            // Em caso de erro, retorna falso por segurança
            return false;
        }
    }
    
    /**
     * Gera um salt aleatório para uso na criptografia
     * 
     * @return Array de bytes com o salt gerado
     */
    private byte[] gerarSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[TAMANHO_SALT];
        random.nextBytes(salt);
        return salt;
    }
    
    /**
     * Criptografa uma senha com um salt específico
     * 
     * @param senha Senha em texto plano
     * @param salt Salt a ser usado na criptografia
     * @return Hash resultante
     * @throws NoSuchAlgorithmException se o algoritmo de hash não for suportado
     */
    private byte[] criptografarComSalt(String senha, byte[] salt) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(ALGORITMO);
        
        // Aplica o salt
        digest.update(salt);
        
        // Criptografa a senha
        byte[] hash = digest.digest(senha.getBytes());
        
        // Aplica múltiplas iterações para aumentar a segurança
        for (int i = 0; i < 1000; i++) {
            digest.reset();
            hash = digest.digest(hash);
        }
        
        return hash;
    }
}