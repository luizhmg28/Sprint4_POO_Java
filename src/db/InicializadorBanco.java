package db;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class InicializadorBanco {
    public void executarScripts(String... nomesArquivos) throws SQLException, IOException {
        for (String nomeArquivo : nomesArquivos) {
            executarScript(localizarArquivo(nomeArquivo));
        }
    }

    private void executarScript(Path caminho) throws SQLException, IOException {
        String conteudo = Files.readString(caminho, StandardCharsets.UTF_8);
        List<String> comandos = separarComandos(conteudo);
        Connection conn = ConexaoBD.getInstancia().conectar();

        try (Statement stmt = conn.createStatement()) {
            for (String comando : comandos) {
                stmt.execute(comando);
            }
        }
    }

    private List<String> separarComandos(String conteudo) {
        List<String> comandos = new ArrayList<>();
        StringBuilder atual = new StringBuilder();
        boolean blocoPlsql = false;

        for (String linha : conteudo.split("\\R")) {
            String texto = linha.trim();

            if (texto.isEmpty() || texto.startsWith("--")) {
                continue;
            }

            if (atual.isEmpty() && ehInicioBlocoPlsql(texto)) {
                blocoPlsql = true;
            }

            if (blocoPlsql && texto.equals("/")) {
                adicionarComando(comandos, atual.toString(), false);
                atual.setLength(0);
                blocoPlsql = false;
                continue;
            }

            atual.append(linha).append(System.lineSeparator());

            if (!blocoPlsql && texto.endsWith(";")) {
                adicionarComando(comandos, atual.toString(), true);
                atual.setLength(0);
            }
        }

        adicionarComando(comandos, atual.toString(), !blocoPlsql);
        return comandos;
    }

    private boolean ehInicioBlocoPlsql(String texto) {
        String normalizado = texto.toUpperCase();
        return normalizado.startsWith("BEGIN")
                || normalizado.startsWith("DECLARE")
                || normalizado.startsWith("CREATE OR REPLACE");
    }

    private void adicionarComando(List<String> comandos, String comando, boolean removerPontoVirgula) {
        String normalizado = comando.trim();
        if (removerPontoVirgula && normalizado.endsWith(";")) {
            normalizado = normalizado.substring(0, normalizado.length() - 1);
        }

        if (!normalizado.isBlank()) {
            comandos.add(normalizado);
        }
    }

    private Path localizarArquivo(String nomeArquivo) throws IOException {
        Path raizAtual = Path.of("").toAbsolutePath();
        Path[] candidatos = {
                raizAtual.resolve(nomeArquivo),
                raizAtual.resolve("Sprint3_POO-Java").resolve(nomeArquivo)
        };

        for (Path candidato : candidatos) {
            if (Files.exists(candidato)) {
                return candidato;
            }
        }

        throw new IOException("Arquivo SQL nao encontrado: " + nomeArquivo);
    }
}
