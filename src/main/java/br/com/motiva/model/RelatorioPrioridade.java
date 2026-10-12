package br.com.motiva.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "RELATORIO_PRIORIDADE")
public class RelatorioPrioridade {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_RELATORIO_PRIORIDADE")
    @SequenceGenerator(name = "SEQ_RELATORIO_PRIORIDADE", sequenceName = "SEQ_RELATORIO_PRIORIDADE", allocationSize = 1)
    private Long id;

    @Column(name = "DATA_GERACAO", nullable = false)
    private LocalDateTime dataGeracao;

    @Column(name = "QT_URGENTE", precision = 5, nullable = false)
    private Integer qtUrgente;

    @Column(name = "QT_CRITICO", precision = 5, nullable = false)
    private Integer qtCritico;

    @Column(name = "QT_ATENCAO", precision = 5, nullable = false)
    private Integer qtAtencao;

    @Column(name = "QT_NORMAL", precision = 5, nullable = false)
    private Integer qtNormal;

    @Lob
    @Column(name = "RESUMO", nullable = false)
    private String resumo;

    public RelatorioPrioridade() {}

    public Long getId() { return id; }
    public LocalDateTime getDataGeracao() { return dataGeracao; }
    public void setDataGeracao(LocalDateTime dataGeracao) { this.dataGeracao = dataGeracao; }
    public Integer getQtUrgente() { return qtUrgente; }
    public void setQtUrgente(Integer qtUrgente) { this.qtUrgente = qtUrgente; }
    public Integer getQtCritico() { return qtCritico; }
    public void setQtCritico(Integer qtCritico) { this.qtCritico = qtCritico; }
    public Integer getQtAtencao() { return qtAtencao; }
    public void setQtAtencao(Integer qtAtencao) { this.qtAtencao = qtAtencao; }
    public Integer getQtNormal() { return qtNormal; }
    public void setQtNormal(Integer qtNormal) { this.qtNormal = qtNormal; }
    public String getResumo() { return resumo; }
    public void setResumo(String resumo) { this.resumo = resumo; }
}
