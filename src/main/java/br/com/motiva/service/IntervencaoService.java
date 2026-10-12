package br.com.motiva.service;

import br.com.motiva.dto.*;
import br.com.motiva.model.IntervencaoOperacional;
import br.com.motiva.repository.*;
import br.com.motiva.exception.RecursoNaoEncontradoException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class IntervencaoService {
    private final IntervencaoOperacionalRepository repository;
    private final ValidacaoService validacao;
    private final TrechoRodoviaRepository trechos;
    private final EquipeManutencaoRepository equipes;

    public IntervencaoService(IntervencaoOperacionalRepository repository, ValidacaoService validacao, TrechoRodoviaRepository trechos, EquipeManutencaoRepository equipes) {
        this.repository = repository;
        this.validacao = validacao;
        this.trechos = trechos;
        this.equipes = equipes;
    }

    public List<IntervencaoResponse> listar(String tipoServico) {
        var dados = tipoServico == null ? repository.findAll(Sort.by("id")) : repository.findByTipoServicoOrderByIdAsc(tipoServico);
        return dados.stream().map(IntervencaoResponse::from).toList();
    }

    public IntervencaoResponse buscar(Long id) {
        return IntervencaoResponse.from(encontrar(id));
    }

    @Transactional
    public IntervencaoResponse criar(IntervencaoRequest dados) {
        var entity = new IntervencaoOperacional();
        preencher(entity, dados);
        return IntervencaoResponse.from(repository.saveAndFlush(entity));
    }

    @Transactional
    public IntervencaoResponse atualizar(Long id, IntervencaoRequest dados) {
        var entity = encontrar(id);
        preencher(entity, dados);
        return IntervencaoResponse.from(repository.saveAndFlush(entity));
    }

    @Transactional
    public void remover(Long id) {
        var entity = encontrar(id);
        
        repository.delete(entity);
        repository.flush();
    }

    private IntervencaoOperacional encontrar(Long id) {
        return repository.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException("Intervencao", id));
    }

    private void preencher(IntervencaoOperacional entity, IntervencaoRequest dados) {
        validacao.validar(dados);
        
        entity.setTrecho(trechos.findById(dados.trechoId()).orElseThrow(() -> new RecursoNaoEncontradoException("Trecho", dados.trechoId())));
        entity.setEquipe(equipes.findById(dados.equipeId()).orElseThrow(() -> new RecursoNaoEncontradoException("Equipe", dados.equipeId())));
        entity.setTipoServico(dados.tipoServico().trim());
        entity.setPrioridade(dados.prioridade().trim());
        entity.setStatus(dados.status().trim());
        entity.setDataProgramada(dados.dataProgramada());
        entity.setObservacao(dados.observacao());
    }
}
