package br.com.motiva.controller;

import br.com.motiva.dto.*;
import br.com.motiva.service.EquipeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/equipes")
public class EquipeController {
    private final EquipeService service;

    public EquipeController(EquipeService service) { this.service = service; }

    @GetMapping
    public List<EquipeResponse> listar(@RequestParam(required = false) String status) {
        return service.listar(status);
    }

    @GetMapping("/{id}")
    public EquipeResponse buscar(@PathVariable Long id) { return service.buscar(id); }

    @PostMapping
    public ResponseEntity<EquipeResponse> criar(@Valid @RequestBody EquipeRequest dados) {
        var criado = service.criar(dados);
        return ResponseEntity.created(URI.create("/api/equipes/" + criado.id())).body(criado);
    }

    @PutMapping("/{id}")
    public EquipeResponse atualizar(@PathVariable Long id, @Valid @RequestBody EquipeRequest dados) {
        return service.atualizar(id, dados);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        service.remover(id);
        return ResponseEntity.noContent().build();
    }
}
