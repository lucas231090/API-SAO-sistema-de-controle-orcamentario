package br.com.senac.projeto.budgetarynature.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class BudgetaryNatureNotFoundException extends RuntimeException {
    public BudgetaryNatureNotFoundException(String msg) {
        super(msg);
    }
}
