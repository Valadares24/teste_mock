package com.exemplo.crud.service;

import com.exemplo.crud.model.Cliente;
import com.exemplo.crud.repository.ClienteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    @Test
    @DisplayName("Deve salvar cliente com sucesso quando dados válidos")
    void deveSalvarClienteComSucesso() {
        Cliente cliente = new Cliente("Ana Souza", "ana@exemplo.com", "11988887777");
        when(clienteRepository.existsByEmail("ana@exemplo.com")).thenReturn(false);
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocation -> {
            Cliente c = invocation.getArgument(0);
            c.setId(1L);
            return c;
        });

        Cliente salvo = clienteService.salvar(cliente);

        assertThat(salvo).isNotNull();
        assertThat(salvo.getId()).isEqualTo(1L);
        assertThat(salvo.getNome()).isEqualTo("Ana Souza");
        assertThat(salvo.getEmail()).isEqualTo("ana@exemplo.com");
        verify(clienteRepository, times(1)).save(cliente);
    }

    @Test
    @DisplayName("Deve lançar exceção ao salvar cliente com e-mail já existente")
    void deveLancarExcecaoAoSalvarClienteComEmailDuplicado() {
        Cliente cliente = new Cliente("Carlos Lima", "carlos@exemplo.com", "11977776666");
        when(clienteRepository.existsByEmail("carlos@exemplo.com")).thenReturn(true);

        assertThatThrownBy(() -> clienteService.salvar(cliente))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Já existe um cliente cadastrado com o e-mail");

        verify(clienteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao salvar cliente nulo")
    void deveLancarExcecaoAoSalvarClienteNulo() {
        assertThatThrownBy(() -> clienteService.salvar(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cliente não pode ser nulo");
    }

    @Test
    @DisplayName("Deve atualizar cliente com sucesso")
    void deveAtualizarClienteComSucesso() {
        Long id = 1L;
        Cliente existente = new Cliente(id, "Lucas Antigo", "lucas@exemplo.com", "111111111");
        Cliente novosDados = new Cliente(null, "Lucas Novo", "lucas.novo@exemplo.com", "222222222");

        when(clienteRepository.findById(id)).thenReturn(Optional.of(existente));
        when(clienteRepository.existsByEmailAndIdNot("lucas.novo@exemplo.com", id)).thenReturn(false);
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(inv -> inv.getArgument(0));

        Cliente atualizado = clienteService.atualizar(id, novosDados);

        assertThat(atualizado.getNome()).isEqualTo("Lucas Novo");
        assertThat(atualizado.getEmail()).isEqualTo("lucas.novo@exemplo.com");
        assertThat(atualizado.getTelefone()).isEqualTo("222222222");
        verify(clienteRepository).save(existente);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar cliente inexistente")
    void deveLancarExcecaoAoAtualizarClienteInexistente() {
        Long id = 99L;
        Cliente novosDados = new Cliente(null, "Lucas", "lucas@exemplo.com", "111");
        when(clienteRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clienteService.atualizar(id, novosDados))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("não encontrado");
    }

    @Test
    @DisplayName("Deve excluir cliente com sucesso quando ID existir")
    void deveExcluirClienteComSucesso() {
        Long id = 1L;
        when(clienteRepository.existsById(id)).thenReturn(true);

        clienteService.excluir(id);

        verify(clienteRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar excluir cliente com ID inexistente")
    void deveLancarExcecaoAoExcluirClienteInexistente() {
        Long id = 99L;
        when(clienteRepository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> clienteService.excluir(id))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("não encontrado para exclusão");

        verify(clienteRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Deve listar todos os clientes")
    void deveListarTodosClientes() {
        List<Cliente> lista = Arrays.asList(
                new Cliente(1L, "João", "joao@email.com", "111"),
                new Cliente(2L, "Maria", "maria@email.com", "222")
        );
        when(clienteRepository.findAll()).thenReturn(lista);

        List<Cliente> resultado = clienteService.listarTodos();

        assertThat(resultado).hasSize(2);
        verify(clienteRepository).findAll();
    }
}

