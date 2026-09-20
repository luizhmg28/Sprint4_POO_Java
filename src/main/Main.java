package main;

import dao.EquipeManutencaoDAO;
import dao.IntervencaoOperacionalDAO;
import dao.RelatorioPrioridadeDAO;
import dao.TrechoRodoviaDAO;
import db.ConexaoBD;
import db.InicializadorBanco;
import model.EquipeManutencao;
import model.IntervencaoOperacional;
import model.RelatorioPrioridade;
import model.TrechoMonitoradoIoT;
import model.TrechoRodovia;
import service.GeradorRelatorio;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        ConexaoBD conexao = ConexaoBD.getInstancia();

        try {
            conexao.conectar();
            System.out.println("Conexao realizada com sucesso.");

            if (devePrepararBanco(args)) {
                InicializadorBanco inicializador = new InicializadorBanco();
                inicializador.executarScripts("motiva-criacao-banco.sql", "motiva-dados-teste.sql");
                System.out.println("Banco preparado com os scripts do projeto.");
            }

            EquipeManutencaoDAO daoEquipe = new EquipeManutencaoDAO();
            TrechoRodoviaDAO daoTrecho = new TrechoRodoviaDAO();
            IntervencaoOperacionalDAO daoIntervencao = new IntervencaoOperacionalDAO();
            RelatorioPrioridadeDAO daoRelatorio = new RelatorioPrioridadeDAO();

            EquipeManutencao equipe = daoEquipe.inserir(new EquipeManutencao(0, "Equipe Sprint 3", "Rocada", 5, "ATIVA"));
            System.out.println("Equipe inserida: " + equipe);
            System.out.println("Equipe encontrada: " + daoEquipe.buscarPorId(equipe.id()).orElseThrow());

            EquipeManutencao equipeAtualizada = new EquipeManutencao(equipe.id(), "Equipe Sprint 3 Atualizada", "Rocada e Pulverizacao", 6, "ATIVA");
            daoEquipe.atualizar(equipeAtualizada);
            System.out.println("Equipes cadastradas:");
            daoEquipe.listarTodas().forEach(System.out::println);

            TrechoRodovia trecho = daoTrecho.inserir(new TrechoMonitoradoIoT(25, 26, 64.5, "umido", true, 303));
            System.out.println("Trecho inserido: " + trecho.getId());
            System.out.println("Trecho encontrado: " + daoTrecho.buscarPorId(trecho.getId()).orElseThrow().getKmInicial());

            TrechoRodovia trechoAtualizado = new TrechoMonitoradoIoT(trecho.getId(), 25, 26, 72.0, "umido", true, 303);
            daoTrecho.atualizar(trechoAtualizado);
            System.out.println("Trechos cadastrados:");
            daoTrecho.listarTodas().forEach(TrechoRodovia::exibirDados);

            IntervencaoOperacional intervencao = daoIntervencao.inserir(new IntervencaoOperacional(
                    0,
                    trecho.getId(),
                    equipe.id(),
                    "Rocada Manual",
                    "URGENTE",
                    "PROGRAMADA",
                    LocalDate.now().plusDays(3),
                    "Atendimento em trecho monitorado por sensor"
            ));
            System.out.println("Intervencao inserida: " + intervencao);
            System.out.println("Intervencao encontrada: " + daoIntervencao.buscarPorId(intervencao.id()).orElseThrow());

            IntervencaoOperacional intervencaoAtualizada = new IntervencaoOperacional(
                    intervencao.id(),
                    trecho.getId(),
                    equipe.id(),
                    "Rocada Manual",
                    "URGENTE",
                    "EM_ANDAMENTO",
                    LocalDate.now().plusDays(2),
                    "Servico antecipado por prioridade operacional"
            );
            daoIntervencao.atualizar(intervencaoAtualizada);
            System.out.println("Intervencoes cadastradas:");
            daoIntervencao.listarTodas().forEach(System.out::println);

            GeradorRelatorio gerador = new GeradorRelatorio();
            List<TrechoRodovia> trechos = daoTrecho.listarTodas();
            gerador.gerarRelatorio(trechos.toArray(new TrechoRodovia[0]));

            System.out.println("Historico de relatorios:");
            daoRelatorio.listarTodas().forEach(Main::imprimirRelatorio);

            daoIntervencao.deletar(intervencao.id());
            daoTrecho.deletar(trecho.getId());
            daoEquipe.deletar(equipe.id());
            System.out.println("Registros de teste removidos.");
        } catch (SQLException | IOException e) {
            System.err.println("Erro na execucao: " + e.getMessage());
            e.printStackTrace();
        } finally {
            conexao.desconectar();
        }
    }

    private static boolean devePrepararBanco(String[] args) {
        return Arrays.asList(args).contains("--setup-db");
    }

    private static void imprimirRelatorio(RelatorioPrioridade relatorio) {
        System.out.println("Relatorio #" + relatorio.id()
                + " | urgente=" + relatorio.qtUrgente()
                + " | critico=" + relatorio.qtCritico()
                + " | atencao=" + relatorio.qtAtencao()
                + " | normal=" + relatorio.qtNormal());
    }
}
