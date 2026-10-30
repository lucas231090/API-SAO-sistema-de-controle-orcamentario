package br.com.senac.projeto.resourcesource.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceSourceNotFoundException extends RuntimeException{
    public ResourceSourceNotFoundException(String msg) {
        super(msg);
    }
}
