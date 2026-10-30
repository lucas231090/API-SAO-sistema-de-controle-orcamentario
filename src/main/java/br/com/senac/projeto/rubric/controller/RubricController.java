package br.com.senac.projeto.rubric.controller;

import br.com.senac.projeto.rubric.domain.Rubric;
import br.com.senac.projeto.rubric.exception.RubricNotFoundException;
import br.com.senac.projeto.rubric.service.RubricService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rubric")
@CrossOrigin
public class RubricController {

    private final RubricService rubricService;

    @GetMapping
    public ResponseEntity<Page<Rubric>> List(Pageable pageable){
        return ResponseEntity.ok(rubricService.getAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Rubric> getById(@PathVariable Long id){
        try{
            Rubric rubric = rubricService.getById(id);
            return ResponseEntity.ok(rubric);
        }catch (RubricNotFoundException e){
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Rubric> create(@RequestBody Rubric rubric){
        return ResponseEntity.status(HttpStatus.CREATED).body(rubricService.create(rubric));
    }

    @PutMapping("/{id}")
    public Rubric update(@PathVariable Long id, @RequestBody Rubric updateRubric){
        return rubricService.update(id, updateRubric);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        rubricService.delete(id);
    }
}

