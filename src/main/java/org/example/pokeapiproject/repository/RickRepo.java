
package org.example.pokeapiproject.repository;

import org.example.pokeapiproject.dto.RickCharacterResponseDTO;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RickRepo extends MongoRepository<RickCharacterResponseDTO, String> {
}
