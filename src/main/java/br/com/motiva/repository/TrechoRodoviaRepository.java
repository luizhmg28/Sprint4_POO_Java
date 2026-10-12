package br.com.motiva.repository;

import br.com.motiva.model.TrechoRodovia;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface TrechoRodoviaRepository extends JpaRepository<TrechoRodovia, Long> {
    List<TrechoRodovia> findByAlturaVegetacaoGreaterThanEqualOrderByIdAsc(BigDecimal minimo);
}
