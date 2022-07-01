package br.com.handora.springoauth2test.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.handora.springoauth2test.models.Contato;

public interface ContatoRepository extends JpaRepository<Contato, Long> {

}
