package br.com.senac.projeto.budgetarynature.repository;

import br.com.senac.projeto.budgetarynature.domain.BudgetaryNature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BudgetaryNatureRepository extends JpaRepository<BudgetaryNature, Long> {
}
