package dao;

import db.ConexaoBD;
import model.IntervencaoOperacional;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class IntervencaoOperacionalDAO {
    private static final String INSERT = """
            INSERT INTO INTERVENCAO_OPERACIONAL
            (TRECHO_ID, EQUIPE_ID, TIPO_SERVICO, PRIORIDADE, STATUS, DATA_PROGRAMADA, OBSERVACAO)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;
    private static final String SELECT_BY_ID = """
            SELECT ID, TRECHO_ID, EQUIPE_ID, TIPO_SERVICO, PRIORIDADE, STATUS, DATA_PROGRAMADA, OBSERVACAO
            FROM INTERVENCAO_OPERACIONAL
            WHERE ID = ?
            """;
    private static final String SELECT_ALL = """
            SELECT ID, TRECHO_ID, EQUIPE_ID, TIPO_SERVICO, PRIORIDADE, STATUS, DATA_PROGRAMADA, OBSERVACAO
            FROM INTERVENCAO_OPERACIONAL
            ORDER BY ID
            """;
    private static final String UPDATE = """
            UPDATE INTERVENCAO_OPERACIONAL
            SET TRECHO_ID = ?, EQUIPE_ID = ?, TIPO_SERVICO = ?, PRIORIDADE = ?,
                STATUS = ?, DATA_PROGRAMADA = ?, OBSERVACAO = ?
            WHERE ID = ?
            """;
    private static final String DELETE = "DELETE FROM INTERVENCAO_OPERACIONAL WHERE ID = ?";

    public IntervencaoOperacionalDAO() {
    }

    public IntervencaoOperacional inserir(IntervencaoOperacional intervencao) throws SQLException {
        Connection conn = ConexaoBD.getInstancia().conectar();
        try (PreparedStatement stmt = conn.prepareStatement(INSERT, new String[]{"ID"})) {
            preencherStatement(stmt, intervencao);
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return new IntervencaoOperacional(
                            rs.getInt(1),
                            intervencao.trechoId(),
                            intervencao.equipeId(),
                            intervencao.tipoServico(),
                            intervencao.prioridade(),
                            intervencao.status(),
                            intervencao.dataProgramada(),
                            intervencao.observacao()
                    );
                }
            }
        }
        return intervencao;
    }

    public Optional<IntervencaoOperacional> buscarPorId(int id) throws SQLException {
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

    public List<IntervencaoOperacional> listarTodas() throws SQLException {
        List<IntervencaoOperacional> intervencoes = new ArrayList<>();
        Connection conn = ConexaoBD.getInstancia().conectar();
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_ALL);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                intervencoes.add(mapear(rs));
            }
        }
        return intervencoes;
    }

    public boolean atualizar(IntervencaoOperacional intervencao) throws SQLException {
        Connection conn = ConexaoBD.getInstancia().conectar();
        try (PreparedStatement stmt = conn.prepareStatement(UPDATE)) {
            preencherStatement(stmt, intervencao);
            stmt.setInt(8, intervencao.id());
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

    private void preencherStatement(PreparedStatement stmt, IntervencaoOperacional intervencao) throws SQLException {
        stmt.setInt(1, intervencao.trechoId());
        stmt.setInt(2, intervencao.equipeId());
        stmt.setString(3, intervencao.tipoServico());
        stmt.setString(4, intervencao.prioridade());
        stmt.setString(5, intervencao.status());
        stmt.setDate(6, Date.valueOf(intervencao.dataProgramada()));
        stmt.setString(7, intervencao.observacao());
    }

    private IntervencaoOperacional mapear(ResultSet rs) throws SQLException {
        return new IntervencaoOperacional(
                rs.getInt("ID"),
                rs.getInt("TRECHO_ID"),
                rs.getInt("EQUIPE_ID"),
                rs.getString("TIPO_SERVICO"),
                rs.getString("PRIORIDADE"),
                rs.getString("STATUS"),
                rs.getDate("DATA_PROGRAMADA").toLocalDate(),
                rs.getString("OBSERVACAO")
        );
    }
}
