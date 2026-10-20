package org.example.pokeapiproject.exceptions;

public class NotAllowedEndpoint extends RuntimeException {
    public NotAllowedEndpoint(String message) {
        super(message);
    }
}
