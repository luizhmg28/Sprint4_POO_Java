package dao;

import db.ConexaoBD;
import model.RelatorioPrioridade;

import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RelatorioPrioridadeDAO {
    private static final String INSERT = """
            INSERT INTO RELATORIO_PRIORIDADE
            (DATA_GERACAO, QT_URGENTE, QT_CRITICO, QT_ATENCAO, QT_NORMAL, RESUMO)
            VALUES (SYSTIMESTAMP, ?, ?, ?, ?, ?)
            """;
    private static final String SELECT_BY_ID = """
            SELECT ID, DATA_GERACAO, QT_URGENTE, QT_CRITICO, QT_ATENCAO, QT_NORMAL, RESUMO
            FROM RELATORIO_PRIORIDADE
            WHERE ID = ?
            """;
    private static final String SELECT_ALL = """
            SELECT ID, DATA_GERACAO, QT_URGENTE, QT_CRITICO, QT_ATENCAO, QT_NORMAL, RESUMO
            FROM RELATORIO_PRIORIDADE
            ORDER BY DATA_GERACAO DESC, ID DESC
            """;
    private static final String UPDATE = """
            UPDATE RELATORIO_PRIORIDADE
            SET QT_URGENTE = ?, QT_CRITICO = ?, QT_ATENCAO = ?, QT_NORMAL = ?, RESUMO = ?
            WHERE ID = ?
            """;
    private static final String DELETE = "DELETE FROM RELATORIO_PRIORIDADE WHERE ID = ?";

    public RelatorioPrioridadeDAO() {
    }

    public RelatorioPrioridade salvarRelatorio(int qtUrgente, int qtCritico, int qtAtencao, int qtNormal, String resumo) throws SQLException {
        return inserir(new RelatorioPrioridade(0, null, qtUrgente, qtCritico, qtAtencao, qtNormal, resumo));
    }

    public RelatorioPrioridade inserir(RelatorioPrioridade relatorio) throws SQLException {
        Connection conn = ConexaoBD.getInstancia().conectar();
        try (PreparedStatement stmt = conn.prepareStatement(INSERT, new String[]{"ID"})) {
            preencherStatement(stmt, relatorio);
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return buscarPorId(rs.getInt(1)).orElse(relatorio);
                }
            }
        }
        return relatorio;
    }

    public Optional<RelatorioPrioridade> buscarPorId(int id) throws SQLException {
        Connection conn = ConexaoBD.getInstancia().conectar();
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapear(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<RelatorioPrioridade> listarTodas() throws SQLException {
        List<RelatorioPrioridade> relatorios = new ArrayList<>();
        Connection conn = ConexaoBD.getInstancia().conectar();
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_ALL);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                relatorios.add(mapear(rs));
            }
        }
        return relatorios;
    }

    public boolean atualizar(RelatorioPrioridade relatorio) throws SQLException {
        Connection conn = ConexaoBD.getInstancia().conectar();
        try (PreparedStatement stmt = conn.prepareStatement(UPDATE)) {
            preencherStatement(stmt, relatorio);
            stmt.setInt(6, relatorio.id());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean deletar(int id) throws SQLException {
        Connection conn = ConexaoBD.getInstancia().conectar();
        try (PreparedStatement stmt = conn.prepareStatement(DELETE)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    private void preencherStatement(PreparedStatement stmt, RelatorioPrioridade relatorio) throws SQLException {
        stmt.setInt(1, relatorio.qtUrgente());
        stmt.setInt(2, relatorio.qtCritico());
        stmt.setInt(3, relatorio.qtAtencao());
        stmt.setInt(4, relatorio.qtNormal());
        stmt.setString(5, relatorio.resumo());
    }

    private RelatorioPrioridade mapear(ResultSet rs) throws SQLException {
        Timestamp timestamp = rs.getTimestamp("DATA_GERACAO");
        return new RelatorioPrioridade(
                rs.getInt("ID"),
                timestamp == null ? null : timestamp.toLocalDateTime(),
                rs.getInt("QT_URGENTE"),
                rs.getInt("QT_CRITICO"),
                rs.getInt("QT_ATENCAO"),
                rs.getInt("QT_NORMAL"),
                lerClob(rs.getClob("RESUMO"))
        );
    }

    private String lerClob(Clob clob) throws SQLException {
        if (clob == null) {
            return "";
        }
        return clob.getSubString(1, (int) clob.length());
    }
}
