package org.example.pokeapiproject.service;


import org.example.pokeapiproject.api.PokemonResponse;
import org.example.pokeapiproject.dto.PokemonCharacterResponseRaw;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ExternalClientApi {

    public final RestClient restClient;

    public ExternalClientApi(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
                .baseUrl("https://pokeapi.co/api/v2/pokemon/")
                .build();
    }

    public PokemonResponse getAllPokemon() {
        return restClient.get()
                .retrieve()
                .body(PokemonResponse.class);
    }

    public PokemonCharacterResponseRaw getPokemonByName(String name) {
        return restClient.get()
                .uri(name)
                .retrieve()
                .body(PokemonCharacterResponseRaw.class);
    }
}
