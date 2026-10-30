package br.com.senac.projeto.rubric.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class RubricNotFoundException extends RuntimeException {
    public RubricNotFoundException(String msg) {
        super(msg);
    }
}
