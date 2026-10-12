package br.com.motiva.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "EQUIPE_MANUTENCAO")
public class EquipeManutencao {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_EQUIPE_MANUTENCAO")
    @SequenceGenerator(name = "SEQ_EQUIPE_MANUTENCAO", sequenceName = "SEQ_EQUIPE_MANUTENCAO", allocationSize = 1)
    private Long id;

    @Column(name = "NOME", length = 100, nullable = false)
    private String nome;

    @Column(name = "ESPECIALIDADE", length = 80, nullable = false)
    private String especialidade;

    @Column(name = "QUANTIDADE_INTEGRANTES", precision = 3, nullable = false)
    private Integer quantidadeIntegrantes;

    @Column(name = "STATUS", length = 30, nullable = false)
    private String status;

    public EquipeManutencao() {}

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEspecialidade() { return especialidade; }
    public void setEspecialidade(String especialidade) { this.especialidade = especialidade; }
    public Integer getQuantidadeIntegrantes() { return quantidadeIntegrantes; }
    public void setQuantidadeIntegrantes(Integer quantidadeIntegrantes) { this.quantidadeIntegrantes = quantidadeIntegrantes; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
