package service;

import dao.RelatorioPrioridadeDAO;
import model.MonitoravelViaIoT;
import model.TrechoRodovia;

import java.sql.SQLException;

public class GeradorRelatorio {
    public void gerarRelatorio(TrechoRodovia[] trechos) throws SQLException {
        int qtUrgente = 0;
        int qtCritico = 0;
        int qtAtencao = 0;
        int qtNormal = 0;
        StringBuilder resumo = new StringBuilder();

        imprimirCabecalho();

        for (int i = 0; i < trechos.length; i++) {
            TrechoRodovia trecho = trechos[i];
            if (trecho instanceof MonitoravelViaIoT monitorado) {
                monitorado.transmitirDadosSensor();
                System.out.println();
            }

            AnalisePrioridade analise = analisarTrecho(trecho);

            switch (analise.prioridade()) {
                case "URGENTE" -> qtUrgente++;
                case "CRITICO" -> qtCritico++;
                case "ATENCAO" -> qtAtencao++;
                default -> qtNormal++;
            }

            imprimirTrecho(i + 1, trecho, analise);
            resumo.append("Trecho KM ")
                    .append(trecho.getKmInicial())
                    .append(" ao KM ")
                    .append(trecho.getKmFinal())
                    .append(": ")
                    .append(analise.prioridade())
                    .append(" - ")
                    .append(analise.acaoRecomendada())
                    .append(System.lineSeparator());
        }

        imprimirResumo(qtUrgente, qtCritico, qtAtencao, qtNormal);

        RelatorioPrioridadeDAO dao = new RelatorioPrioridadeDAO();
        dao.salvarRelatorio(qtUrgente, qtCritico, qtAtencao, qtNormal, resumo.toString());
    }

    private void imprimirCabecalho() {
        System.out.println("========================================");
        System.out.println(" RELATORIO DE PRIORIDADE DE ROCADA");
        System.out.println("========================================");
    }

    private void imprimirTrecho(int numero, TrechoRodovia trecho, AnalisePrioridade analise) {
        System.out.println();
        System.out.println("Trecho " + numero);
        System.out.println("----------------------------------------");
        trecho.exibirDados();
        System.out.println("Prioridade: " + analise.prioridade());
        System.out.println("Motivo: " + analise.motivo());
        System.out.println("Acao recomendada: " + analise.acaoRecomendada());

        if (analise.intervencao() != null) {
            analise.intervencao().executarServico(trecho);
        }

        System.out.println("----------------------------------------");
    }

    private void imprimirResumo(int qtUrgente, int qtCritico, int qtAtencao, int qtNormal) {
        System.out.println();
        System.out.println("Resumo");
        System.out.println("Urgente: " + qtUrgente);
        System.out.println("Critico: " + qtCritico);
        System.out.println("Atencao: " + qtAtencao);
        System.out.println("Normal: " + qtNormal);
    }

    private AnalisePrioridade analisarTrecho(TrechoRodovia trecho) {
        double altura = trecho.getAlturaVegetacao();

        if (altura < 30) {
            return new AnalisePrioridade(
                    "NORMAL",
                    "vegetacao abaixo de 30 cm, sem risco operacional no momento.",
                    "Apenas monitoramento.",
                    null
            );
        }

        if (altura <= 60) {
            ServicoOperacional intervencao = new Pulverizacao();
            return new AnalisePrioridade(
                    "ATENCAO",
                    "vegetacao entre 30 cm e 60 cm.",
                    intervencao.getNomeServico() + ".",
                    intervencao
            );
        }

        if (trecho.isAcessoDificil()) {
            ServicoOperacional intervencao = new RocadaManual();
            return new AnalisePrioridade(
                    "URGENTE",
                    "vegetacao acima de 60 cm em trecho de dificil acesso.",
                    intervencao.getNomeServico() + ".",
                    intervencao
            );
        }

        ServicoOperacional intervencao = new RocadaMecanizada();
        return new AnalisePrioridade(
                "CRITICO",
                "vegetacao acima de 60 cm em trecho com acesso adequado para equipamento.",
                intervencao.getNomeServico() + ".",
                intervencao
        );
    }

    private record AnalisePrioridade(
            String prioridade,
            String motivo,
            String acaoRecomendada,
            ServicoOperacional intervencao
    ) {
    }
}
