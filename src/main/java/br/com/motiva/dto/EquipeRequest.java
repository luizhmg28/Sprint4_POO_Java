package br.com.motiva.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record EquipeRequest(
        @NotBlank @Size(max = 100) String nome,
        @NotBlank @Size(max = 80) String especialidade,
        @NotNull @Min(1) @Max(999) Integer quantidadeIntegrantes,
        @NotBlank @Pattern(regexp = "ATIVA|INATIVA|EM_CAMPO") String status
) {}
