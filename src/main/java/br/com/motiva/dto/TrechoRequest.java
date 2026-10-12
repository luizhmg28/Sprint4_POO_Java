package br.com.motiva.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record TrechoRequest(
        @NotNull @Min(0) @Max(999999) Integer kmInicial,
        @NotNull @Min(1) @Max(999999) Integer kmFinal,
        @NotNull @DecimalMin("0.0") @Digits(integer = 4, fraction = 2) BigDecimal alturaVegetacao,
        @NotBlank @Size(max = 30) String tipoAmbiente,
        @NotNull Boolean acessoDificil,
        @Positive @Max(9999999999L) Long codigoSensor,
        @NotNull Boolean monitoradoIoT
) {}
