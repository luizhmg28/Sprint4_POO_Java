package br.com.motiva.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "INTERVENCAO_OPERACIONAL")
public class IntervencaoOperacional {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_INTERVENCAO_OPERACIONAL")
    @SequenceGenerator(name = "SEQ_INTERVENCAO_OPERACIONAL", sequenceName = "SEQ_INTERVENCAO_OPERACIONAL", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TRECHO_ID", nullable = false)
    private TrechoRodovia trecho;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "EQUIPE_ID", nullable = false)
    private EquipeManutencao equipe;

    @Column(name = "TIPO_SERVICO", length = 80, nullable = false)
    private String tipoServico;

    @Column(name = "PRIORIDADE", length = 20, nullable = false)
    private String prioridade;

    @Column(name = "STATUS", length = 30, nullable = false)
    private String status;

    @Column(name = "DATA_PROGRAMADA", nullable = false)
    private LocalDate dataProgramada;

    @Column(name = "OBSERVACAO", length = 300, nullable = true)
    private String observacao;

    public IntervencaoOperacional() {}

    public Long getId() { return id; }
    public TrechoRodovia getTrecho() { return trecho; }
    public void setTrecho(TrechoRodovia trecho) { this.trecho = trecho; }
    public EquipeManutencao getEquipe() { return equipe; }
    public void setEquipe(EquipeManutencao equipe) { this.equipe = equipe; }
    public String getTipoServico() { return tipoServico; }
    public void setTipoServico(String tipoServico) { this.tipoServico = tipoServico; }
    public String getPrioridade() { return prioridade; }
    public void setPrioridade(String prioridade) { this.prioridade = prioridade; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDate getDataProgramada() { return dataProgramada; }
    public void setDataProgramada(LocalDate dataProgramada) { this.dataProgramada = dataProgramada; }
    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
}
