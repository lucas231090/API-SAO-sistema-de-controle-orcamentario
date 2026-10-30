package br.com.senac.projeto.activity.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ActivityNotFoundException extends RuntimeException {
    public ActivityNotFoundException(String msg) {
        super(msg);
    }
}
