package br.com.senac.projeto.costcenter.controller;

import br.com.senac.projeto.costcenter.domain.CostCenter;
import br.com.senac.projeto.costcenter.domain.CostCenterDTO;
import br.com.senac.projeto.costcenter.exceptions.CostCenterNotFoundException;
import br.com.senac.projeto.costcenter.service.CostCenterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cost-center")
@CrossOrigin
public class CostCenterController {

    private final CostCenterService costCenterService;

    @GetMapping
    public ResponseEntity<Page<CostCenterDTO>> list(Pageable pageable){
        Page<CostCenterDTO> costCenterDTOs = costCenterService.getAll(pageable);
        return ResponseEntity.ok(costCenterDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CostCenterDTO> getById(@PathVariable Long id){
        try {
            CostCenterDTO costCenterDTO = costCenterService.getById(id);
            return ResponseEntity.ok(costCenterDTO);
        }catch (CostCenterNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(costCenterService.getById(id));
        }
    }

    @PostMapping
    public ResponseEntity<CostCenterDTO> create(/*@Valid*/ @RequestBody CostCenterDTO costCenterDTO){
        CostCenterDTO createdCostCenterDTO = costCenterService.create(costCenterDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCostCenterDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CostCenterDTO> update(/*@Valid*/ @PathVariable("id") Long id, @RequestBody CostCenterDTO updateCostCenterDTO){
        CostCenterDTO updatedCostCenterDTO = costCenterService.update(id, updateCostCenterDTO);

        return ResponseEntity.ok(updatedCostCenterDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CostCenter> delete(@PathVariable Long id){
        costCenterService.delete(id);
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
