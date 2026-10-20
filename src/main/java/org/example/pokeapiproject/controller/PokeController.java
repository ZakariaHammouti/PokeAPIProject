package org.example.pokeapiproject.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.example.pokeapiproject.api.PokemonResponse;
import org.example.pokeapiproject.dto.FavoriteDTO;
import org.example.pokeapiproject.dto.PokemonCharacterResponseDTO;
import org.example.pokeapiproject.dto.UpdateFavoriteDTO;
import org.example.pokeapiproject.service.PokeService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api")
@Validated
public class PokeController {

    private final PokeService service;

    public PokeController(PokeService service) {
        this.service = service;
    }

    @GetMapping
    //gibt nur die erste Seite von external API zurück
    // public List<PokemonCharacter> getAllPokemon() {
    public PokemonResponse getAllPokemon() {
        return service.getAllPokemon();
    }

    @GetMapping("/pokemon/{name}")
    public PokemonCharacterResponseDTO getPokemonByNameFromPokeApi(@PathVariable
                                                                   @NotBlank(message = "name must not be blank")
                                                                   @Size(message = "at least 2 letters and max. 20 letters",
                                                                           min = 2, max = 20)
                                                                   String name) {
        return service.getPokemonByName(name);
    }

    @PostMapping("/collection")
    public PokemonCharacterResponseDTO postPokemonByName(@RequestBody
                                                         @Valid
                                                         FavoriteDTO favoriteDTO) {
        return service.postPokemonByName(favoriteDTO);
    }

    @GetMapping("/collection")
    public List<PokemonCharacterResponseDTO> getAllPokemonsCollection() {
        return service.getAllPokemonsCollection();
    }

    @GetMapping("/collection/{id}")
    public PokemonCharacterResponseDTO getPokemonById(
            @PathVariable
            @NotBlank(message = "name must not be blank")
            @Size(message = "size must be 36 characters long", min = 36, max = 36)

            String id
    ) {
        return service.getPokemonById(id);
    }

    @DeleteMapping("/collection/{id}")
    public ResponseEntity<Void> deletePokemonById(
            @PathVariable
            @NotBlank(message = "name must not be blank")
            @Size(message = "size must be 36 digits long", min = 36, max = 36)
            String id
    ) {
        service.deletePokemonById(id);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/collection/{id}")
    public PokemonCharacterResponseDTO updatePokemonByName(@RequestBody
                                                           @Valid
                                                           UpdateFavoriteDTO nickNameDto,
                                                           @PathVariable
                                                           @NotBlank(message = "name must not be blank")
                                                           @Size(message = "size must be 36 digits long", min = 36, max = 36)
                                                           String id) {
        return service.updatePokemonByName(nickNameDto, id);
    }
}
