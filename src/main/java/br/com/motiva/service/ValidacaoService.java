package br.com.motiva.service;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.stereotype.Service;

@Service
public class ValidacaoService {
    private final Validator validator;

    public ValidacaoService(Validator validator) { this.validator = validator; }

    public void validar(Object dados) {
        var erros = validator.validate(dados);
        if (!erros.isEmpty()) throw new ConstraintViolationException(erros);
    }
}
