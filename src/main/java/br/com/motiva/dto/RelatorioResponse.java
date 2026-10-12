package br.com.motiva.dto;

import br.com.motiva.model.RelatorioPrioridade;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record RelatorioResponse(Long id, LocalDateTime dataGeracao, Integer qtUrgente, Integer qtCritico, Integer qtAtencao, Integer qtNormal, String resumo) {
    public static RelatorioResponse from(RelatorioPrioridade entity) {
        return new RelatorioResponse(entity.getId(), entity.getDataGeracao(), entity.getQtUrgente(), entity.getQtCritico(), entity.getQtAtencao(), entity.getQtNormal(), entity.getResumo());
    }
}
