package org.example.pokeapiproject.exceptions;


import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
//400 Bad Request – ungültiger Request-Body oder fehlgeschlagene Validierung @NotBlank
//404 Not Found – Pokémon oder Favorit existiert nicht
//405 Method Not Allowed – HTTP-Methode passt nicht zum Endpunkt
//409 Conflict – Pokémon ist bereits als Favorit gespeichert
//500 Internal Server Error – unerwarteter Fehler auf dem Server

    //400 Bad Request
    @ExceptionHandler(ExceptionBadRequest.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleBadRequestException(ExceptionBadRequest e) {
        return new ErrorMessage(e.getMessage(),
                LocalDateTime.now());
    }

    //404 Not Found
    @ExceptionHandler(ExceptionNotFound.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessage handleNotAcceptableException(ExceptionNotFound e) {
        return new ErrorMessage(e.getMessage(),
                LocalDateTime.now());
    }

    //405 METHOD_NOT_ALLOWED
    @ExceptionHandler(NotAllowedEndpoint.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ErrorMessage handleNotAllowed(NotAllowedEndpoint e) {
        return new ErrorMessage(e.getMessage(), LocalDateTime.now());
    }

    //409 Conflict
    @ExceptionHandler(ExceptionAlreadyExists.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorMessage handleConflictException(Exception e) {
        return new ErrorMessage(e.getMessage(),
                LocalDateTime.now());
    }

    //500 Internal Server Error
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage handleRunTimeException(Exception e) {
        return new ErrorMessage(e.getMessage(),
                LocalDateTime.now());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        Map<String, List<String>> validationErrors = new HashMap<>();

        List<ObjectError> allErrors = ex.getBindingResult().getAllErrors();

        allErrors.forEach(error -> {
            String fieldName = error instanceof FieldError fieldError
                    ? fieldError.getField()
                    : error.getObjectName();

            String errorMessage = error.getDefaultMessage();

            validationErrors
                    .computeIfAbsent(fieldName, key -> new ArrayList<>())
                    .add(errorMessage);
        });

        return new ResponseEntity<>(validationErrors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(
            ConstraintViolationException ex,
            WebRequest request) {

        List<String> errors = ex.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .toList();

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    // 404 Not Found: Endpunkt oder statische Ressource nicht gefunden
    @Override
    protected ResponseEntity<Object> handleNoResourceFoundException(
            NoResourceFoundException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        ErrorMessage error = new ErrorMessage(
                "Der angeforderte Endpoint ist nicht implementiert",
                LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        ErrorMessage error = new ErrorMessage(
                "Die HTTP-Methode " + ex.getMethod() +
                        " ist für diesen Endpunkt nicht erlaubt",
                LocalDateTime.now()
        );

        return new ResponseEntity<>(
                error,
                HttpStatus.METHOD_NOT_ALLOWED
        );
    }
}
