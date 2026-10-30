package br.com.senac.projeto.activity.controller;

import br.com.senac.projeto.activity.domain.ActivityDTO;
import br.com.senac.projeto.report.domain.ActivityDTOReport;
import br.com.senac.projeto.activity.exceptions.ActivityNotFoundException;
import br.com.senac.projeto.report.service.ClassReport;
import br.com.senac.projeto.activity.service.ActivityService;
import br.com.senac.projeto.product.domain.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/activities")
@Log4j2
public class ActivityController {

    private final ActivityService activityService;

    @GetMapping
    public ResponseEntity<List<ActivityDTO>> list(@RequestParam(value = "isDeleted", required = false, defaultValue = "false")boolean isDeleted){
        return ResponseEntity.ok(activityService.getAll(isDeleted));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActivityDTO> getById(@PathVariable Long id){
        try{
            ActivityDTO activityDTO = activityService.getById(id);
            return ResponseEntity.ok(activityDTO);
        }catch (ActivityNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(activityService.getById(id));
        }
    }

    @PostMapping("/{id}")
    public ResponseEntity<ActivityDTO> create(@PathVariable Long id, @RequestBody ActivityDTO activityDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(activityService.create(id, activityDTO));
    }

    @PutMapping("/{id}")
    public ActivityDTO update(@PathVariable Long id, @RequestBody ActivityDTO updateActivityDTO){
        return activityService.update(id, updateActivityDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ActivityDTO> delete(@PathVariable Long id){
        activityService.delete(id);
        return ResponseEntity.noContent().build();
    }

}