
package org.example.pokeapiproject.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.example.pokeapiproject.dto.RickFavoriteDTO;
import org.example.pokeapiproject.dto.RickCharacterResponseDTO;
import org.example.pokeapiproject.service.RickService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/rick/collection")
@Validated
public class RickController {

    private final RickService service;

    public RickController(RickService service) {
        this.service = service;
    }

    // GET: Alle Rick-&-Morty-Favoriten laden
    @GetMapping
    public List<RickCharacterResponseDTO> getAllRickFavorites() {
        return service.getAllRickFavorites();
    }

    // POST: Einen Rick-&-Morty-Favoriten speichern
    @PostMapping
    public RickCharacterResponseDTO addRickFavorite(
            @RequestBody @Valid RickFavoriteDTO favoriteDTO
    ) {
        return service.addRickFavorite(favoriteDTO);
    }

    // DELETE: Einen Rick-&-Morty-Favoriten löschen
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRickFavorite(
            @PathVariable
            @NotBlank(message = "ID darf nicht leer sein")
            @Size(min = 36, max = 36, message = "ID muss 36 Zeichen lang sein")
            String id
    ) {
        service.deleteRickFavorite(id);
        return ResponseEntity.noContent().build();
    }
}
