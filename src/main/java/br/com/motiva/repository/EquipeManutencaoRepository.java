package br.com.motiva.repository;

import br.com.motiva.model.EquipeManutencao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface EquipeManutencaoRepository extends JpaRepository<EquipeManutencao, Long> {
    List<EquipeManutencao> findByStatusOrderByIdAsc(String status);
}
