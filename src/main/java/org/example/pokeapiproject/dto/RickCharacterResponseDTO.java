
package org.example.pokeapiproject.dto;

import lombok.Builder;
import org.springframework.data.annotation.Id;

@Builder
public record RickCharacterResponseDTO(
        @Id String id,
        String characterId,
        String name,
        String status,
        String species,
        String gender,
        String image
) {
}
