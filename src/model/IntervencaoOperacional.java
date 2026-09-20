package model;

import java.time.LocalDate;

public record IntervencaoOperacional(
        int id,
        int trechoId,
        int equipeId,
        String tipoServico,
        String prioridade,
        String status,
        LocalDate dataProgramada,
        String observacao
) {
}
