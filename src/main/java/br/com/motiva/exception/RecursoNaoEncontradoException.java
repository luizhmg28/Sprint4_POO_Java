package br.com.motiva.exception;

public class RecursoNaoEncontradoException extends RuntimeException {
    public RecursoNaoEncontradoException(String recurso, Long id) {
        super(recurso + " " + id + " nao encontrado.");
    }
}
