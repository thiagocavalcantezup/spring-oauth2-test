package br.com.handora.springoauth2test.controllers;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import br.com.handora.springoauth2test.models.Contato;

public class ContatoRequest {

    @NotBlank
    private String nome;

    @NotBlank
    private String empresa;

    @NotBlank
    private String criadoPor;

    @Valid
    private List<TelefoneRequest> telefones;

    public ContatoRequest() {}

    public ContatoRequest(@NotBlank String nome, @NotBlank String empresa,
                          @NotBlank String criadoPor, @Valid List<TelefoneRequest> telefones) {
        this.nome = nome;
        this.empresa = empresa;
        this.criadoPor = criadoPor;
        this.telefones = telefones;
    }

    public Contato toModel() {
        return new Contato(
            nome, empresa, criadoPor,
            telefones.stream().map(TelefoneRequest::toModel).collect(Collectors.toList())
        );
    }

    public String getNome() {
        return nome;
    }

    public String getEmpresa() {
        return empresa;
    }

    public String getCriadoPor() {
        return criadoPor;
    }

    public List<TelefoneRequest> getTelefones() {
        return telefones;
    }

}
