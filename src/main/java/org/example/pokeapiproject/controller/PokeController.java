package org.example.pokeapiproject.controller;

import org.example.pokeapiproject.api.PokemonResponse;
import org.example.pokeapiproject.dto.FavoriteDTO;
import org.example.pokeapiproject.dto.PokemonCharacterResponseDTO;
import org.example.pokeapiproject.dto.PokemonCharacterResponseRaw;
import org.example.pokeapiproject.service.PokeService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RestController
@RequestMapping("/api")
@Validated
public class PokeController {

    private final PokeService service;

    public PokeController(PokeService service) {
        this.service = service;
    }

    @GetMapping
    //gibnr die erste Seite zurück
    // public List<PokemonCharacter> getAllPokemon() {
    public PokemonResponse getAllPokemon() {
        return service.getAllPokemon();
    }

    @GetMapping("/pokemon/{name}")
    public PokemonCharacterResponseDTO getPokemonByName(@PathVariable String name) {
        return service.getPokemonByName(name);
    }

    @PostMapping("/collection")
    public PokemonCharacterResponseDTO postPokemonByName(@RequestBody FavoriteDTO favoriteDTO) {
        return service.postPokemonByName(favoriteDTO);
    }
}
