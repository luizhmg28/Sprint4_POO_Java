# Registro de requisicoes

Spring Boot + MockMvc + JPA. Perfil test, H2 em modo Oracle.

### POST /api/trechos

Parametros: []

Corpo enviado:

```json
{"kmInicial":10,"kmFinal":11,"alturaVegetacao":80,"tipoAmbiente":"umido",
 "acessoDificil":true,"codigoSensor":101,"monitoradoIoT":true}

```

HTTP 201

```json
{"id":8,"kmInicial":10,"kmFinal":11,"alturaVegetacao":80,"tipoAmbiente":"umido","acessoDificil":true,"codigoSensor":101,"monitoradoIoT":true}
```

### POST /api/equipes

Parametros: []

Corpo enviado:

```json
{"nome":"Equipe API","especialidade":"Rocada Manual","quantidadeIntegrantes":4,"status":"ATIVA"}

```

HTTP 201

```json
{"id":2,"nome":"Equipe API","especialidade":"Rocada Manual","quantidadeIntegrantes":4,"status":"ATIVA"}
```

### POST /api/intervencoes

Parametros: []

Corpo enviado:

```json
{"trechoId":8,"equipeId":2,"tipoServico":"Rocada Manual","prioridade":"URGENTE",
 "status":"PROGRAMADA","dataProgramada":"2026-10-20","observacao":"Inspecao"}

```

HTTP 201

```json
{"id":2,"trechoId":8,"equipeId":2,"tipoServico":"Rocada Manual","prioridade":"URGENTE","status":"PROGRAMADA","dataProgramada":"2026-10-20","observacao":"Inspecao"}
```

### GET /api/trechos/8

Parametros: []

Corpo enviado:

```json
null
```

HTTP 200

```json
{"id":8,"kmInicial":10,"kmFinal":11,"alturaVegetacao":80,"tipoAmbiente":"umido","acessoDificil":true,"codigoSensor":101,"monitoradoIoT":true}
```

### PUT /api/trechos/8

Parametros: []

Corpo enviado:

```json
{"kmInicial":10,"kmFinal":11,"alturaVegetacao":85,"tipoAmbiente":"umido",
 "acessoDificil":true,"codigoSensor":101,"monitoradoIoT":true}

```

HTTP 200

```json
{"id":8,"kmInicial":10,"kmFinal":11,"alturaVegetacao":85,"tipoAmbiente":"umido","acessoDificil":true,"codigoSensor":101,"monitoradoIoT":true}
```

### PUT /api/equipes/2

Parametros: []

Corpo enviado:

```json
{"nome":"Equipe Atualizada","especialidade":"Rocada Manual","quantidadeIntegrantes":4,"status":"ATIVA"}

```

HTTP 200

```json
{"id":2,"nome":"Equipe Atualizada","especialidade":"Rocada Manual","quantidadeIntegrantes":4,"status":"ATIVA"}
```

### PUT /api/intervencoes/2

Parametros: []

Corpo enviado:

```json
{"trechoId":8,"equipeId":2,"tipoServico":"Rocada Manual","prioridade":"URGENTE",
 "status":"CONCLUIDA","dataProgramada":"2026-10-20","observacao":"Inspecao"}

```

HTTP 200

```json
{"id":2,"trechoId":8,"equipeId":2,"tipoServico":"Rocada Manual","prioridade":"URGENTE","status":"CONCLUIDA","dataProgramada":"2026-10-20","observacao":"Inspecao"}
```

### GET /api/equipes/2

Parametros: []

Corpo enviado:

```json
null
```

HTTP 200

```json
{"id":2,"nome":"Equipe Atualizada","especialidade":"Rocada Manual","quantidadeIntegrantes":4,"status":"ATIVA"}
```

### GET /api/intervencoes/2

Parametros: []

Corpo enviado:

```json
null
```

HTTP 200

```json
{"id":2,"trechoId":8,"equipeId":2,"tipoServico":"Rocada Manual","prioridade":"URGENTE","status":"CONCLUIDA","dataProgramada":"2026-10-20","observacao":"Inspecao"}
```

### POST /api/relatorios

Parametros: []

Corpo enviado:

```json
null
```

HTTP 201

```json
{"id":4,"dataGeracao":"2026-10-12T17:35:33.422305","qtUrgente":1,"qtCritico":0,"qtAtencao":0,"qtNormal":0,"resumo":"Trecho KM 10 ao KM 11: URGENTE - Rocada Manual\n"}
```

### GET /api/relatorios

Parametros: []

Corpo enviado:

```json
null
```

HTTP 200

```json
[{"id":4,"dataGeracao":"2026-10-12T17:35:33.422305","qtUrgente":1,"qtCritico":0,"qtAtencao":0,"qtNormal":0,"resumo":"Trecho KM 10 ao KM 11: URGENTE - Rocada Manual\n"}]
```

### GET /api/relatorios/periodo

Parametros: [inicio=2020-01-01T00:00:00, fim=2100-01-01T00:00:00]

Corpo enviado:

```json
null
```

HTTP 200

```json
[{"id":4,"dataGeracao":"2026-10-12T17:35:33.422305","qtUrgente":1,"qtCritico":0,"qtAtencao":0,"qtNormal":0,"resumo":"Trecho KM 10 ao KM 11: URGENTE - Rocada Manual\n"}]
```

### DELETE /api/trechos/8

Parametros: []

Corpo enviado:

```json
null
```

HTTP 400

```json
{"detail":"Trecho possui intervencoes vinculadas.","instance":"/api/trechos/8","status":400,"title":"Bad Request"}
```

### DELETE /api/equipes/2

Parametros: []

Corpo enviado:

```json
null
```

HTTP 400

```json
{"detail":"Equipe possui intervencoes vinculadas.","instance":"/api/equipes/2","status":400,"title":"Bad Request"}
```

### DELETE /api/intervencoes/2

Parametros: []

Corpo enviado:

```json
null
```

HTTP 204

```json

```

### DELETE /api/equipes/2

Parametros: []

Corpo enviado:

```json
null
```

HTTP 204

```json

```

### DELETE /api/trechos/8

Parametros: []

Corpo enviado:

```json
null
```

HTTP 204

```json

```

### GET /api/trechos/8

Parametros: []

Corpo enviado:

```json
null
```

HTTP 404

```json
{"detail":"Trecho 8 nao encontrado.","instance":"/api/trechos/8","status":404,"title":"Not Found"}
```
