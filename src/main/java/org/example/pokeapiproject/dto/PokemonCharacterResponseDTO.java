package org.example.pokeapiproject.dto;

import lombok.Builder;
import org.springframework.data.annotation.Id;

import java.util.List;

@Builder
public record PokemonCharacterResponseDTO(
        @Id
        String id,
        String pokemonId,
        String nickname,
        String pokemonName,
        String pictureUrl,
        Integer height,
        Integer weight,
        List<String> types
) {
}
