# MOTIVA - Sprint 3

Projeto Java do sistema MOTIVA para monitoramento de vegetacao em rodovias, evoluido na Sprint 3 com persistencia em banco Oracle usando JDBC puro.

## Objetivo Da Sprint

A Sprint 3 adiciona persistencia aos dados que antes existiam apenas em memoria e no console. O projeto agora conecta no Oracle, cria as tabelas do dominio, executa operacoes CRUD por meio de DAOs e grava o historico dos relatorios de prioridade.

O projeto continua usando JDBC puro, conforme pedido na sprint. Por isso nao foi usado Spring Boot, JPA ou Hibernate.

## Estrutura Do Projeto

- `src/db`: conexao com o banco Oracle e inicializacao dos scripts SQL.
- `src/dao`: classes DAO responsaveis por inserir, buscar, listar, atualizar e deletar dados.
- `src/model`: entidades do sistema, como trecho, equipe, intervencao e relatorio.
- `src/service`: regras de negocio do relatorio e servicos operacionais.
- `src/main`: classe principal usada para demonstrar a sprint.
- `lib/ojdbc17.jar`: driver JDBC do Oracle.
- `motiva-criacao-banco.sql`: script de criacao das tabelas.
- `motiva-dados-teste.sql`: script com dados iniciais para teste.

## Por Que Existe Um Inicializador De Banco

Nas aulas, o professor usou Spring Boot, que consegue executar arquivos SQL automaticamente por convencao. Em um projeto Spring Boot, arquivos como `schema.sql` e `data.sql` podem ser carregados pelo proprio framework.

Nesta sprint, a exigencia e usar JDBC puro. Como JDBC nao executa scripts automaticamente, foi criada a classe `InicializadorBanco`. Ela cumpre uma funcao parecida com a inicializacao automatica do Spring Boot, mas sem usar Spring: o proprio Java le os arquivos `.sql` e envia os comandos para o Oracle usando JDBC.

Esse recurso e acionado apenas quando o programa roda com o argumento `--setup-db`.

## Banco De Dados

As tabelas criadas pelo projeto sao:

- `EQUIPE_MANUTENCAO`
- `TRECHO_RODOVIA`
- `INTERVENCAO_OPERACIONAL`
- `RELATORIO_PRIORIDADE`

O arquivo `motiva-criacao-banco.sql` remove as tabelas antigas, quando existirem, e cria a estrutura novamente. O arquivo `motiva-dados-teste.sql` insere dados iniciais para demonstrar as operacoes.

Use `--setup-db` com cuidado, porque ele recria as tabelas e pode apagar dados anteriores.

## Configuracao No IntelliJ

1. Abra o projeto pela pasta `Sprint 3`.
2. Confirme se o modulo ativo e `Sprint3_POO-Java`.
3. Va em `File > Project Structure`.
4. Em `Project`, selecione JDK 17 ou superior.
5. Em `Modules`, selecione `Sprint3_POO-Java`.
6. Abra a aba `Dependencies`.
7. Confirme se existe a dependencia `lib/ojdbc17.jar`.
8. Se nao existir, clique em `+`, escolha `JARs or directories` e selecione `Sprint3_POO-Java/lib/ojdbc17.jar`.
9. Clique em `Apply` e depois em `OK`.

O erro `Driver Oracle nao encontrado no classpath` acontece quando o `ojdbc17.jar` nao esta nas dependencias do modulo ou da configuracao de execucao.

## Configuracao Da Conexao

A conexao fica em `src/db/ConexaoBD.java`.

Os parametros usados sao:

- `HOST`
- `PORTA`
- `SERVICO`
- `USUARIO`
- `SENHA`

Tambem e possivel configurar por variaveis de ambiente:

- `ORACLE_HOST`
- `ORACLE_PORTA`
- `ORACLE_SERVICO`
- `ORACLE_USUARIO`
- `ORACLE_SENHA`

Se as variaveis de ambiente nao forem configuradas, o sistema usa os valores padrao escritos em `ConexaoBD.java`.

## Primeira Execucao Pelo IntelliJ

1. Abra `src/main/Main.java`.
2. Clique com o botao direito no arquivo.
3. Escolha `Run 'Main.main()'`.
4. Depois va em `Run > Edit Configurations`.
5. Selecione a configuracao `Main`.
6. No campo `Program arguments`, coloque:

```text
--setup-db
```

7. Confira se o `Working directory` esta apontando para a pasta `Sprint 3` ou para `Sprint3_POO-Java`.
8. Clique em `Apply`.
9. Clique em `OK`.
10. Rode o `Main` novamente.

Com `--setup-db`, o programa conecta no Oracle, executa `motiva-criacao-banco.sql`, executa `motiva-dados-teste.sql` e depois roda a demonstracao dos DAOs.

## Execucoes Depois Da Primeira

Depois que o banco ja estiver criado, remova o argumento:

```text
--setup-db
```

Assim o programa nao recria as tabelas toda vez. Ele apenas conecta no banco, executa os testes de CRUD e grava um novo relatorio.

## O Que O Main Demonstra

A classe `main.Main` executa:

- teste de conexao com Oracle;
- CRUD de `EquipeManutencao`;
- CRUD de `TrechoRodovia`;
- CRUD de `IntervencaoOperacional`;
- geracao de relatorio no console;
- gravacao do relatorio em `RELATORIO_PRIORIDADE`;
- listagem do historico de relatorios;
- remocao dos registros temporarios criados durante o teste.

## Execucao Pelo Terminal

Na pasta `Sprint3_POO-Java`, compile com:

```powershell
javac -encoding UTF-8 -cp "lib\ojdbc17.jar" -d out (Get-ChildItem src -Filter *.java -Recurse).FullName
```

Para preparar o banco e rodar:

```powershell
java -cp "out;lib\ojdbc17.jar" main.Main --setup-db
```

Para rodar sem recriar o banco:

```powershell
java -cp "out;lib\ojdbc17.jar" main.Main
```

## Verificacao No Oracle

Depois de rodar o projeto, consulte:

```sql
SELECT COUNT(*) FROM EQUIPE_MANUTENCAO;
SELECT COUNT(*) FROM TRECHO_RODOVIA;
SELECT COUNT(*) FROM INTERVENCAO_OPERACIONAL;
SELECT COUNT(*) FROM RELATORIO_PRIORIDADE;
```

Para conferir os relatorios gravados:

```sql
SELECT ID, DATA_GERACAO, QT_URGENTE, QT_CRITICO, QT_ATENCAO, QT_NORMAL
FROM RELATORIO_PRIORIDADE
ORDER BY ID DESC;
```

## Problemas Comuns

- `Driver Oracle nao encontrado no classpath`: adicione `lib/ojdbc17.jar` nas dependencias do modulo.
- `ORA-01017`: usuario ou senha incorretos.
- `ORA-00942`: as tabelas ainda nao foram criadas; rode com `--setup-db`.
- `ORA-02292`: existe registro relacionado por chave estrangeira.
- `Arquivo SQL nao encontrado`: confira o `Working directory` da configuracao de execucao.
- Erro com `record`: o projeto esta rodando com Java anterior ao 16; use JDK 17 ou superior.
