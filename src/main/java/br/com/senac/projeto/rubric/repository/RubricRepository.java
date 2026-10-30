package br.com.senac.projeto.rubric.repository;

import br.com.senac.projeto.rubric.domain.Rubric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RubricRepository extends JpaRepository<Rubric, Long > {
}
