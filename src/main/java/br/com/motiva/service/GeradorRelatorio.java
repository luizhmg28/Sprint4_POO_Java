package br.com.motiva.service;

import br.com.motiva.dto.RelatorioResponse;
import br.com.motiva.model.RelatorioPrioridade;
import br.com.motiva.model.TrechoRodovia;
import br.com.motiva.repository.RelatorioPrioridadeRepository;
import br.com.motiva.repository.TrechoRodoviaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class GeradorRelatorio {
    private final TrechoRodoviaRepository trechos;
    private final RelatorioPrioridadeRepository relatorios;

    public GeradorRelatorio(TrechoRodoviaRepository trechos, RelatorioPrioridadeRepository relatorios) {
        this.trechos = trechos;
        this.relatorios = relatorios;
    }

    public AnalisePrioridade analisar(TrechoRodovia trecho) {
        if (trecho.getAlturaVegetacao().compareTo(new BigDecimal("30")) < 0) {
            return new AnalisePrioridade("NORMAL", "Apenas monitoramento");
        }
        if (trecho.getAlturaVegetacao().compareTo(new BigDecimal("60")) <= 0) {
            return new AnalisePrioridade("ATENCAO", "Pulverizacao");
        }
        if (Boolean.TRUE.equals(trecho.getAcessoDificil())) {
            return new AnalisePrioridade("URGENTE", "Rocada Manual");
        }
        return new AnalisePrioridade("CRITICO", "Rocada Mecanizada");
    }

    @Transactional
    public RelatorioResponse gerar() {
        var atuais = trechos.findAll(Sort.by("id"));
        int urgente = 0, critico = 0, atencao = 0, normal = 0;
        var resumo = new StringBuilder();
        for (var trecho : atuais) {
            var analise = analisar(trecho);
            switch (analise.prioridade()) {
                case "URGENTE" -> urgente++;
                case "CRITICO" -> critico++;
                case "ATENCAO" -> atencao++;
                default -> normal++;
            }
            resumo.append("Trecho KM ").append(trecho.getKmInicial())
                    .append(" ao KM ").append(trecho.getKmFinal()).append(": ")
                    .append(analise.prioridade()).append(" - ")
                    .append(analise.acaoRecomendada()).append("\n");
        }
        if (atuais.isEmpty()) resumo.append("Nenhum trecho cadastrado.");
        if (Math.max(Math.max(urgente, critico), Math.max(atencao, normal)) > 99999) {
            throw new IllegalArgumentException("Quantidade de trechos excede o limite do relatorio.");
        }
        var relatorio = new RelatorioPrioridade();
        relatorio.setDataGeracao(LocalDateTime.now().truncatedTo(java.time.temporal.ChronoUnit.MICROS));
        relatorio.setQtUrgente(urgente);
        relatorio.setQtCritico(critico);
        relatorio.setQtAtencao(atencao);
        relatorio.setQtNormal(normal);
        relatorio.setResumo(resumo.toString());
        return RelatorioResponse.from(relatorios.saveAndFlush(relatorio));
    }

    public List<RelatorioResponse> listar() {
        return relatorios.findAll(Sort.by(Sort.Direction.DESC, "dataGeracao", "id"))
                .stream().map(RelatorioResponse::from).toList();
    }

    public List<RelatorioResponse> porPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        if (inicio == null || fim == null || inicio.isAfter(fim)) {
            throw new IllegalArgumentException("Periodo deve conter inicio e fim em ordem cronologica.");
        }
        return relatorios.findByDataGeracaoBetweenOrderByDataGeracaoDescIdDesc(inicio, fim)
                .stream().map(RelatorioResponse::from).toList();
    }

    public record AnalisePrioridade(String prioridade, String acaoRecomendada) {}
}
