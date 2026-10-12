package br.com.motiva.controller;

import br.com.motiva.dto.RelatorioResponse;
import br.com.motiva.service.GeradorRelatorio;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/relatorios")
public class RelatorioController {
    private final GeradorRelatorio service;

    public RelatorioController(GeradorRelatorio service) { this.service = service; }

    @PostMapping
    public ResponseEntity<RelatorioResponse> gerar() {
        return ResponseEntity.status(201).body(service.gerar());
    }

    @GetMapping
    public List<RelatorioResponse> listar() { return service.listar(); }

    @GetMapping("/periodo")
    public List<RelatorioResponse> porPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        return service.porPeriodo(inicio, fim);
    }
}
