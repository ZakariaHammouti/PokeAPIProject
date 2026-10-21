package org.example.pokeapiproject.service;

import org.example.pokeapiproject.api.PokemonResponse;
import org.example.pokeapiproject.apiPicture.*;
import org.example.pokeapiproject.dto.FavoriteDTO;
import org.example.pokeapiproject.dto.PokemonCharacterResponseDTO;
import org.example.pokeapiproject.dto.PokemonCharacterResponseRaw;
import org.example.pokeapiproject.dto.UpdateFavoriteDTO;
import org.example.pokeapiproject.exceptions.ExceptionNotFound;
import org.example.pokeapiproject.model.IdService;
import org.example.pokeapiproject.model.PokemonCharacter;
import org.example.pokeapiproject.repository.PokeRepo;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PokeServiceTest {

    @Test
    void getAllPokemon_shouldReturnExpectedObject_ByGivenRealPokemonResponse() {
        //Given
        ExternalClientApi externalClientApi = mock(ExternalClientApi.class);
        IdService idService = mock(IdService.class);
        PokeRepo pokeRepo = mock(PokeRepo.class);

        PokemonResponse expected = new PokemonResponse(
                "1351",
                "https://pokeapi.co/api/v2/pokemon?offset=20&limit=20",
                null,
                List.of(
                        new PokemonCharacter(
                                "bulbasaur",
                                "https://pokeapi.co/api/v2/pokemon/1/"
                        ),
                        new PokemonCharacter(
                                "ivysaur",
                                "https://pokeapi.co/api/v2/pokemon/2/"
                        ),
                        new PokemonCharacter(
                                "venusaur",
                                "https://pokeapi.co/api/v2/pokemon/3/"
                        ),
                        new PokemonCharacter(
                                "charmander",
                                "https://pokeapi.co/api/v2/pokemon/4/"
                        )
                )
        );
        ;
        when(externalClientApi.getAllPokemon())
                .thenReturn(expected);

        PokeService pockService = new PokeService(pokeRepo, externalClientApi, idService);

        //When and Then

        PokemonResponse actual = pockService.getAllPokemon();

        assertEquals(expected, actual);
        verify(externalClientApi).getAllPokemon();
    }

    @Test
    void getAllPokemon_shouldReturnExpectedObject_ByGivenMockedPokemonResponse() {
        //Given
        ExternalClientApi externalClientApi = mock(ExternalClientApi.class);
        IdService idService = mock(IdService.class);
        PokeRepo pokeRepo = mock(PokeRepo.class);

        PokemonResponse expected = mock(PokemonResponse.class);
        when(externalClientApi.getAllPokemon())
                .thenReturn(expected);

        PokeService pockService = new PokeService(pokeRepo, externalClientApi, idService);

        //When and Then

        PokemonResponse actual = pockService.getAllPokemon();

        assertSame(expected, actual);
        verify(externalClientApi).getAllPokemon();
    }

    @Test
    void getPokemonByName_ShouldReturnExpectedObject_ByGivenRealPokemonResponse() {
        //Given
        ExternalClientApi externalClientApi = mock(ExternalClientApi.class);
        IdService idService = mock(IdService.class);
        PokeRepo pokeRepo = mock(PokeRepo.class);
        PokemonCharacterResponseDTO expected = new PokemonCharacterResponseDTO(
                "1",
                "1",
                null,
                "bulbasaur",
                "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/1.png",
                7,
                69,
                List.of("grass", "poison")
        );
        PokemonCharacterResponseRaw rawResponse = new PokemonCharacterResponseRaw(
                1,
                "bulbasaur",
                7,
                69,
                new SpritesApi(
                        new OtherSpritesApi(
                                new OfficialArtWorkApi(
                                        "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/1.png"
                                )
                        )
                ),
                List.of(new TypesApi(new TypeApi("grass")),
                        new TypesApi(new TypeApi("poison")))
        );

        when(externalClientApi.getPokemonByName("bulbasaur"))
                .thenReturn(rawResponse);
        // When

        PokeService pockService = new PokeService(pokeRepo, externalClientApi, idService);

        //When and Then

        // When
        PokemonCharacterResponseDTO actual =
                pockService.getPokemonByName("bulbasaur");

        assertEquals(expected, actual);
        verify(externalClientApi).getPokemonByName("bulbasaur");
    }

    @Test
    void postPokemonByName_ShouldCreateNewObjecByValidData() {
        // Given
        ExternalClientApi externalClientApi = mock(ExternalClientApi.class);
        IdService idService = mock(IdService.class);
        PokeRepo pokeRepo = mock(PokeRepo.class);

        PokeService pokeService =
                new PokeService(pokeRepo, externalClientApi, idService);

        FavoriteDTO favoriteDTO =
                new FavoriteDTO("bulbasaur", "asffff");

        PokemonCharacterResponseRaw rawResponse =
                new PokemonCharacterResponseRaw(
                        1,
                        "bulbasaur",
                        7,
                        69,
                        new SpritesApi(
                                new OtherSpritesApi(
                                        new OfficialArtWorkApi(
                                                "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/1.png"
                                        )
                                )
                        ),
                        List.of(
                                new TypesApi(new TypeApi("grass")),
                                new TypesApi(new TypeApi("poison"))
                        )
                );

        when(externalClientApi.getPokemonByName("bulbasaur"))
                .thenReturn(rawResponse);

        when(pokeRepo.existsByPokemonNameIgnoreCase("bulbasaur"))
                .thenReturn(false);

        when(idService.createId()).thenReturn("16add4f6-6821-4396-b049-6d5ad4ad6bf6");

        when(pokeRepo.save(any(PokemonCharacterResponseDTO.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        PokemonCharacterResponseDTO actual =
                pokeService.postPokemonByName(favoriteDTO);

        // Then
        PokemonCharacterResponseDTO expected =
                new PokemonCharacterResponseDTO(
                        "16add4f6-6821-4396-b049-6d5ad4ad6bf6",
                        "1",
                        "asffff",
                        "bulbasaur",
                        "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/1.png",
                        7,
                        69,
                        List.of("grass", "poison")
                );

        assertEquals(expected, actual);

        verify(externalClientApi).getPokemonByName("bulbasaur");
        verify(pokeRepo).existsByPokemonNameIgnoreCase("bulbasaur");
        verify(pokeRepo).save(expected);
    }

    @Test
    void getAllPokemonsCollection_ShouldReturnExpectedPokemonList() {
// Given
        PokeRepo pokeRepo = mock(PokeRepo.class);
        ExternalClientApi externalClientApi = mock(ExternalClientApi.class);
        IdService idService = mock(IdService.class);

        PokeService pokeService =
                new PokeService(pokeRepo, externalClientApi, idService);

        List<PokemonCharacterResponseDTO> expected = List.of(
                new PokemonCharacterResponseDTO(
                        "1234",
                        "1",
                        "Bubi",
                        "bulbasaur",
                        "https://example.com/bulbasaur.png",
                        7,
                        69,
                        List.of("grass", "poison")
                )
        );

        when(pokeRepo.findAll()).thenReturn(expected);

        // When
        List<PokemonCharacterResponseDTO> actual =
                pokeService.getAllPokemonsCollection();

        // Then
        assertEquals(expected, actual);
        verify(pokeRepo).findAll();
    }

    @Test
    void getPokemonById_shouldReturnPokemon() {

        String id = "1234";
        // Given
        PokeRepo pokeRepo = mock(PokeRepo.class);
        ExternalClientApi externalClientApi = mock(ExternalClientApi.class);
        IdService idService = mock(IdService.class);

        PokeService pokeService =
                new PokeService(pokeRepo, externalClientApi, idService);

        PokemonCharacterResponseDTO expected =
                new PokemonCharacterResponseDTO(
                        "1234",
                        "1",
                        "Bubi",
                        "bulbasaur",
                        "https://example.com/bulbasaur.png",
                        7,
                        69,
                        List.of("grass", "poison")
                );

        when(pokeRepo.findById(id)).thenReturn(Optional.of(expected));

        // When
        PokemonCharacterResponseDTO actual = pokeService.getPokemonById(id);

        // Then
        assertEquals(expected, actual);
    }

    //get by id exception tested
    @Test
    void getPokemonById_shouldThrowExceptionByNotFound() {
        String id = "1234";
        // Given
        PokeRepo pokeRepo = mock(PokeRepo.class);
        ExternalClientApi externalClientApi = mock(ExternalClientApi.class);
        IdService idService = mock(IdService.class);

        PokeService pokeService =
                new PokeService(pokeRepo, externalClientApi, idService);

        when(pokeRepo.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(
                ExceptionNotFound.class,
                () -> pokeService.getPokemonById(id)
        );
    }

    @Test
    void deletePokemonById_ShouldDeltetePokemon() {
        String id = "1234";
        // Given
        PokeRepo pokeRepo = mock(PokeRepo.class);
        ExternalClientApi externalClientApi = mock(ExternalClientApi.class);
        IdService idService = mock(IdService.class);

        PokeService pokeService =
                new PokeService(pokeRepo, externalClientApi, idService);
        when(pokeRepo.existsById(id)).thenReturn(true);

        // When
        pokeService.deletePokemonById(id);

        // Then
        verify(pokeRepo).existsById(id);
        verify(pokeRepo).deleteById(id);

    }

    //delete by id exception tested
    @Test
    void deletePokemonById_ShouldThrowExceptionByNotFound() {

        String id = "1234";
        // Given
        PokeRepo pokeRepo = mock(PokeRepo.class);
        ExternalClientApi externalClientApi = mock(ExternalClientApi.class);
        IdService idService = mock(IdService.class);
        when(pokeRepo.existsById(id)).thenReturn(false);

        PokeService pokeService =
                new PokeService(pokeRepo, externalClientApi, idService);
        // When & Then
        assertThrows(
                ExceptionNotFound.class,
                () -> pokeService.deletePokemonById(id)
        );

        verify(pokeRepo, never()).deleteById(id);
    }

    @Test
    void updatePokemonByName_ShouldUptadePokemon() {
        String id = "1234";
        // Given
        PokeRepo pokeRepo = mock(PokeRepo.class);
        ExternalClientApi externalClientApi = mock(ExternalClientApi.class);
        IdService idService = mock(IdService.class);

        PokeService pokeService =
                new PokeService(pokeRepo, externalClientApi, idService);

        PokemonCharacterResponseDTO originalPokemon =
                new PokemonCharacterResponseDTO(
                        id,
                        "1",
                        "Bubi",
                        "bulbasaur",
                        "bulbasaur.png",
                        7,
                        69,
                        List.of("grass", "poison")
                );
        UpdateFavoriteDTO updateDTO = new UpdateFavoriteDTO("Fan");

        when(pokeRepo.findById(id))
                .thenReturn(Optional.of(originalPokemon));

        when(pokeRepo.save(any(PokemonCharacterResponseDTO.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PokemonCharacterResponseDTO actual =
                pokeService.updatePokemonByName(updateDTO, id);
        
        // Then
        assertEquals(id, actual.id());
        assertEquals("Fan", actual.nickname());


        assertEquals("1", actual.pokemonId());
        assertEquals("bulbasaur", actual.pokemonName());
        assertEquals("bulbasaur.png", actual.pictureUrl());
        assertEquals(7, actual.height());
        assertEquals(69, actual.weight());
        assertEquals(List.of("grass", "poison"), actual.types());

        verify(pokeRepo).findById(id);
        verify(pokeRepo).save(any(PokemonCharacterResponseDTO.class));
    }
}