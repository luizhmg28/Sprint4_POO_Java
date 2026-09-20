package model;

import java.time.LocalDateTime;

public record RelatorioPrioridade(
        int id,
        LocalDateTime dataGeracao,
        int qtUrgente,
        int qtCritico,
        int qtAtencao,
        int qtNormal,
        String resumo
) {
}
