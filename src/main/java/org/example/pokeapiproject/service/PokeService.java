package org.example.pokeapiproject.service;

import org.example.pokeapiproject.api.PokemonResponse;
import org.example.pokeapiproject.dto.FavoriteDTO;
import org.example.pokeapiproject.dto.PokemonCharacterResponseDTO;
import org.example.pokeapiproject.dto.PokemonCharacterResponseRaw;
import org.example.pokeapiproject.dto.UpdateFavoriteDTO;
import org.example.pokeapiproject.exceptions.ExceptionAlreadyExists;
import org.example.pokeapiproject.exceptions.ExceptionNotFound;
import org.example.pokeapiproject.model.IdService;
import org.example.pokeapiproject.repository.PokeRepo;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;

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
        return restClient.get()
                .retrieve()
                .body(PokemonResponse.class);
    }

    private PokemonCharacterResponseDTO mapPokemonCharacterResponseDTO(PokemonCharacterResponseRaw response,
                                                                       String id, String nickName) {
        return PokemonCharacterResponseDTO.builder()
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
                throw new ExceptionNotFound(
                        "Pokemon " + name + "' existiert nicht"
                );
            }
            String DUMMY_ID = "1";
            return mapPokemonCharacterResponseDTO(response, DUMMY_ID, null);

        } catch (HttpClientErrorException.NotFound ex) {
            throw new ExceptionNotFound(
                    "Pokemon " + name + "' existiert nicht"
            );
        }
    }

    public PokemonCharacterResponseDTO postPokemonByName(FavoriteDTO favoriteDTO) {
        PokemonCharacterResponseRaw response;

        //Prüfen, ob es in der External API existiert
        try {
            response = restClient.get()
                    .uri(favoriteDTO.pokemonName())
                    .retrieve()
                    .body(PokemonCharacterResponseRaw.class);

        } catch (HttpClientErrorException.NotFound e) {
            throw new ExceptionNotFound(
                    "Pokemon " + favoriteDTO.pokemonName() + "' existiert nicht"
            );
        }

        if (response == null) {
            throw new ExceptionNotFound(
                    "Pokemon " + favoriteDTO.pokemonName() + "' existiert nicht"
            );
        }

        //Prüfe, ob in Internal API dieser Name schon existiert
        if (pokeRepo.existsByPokemonNameIgnoreCase(
                favoriteDTO.pokemonName())) {
            throw new ExceptionAlreadyExists(
                    "Dieses Pokemon ist bereits als Favorit gespeichert"
            );
        }

        return pokeRepo.save
                (mapPokemonCharacterResponseDTO(
                        response,
                        idService.createId(),
                        favoriteDTO.nickname()));
    }

    public List<PokemonCharacterResponseDTO> getAllPokemonsCollection() {
        return pokeRepo.findAll();
    }


    public PokemonCharacterResponseDTO getPokemonById(String id) {
        return pokeRepo.findById(id)
                .orElseThrow(() -> new ExceptionNotFound(
                        "Favorit mit ID '" + id + "' wurde nicht gefunden"));
    }

    public void deletePokemonById(String id) {
        if (!pokeRepo.existsById(id)) {
            throw new ExceptionNotFound(
                    "Favorit mit ID " + id + " wurde nicht gefunden"
            );
        }
        pokeRepo.deleteById(id);
    }

    public PokemonCharacterResponseDTO updatePokemonByName(UpdateFavoriteDTO nickNameDto,
                                                           String id) {
        PokemonCharacterResponseDTO responseDTO = pokeRepo.findById(id).
                orElseThrow(() -> new ExceptionNotFound(
                        "Pokemon mit der " + id + " existiert nicht"));


        return pokeRepo.save(PokemonCharacterResponseDTO.builder()
                .id(id)
                .pokemonId(responseDTO.pokemonId())
                .nickname(nickNameDto.nickname())
                .pokemonName(responseDTO.pokemonName())
                .pictureUrl(responseDTO.pictureUrl())
                .height(responseDTO.height())
                .weight(responseDTO.weight())
                //gefordert nur Namen rausholen, nicht die ganze types Liste
                .types(responseDTO.types())
                .build());
    }
}
