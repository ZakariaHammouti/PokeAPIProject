package org.example.pokeapiproject.exceptions;

public class ExceptionAlreadyExists extends RuntimeException {
    public ExceptionAlreadyExists(String message) {
        super(message);
    }
}
