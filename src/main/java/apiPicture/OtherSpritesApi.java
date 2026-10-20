package apiPicture;

import com.fasterxml.jackson.annotation.JsonProperty;


public record OtherSpritesApi(
        @JsonProperty("official-artwork")
        OfficialArtWorkApi officialArtWork
) {
}
