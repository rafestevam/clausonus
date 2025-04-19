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
import br.com.rockambole.clausonus.funcionario.mapper.FuncionarioMapper;
import br.com.rockambole.clausonus.funcionario.repository.FuncionarioRepository;
import lombok.extern.slf4j.Slf4j;

/**
 * Serviço para gerenciamento de Funcionários
 */
@Slf4j
@ApplicationScoped
public class FuncionarioService {
    
    private final FuncionarioRepository funcionarioRepository;
    private final FuncionarioMapper funcionarioMapper;
    
    @Inject
    public FuncionarioService(FuncionarioRepository funcionarioRepository, FuncionarioMapper funcionarioMapper) {
        this.funcionarioRepository = funcionarioRepository;
        this.funcionarioMapper = funcionarioMapper;
    }
    
    /**
     * Lista todos os funcionários cadastrados
     * 
     * @return Lista de FuncionarioDTO
     */
    public List<FuncionarioDTO> listarTodos() {
        log.info("Listando todos os funcionários");
        return funcionarioRepository.listarTodos().stream()
                .map(funcionarioMapper::toDto)
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
                .map(funcionarioMapper::toDto)
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
                .map(funcionarioMapper::toDto)
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
                .map(funcionarioMapper::toDto);
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
                .map(funcionarioMapper::toDto);
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
            throw new IllegalArgumentException("Já existe um funcionário cadastrado com o CPF: " + funcionarioDTO.getCpf());
        }
        
        // Verifica se já existe funcionário com o mesmo login
        Optional<Funcionario> existentePorLogin = funcionarioRepository.buscarPorLogin(funcionarioDTO.getLogin());
        if (existentePorLogin.isPresent()) {
            throw new IllegalArgumentException("Já existe um funcionário cadastrado com o login: " + funcionarioDTO.getLogin());
        }
        
        Funcionario funcionario = funcionarioMapper.toEntity(funcionarioDTO);
        funcionarioRepository.salvar(funcionario);
        
        return funcionarioMapper.toDto(funcionario);
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
            if (existentePorCpf.isPresent() && !existentePorCpf.get().getId().equals(id)) {
                throw new IllegalArgumentException("Já existe um funcionário cadastrado com o CPF: " + funcionarioDTO.getCpf());
            }
        }
        
        // Verifica se o login já está sendo usado por outro funcionário
        if (!funcionario.getLogin().equals(funcionarioDTO.getLogin())) {
            Optional<Funcionario> existentePorLogin = funcionarioRepository.buscarPorLogin(funcionarioDTO.getLogin());
            if (existentePorLogin.isPresent() && !existentePorLogin.get().getId().equals(id)) {
                throw new IllegalArgumentException("Já existe um funcionário cadastrado com o login: " + funcionarioDTO.getLogin());
            }
        }
        
        // Atualiza os campos
        funcionario.setNome(funcionarioDTO.getNome());
        funcionario.setCpf(funcionarioDTO.getCpf());
        funcionario.setCargo(funcionarioDTO.getCargo());
        funcionario.setLogin(funcionarioDTO.getLogin());
        // Não atualiza a senha se estiver em branco
        if (funcionarioDTO.getSenha() != null && !funcionarioDTO.getSenha().isEmpty()) {
            funcionario.setSenha(funcionarioDTO.getSenha());
        }
        funcionario.setAtivo(funcionarioDTO.isAtivo());
        
        funcionarioRepository.salvar(funcionario);
        
        return funcionarioMapper.toDto(funcionario);
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