package br.com.senac.projeto.resourcesource.repository;

import br.com.senac.projeto.resourcesource.domain.ResourceSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceSourceRepository extends JpaRepository<ResourceSource, Long> {
}
