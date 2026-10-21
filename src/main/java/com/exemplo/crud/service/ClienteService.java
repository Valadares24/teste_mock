package com.exemplo.crud.service;

import com.exemplo.crud.model.Cliente;
import com.exemplo.crud.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Transactional(readOnly = true)
    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepository.findById(id);
    }

    @Transactional
    public Cliente salvar(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente não pode ser nulo.");
        }
        if (cliente.getEmail() == null || cliente.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("O e-mail é obrigatório.");
        }
        if (clienteRepository.existsByEmail(cliente.getEmail())) {
            throw new IllegalArgumentException("Já existe um cliente cadastrado com o e-mail: " + cliente.getEmail());
        }
        return clienteRepository.save(cliente);
    }

    @Transactional
    public Cliente atualizar(Long id, Cliente dadosAtualizados) {
        Cliente clienteExistente = clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente com ID " + id + " não encontrado."));

        if (dadosAtualizados.getEmail() != null &&
                clienteRepository.existsByEmailAndIdNot(dadosAtualizados.getEmail(), id)) {
            throw new IllegalArgumentException("Já existe outro cliente com o e-mail: " + dadosAtualizados.getEmail());
        }

        clienteExistente.setNome(dadosAtualizados.getNome());
        clienteExistente.setEmail(dadosAtualizados.getEmail());
        clienteExistente.setTelefone(dadosAtualizados.getTelefone());

        return clienteRepository.save(clienteExistente);
    }

    @Transactional
    public void excluir(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new IllegalArgumentException("Cliente com ID " + id + " não encontrado para exclusão.");
        }
        clienteRepository.deleteById(id);
    }
}

