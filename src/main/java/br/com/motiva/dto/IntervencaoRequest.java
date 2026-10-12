package br.com.motiva.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record IntervencaoRequest(
        @NotNull @Positive Long trechoId,
        @NotNull @Positive Long equipeId,
        @NotBlank @Size(max = 80) String tipoServico,
        @NotBlank @Pattern(regexp = "URGENTE|CRITICO|ATENCAO|NORMAL") String prioridade,
        @NotBlank @Pattern(regexp = "PROGRAMADA|EM_ANDAMENTO|CONCLUIDA|CANCELADA") String status,
        @NotNull LocalDate dataProgramada,
        @Size(max = 300) String observacao
) {}
