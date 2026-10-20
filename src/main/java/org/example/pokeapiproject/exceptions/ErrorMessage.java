package org.example.pokeapiproject.exceptions;

import java.time.LocalDateTime;

public record ErrorMessage(
        String message, LocalDateTime timestamp
) {
}
