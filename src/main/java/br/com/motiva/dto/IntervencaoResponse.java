package br.com.motiva.dto;

import br.com.motiva.model.IntervencaoOperacional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record IntervencaoResponse(Long id, Long trechoId, Long equipeId, String tipoServico, String prioridade, String status, LocalDate dataProgramada, String observacao) {
    public static IntervencaoResponse from(IntervencaoOperacional entity) {
        return new IntervencaoResponse(entity.getId(), entity.getTrecho().getId(), entity.getEquipe().getId(), entity.getTipoServico(), entity.getPrioridade(), entity.getStatus(), entity.getDataProgramada(), entity.getObservacao());
    }
}
