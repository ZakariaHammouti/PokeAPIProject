package org.example.pokeapiproject.repository;

import org.example.pokeapiproject.dto.FavoriteDTO;
import org.example.pokeapiproject.dto.PokemonCharacterResponseDTO;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface PokeRepo extends MongoRepository<PokemonCharacterResponseDTO, String> {

    boolean existsByPokemonNameIgnoreCase(String pokemonName);

    Optional<PokemonCharacterResponseDTO> findById(String id);
}
