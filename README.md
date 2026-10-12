# MOTIVA - Sprint 4

API REST para monitoramento de vegetacao em rodovias, equipes e intervencoes.

Java 17, Spring Boot 4.1.1, Spring Data JPA, Maven e Oracle. O driver `ojdbc17` e resolvido pelo Maven.

## Estrutura

```text
src/main/java/br/com/motiva/
  MotivaApplication.java
  model/
  repository/
  service/
  controller/
  dto/
  exception/
src/main/resources/application.properties
src/test/java/br/com/motiva/
src/test/resources/application-test.properties
database/
docs/evidencias/
scripts/
```

Controllers recebem HTTP e chamam Services. Services validam os dados e acessam Repositories. DTOs definem os corpos da API.

## Banco Oracle

Conexao padrao: `jdbc:oracle:thin:@oracle.fiap.com.br:1521:ORCL`.

| Variavel | Uso |
| --- | --- |
| ORACLE_USUARIO | Usuario do schema |
| ORACLE_SENHA | Senha do schema |
| ORACLE_URL | URL opcional para outra instancia |
| PORT | Porta HTTP, padrao 8080 |

O schema existente usa IDs com `IDENTITY`. O script `database/migracao-sequences.sql` converte esses geradores em sequences nomeadas, iniciadas acima do maior ID de cada tabela. Os registros e relacionamentos sao preservados. A execucao deve ocorrer com a API e outros escritores parados.

No SQL Developer, conectado ao schema do projeto, a preparacao do banco existente consiste em executar `database/migracao-sequences.sql` como script (F5).

Para um schema vazio, a ordem e `database/criacao-oracle.sql` e, opcionalmente, `database/dados-teste.sql`. O script de criacao nao remove tabelas e nao deve ser executado sobre o schema existente.

| Entidade | Tabela | Sequence |
| --- | --- | --- |
| TrechoRodovia | TRECHO_RODOVIA | SEQ_TRECHO_RODOVIA |
| EquipeManutencao | EQUIPE_MANUTENCAO | SEQ_EQUIPE_MANUTENCAO |
| IntervencaoOperacional | INTERVENCAO_OPERACIONAL | SEQ_INTERVENCAO_OPERACIONAL |
| RelatorioPrioridade | RELATORIO_PRIORIDADE | SEQ_RELATORIO_PRIORIDADE |

Cada entidade usa `@SequenceGenerator` com `allocationSize = 1`. Booleanos continuam armazenados como `S/N`. Intervencoes usam `@ManyToOne` para trecho e equipe, sem exclusao em cascata.

`ddl-auto=validate` confere o esquema ao iniciar. A aplicacao nao cria nem apaga tabelas automaticamente.

## Execucao

Requisitos: JDK 17 e acesso ao Oracle. O Maven Wrapper baixa o Maven na primeira execucao.

Com as variaveis de ambiente configuradas:

```powershell
.\mvnw.cmd spring-boot:run
```

Com Maven instalado: `mvn spring-boot:run`. No Linux/macOS: `sh mvnw spring-boot:run`.

No IntelliJ, o projeto e importado pelo `pom.xml`, com SDK 17. A classe principal e `br.com.motiva.MotivaApplication`; as variaveis do Oracle pertencem a configuracao de execucao.

Base HTTP: `http://localhost:8080/api`.

## Endpoints

| Metodo | Rota | Sucesso | Erros de entrada |
| --- | --- | --- | --- |
| GET | /api/trechos | 200 | 400 |
| GET | /api/trechos/{id} | 200 | 400, 404 |
| POST | /api/trechos | 201 + Location | 400 |
| PUT | /api/trechos/{id} | 200 | 400, 404 |
| DELETE | /api/trechos/{id} | 204 | 400, 404 |
| GET | /api/equipes | 200 | 400 |
| GET | /api/equipes/{id} | 200 | 400, 404 |
| POST | /api/equipes | 201 + Location | 400 |
| PUT | /api/equipes/{id} | 200 | 400, 404 |
| DELETE | /api/equipes/{id} | 204 | 400, 404 |
| GET | /api/intervencoes | 200 | 400 |
| GET | /api/intervencoes/{id} | 200 | 400, 404 |
| POST | /api/intervencoes | 201 + Location | 400, 404 |
| PUT | /api/intervencoes/{id} | 200 | 400, 404 |
| DELETE | /api/intervencoes/{id} | 204 | 400, 404 |
| POST | /api/relatorios | 201 | 400 |
| GET | /api/relatorios | 200 | - |
| GET | /api/relatorios/periodo?inicio=...&fim=... | 200 | 400 |

Filtros opcionais com derived queries:

- `GET /api/trechos?alturaMinima=60`
- `GET /api/equipes?status=ATIVA`
- `GET /api/intervencoes?tipoServico=Rocada%20Manual`

O periodo recebe data e hora ISO 8601 sem fuso, como `2026-10-12T00:00:00`. Os limites sao inclusivos. O historico usa o horario local do servidor e precisao de microssegundos.

## Validacoes

- KM inicial entre 0 e 999999; KM final maior que o inicial e no maximo 999999.
- Altura entre 0 e 9999.99 cm, com ate duas casas decimais.
- Trecho monitorado exige codigo de sensor positivo, com ate dez digitos.
- Equipe exige nome, especialidade e entre 1 e 999 integrantes.
- Status de equipe: `ATIVA`, `INATIVA`, `EM_CAMPO`.
- Intervencao exige trecho e equipe existentes, tipo de servico, prioridade, status e data.
- Prioridades: `NORMAL`, `ATENCAO`, `CRITICO`, `URGENTE`.
- Status de intervencao: `PROGRAMADA`, `EM_ANDAMENTO`, `CONCLUIDA`, `CANCELADA`.
- Trechos e equipes com intervencoes vinculadas nao podem ser removidos.
- PUT substitui todos os campos do cadastro.

Bean Validation valida os DTOs na entrada e novamente no Service. As regras entre campos ficam no Service. Erros usam `application/problem+json`, com status e descricao; erros de campos tambem incluem `campos`.

## Motor De Prioridade

O motor preserva os limiares do projeto:

| Condicao | Prioridade | Acao |
| --- | --- | --- |
| Altura < 30 cm | NORMAL | Monitoramento |
| 30 cm <= altura <= 60 cm | ATENCAO | Pulverizacao |
| Altura > 60 cm e acesso dificil | URGENTE | Rocada Manual |
| Altura > 60 cm e acesso adequado | CRITICO | Rocada Mecanizada |

`GeradorRelatorio` calcula prioridades e grava totais, resumo e data em uma transacao. Gerar relatorio nao altera medicoes, nao simula sensores e nao executa intervencoes. Sem trechos, os totais sao zero. O historico permanece depois da exclusao dos cadastros.

## Exemplos cURL

Exemplos para Bash, com `jq` para extrair os IDs das respostas. O fluxo PowerShell esta em `scripts/testar-api.ps1`.

```bash
trecho=$(curl -fsS -X POST http://localhost:8080/api/trechos -H 'Content-Type: application/json' -d '{"kmInicial":10,"kmFinal":11,"alturaVegetacao":80,"tipoAmbiente":"umido","acessoDificil":true,"codigoSensor":101,"monitoradoIoT":true}')
trecho_id=$(printf '%s' "$trecho" | jq -r '.id')

equipe=$(curl -fsS -X POST http://localhost:8080/api/equipes -H 'Content-Type: application/json' -d '{"nome":"Equipe Norte","especialidade":"Rocada Manual","quantidadeIntegrantes":4,"status":"ATIVA"}')
equipe_id=$(printf '%s' "$equipe" | jq -r '.id')

corpo=$(jq -n --argjson t "$trecho_id" --argjson e "$equipe_id" '{trechoId:$t,equipeId:$e,tipoServico:"Rocada Manual",prioridade:"URGENTE",status:"PROGRAMADA",dataProgramada:"2026-10-20",observacao:"Inspecao"}')
intervencao=$(curl -fsS -X POST http://localhost:8080/api/intervencoes -H 'Content-Type: application/json' -d "$corpo")
intervencao_id=$(printf '%s' "$intervencao" | jq -r '.id')

curl -i "http://localhost:8080/api/trechos/$trecho_id"
curl -i -X PUT "http://localhost:8080/api/trechos/$trecho_id" -H 'Content-Type: application/json' -d '{"kmInicial":10,"kmFinal":11,"alturaVegetacao":85,"tipoAmbiente":"umido","acessoDificil":true,"codigoSensor":101,"monitoradoIoT":true}'
curl -i -X POST http://localhost:8080/api/relatorios
curl -i http://localhost:8080/api/relatorios
curl -i 'http://localhost:8080/api/relatorios/periodo?inicio=2026-01-01T00:00:00&fim=2026-12-31T23:59:59.999999'
curl -i -X DELETE "http://localhost:8080/api/intervencoes/$intervencao_id"
curl -i -X DELETE "http://localhost:8080/api/equipes/$equipe_id"
curl -i -X DELETE "http://localhost:8080/api/trechos/$trecho_id"
curl -i "http://localhost:8080/api/trechos/$trecho_id"
```

O ultimo GET retorna 404.

## Testes E Registros

```powershell
.\mvnw.cmd clean verify
```

Os testes usam `@SpringBootTest`, MockMvc e H2 em modo Oracle, sem credenciais e sem acessar o banco remoto. Cobrem CRUD, respostas 200/201/204/400/404, filtros, vinculos, validacao no Service, limites do motor, historico e periodo.

`target/evidencias/requisicoes.md` e gerado pelo teste de fluxo completo. Um registro da execucao esta em [docs/evidencias/requisicoes.md](docs/evidencias/requisicoes.md). O fluxo HTTP do script tambem foi executado com H2 local: [api-http-h2.json](docs/evidencias/api-http-h2.json). Esses registros nao comprovam a conexao com o Oracle.

Com a API conectada ao Oracle:

```powershell
.\scripts\testar-api.ps1
```

O script registra as respostas em `target/evidencias/api-http.json`. Cria cadastros temporarios, gera um relatorio e remove apenas os cadastros criados. O relatorio permanece no historico.

O JAR gerado pode ser iniciado com:

```shell
java -jar target/motiva-api-4.0.0.jar
```

## Perguntas De Reflexao

**Por que Repository e uma interface? Quem implementa e quando?**

A interface declara as operacoes e os tipos usados. Ao iniciar o contexto da aplicacao, o Spring Data cria um proxy que fornece o comportamento do repository. As operacoes comuns usam a implementacao do framework; metodos de consulta sao interpretados a partir de seus nomes.

**O DAO morreu na migracao?**

Nao. A responsabilidade de acesso aos dados continua existindo. O Repository assume esse papel e o framework cuida da implementacao que antes era escrita em cada DAO.

**Por que validar a altura no Service?**

A altura negativa e invalida para o dominio, independentemente da origem dos dados. No Service, a regra tambem vale para chamadas feitas fora do HTTP. O Controller cuida do contrato da requisicao.

**Onde esta o SQL de uma consulta pelo tipo?**

O Spring Data interpreta o nome do metodo e monta a consulta JPA. O Hibernate gera o SQL adequado ao Oracle. Neste projeto, `findByTipoServicoOrderByIdAsc` busca intervencoes pelo campo `tipoServico`; nao existe SQL manual no Repository, Service ou Controller.

## Referencias

- [Spring Boot: requisitos](https://docs.spring.io/spring-boot/system-requirements.html)
- [Spring Data JPA: consultas](https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html)
