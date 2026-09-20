package dao;

import db.ConexaoBD;
import model.EquipeManutencao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EquipeManutencaoDAO {
    private static final String INSERT = """
            INSERT INTO EQUIPE_MANUTENCAO (NOME, ESPECIALIDADE, QUANTIDADE_INTEGRANTES, STATUS)
            VALUES (?, ?, ?, ?)
            """;
    private static final String SELECT_BY_ID = """
            SELECT ID, NOME, ESPECIALIDADE, QUANTIDADE_INTEGRANTES, STATUS
            FROM EQUIPE_MANUTENCAO
            WHERE ID = ?
            """;
    private static final String SELECT_ALL = """
            SELECT ID, NOME, ESPECIALIDADE, QUANTIDADE_INTEGRANTES, STATUS
            FROM EQUIPE_MANUTENCAO
            ORDER BY ID
            """;
    private static final String UPDATE = """
            UPDATE EQUIPE_MANUTENCAO
            SET NOME = ?, ESPECIALIDADE = ?, QUANTIDADE_INTEGRANTES = ?, STATUS = ?
            WHERE ID = ?
            """;
    private static final String DELETE = "DELETE FROM EQUIPE_MANUTENCAO WHERE ID = ?";

    public EquipeManutencaoDAO() {
    }

    public EquipeManutencao inserir(EquipeManutencao equipe) throws SQLException {
        Connection conn = ConexaoBD.getInstancia().conectar();
        try (PreparedStatement stmt = conn.prepareStatement(INSERT, new String[]{"ID"})) {
            preencherStatement(stmt, equipe);
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return new EquipeManutencao(rs.getInt(1), equipe.nome(), equipe.especialidade(), equipe.quantidadeIntegrantes(), equipe.status());
                }
            }
        }
        return equipe;
    }

    public Optional<EquipeManutencao> buscarPorId(int id) throws SQLException {
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

    public List<EquipeManutencao> listarTodas() throws SQLException {
        List<EquipeManutencao> equipes = new ArrayList<>();
        Connection conn = ConexaoBD.getInstancia().conectar();
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_ALL);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                equipes.add(mapear(rs));
            }
        }
        return equipes;
    }

    public boolean atualizar(EquipeManutencao equipe) throws SQLException {
        Connection conn = ConexaoBD.getInstancia().conectar();
        try (PreparedStatement stmt = conn.prepareStatement(UPDATE)) {
            preencherStatement(stmt, equipe);
            stmt.setInt(5, equipe.id());
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

    private void preencherStatement(PreparedStatement stmt, EquipeManutencao equipe) throws SQLException {
        stmt.setString(1, equipe.nome());
        stmt.setString(2, equipe.especialidade());
        stmt.setInt(3, equipe.quantidadeIntegrantes());
        stmt.setString(4, equipe.status());
    }

    private EquipeManutencao mapear(ResultSet rs) throws SQLException {
        return new EquipeManutencao(
                rs.getInt("ID"),
                rs.getString("NOME"),
                rs.getString("ESPECIALIDADE"),
                rs.getInt("QUANTIDADE_INTEGRANTES"),
                rs.getString("STATUS")
        );
    }
}
