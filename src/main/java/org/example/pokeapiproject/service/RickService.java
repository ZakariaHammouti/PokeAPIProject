
package org.example.pokeapiproject.service;

import org.example.pokeapiproject.dto.RickFavoriteDTO;
import org.example.pokeapiproject.dto.RickCharacterResponseDTO;
import org.example.pokeapiproject.model.IdService;
import org.example.pokeapiproject.repository.RickRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RickService {

    private final IdService idService;
    private final RickRepo repository;

    public RickService(IdService idService, RickRepo repository) {
        this.idService = idService;
        this.repository = repository;
    }

    public List<RickCharacterResponseDTO> getAllRickFavorites() {
        return repository.findAll();
    }

    public RickCharacterResponseDTO addRickFavorite(RickFavoriteDTO favoriteDTO) {
        RickCharacterResponseDTO favorite = RickCharacterResponseDTO.builder()
                .id(idService.createId())
                .characterId(favoriteDTO.characterId())
                .name(favoriteDTO.name())
                .status(favoriteDTO.status())
                .species(favoriteDTO.species())
                .gender(favoriteDTO.gender())
                .image(favoriteDTO.image())
                .build();

        return repository.save(favorite);
    }

    public void deleteRickFavorite(String id) {
        repository.deleteById(id);

    }
}
