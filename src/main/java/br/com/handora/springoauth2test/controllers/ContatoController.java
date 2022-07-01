package br.com.handora.springoauth2test.controllers;

import java.net.URI;
import java.util.stream.Collectors;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.handora.springoauth2test.models.Contato;
import br.com.handora.springoauth2test.repositories.ContatoRepository;

@RestController
@RequestMapping(ContatoController.BASE_URI)
public class ContatoController {

    public final static String BASE_URI = "/api/contatos";

    private final ContatoRepository contatoRepository;

    public ContatoController(ContatoRepository contatoRepository) {
        this.contatoRepository = contatoRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {
        Contato contato = contatoRepository.findById(id)
                                           .orElseThrow(
                                               () -> new ResponseStatusException(
                                                   HttpStatus.NOT_FOUND, "Contato não encontrado."
                                               )
                                           );

        return ResponseEntity.ok(new ContatoResponse(contato));
    }

    @GetMapping
    public ResponseEntity<?> index() {
        return ResponseEntity.ok(
            contatoRepository.findAll()
                             .stream()
                             .map(ContatoResponse::new)
                             .collect(Collectors.toList())
        );
    }

    @Transactional
    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid ContatoRequest contatoRequest,
                                    UriComponentsBuilder ucb) {
        Contato contato = contatoRepository.save(contatoRequest.toModel());

        URI location = ucb.path(BASE_URI + "/{id}").buildAndExpand(contato.getId()).toUri();

        return ResponseEntity.created(location).build();
    }

    @Transactional
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_contatos:write')")
    public ResponseEntity<?> destroy(@PathVariable Long id) {
        Contato contato = contatoRepository.findById(id)
                                           .orElseThrow(
                                               () -> new ResponseStatusException(
                                                   HttpStatus.NOT_FOUND, "Contato não encontrado."
                                               )
                                           );

        contatoRepository.delete(contato);

        return ResponseEntity.noContent().build();
    }

}
