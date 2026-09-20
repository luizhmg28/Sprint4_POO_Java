package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoBD {
    private static final String DRIVER = "oracle.jdbc.driver.OracleDriver";
    private static final String HOST = valorAmbiente("ORACLE_HOST", "oracle.fiap.com.br");
    private static final String PORTA = valorAmbiente("ORACLE_PORTA", "1521");
    private static final String SERVICO = valorAmbiente("ORACLE_SERVICO", "ORCL");
    private static final String USUARIO = valorAmbiente("ORACLE_USUARIO", "RM______");
    private static final String SENHA = valorAmbiente("ORACLE_SENHA", "______");
    private static final String URL = "jdbc:oracle:thin:@//" + HOST + ":" + PORTA + "/" + SERVICO;

    private static ConexaoBD instancia;
    private Connection connection;

    private ConexaoBD() {
    }

    public static ConexaoBD getInstancia() {
        if (instancia == null) {
            instancia = new ConexaoBD();
        }
        return instancia;
    }

    public Connection conectar() throws SQLException {
        if (connection == null || connection.isClosed()) {
            carregarDriver();
            connection = DriverManager.getConnection(URL, USUARIO, SENHA);
        }
        return connection;
    }

    public void desconectar() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Erro ao fechar conexao: " + e.getMessage());
            }
        }
    }

    private void carregarDriver() throws SQLException {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver Oracle nao encontrado no classpath.", e);
        }
    }

    private static String valorAmbiente(String chave, String valorPadrao) {
        String valor = System.getenv(chave);
        return valor == null || valor.isBlank() ? valorPadrao : valor;
    }
}
