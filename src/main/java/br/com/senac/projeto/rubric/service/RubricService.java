package br.com.senac.projeto.rubric.service;

import br.com.senac.projeto.rubric.domain.Rubric;
import br.com.senac.projeto.rubric.exception.RubricNotFoundException;
import br.com.senac.projeto.rubric.repository.RubricRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RubricService {

    private final RubricRepository rubricRepository;

    public Page<Rubric> getAll(Pageable pageable){
        return rubricRepository.findAll(pageable);
    }

    public Rubric getById(Long id){
        return rubricRepository.findById(id).orElseThrow(()->
                new RubricNotFoundException("Rubric not found with id: " +id));
    }

    @Transactional
    public Rubric create(Rubric rubric){
        return rubricRepository.save(rubric);
    }

    @Transactional
    public Rubric update(Long id, Rubric updateRubric){
        Rubric rubricFound = getById(id);

        rubricFound.setCodeNumber(updateRubric.getCodeNumber());
        rubricFound.setCodeName(updateRubric.getCodeName());
        return rubricRepository.save(updateRubric);
    }

    public void delete(Long id){
        rubricRepository.deleteById(id);
    }
}
