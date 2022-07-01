package br.com.handora.springoauth2test.controllers;

import javax.validation.constraints.NotBlank;

import br.com.handora.springoauth2test.models.Telefone;

public class TelefoneRequest {

    @NotBlank
    private String tipo;

    @NotBlank
    private String numero;

    public TelefoneRequest() {}

    public TelefoneRequest(@NotBlank String tipo, @NotBlank String numero) {
        this.tipo = tipo;
        this.numero = numero;
    }

    public Telefone toModel() {
        return new Telefone(tipo, numero);
    }

    public String getTipo() {
        return tipo;
    }

    public String getNumero() {
        return numero;
    }

}
