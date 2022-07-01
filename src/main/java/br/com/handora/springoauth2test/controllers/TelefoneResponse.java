package br.com.handora.springoauth2test.controllers;

import br.com.handora.springoauth2test.models.Telefone;

public class TelefoneResponse {

    private Long id;
    private String tipo;
    private String numero;

    public TelefoneResponse() {}

    public TelefoneResponse(Telefone telefone) {
        this.id = telefone.getId();
        this.tipo = telefone.getTipo();
        this.numero = telefone.getNumero();
    }

    public Long getId() {
        return id;
    }

    public String getTipo() {
        return tipo;
    }

    public String getNumero() {
        return numero;
    }

}
