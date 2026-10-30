package br.com.senac.projeto.costcenter.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CostCenterNotFoundException extends RuntimeException {
    public CostCenterNotFoundException(String msg) {
        super(msg);
    }
}
