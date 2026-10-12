package br.com.motiva.service;

import br.com.motiva.dto.*;
import br.com.motiva.model.EquipeManutencao;
import br.com.motiva.repository.*;
import br.com.motiva.exception.RecursoNaoEncontradoException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class EquipeService {
    private final EquipeManutencaoRepository repository;
    private final ValidacaoService validacao;
    private final IntervencaoOperacionalRepository intervencoes;

    public EquipeService(EquipeManutencaoRepository repository, ValidacaoService validacao, IntervencaoOperacionalRepository intervencoes) {
        this.repository = repository;
        this.validacao = validacao;
        this.intervencoes = intervencoes;
    }

    public List<EquipeResponse> listar(String status) {
        if (status != null && !java.util.Set.of("ATIVA", "INATIVA", "EM_CAMPO").contains(status)) throw new IllegalArgumentException("Status de equipe invalido.");
        var dados = status == null ? repository.findAll(Sort.by("id")) : repository.findByStatusOrderByIdAsc(status);
        return dados.stream().map(EquipeResponse::from).toList();
    }

    public EquipeResponse buscar(Long id) {
        return EquipeResponse.from(encontrar(id));
    }

    @Transactional
    public EquipeResponse criar(EquipeRequest dados) {
        var entity = new EquipeManutencao();
        preencher(entity, dados);
        return EquipeResponse.from(repository.saveAndFlush(entity));
    }

    @Transactional
    public EquipeResponse atualizar(Long id, EquipeRequest dados) {
        var entity = encontrar(id);
        preencher(entity, dados);
        return EquipeResponse.from(repository.saveAndFlush(entity));
    }

    @Transactional
    public void remover(Long id) {
        var entity = encontrar(id);
        if (intervencoes.existsByEquipeId(id)) throw new IllegalArgumentException("Equipe possui intervencoes vinculadas.");
        repository.delete(entity);
        repository.flush();
    }

    private EquipeManutencao encontrar(Long id) {
        return repository.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException("Equipe", id));
    }

    private void preencher(EquipeManutencao entity, EquipeRequest dados) {
        validacao.validar(dados);
        
        entity.setNome(dados.nome().trim());
        entity.setEspecialidade(dados.especialidade().trim());
        entity.setQuantidadeIntegrantes(dados.quantidadeIntegrantes());
        entity.setStatus(dados.status().trim());
    }
}
