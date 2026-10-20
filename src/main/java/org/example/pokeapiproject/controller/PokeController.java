package org.example.pokeapiproject.controller;

import org.example.pokeapiproject.api.PokemonResponse;
import org.example.pokeapiproject.dto.PokemonCharacterResponseDTO;
import org.example.pokeapiproject.dto.PokemonCharacterResponseRaw;
import org.example.pokeapiproject.service.PokeService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/pokemon")
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

    @GetMapping("/{name}")
    public PokemonCharacterResponseDTO getPokemonByName(@PathVariable String name) {
        return service.getPokemonByName(name);
    }
}
