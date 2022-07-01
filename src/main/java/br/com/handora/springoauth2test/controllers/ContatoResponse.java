package br.com.handora.springoauth2test.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import br.com.handora.springoauth2test.models.Contato;

public class ContatoResponse {

    private Long id;
    private String nome;
    private String empresa;
    private String criadoPor;
    private List<TelefoneResponse> telefones = new ArrayList<>();

    public ContatoResponse() {}

    public ContatoResponse(Contato contato) {
        this.id = contato.getId();
        this.nome = contato.getNome();
        this.empresa = contato.getEmpresa();
        this.criadoPor = contato.getCriadoPor();
        this.telefones = contato.getTelefones()
                                .stream()
                                .map(TelefoneResponse::new)
                                .collect(Collectors.toList());
    }

    public Long getId() {
        return id;
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

    public List<TelefoneResponse> getTelefones() {
        return telefones;
    }

}
