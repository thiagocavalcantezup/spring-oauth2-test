package br.com.handora.springoauth2test.controllers;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import br.com.handora.springoauth2test.models.Contato;
import br.com.handora.springoauth2test.models.Telefone;
import br.com.handora.springoauth2test.repositories.ContatoRepository;

@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@ActiveProfiles("test")
public class ContatoControllerTest {

    @Autowired
    ContatoRepository contatoRepository;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        contatoRepository.deleteAll();
    }

    @Test
    void deveDetalharUmContato() throws Exception {
        // given
        Contato contato = new Contato(
            "Jordi", "Zup", "thiago.cavalcante", List.of(new Telefone("celular", "+5511999998888"))
        );
        contatoRepository.save(contato);
        Telefone telefone = contato.getTelefones().get(0);

        MockHttpServletRequestBuilder requestBuilder = get(
            ContatoController.BASE_URI + "/{id}", contato.getId()
        ).contentType(APPLICATION_JSON)
         .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_contatos:read")));

        // when
        String response = mockMvc.perform(requestBuilder)
                                 .andExpect(status().isOk()) // then
                                 .andReturn()
                                 .getResponse()
                                 .getContentAsString(UTF_8);

        ContatoResponse contatoResponse = objectMapper.readValue(response, ContatoResponse.class);
        TelefoneResponse telefoneResponse = contatoResponse.getTelefones().get(0);

        // then
        assertThat(contatoResponse).extracting("id", "nome", "empresa", "criadoPor")
                                   .contains(
                                       contato.getId(), contato.getNome(), contato.getEmpresa(),
                                       contato.getCriadoPor()
                                   );
        assertThat(telefoneResponse).extracting("id", "tipo", "numero")
                                    .contains(
                                        telefone.getId(), telefone.getTipo(), telefone.getNumero()
                                    );
    }

    @Test
    void naoDeveDetalharUmContatoQueNaoEstaCadastrado() throws Exception {
        // given
        MockHttpServletRequestBuilder requestBuilder = get(
            ContatoController.BASE_URI + "/{id}", Long.MAX_VALUE
        ).contentType(APPLICATION_JSON)
         .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_contatos:read")));

        // when
        String response = mockMvc.perform(requestBuilder)
                                 .andExpect(status().isNotFound()) // then
                                 .andReturn()
                                 .getResponse()
                                 .getContentAsString(UTF_8);

        // then
        ErrorResponse errorResponse = objectMapper.readValue(response, ErrorResponse.class);

        assertEquals("Contato não encontrado.", errorResponse.getMensagens().get(0));
    }

    @Test
    void naoDeveDetalharUmContatoSemToken() throws Exception {
        // given
        Contato contato = new Contato(
            "Jordi", "Zup", "thiago.cavalcante", List.of(new Telefone("celular", "+5511999998888"))
        );
        contatoRepository.save(contato);

        MockHttpServletRequestBuilder requestBuilder = get(
            ContatoController.BASE_URI + "/{id}", contato.getId()
        ).contentType(APPLICATION_JSON);

        // when
        mockMvc.perform(requestBuilder).andExpect(status().isUnauthorized()); // then
    }

    @Test
    void naoDeveDetalharUmContatoSemScope() throws Exception {
        // given
        Contato contato = new Contato(
            "Jordi", "Zup", "thiago.cavalcante", List.of(new Telefone("celular", "+5511999998888"))
        );
        contatoRepository.save(contato);

        MockHttpServletRequestBuilder requestBuilder = get(
            ContatoController.BASE_URI + "/{id}", contato.getId()
        ).contentType(APPLICATION_JSON).with(jwt());

        // when
        mockMvc.perform(requestBuilder).andExpect(status().isForbidden()); // then
    }

    @Test
    void naoDeveDetalharUmContatoComScopeIncorreto() throws Exception {
        // given
        Contato contato = new Contato(
            "Jordi", "Zup", "thiago.cavalcante", List.of(new Telefone("celular", "+5511999998888"))
        );
        contatoRepository.save(contato);

        MockHttpServletRequestBuilder requestBuilder = get(
            ContatoController.BASE_URI + "/{id}", contato.getId()
        ).contentType(APPLICATION_JSON)
         .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_contatos:write")));

        // when
        mockMvc.perform(requestBuilder).andExpect(status().isForbidden()); // then
    }

    @Test
    void deveRetornarUmaColecaoDeContatos() throws Exception {
        // given
        Contato contato1 = new Contato(
            "Jordi", "Zup", "thiago.cavalcante", List.of(new Telefone("celular", "+5511999998888"))
        );
        Contato contato2 = new Contato(
            "Rafael", "Zup", "thiago.cavalcante", List.of(new Telefone("celular", "+5511999997777"))
        );
        contatoRepository.save(contato1);
        contatoRepository.save(contato2);

        Telefone tel1 = contato1.getTelefones().get(0);
        Telefone tel2 = contato2.getTelefones().get(0);

        MockHttpServletRequestBuilder requestBuilder = get(ContatoController.BASE_URI).contentType(
            APPLICATION_JSON
        ).with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_contatos:read")));

        // when
        String response = mockMvc.perform(requestBuilder)
                                 .andExpect(status().isOk()) // then
                                 .andReturn()
                                 .getResponse()
                                 .getContentAsString(UTF_8);

        TypeFactory typeFactory = objectMapper.getTypeFactory();
        List<ContatoResponse> contatos = objectMapper.readValue(
            response, typeFactory.constructCollectionType(List.class, ContatoResponse.class)
        );

        // then
        assertThat(contatos).hasSize(2)
                            .extracting("id", "nome", "empresa", "criadoPor")
                            .contains(
                                new Tuple(
                                    contato1.getId(), contato1.getNome(), contato1.getEmpresa(),
                                    contato1.getCriadoPor()
                                ),
                                new Tuple(
                                    contato2.getId(), contato2.getNome(), contato2.getEmpresa(),
                                    contato2.getCriadoPor()
                                )
                            );

        List<TelefoneResponse> telefones = List.of(
            contatos.get(0).getTelefones().get(0), contatos.get(1).getTelefones().get(0)
        );
        assertThat(telefones).hasSize(2)
                             .extracting("id", "tipo", "numero")
                             .contains(
                                 new Tuple(tel1.getId(), tel1.getTipo(), tel1.getNumero()),
                                 new Tuple(tel2.getId(), tel2.getTipo(), tel2.getNumero())
                             );
    }

    @Test
    void deveRetornarUmaColecaoDeContatosVazia() throws Exception {
        // given
        MockHttpServletRequestBuilder requestBuilder = get(ContatoController.BASE_URI).contentType(
            APPLICATION_JSON
        ).with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_contatos:read")));

        // when
        String response = mockMvc.perform(requestBuilder)
                                 .andExpect(status().isOk()) // then
                                 .andReturn()
                                 .getResponse()
                                 .getContentAsString(UTF_8);

        TypeFactory typeFactory = objectMapper.getTypeFactory();
        List<ContatoResponse> contatos = objectMapper.readValue(
            response, typeFactory.constructCollectionType(List.class, ContatoResponse.class)
        );

        // then
        assertThat(contatos).isEmpty();
    }

    @Test
    void naoDeveRetornarUmaColecaoDeContatosSemToken() throws Exception {
        // given
        Contato contato = new Contato(
            "Jordi", "Zup", "thiago.cavalcante", List.of(new Telefone("celular", "+5511999998888"))
        );
        contatoRepository.save(contato);

        MockHttpServletRequestBuilder requestBuilder = get(ContatoController.BASE_URI).contentType(
            APPLICATION_JSON
        );

        // when
        mockMvc.perform(requestBuilder).andExpect(status().isUnauthorized()); // then
    }

    @Test
    void naoDeveRetornarUmaColecaoDeContatosSemScope() throws Exception {
        // given
        Contato contato = new Contato(
            "Jordi", "Zup", "thiago.cavalcante", List.of(new Telefone("celular", "+5511999998888"))
        );
        contatoRepository.save(contato);

        MockHttpServletRequestBuilder requestBuilder = get(ContatoController.BASE_URI).contentType(
            APPLICATION_JSON
        ).with(jwt());

        // when
        mockMvc.perform(requestBuilder).andExpect(status().isForbidden()); // then
    }

    @Test
    void naoDeveRetornarUmaColecaoDeContatosComScopeIncorreto() throws Exception {
        // given
        Contato contato = new Contato(
            "Jordi", "Zup", "thiago.cavalcante", List.of(new Telefone("celular", "+5511999998888"))
        );
        contatoRepository.save(contato);

        MockHttpServletRequestBuilder requestBuilder = get(ContatoController.BASE_URI).contentType(
            APPLICATION_JSON
        ).with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_contatos:write")));

        // when
        mockMvc.perform(requestBuilder).andExpect(status().isForbidden()); // then
    }

    @Test
    void deveCadastrarUmContato() throws Exception {
        // given
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();

        ContatoRequest contatoRequest = new ContatoRequest(
            "Jordi", "Zup", List.of(new TelefoneRequest("celular", "+5511999998888"))
        );

        String requestPayload = objectMapper.writeValueAsString(contatoRequest);

        MockHttpServletRequestBuilder requestBuilder = post(
            ContatoController.BASE_URI
        ).contentType(APPLICATION_JSON)
         .content(requestPayload)
         .with(
             jwt().jwt(jwt -> jwt.claim("preferred_username", "thiago.cavalcante"))
                  .authorities(new SimpleGrantedAuthority("SCOPE_contatos:write"))
         );

        // when
        mockMvc.perform(requestBuilder)
               .andExpect(status().isCreated()) // then
               .andExpect(redirectedUrlPattern(baseUrl + ContatoController.BASE_URI + "/*")); // then

        // then
        List<Contato> contatos = contatoRepository.findAll();

        assertEquals(1, contatos.size());
        assertEquals("thiago.cavalcante", contatoRepository.findAll().get(0).getCriadoPor());
    }

    @Test
    void deveCadastrarUmContatoComUsuarioAnonimo() throws Exception {
        // given
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();

        ContatoRequest contatoRequest = new ContatoRequest(
            "Jordi", "Zup", List.of(new TelefoneRequest("celular", "+5511999998888"))
        );

        String requestPayload = objectMapper.writeValueAsString(contatoRequest);

        MockHttpServletRequestBuilder requestBuilder = post(
            ContatoController.BASE_URI
        ).contentType(APPLICATION_JSON)
         .content(requestPayload)
         .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_contatos:write")));

        // when
        mockMvc.perform(requestBuilder)
               .andExpect(status().isCreated()) // then
               .andExpect(redirectedUrlPattern(baseUrl + ContatoController.BASE_URI + "/*")); // then

        // then
        List<Contato> contatos = contatoRepository.findAll();

        assertEquals(1, contatos.size());
        assertEquals("anonymous", contatoRepository.findAll().get(0).getCriadoPor());
    }

    @Test
    void naoDeveCadastrarUmContatoComDadosNulos() throws Exception {
        // given
        ContatoRequest contatoRequest = new ContatoRequest(null, null, null);

        String requestPayload = objectMapper.writeValueAsString(contatoRequest);

        MockHttpServletRequestBuilder requestBuilder = post(
            ContatoController.BASE_URI
        ).contentType(APPLICATION_JSON)
         .content(requestPayload)
         .header("Accept-Language", "pt-br")
         .with(
             jwt().jwt(jwt -> jwt.claim("preferred_username", "thiago.cavalcante"))
                  .authorities(new SimpleGrantedAuthority("SCOPE_contatos:write"))
         );

        // when
        String response = mockMvc.perform(requestBuilder)
                                 .andExpect(status().isBadRequest())
                                 .andReturn()
                                 .getResponse()
                                 .getContentAsString(UTF_8);

        ErrorResponse errorResponse = objectMapper.readValue(response, ErrorResponse.class);
        List<String> mensagens = errorResponse.getMensagens();

        // then
        assertThat(mensagens).hasSize(3)
                             .contains(
                                 "nome: não deve estar em branco",
                                 "empresa: não deve estar em branco",
                                 "telefones: não deve estar vazio"
                             );
    }

    @Test
    void naoDeveCadastrarUmContatoComTelefoneComDadosNulos() throws Exception {
        // given
        ContatoRequest contatoRequest = new ContatoRequest(
            "Jordi", "Zup", List.of(new TelefoneRequest(null, null))
        );

        String requestPayload = objectMapper.writeValueAsString(contatoRequest);

        MockHttpServletRequestBuilder requestBuilder = post(
            ContatoController.BASE_URI
        ).contentType(APPLICATION_JSON)
         .content(requestPayload)
         .header("Accept-Language", "pt-br")
         .with(
             jwt().jwt(jwt -> jwt.claim("preferred_username", "thiago.cavalcante"))
                  .authorities(new SimpleGrantedAuthority("SCOPE_contatos:write"))
         );

        // when
        String response = mockMvc.perform(requestBuilder)
                                 .andExpect(status().isBadRequest())
                                 .andReturn()
                                 .getResponse()
                                 .getContentAsString(UTF_8);

        ErrorResponse errorResponse = objectMapper.readValue(response, ErrorResponse.class);
        List<String> mensagens = errorResponse.getMensagens();

        // then
        assertThat(mensagens).hasSize(2)
                             .contains(
                                 "telefones[0].tipo: não deve estar em branco",
                                 "telefones[0].numero: não deve estar em branco"
                             );
    }

    @Test
    void naoDeveCadastrarUmContatoSemToken() throws Exception {
        // given
        ContatoRequest contatoRequest = new ContatoRequest(
            "Jordi", "Zup", List.of(new TelefoneRequest("celular", "+5511999998888"))
        );

        String requestPayload = objectMapper.writeValueAsString(contatoRequest);

        MockHttpServletRequestBuilder requestBuilder = post(ContatoController.BASE_URI).contentType(
            APPLICATION_JSON
        ).content(requestPayload);

        // when
        mockMvc.perform(requestBuilder).andExpect(status().isUnauthorized()); // then
    }

    @Test
    void naoDeveCadastrarUmContatoSemScope() throws Exception {
        // given
        ContatoRequest contatoRequest = new ContatoRequest(
            "Jordi", "Zup", List.of(new TelefoneRequest("celular", "+5511999998888"))
        );

        String requestPayload = objectMapper.writeValueAsString(contatoRequest);

        MockHttpServletRequestBuilder requestBuilder = post(
            ContatoController.BASE_URI
        ).contentType(APPLICATION_JSON)
         .content(requestPayload)
         .with(jwt().jwt(jwt -> jwt.claim("preferred_username", "thiago.cavalcante")));

        // when
        mockMvc.perform(requestBuilder).andExpect(status().isForbidden()); // then
    }

    @Test
    void naoDeveCadastrarUmContatoComScopeIncorreto() throws Exception {
        // given
        ContatoRequest contatoRequest = new ContatoRequest(
            "Jordi", "Zup", List.of(new TelefoneRequest("celular", "+5511999998888"))
        );

        String requestPayload = objectMapper.writeValueAsString(contatoRequest);

        MockHttpServletRequestBuilder requestBuilder = post(
            ContatoController.BASE_URI
        ).contentType(APPLICATION_JSON)
         .content(requestPayload)
         .with(
             jwt().jwt(jwt -> jwt.claim("preferred_username", "thiago.cavalcante"))
                  .authorities(new SimpleGrantedAuthority("SCOPE_contatos:read"))
         );

        // when
        mockMvc.perform(requestBuilder).andExpect(status().isForbidden()); // then
    }

    @Test
    void deveRemoverUmContato() throws Exception {
        // given
        Contato contato = new Contato(
            "Jordi", "Zup", "thiago.cavalcante", List.of(new Telefone("celular", "+5511999998888"))
        );
        contatoRepository.save(contato);

        MockHttpServletRequestBuilder requestBuilder = delete(
            ContatoController.BASE_URI + "/{id}", contato.getId()
        ).contentType(APPLICATION_JSON)
         .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_contatos:write")));

        // when
        mockMvc.perform(requestBuilder).andExpect(status().isNoContent()); // then

        // then
        assertFalse(contatoRepository.existsById(contato.getId()));
    }

    @Test
    void naoDeveRemoverUmContatoQueNaoEstaCadastrado() throws Exception {
        // given
        MockHttpServletRequestBuilder requestBuilder = delete(
            ContatoController.BASE_URI + "/{id}", Long.MAX_VALUE
        ).contentType(APPLICATION_JSON)
         .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_contatos:write")));

        // when
        String response = mockMvc.perform(requestBuilder)
                                 .andExpect(status().isNotFound()) // then
                                 .andReturn()
                                 .getResponse()
                                 .getContentAsString(UTF_8);

        // then
        ErrorResponse errorResponse = objectMapper.readValue(response, ErrorResponse.class);

        assertEquals("Contato não encontrado.", errorResponse.getMensagens().get(0));
    }

    @Test
    void naoDeveRemoverUmContatoSemToken() throws Exception {
        // given
        Contato contato = new Contato(
            "Jordi", "Zup", "thiago.cavalcante", List.of(new Telefone("celular", "+5511999998888"))
        );
        contatoRepository.save(contato);

        MockHttpServletRequestBuilder requestBuilder = delete(
            ContatoController.BASE_URI + "/{id}", contato.getId()
        ).contentType(APPLICATION_JSON);

        // when
        mockMvc.perform(requestBuilder).andExpect(status().isUnauthorized()); // then
    }

    @Test
    void naoDeveRemoverUmContatoSemScope() throws Exception {
        // given
        Contato contato = new Contato(
            "Jordi", "Zup", "thiago.cavalcante", List.of(new Telefone("celular", "+5511999998888"))
        );
        contatoRepository.save(contato);

        MockHttpServletRequestBuilder requestBuilder = delete(
            ContatoController.BASE_URI + "/{id}", contato.getId()
        ).contentType(APPLICATION_JSON).with(jwt());

        // when
        mockMvc.perform(requestBuilder).andExpect(status().isForbidden()); // then
    }

    @Test
    void naoDeveRemoverUmContatoComScopeIncorreto() throws Exception {
        // given
        Contato contato = new Contato(
            "Jordi", "Zup", "thiago.cavalcante", List.of(new Telefone("celular", "+5511999998888"))
        );
        contatoRepository.save(contato);

        MockHttpServletRequestBuilder requestBuilder = delete(
            ContatoController.BASE_URI + "/{id}", contato.getId()
        ).contentType(APPLICATION_JSON)
         .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_contatos:read")));

        // when
        mockMvc.perform(requestBuilder).andExpect(status().isForbidden()); // then
    }

}
