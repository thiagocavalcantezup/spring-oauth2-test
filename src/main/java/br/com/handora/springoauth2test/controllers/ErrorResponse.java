package br.com.handora.springoauth2test.controllers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintViolation;

import org.springframework.validation.FieldError;

public class ErrorResponse {

    private Integer codigoHttp;
    private String mensagemHttp;
    private String mensagemGeral;
    private List<String> mensagens;
    private String caminho;
    private LocalDateTime dataHora = LocalDateTime.now();

    public ErrorResponse(Integer codigoHttp, String mensagemHttp, String mensagemGeral,
                         String caminho) {
        this.codigoHttp = codigoHttp;
        this.mensagemHttp = mensagemHttp;
        this.mensagemGeral = mensagemGeral;
        this.caminho = caminho;
        this.mensagens = new ArrayList<>();
    }

    public void adicionarErro(FieldError fieldError) {
        mensagens.add(fieldError.getField() + ": " + fieldError.getDefaultMessage());
    }

    public void adicionarErro(ConstraintViolation<?> constraintViolation) {
        mensagens.add(
            constraintViolation.getPropertyPath() + ": " + constraintViolation.getMessage()
        );
    }

    public void adicionarErro(String erro) {
        mensagens.add(erro);
    }

    public Integer getCodigoHttp() {
        return codigoHttp;
    }

    public String getMensagemHttp() {
        return mensagemHttp;
    }

    public String getMensagemGeral() {
        return mensagemGeral;
    }

    public List<String> getMensagens() {
        return mensagens;
    }

    public String getCaminho() {
        return caminho;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

}
