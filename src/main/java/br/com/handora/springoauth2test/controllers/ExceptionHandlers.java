package br.com.handora.springoauth2test.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ExceptionHandlers {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    ErrorResponse handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                               WebRequest webRequest) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        Integer totalErros = fieldErrors.size();
        String palavraErro = totalErros == 1 ? "erro" : "erros";
        String mensagemGeral = "Validação falhou com " + totalErros + " " + palavraErro + ".";

        ErrorResponse erroPadronizado = getErrorResponse(
            HttpStatus.BAD_REQUEST, webRequest, mensagemGeral
        );

        fieldErrors.forEach(erroPadronizado::adicionarErro);

        return erroPadronizado;
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatus(ResponseStatusException ex,
                                                              WebRequest webRequest) {
        HttpStatus httpStatus = ex.getStatus();
        String mensagemGeral = "Houve um problema com a sua requisição.";

        ErrorResponse erroPadronizado = getErrorResponse(httpStatus, webRequest, mensagemGeral);

        erroPadronizado.adicionarErro(ex.getReason());

        return ResponseEntity.status(httpStatus).body(erroPadronizado);
    }

    private ErrorResponse getErrorResponse(HttpStatus status, WebRequest webRequest,
                                           String mensagemGeral) {
        Integer codigoHttp = status.value();
        String mensagemHttp = status.getReasonPhrase();
        String caminho = webRequest.getDescription(false).replace("uri=", "");

        return new ErrorResponse(codigoHttp, mensagemHttp, mensagemGeral, caminho);
    }

}
