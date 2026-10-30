package br.com.senac.projeto.resourcesource.controller;

import br.com.senac.projeto.resourcesource.domain.ResourceSource;
import br.com.senac.projeto.resourcesource.domain.ResourceSourceDTO;
import br.com.senac.projeto.resourcesource.exceptions.ResourceSourceNotFoundException;
import br.com.senac.projeto.resourcesource.service.ResourceSourceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.validation.FieldError;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/resource-source")
@RequiredArgsConstructor
@CrossOrigin
@Slf4j
public class ResourceSourceController {

    private final ResourceSourceService resourceSourceService;

    @GetMapping
    public ResponseEntity<Page<ResourceSourceDTO>> list(Pageable pageable){
        Page<ResourceSourceDTO> resourceSourceDTOs = resourceSourceService.getAll(pageable);
        return ResponseEntity.ok(resourceSourceDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResourceSourceDTO> getById(@PathVariable Long id){
        try {
            ResourceSourceDTO resourceSourceDTO = resourceSourceService.getById(id);
            return ResponseEntity.ok(resourceSourceDTO);
        }catch (ResourceSourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resourceSourceService.getById(id));
        }
    }

    @PostMapping
    public ResponseEntity<ResourceSourceDTO> create(@Valid @RequestBody ResourceSourceDTO resourceSourceDTO){
        ResourceSourceDTO createdResourceSourceDTO = resourceSourceService.create(resourceSourceDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdResourceSourceDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResourceSourceDTO> update(@Valid @PathVariable("id") Long id,@RequestBody ResourceSourceDTO updateResourceSourceDTO){
        ResourceSourceDTO updatedResourceSourceDTO = resourceSourceService.update(id, updateResourceSourceDTO);
        return ResponseEntity.ok(updatedResourceSourceDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResourceSource> delete(@PathVariable Long id){
        resourceSourceService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError)error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}