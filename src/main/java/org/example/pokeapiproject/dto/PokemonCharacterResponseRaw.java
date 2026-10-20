package org.example.pokeapiproject.dto;

import apiPicture.SpritesApi;
import apiPicture.TypesApi;

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