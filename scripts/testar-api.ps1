param([string]$BaseUrl = 'http://localhost:8080')

$ErrorActionPreference = 'Stop'
$registros = [System.Collections.Generic.List[object]]::new()
$ids = @{}

function Invoke-Api {
    param([string]$Metodo, [string]$Rota, [object]$Corpo, [int]$Status)
    $parametros = @{ Uri = "$BaseUrl$Rota"; Method = $Metodo; UseBasicParsing = $true }
    if ($null -ne $Corpo) {
        $json = $Corpo | ConvertTo-Json -Depth 10 -Compress
        $parametros.ContentType = 'application/json; charset=utf-8'
        $parametros.Body = [System.Text.Encoding]::UTF8.GetBytes($json)
    }
    $resposta = Invoke-WebRequest @parametros
    $registros.Add([ordered]@{
        data = (Get-Date).ToString('o')
        metodo = $Metodo
        rota = $Rota
        corpo = $(if ($null -ne $Corpo) { $json | ConvertFrom-Json } else { $null })
        status = [int]$resposta.StatusCode
        resposta = $resposta.Content
    })
    if ([int]$resposta.StatusCode -ne $Status) {
        throw "HTTP $($resposta.StatusCode), esperado $Status em $Metodo $Rota"
    }
    if ($resposta.Content) { return $resposta.Content | ConvertFrom-Json }
}

try {
    $trecho = @{
        kmInicial = 10; kmFinal = 11; alturaVegetacao = 80; tipoAmbiente = 'umido'
        acessoDificil = $true; codigoSensor = 101; monitoradoIoT = $true
    }
    $ids.trecho = (Invoke-Api POST '/api/trechos' $trecho 201).id
    $equipe = @{ nome = 'Equipe teste API'; especialidade = 'Rocada Manual'; quantidadeIntegrantes = 4; status = 'ATIVA' }
    $ids.equipe = (Invoke-Api POST '/api/equipes' $equipe 201).id
    $intervencao = @{
        trechoId = $ids.trecho; equipeId = $ids.equipe; tipoServico = 'Rocada Manual'
        prioridade = 'URGENTE'; status = 'PROGRAMADA'; dataProgramada = (Get-Date).AddDays(1).ToString('yyyy-MM-dd')
        observacao = 'Teste HTTP'
    }
    $ids.intervencao = (Invoke-Api POST '/api/intervencoes' $intervencao 201).id
    $null = Invoke-Api GET "/api/trechos/$($ids.trecho)" $null 200
    $trecho.alturaVegetacao = 85
    $null = Invoke-Api PUT "/api/trechos/$($ids.trecho)" $trecho 200
    $equipe.nome = 'Equipe teste atualizada'
    $null = Invoke-Api PUT "/api/equipes/$($ids.equipe)" $equipe 200
    $intervencao.status = 'CONCLUIDA'
    $null = Invoke-Api PUT "/api/intervencoes/$($ids.intervencao)" $intervencao 200
    $null = Invoke-Api POST '/api/relatorios' $null 201
    $null = Invoke-Api GET '/api/relatorios' $null 200
    $inicio = (Get-Date).Date.ToString('yyyy-MM-ddTHH:mm:ss')
    $fim = (Get-Date).Date.AddDays(1).ToString('yyyy-MM-ddTHH:mm:ss')
    $null = Invoke-Api GET "/api/relatorios/periodo?inicio=$inicio&fim=$fim" $null 200
} finally {
    try {
        if ($ids.intervencao) { $null = Invoke-Api DELETE "/api/intervencoes/$($ids.intervencao)" $null 204 }
        if ($ids.equipe) { $null = Invoke-Api DELETE "/api/equipes/$($ids.equipe)" $null 204 }
        if ($ids.trecho) { $null = Invoke-Api DELETE "/api/trechos/$($ids.trecho)" $null 204 }
    } finally {
        $destino = Join-Path $PSScriptRoot '../target/evidencias'
        $null = New-Item -ItemType Directory -Path $destino -Force
        $registros | ConvertTo-Json -Depth 10 | Set-Content (Join-Path $destino 'api-http.json') -Encoding UTF8
    }
}

Write-Output "$($registros.Count) requisicoes verificadas. Registro: target/evidencias/api-http.json"
