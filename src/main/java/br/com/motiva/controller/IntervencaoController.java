package br.com.motiva.controller;

import br.com.motiva.dto.*;
import br.com.motiva.service.IntervencaoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/intervencoes")
public class IntervencaoController {
    private final IntervencaoService service;

    public IntervencaoController(IntervencaoService service) { this.service = service; }

    @GetMapping
    public List<IntervencaoResponse> listar(@RequestParam(required = false) String tipoServico) {
        return service.listar(tipoServico);
    }

    @GetMapping("/{id}")
    public IntervencaoResponse buscar(@PathVariable Long id) { return service.buscar(id); }

    @PostMapping
    public ResponseEntity<IntervencaoResponse> criar(@Valid @RequestBody IntervencaoRequest dados) {
        var criado = service.criar(dados);
        return ResponseEntity.created(URI.create("/api/intervencoes/" + criado.id())).body(criado);
    }

    @PutMapping("/{id}")
    public IntervencaoResponse atualizar(@PathVariable Long id, @Valid @RequestBody IntervencaoRequest dados) {
        return service.atualizar(id, dados);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        service.remover(id);
        return ResponseEntity.noContent().build();
    }
}
