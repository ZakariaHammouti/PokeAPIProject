package org.example.pokeapiproject.service;


import org.example.pokeapiproject.api.PokemonResponse;
import org.example.pokeapiproject.dto.PokemonCharacterResponseDTO;
import org.example.pokeapiproject.dto.PokemonCharacterResponseRaw;
import org.example.pokeapiproject.model.IdService;
import org.example.pokeapiproject.repository.PokeRepo;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class PokeService {


    private final IdService idService;
    //private final PokeRepo pokeRepo;
    private final RestClient restClient;


    public PokeService(PokeRepo pokeRepo, RestClient.Builder restClientBuilder,
                       IdService idService) {
        // this.pokeRepo = pokeRepo;
        this.idService = idService;
        this.restClient = restClientBuilder
                .baseUrl("https://pokeapi.co/api/v2/pokemon/")
                .build();
    }

    ///  api/pokemon
    /// got only from first page
    // public List<PokemonCharacter> getAllPokemon() {
    public PokemonResponse getAllPokemon() {
        PokemonResponse response = restClient.get()
                .retrieve()
                .body(PokemonResponse.class);

        return response;
       /* return response.results().stream()
                .map(pokemon -> new PokemonCharacter(
                        pokemon.name(),
                        pokemon.url()
                ))
                .toList();*/
    }

    public PokemonCharacterResponseDTO getPokemonByName(String name) {
        PokemonCharacterResponseRaw response = restClient.get()
                .uri(name)
                .retrieve()
                .body(PokemonCharacterResponseRaw.class);

        PokemonCharacterResponseDTO pokemonCharacterResponseDTO =
                PokemonCharacterResponseDTO.builder()
                        .id(idService.createId())
                        .pokemonId(String.valueOf(response.id()))
                        .nickname(null)
                        .pokemonName(response.name())
                        .pictureUrl(response.sprites().other().officialArtWork().front_default())
                        .height(response.height())
                        .weight(response.weight())
                        //gefordert nur Namen rausholen, nicht die ganze types Liste
                        .types(response.types().stream()
                                .map(typesApi -> typesApi.type().name())
                                .toList())
                        .build();
        return pokemonCharacterResponseDTO;


    }
}
