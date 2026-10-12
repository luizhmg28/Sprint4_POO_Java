package br.com.motiva.dto;

import br.com.motiva.model.EquipeManutencao;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record EquipeResponse(Long id, String nome, String especialidade, Integer quantidadeIntegrantes, String status) {
    public static EquipeResponse from(EquipeManutencao entity) {
        return new EquipeResponse(entity.getId(), entity.getNome(), entity.getEspecialidade(), entity.getQuantidadeIntegrantes(), entity.getStatus());
    }
}
