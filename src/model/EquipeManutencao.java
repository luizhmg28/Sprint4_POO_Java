package model;

public record EquipeManutencao(
        int id,
        String nome,
        String especialidade,
        int quantidadeIntegrantes,
        String status
) {
}
