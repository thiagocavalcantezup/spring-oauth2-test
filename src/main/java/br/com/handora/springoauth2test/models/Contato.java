package br.com.handora.springoauth2test.models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "contatos")
public class Contato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String empresa;

    @Column(nullable = false)
    private String criadoPor;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Telefone> telefones = new ArrayList<>();

    /**
     * @deprecated Construtor de uso exclusivo do Hibernate
     */
    @Deprecated
    public Contato() {}

    public Contato(String nome, String empresa, String criadoPor, List<Telefone> telefones) {
        this.nome = nome;
        this.empresa = empresa;
        this.criadoPor = criadoPor;
        this.telefones = telefones;
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

    public List<Telefone> getTelefones() {
        return telefones;
    }

}
