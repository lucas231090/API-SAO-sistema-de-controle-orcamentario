package br.com.senac.projeto.budgetarynature.controller;

import br.com.senac.projeto.budgetarynature.domain.BudgetaryNature;
import br.com.senac.projeto.budgetarynature.domain.BudgetaryNatureDTO;
import br.com.senac.projeto.budgetarynature.exception.BudgetaryNatureNotFoundException;
import br.com.senac.projeto.budgetarynature.service.BudgetaryNatureService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/budgetary_nature")
public class BudgetaryNatureController {

    private final BudgetaryNatureService budgetaryNatureService;

    @GetMapping
    public ResponseEntity<List<BudgetaryNatureDTO>> list(@RequestParam(value = "isDeleted", required = false, defaultValue = "false") boolean isDeleted){
        return ResponseEntity.ok(budgetaryNatureService.getAll(isDeleted));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BudgetaryNatureDTO> getById(@PathVariable Long id){
        try{
            BudgetaryNatureDTO budgetaryNatureDTO = budgetaryNatureService.getById(id);
            return ResponseEntity.ok(budgetaryNatureDTO);
        }catch (BudgetaryNatureNotFoundException e){
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}")
    public ResponseEntity<BudgetaryNatureDTO> create(@PathVariable Long id, @RequestBody @Valid BudgetaryNatureDTO budgetaryNatureDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(budgetaryNatureService.create(id, budgetaryNatureDTO));
    }

    @PutMapping("/{id}")
    public BudgetaryNature update(@PathVariable("id") Long id, @RequestBody BudgetaryNature updateBudgetaryNature){
        return budgetaryNatureService.update(id, updateBudgetaryNature);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BudgetaryNatureDTO> delete(@PathVariable Long id){
        budgetaryNatureService.delete(id);
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
