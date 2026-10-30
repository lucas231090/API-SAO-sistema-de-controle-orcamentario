package br.com.senac.projeto.costcenter.repository;

import br.com.senac.projeto.costcenter.domain.CostCenter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CostCenterRepository extends JpaRepository<CostCenter, Long> {
}