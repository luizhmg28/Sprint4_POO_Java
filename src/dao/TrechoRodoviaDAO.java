package dao;

import db.ConexaoBD;
import model.TrechoMonitoradoIoT;
import model.TrechoRodovia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TrechoRodoviaDAO {
    private static final String INSERT = """
            INSERT INTO TRECHO_RODOVIA
            (KM_INICIAL, KM_FINAL, ALTURA_VEGETACAO, TIPO_AMBIENTE, ACESSO_DIFICIL, CODIGO_SENSOR, MONITORADO_IOT)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;
    private static final String SELECT_BY_ID = """
            SELECT ID, KM_INICIAL, KM_FINAL, ALTURA_VEGETACAO, TIPO_AMBIENTE, ACESSO_DIFICIL, CODIGO_SENSOR, MONITORADO_IOT
            FROM TRECHO_RODOVIA
            WHERE ID = ?
            """;
    private static final String SELECT_ALL = """
            SELECT ID, KM_INICIAL, KM_FINAL, ALTURA_VEGETACAO, TIPO_AMBIENTE, ACESSO_DIFICIL, CODIGO_SENSOR, MONITORADO_IOT
            FROM TRECHO_RODOVIA
            ORDER BY ID
            """;
    private static final String UPDATE = """
            UPDATE TRECHO_RODOVIA
            SET KM_INICIAL = ?, KM_FINAL = ?, ALTURA_VEGETACAO = ?, TIPO_AMBIENTE = ?,
                ACESSO_DIFICIL = ?, CODIGO_SENSOR = ?, MONITORADO_IOT = ?
            WHERE ID = ?
            """;
    private static final String DELETE = "DELETE FROM TRECHO_RODOVIA WHERE ID = ?";

    public TrechoRodoviaDAO() {
    }

    public TrechoRodovia inserir(TrechoRodovia trecho) throws SQLException {
        Connection conn = ConexaoBD.getInstancia().conectar();
        try (PreparedStatement stmt = conn.prepareStatement(INSERT, new String[]{"ID"})) {
            preencherStatement(stmt, trecho);
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return trecho.comId(rs.getInt(1));
                }
            }
        }
        return trecho;
    }

    public Optional<TrechoRodovia> buscarPorId(int id) throws SQLException {
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

    public List<TrechoRodovia> listarTodas() throws SQLException {
        List<TrechoRodovia> trechos = new ArrayList<>();
        Connection conn = ConexaoBD.getInstancia().conectar();
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_ALL);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                trechos.add(mapear(rs));
            }
        }
        return trechos;
    }

    public boolean atualizar(TrechoRodovia trecho) throws SQLException {
        Connection conn = ConexaoBD.getInstancia().conectar();
        try (PreparedStatement stmt = conn.prepareStatement(UPDATE)) {
            preencherStatement(stmt, trecho);
            stmt.setInt(8, trecho.getId());
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

    private void preencherStatement(PreparedStatement stmt, TrechoRodovia trecho) throws SQLException {
        stmt.setInt(1, trecho.getKmInicial());
        stmt.setInt(2, trecho.getKmFinal());
        stmt.setDouble(3, trecho.getAlturaVegetacao());
        stmt.setString(4, trecho.getTipoAmbiente());
        stmt.setString(5, trecho.isAcessoDificil() ? "S" : "N");

        if (trecho.getCodigoSensor() == null) {
            stmt.setNull(6, Types.NUMERIC);
        } else {
            stmt.setInt(6, trecho.getCodigoSensor());
        }

        stmt.setString(7, trecho.isMonitoradoIoT() ? "S" : "N");
    }

    private TrechoRodovia mapear(ResultSet rs) throws SQLException {
        boolean monitorado = "S".equalsIgnoreCase(rs.getString("MONITORADO_IOT"));
        int id = rs.getInt("ID");
        int kmInicial = rs.getInt("KM_INICIAL");
        int kmFinal = rs.getInt("KM_FINAL");
        double alturaVegetacao = rs.getDouble("ALTURA_VEGETACAO");
        String tipoAmbiente = rs.getString("TIPO_AMBIENTE");
        boolean acessoDificil = "S".equalsIgnoreCase(rs.getString("ACESSO_DIFICIL"));

        if (monitorado) {
            return new TrechoMonitoradoIoT(id, kmInicial, kmFinal, alturaVegetacao, tipoAmbiente, acessoDificil, rs.getInt("CODIGO_SENSOR"));
        }

        return new TrechoRodovia(id, kmInicial, kmFinal, alturaVegetacao, tipoAmbiente, acessoDificil);
    }
}
