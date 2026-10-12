package br.com.motiva.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "TRECHO_RODOVIA")
public class TrechoRodovia {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_TRECHO_RODOVIA")
    @SequenceGenerator(name = "SEQ_TRECHO_RODOVIA", sequenceName = "SEQ_TRECHO_RODOVIA", allocationSize = 1)
    private Long id;

    @Column(name = "KM_INICIAL", precision = 6, nullable = false)
    private Integer kmInicial;

    @Column(name = "KM_FINAL", precision = 6, nullable = false)
    private Integer kmFinal;

    @Column(name = "ALTURA_VEGETACAO", precision = 6, scale = 2, nullable = false)
    private BigDecimal alturaVegetacao;

    @Column(name = "TIPO_AMBIENTE", length = 30, nullable = false)
    private String tipoAmbiente;

    @Convert(converter = SimNaoConverter.class)
    @Column(name = "ACESSO_DIFICIL", columnDefinition = "CHAR(1)", nullable = false)
    private Boolean acessoDificil;

    @Column(name = "CODIGO_SENSOR", precision = 10, nullable = true)
    private Long codigoSensor;

    @Convert(converter = SimNaoConverter.class)
    @Column(name = "MONITORADO_IOT", columnDefinition = "CHAR(1)", nullable = false)
    private Boolean monitoradoIoT;

    public TrechoRodovia() {}

    public Long getId() { return id; }
    public Integer getKmInicial() { return kmInicial; }
    public void setKmInicial(Integer kmInicial) { this.kmInicial = kmInicial; }
    public Integer getKmFinal() { return kmFinal; }
    public void setKmFinal(Integer kmFinal) { this.kmFinal = kmFinal; }
    public BigDecimal getAlturaVegetacao() { return alturaVegetacao; }
    public void setAlturaVegetacao(BigDecimal alturaVegetacao) { this.alturaVegetacao = alturaVegetacao; }
    public String getTipoAmbiente() { return tipoAmbiente; }
    public void setTipoAmbiente(String tipoAmbiente) { this.tipoAmbiente = tipoAmbiente; }
    public Boolean getAcessoDificil() { return acessoDificil; }
    public void setAcessoDificil(Boolean acessoDificil) { this.acessoDificil = acessoDificil; }
    public Long getCodigoSensor() { return codigoSensor; }
    public void setCodigoSensor(Long codigoSensor) { this.codigoSensor = codigoSensor; }
    public Boolean getMonitoradoIoT() { return monitoradoIoT; }
    public void setMonitoradoIoT(Boolean monitoradoIoT) { this.monitoradoIoT = monitoradoIoT; }
}
