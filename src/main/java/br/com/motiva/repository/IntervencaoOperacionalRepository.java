package br.com.motiva.repository;

import br.com.motiva.model.IntervencaoOperacional;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface IntervencaoOperacionalRepository extends JpaRepository<IntervencaoOperacional, Long> {
    List<IntervencaoOperacional> findByTipoServicoOrderByIdAsc(String tipoServico);
    boolean existsByTrechoId(Long trechoId);
    boolean existsByEquipeId(Long equipeId);
}
