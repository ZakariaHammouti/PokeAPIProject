package org.example.pokeapiproject.repository;

import org.example.pokeapiproject.model.PokemonCharacter;
import org.springframework.data.repository.Repository;

public interface PokeRepo extends Repository<PokemonCharacter, String> {


}
