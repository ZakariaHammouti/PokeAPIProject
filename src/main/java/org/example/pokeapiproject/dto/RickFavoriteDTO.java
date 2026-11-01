package org.example.pokeapiproject.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record RickFavoriteDTO(
        @NotBlank String characterId,
        @NotBlank String name,
        @NotBlank String status,
        @NotBlank String species,
        @NotBlank String gender,
        @NotBlank String image
) {
}
