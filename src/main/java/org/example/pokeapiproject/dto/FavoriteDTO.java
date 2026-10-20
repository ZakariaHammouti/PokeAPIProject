package org.example.pokeapiproject.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FavoriteDTO(

        @NotBlank(message = "username must not be blank")
        @Size(message = "at least 2 letters", min = 2, max = 50)
        String pokemonName,

        @NotBlank(message = "username must not be blank")
        @Size(message = "at least 2 letters", min = 2, max = 50)
        String nickname
) {
}
