package com.alvaro.clinica_api.exception;

public class ConflitoHorarioException extends RuntimeException {
    public ConflitoHorarioException(String mensagem) {
        super(mensagem);
    }
}