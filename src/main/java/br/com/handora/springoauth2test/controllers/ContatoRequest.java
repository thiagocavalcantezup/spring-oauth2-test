package br.com.handora.springoauth2test.controllers;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import br.com.handora.springoauth2test.models.Contato;

public class ContatoRequest {

    @NotBlank
    private String nome;

    @NotBlank
    private String empresa;

    @NotEmpty
    @Valid
    private List<TelefoneRequest> telefones;

    public ContatoRequest() {}

    public ContatoRequest(@NotBlank String nome, @NotBlank String empresa,
                          @Valid List<TelefoneRequest> telefones) {
        this.nome = nome;
        this.empresa = empresa;
        this.telefones = telefones;
    }

    public Contato toModel(String username) {
        return new Contato(
            nome, empresa, username,
            telefones.stream().map(TelefoneRequest::toModel).collect(Collectors.toList())
        );
    }

    public String getNome() {
        return nome;
    }

    public String getEmpresa() {
        return empresa;
    }

    public List<TelefoneRequest> getTelefones() {
        return telefones;
    }

}
