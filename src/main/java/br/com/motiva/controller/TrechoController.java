package br.com.motiva.controller;

import br.com.motiva.dto.*;
import br.com.motiva.service.TrechoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/trechos")
public class TrechoController {
    private final TrechoService service;

    public TrechoController(TrechoService service) { this.service = service; }

    @GetMapping
    public List<TrechoResponse> listar(@RequestParam(required = false) BigDecimal alturaMinima) {
        return service.listar(alturaMinima);
    }

    @GetMapping("/{id}")
    public TrechoResponse buscar(@PathVariable Long id) { return service.buscar(id); }

    @PostMapping
    public ResponseEntity<TrechoResponse> criar(@Valid @RequestBody TrechoRequest dados) {
        var criado = service.criar(dados);
        return ResponseEntity.created(URI.create("/api/trechos/" + criado.id())).body(criado);
    }

    @PutMapping("/{id}")
    public TrechoResponse atualizar(@PathVariable Long id, @Valid @RequestBody TrechoRequest dados) {
        return service.atualizar(id, dados);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        service.remover(id);
        return ResponseEntity.noContent().build();
    }
}
