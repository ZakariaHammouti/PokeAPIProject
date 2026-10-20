package org.example.pokeapiproject.service;


import org.example.pokeapiproject.api.PokemonResponse;
import org.example.pokeapiproject.dto.FavoriteDTO;
import org.example.pokeapiproject.dto.PokemonCharacterResponseDTO;
import org.example.pokeapiproject.dto.PokemonCharacterResponseRaw;
import org.example.pokeapiproject.model.IdService;
import org.example.pokeapiproject.repository.PokeRepo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class PokeService {


    private final IdService idService;
    private final PokeRepo pokeRepo;
    private final RestClient restClient;


    public PokeService(PokeRepo pokeRepo, RestClient.Builder restClientBuilder,
                       IdService idService) {
        this.pokeRepo = pokeRepo;
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

   /* private PokemonCharacterResponseDTO getPokemonOrThrow(String name) {
        return getPokemonByName(name)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Pokemon does not exist: " + name
                ));
    }*/

    private PokemonCharacterResponseDTO mapPokemonCharacterResponseDTO(PokemonCharacterResponseRaw response,
                                                                       String id, String nickName) {


        return PokemonCharacterResponseDTO.builder()
                //Bentzer will erstmal nr Infos, vlt mit id=1 antworten, richtige ID wird in Post generiert für MongoDB
                //.id(idService.createId())
                .id(id)
                .pokemonId(String.valueOf(response.id()))
                .nickname(nickName)
                .pokemonName(response.name())
                .pictureUrl(response.sprites().other().officialArtWork().front_default())
                .height(response.height())
                .weight(response.weight())
                //gefordert nur Namen rausholen, nicht die ganze types Liste
                .types(response.types().stream()
                        .map(typesApi -> typesApi.type().name())
                        .toList())
                .build();
    }

    public PokemonCharacterResponseDTO getPokemonByName(String name) {
        try {
            PokemonCharacterResponseRaw response = restClient.get()
                    .uri(name)
                    .retrieve()
                    .body(PokemonCharacterResponseRaw.class);

            if (response == null) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Pokemon '" + name + "' existiert nicht"
                );
            }

            String DUMMY_ID = "1";
            return mapPokemonCharacterResponseDTO(response, DUMMY_ID, null);
         /*   PokemonCharacterResponseDTO pokemonCharacterResponseDTO =
                    PokemonCharacterResponseDTO.builder()
                            //Bentzer will erstmal nr Infos, vlt mit id=1 antworten, richtige ID wird in Post generiert für MongoDB
                            //.id(idService.createId())
                            .id(DUMMY_ID)
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
            return pokemonCharacterResponseDTO;*/

        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Pokemon '" + name + "' existiert nicht"
            );
        }
    }

    public PokemonCharacterResponseDTO postPokemonByName(FavoriteDTO favoriteDTO) {
        //Es müssen zwei verschiedene DTOs verwendet werden:
        //
        //1. PokéAPI DTO (Response Mapping)
        //
        //→ Wird ausschließlich für das Mapping der externen PokéAPI verwendet
        //→ enthält nur die benötigten Felder und Verschachtelungen → Wird in Entity/Datentyp umgewandelt und an den Client zurückgegeben
        //
        //2. Favorite DTO (internes System)
        //
        //→ Verfügt nur über zwei Felder: pokemonName und nickname → Wird intern auf die Datenbank Entity gemappt (Anfrage an API + ID generieren)

        //Prüfen über die API ob existiert

        PokemonCharacterResponseRaw response;

        try {
            response = restClient.get()
                    .uri(favoriteDTO.pokemonName())
                    .retrieve()
                    .body(PokemonCharacterResponseRaw.class);

        } catch (HttpClientErrorException.NotFound e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Pokemon '" + favoriteDTO.pokemonName() + "' existiert nicht"
            );
        }

        if (response == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Pokemon '" + favoriteDTO.pokemonName() + "' existiert nicht"
            );
        }

        return pokeRepo.save
                (mapPokemonCharacterResponseDTO
                        (response, idService.createId(), favoriteDTO.nickname())
                );
    }
}
