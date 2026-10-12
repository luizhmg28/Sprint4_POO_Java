package br.com.motiva.dto;

import br.com.motiva.model.TrechoRodovia;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record TrechoResponse(Long id, Integer kmInicial, Integer kmFinal, BigDecimal alturaVegetacao, String tipoAmbiente, Boolean acessoDificil, Long codigoSensor, Boolean monitoradoIoT) {
    public static TrechoResponse from(TrechoRodovia entity) {
        return new TrechoResponse(entity.getId(), entity.getKmInicial(), entity.getKmFinal(), entity.getAlturaVegetacao(), entity.getTipoAmbiente(), entity.getAcessoDificil(), entity.getCodigoSensor(), entity.getMonitoradoIoT());
    }
}
