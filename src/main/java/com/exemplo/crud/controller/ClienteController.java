package com.exemplo.crud.controller;

import com.exemplo.crud.model.Cliente;
import com.exemplo.crud.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public ResponseEntity<List<Cliente>> listarTodos() {
        return ResponseEntity.ok(clienteService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        return clienteService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> criar(@Valid @RequestBody Cliente cliente) {
        try {
            Cliente novoCliente = clienteService.salvar(cliente);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoCliente);
        } catch (IllegalArgumentException e) {
            Map<String, String> erro = new HashMap<>();
            erro.put("mensagem", e.getMessage());
            return ResponseEntity.badRequest().body(erro);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @Valid @RequestBody Cliente cliente) {
        try {
            Cliente atualizado = clienteService.atualizar(id, cliente);
            return ResponseEntity.ok(atualizado);
        } catch (IllegalArgumentException e) {
            Map<String, String> erro = new HashMap<>();
            erro.put("mensagem", e.getMessage());
            return ResponseEntity.badRequest().body(erro);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluir(@PathVariable Long id) {
        try {
            clienteService.excluir(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            Map<String, String> erro = new HashMap<>();
            erro.put("mensagem", e.getMessage());
            return ResponseEntity.badRequest().body(erro);
        }
    }
}

