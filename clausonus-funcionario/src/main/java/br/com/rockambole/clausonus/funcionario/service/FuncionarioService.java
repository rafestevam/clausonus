package br.com.rockambole.clausonus.funcionario.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import br.com.rockambole.clausonus.funcionario.dto.FuncionarioDTO;
import br.com.rockambole.clausonus.funcionario.entity.Funcionario;
import br.com.rockambole.clausonus.funcionario.exception.BusinessException;
import br.com.rockambole.clausonus.funcionario.repository.FuncionarioRepository;
import lombok.extern.slf4j.Slf4j;

/**
 * Serviço para gerenciamento de Funcionários
 */
@Slf4j
@ApplicationScoped
public class FuncionarioService {
    
    private final FuncionarioRepository funcionarioRepository;
    private final SenhaService senhaService;
    
    @Inject
    public FuncionarioService(FuncionarioRepository funcionarioRepository, SenhaService senhaService) {
        this.funcionarioRepository = funcionarioRepository;
        this.senhaService = senhaService;
    }
    
    /**
     * Lista todos os funcionários cadastrados
     * 
     * @return Lista de FuncionarioDTO
     */
    public List<FuncionarioDTO> listarTodos() {
        log.info("Listando todos os funcionários");
        return funcionarioRepository.listarTodos().stream()
                .map(Funcionario::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Lista todos os funcionários ativos
     * 
     * @return Lista de FuncionarioDTO
     */
    public List<FuncionarioDTO> listarAtivos() {
        log.info("Listando funcionários ativos");
        return funcionarioRepository.listarAtivos().stream()
                .map(Funcionario::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Busca um funcionário pelo seu ID
     * 
     * @param id ID do funcionário
     * @return FuncionarioDTO
     * @throws NotFoundException se o funcionário não for encontrado
     */
    public FuncionarioDTO buscarPorId(Long id) {
        log.info("Buscando funcionário pelo ID: {}", id);
        return funcionarioRepository.buscarPorId(id)
                .map(Funcionario::toDTO)
                .orElseThrow(() -> new NotFoundException("Funcionário não encontrado com o ID: " + id));
    }
    
    /**
     * Busca funcionários pelo nome (busca parcial)
     * 
     * @param nome Nome ou parte do nome do funcionário
     * @return Lista de FuncionarioDTO
     */
    public List<FuncionarioDTO> buscarPorNome(String nome) {
        log.info("Buscando funcionários pelo nome: {}", nome);
        return funcionarioRepository.buscarPorNome(nome).stream()
                .map(Funcionario::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Busca funcionários pelo cargo
     * 
     * @param cargo Cargo dos funcionários
     * @return Lista de FuncionarioDTO
     */
    public List<FuncionarioDTO> buscarPorCargo(String cargo) {
        log.info("Buscando funcionários pelo cargo: {}", cargo);
        return funcionarioRepository.buscarPorCargo(cargo).stream()
                .map(Funcionario::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Busca um funcionário pelo CPF
     * 
     * @param cpf CPF do funcionário
     * @return FuncionarioDTO ou null se não encontrado
     */
    public Optional<FuncionarioDTO> buscarPorCpf(String cpf) {
        log.info("Buscando funcionário pelo CPF: {}", cpf);
        return funcionarioRepository.buscarPorCpf(cpf)
                .map(Funcionario::toDTO);
    }
    
    /**
     * Busca um funcionário pelo login
     * 
     * @param login Login do funcionário
     * @return FuncionarioDTO ou null se não encontrado
     */
    public Optional<FuncionarioDTO> buscarPorLogin(String login) {
        log.info("Buscando funcionário pelo login: {}", login);
        return funcionarioRepository.buscarPorLogin(login)
                .map(Funcionario::toDTO);
    }
    
    /**
     * Salva um novo funcionário
     * 
     * @param funcionarioDTO Dados do funcionário
     * @return FuncionarioDTO com o ID gerado
     */
    @Transactional
    public FuncionarioDTO salvar(FuncionarioDTO funcionarioDTO) {
        log.info("Salvando funcionário: {}", funcionarioDTO);
        
        // Verifica se já existe funcionário com o mesmo CPF
        Optional<Funcionario> existentePorCpf = funcionarioRepository.buscarPorCpf(funcionarioDTO.getCpf());
        if (existentePorCpf.isPresent()) {
            throw new BusinessException("Já existe um funcionário cadastrado com o CPF: " + funcionarioDTO.getCpf());
        }
        
        // Verifica se já existe funcionário com o mesmo login
        Optional<Funcionario> existentePorLogin = funcionarioRepository.buscarPorLogin(funcionarioDTO.getLogin());
        if (existentePorLogin.isPresent()) {
            throw new BusinessException("Já existe um funcionário cadastrado com o login: " + funcionarioDTO.getLogin());
        }
        
        // Criptografa a senha
        String senhaCriptografada = senhaService.criptografar(funcionarioDTO.getSenha());
        
        // Cria a entidade a partir do DTO
        Funcionario funcionario = Funcionario.fromDTO(funcionarioDTO, senhaCriptografada);
        
        // Salva a entidade
        funcionarioRepository.salvar(funcionario);
        
        return funcionario.toDTO();
    }
    
    /**
     * Atualiza os dados de um funcionário existente
     * 
     * @param id ID do funcionário a ser atualizado
     * @param funcionarioDTO Novos dados do funcionário
     * @return FuncionarioDTO atualizado
     * @throws NotFoundException se o funcionário não for encontrado
     */
    @Transactional
    public FuncionarioDTO atualizar(Long id, FuncionarioDTO funcionarioDTO) {
        log.info("Atualizando funcionário com ID {}: {}", id, funcionarioDTO);
        
        Funcionario funcionario = funcionarioRepository.buscarPorId(id)
                .orElseThrow(() -> new NotFoundException("Funcionário não encontrado com o ID: " + id));
        
        // Verifica se o CPF já está sendo usado por outro funcionário
        if (!funcionario.getCpf().equals(funcionarioDTO.getCpf())) {
            Optional<Funcionario> existentePorCpf = funcionarioRepository.buscarPorCpf(funcionarioDTO.getCpf());
            if (existentePorCpf.isPresent() && !existentePorCpf.get().id.equals(id)) {
                throw new BusinessException("Já existe um funcionário cadastrado com o CPF: " + funcionarioDTO.getCpf());
            }
        }
        
        // Verifica se o login já está sendo usado por outro funcionário
        if (!funcionario.getLogin().equals(funcionarioDTO.getLogin())) {
            Optional<Funcionario> existentePorLogin = funcionarioRepository.buscarPorLogin(funcionarioDTO.getLogin());
            if (existentePorLogin.isPresent() && !existentePorLogin.get().id.equals(id)) {
                throw new BusinessException("Já existe um funcionário cadastrado com o login: " + funcionarioDTO.getLogin());
            }
        }
        
        // Se for atualizar a senha, criptografa
        if (funcionarioDTO.getSenha() != null && !funcionarioDTO.getSenha().isEmpty()) {
            funcionarioDTO.setSenha(senhaService.criptografar(funcionarioDTO.getSenha()));
        }
        
        // Atualiza a entidade com os dados do DTO
        funcionario.fromDTO(funcionarioDTO);
        
        // Salva as alterações
        funcionarioRepository.salvar(funcionario);
        
        return funcionario.toDTO();
    }
    
    /**
     * Atualiza a senha de um funcionário
     * 
     * @param id ID do funcionário
     * @param senhaAtual Senha atual
     * @param novaSenha Nova senha
     * @throws NotFoundException se o funcionário não for encontrado
     * @throws BusinessException se a senha atual estiver incorreta
     */
    @Transactional
    public void atualizarSenha(Long id, String senhaAtual, String novaSenha) {
        log.info("Atualizando senha do funcionário com ID: {}", id);
        
        Funcionario funcionario = funcionarioRepository.buscarPorId(id)
                .orElseThrow(() -> new NotFoundException("Funcionário não encontrado com o ID: " + id));
        
        // Verifica se a senha atual está correta
        if (!senhaService.verificar(senhaAtual, funcionario.getSenha())) {
            throw new BusinessException("Senha atual incorreta");
        }
        
        // Criptografa e atualiza a nova senha
        String senhaCriptografada = senhaService.criptografar(novaSenha);
        funcionario.setSenha(senhaCriptografada);
        
        funcionarioRepository.salvar(funcionario);
    }
    
    /**
     * Altera o status de um funcionário (ativo/inativo)
     * 
     * @param id ID do funcionário
     * @param ativo Novo status
     * @return FuncionarioDTO atualizado
     * @throws NotFoundException se o funcionário não for encontrado
     */
    @Transactional
    public FuncionarioDTO alterarStatus(Long id, boolean ativo) {
        log.info("Alterando status do funcionário com ID {}: {}", id, ativo);
        
        Funcionario funcionario = funcionarioRepository.buscarPorId(id)
                .orElseThrow(() -> new NotFoundException("Funcionário não encontrado com o ID: " + id));
        
        funcionario.setAtivo(ativo);
        funcionarioRepository.salvar(funcionario);
        
        return funcionario.toDTO();
    }
    
    /**
     * Exclui um funcionário pelo seu ID
     * 
     * @param id ID do funcionário a ser excluído
     * @return true se o funcionário foi excluído com sucesso
     * @throws NotFoundException se o funcionário não for encontrado
     */
    @Transactional
    public boolean excluir(Long id) {
        log.info("Excluindo funcionário com ID: {}", id);
        
        if (!funcionarioRepository.buscarPorId(id).isPresent()) {
            throw new NotFoundException("Funcionário não encontrado com o ID: " + id);
        }
        
        return funcionarioRepository.deletar(id);
    }
}