package org.example.pokeapiproject.dto;

import org.example.pokeapiproject.apiPicture.SpritesApi;
import org.example.pokeapiproject.apiPicture.TypesApi;

import java.util.List;

public record PokemonCharacterResponseRaw(
        Integer id,
        String name,
        Integer height,
        Integer weight,
        SpritesApi sprites,
        List<TypesApi> types
) {
}