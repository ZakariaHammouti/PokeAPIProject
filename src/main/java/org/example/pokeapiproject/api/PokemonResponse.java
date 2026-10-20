package org.example.pokeapiproject.api;

import org.example.pokeapiproject.model.PokemonCharacter;

import java.util.List;

public record PokemonResponse(
        String count,
        String next,
        String previous,
        List<PokemonCharacter> results
) {

}
