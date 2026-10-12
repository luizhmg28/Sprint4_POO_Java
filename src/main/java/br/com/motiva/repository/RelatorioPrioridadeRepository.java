package br.com.motiva.repository;

import br.com.motiva.model.RelatorioPrioridade;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface RelatorioPrioridadeRepository extends JpaRepository<RelatorioPrioridade, Long> {
    List<RelatorioPrioridade> findByDataGeracaoBetweenOrderByDataGeracaoDescIdDesc(LocalDateTime inicio, LocalDateTime fim);
}
