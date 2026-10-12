package br.com.motiva.service;

import br.com.motiva.dto.*;
import br.com.motiva.model.TrechoRodovia;
import br.com.motiva.repository.*;
import br.com.motiva.exception.RecursoNaoEncontradoException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class TrechoService {
    private final TrechoRodoviaRepository repository;
    private final ValidacaoService validacao;
    private final IntervencaoOperacionalRepository intervencoes;

    public TrechoService(TrechoRodoviaRepository repository, ValidacaoService validacao, IntervencaoOperacionalRepository intervencoes) {
        this.repository = repository;
        this.validacao = validacao;
        this.intervencoes = intervencoes;
    }

    public List<TrechoResponse> listar(BigDecimal alturaMinima) {
        if (alturaMinima != null && alturaMinima.signum() < 0) throw new IllegalArgumentException("Altura minima deve ser maior ou igual a zero.");
        var dados = alturaMinima == null ? repository.findAll(Sort.by("id")) : repository.findByAlturaVegetacaoGreaterThanEqualOrderByIdAsc(alturaMinima);
        return dados.stream().map(TrechoResponse::from).toList();
    }

    public TrechoResponse buscar(Long id) {
        return TrechoResponse.from(encontrar(id));
    }

    @Transactional
    public TrechoResponse criar(TrechoRequest dados) {
        var entity = new TrechoRodovia();
        preencher(entity, dados);
        return TrechoResponse.from(repository.saveAndFlush(entity));
    }

    @Transactional
    public TrechoResponse atualizar(Long id, TrechoRequest dados) {
        var entity = encontrar(id);
        preencher(entity, dados);
        return TrechoResponse.from(repository.saveAndFlush(entity));
    }

    @Transactional
    public void remover(Long id) {
        var entity = encontrar(id);
        if (intervencoes.existsByTrechoId(id)) throw new IllegalArgumentException("Trecho possui intervencoes vinculadas.");
        repository.delete(entity);
        repository.flush();
    }

    private TrechoRodovia encontrar(Long id) {
        return repository.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException("Trecho", id));
    }

    private void preencher(TrechoRodovia entity, TrechoRequest dados) {
        validacao.validar(dados);
        if (dados.kmFinal() <= dados.kmInicial()) throw new IllegalArgumentException("KM final deve ser maior que o inicial.");
        if (dados.monitoradoIoT() && dados.codigoSensor() == null) throw new IllegalArgumentException("Trecho monitorado exige codigo do sensor.");
        entity.setKmInicial(dados.kmInicial());
        entity.setKmFinal(dados.kmFinal());
        entity.setAlturaVegetacao(dados.alturaVegetacao());
        entity.setTipoAmbiente(dados.tipoAmbiente().trim());
        entity.setAcessoDificil(dados.acessoDificil());
        entity.setCodigoSensor(dados.codigoSensor());
        entity.setMonitoradoIoT(dados.monitoradoIoT());
    }
}
