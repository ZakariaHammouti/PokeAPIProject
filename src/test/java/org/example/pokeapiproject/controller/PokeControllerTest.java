package org.example.pokeapiproject.controller;

import org.example.pokeapiproject.api.PokemonResponse;
import org.example.pokeapiproject.dto.PokemonCharacterResponseDTO;
import org.example.pokeapiproject.dto.UpdateFavoriteDTO;
import org.example.pokeapiproject.model.PokemonCharacter;
import org.example.pokeapiproject.repository.PokeRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restclient.test.autoconfigure.AutoConfigureMockRestServiceServer;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import java.net.URI;
import java.util.List;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureMockRestServiceServer
class PokeControllerTest {
    //das ist für Anfragen von Außen um den Controller zu injizieren
    //Simuliert den Client
    //Injiziert MockMvc, um HTTP-Anfragen an den Controller zu simulieren
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PokeRepo pokeRepo;

    @Autowired
    private MockRestServiceServer mockRestServiceServer;


    @Test
    void getAllPokemon_ShouldReturnListOfPokemon_WhenCalled() throws Exception {
        //Given
        //External Client API simulation
        //HTTP-Aufruf abfangen und gebe die simulierte JSON-Antwort zurück
        mockRestServiceServer.expect(requestTo("https://pokeapi.co/api/v2/pokemon/"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {
                           "count": 1351,
                           "next": "https://pokeapi.co/api/v2/pokemon/?offset=20&limit=20",
                           "previous": null,
                           "results": [
                             {
                               "name": "bulbasaur",
                               "url": "https://pokeapi.co/api/v2/pokemon/1/"
                             },
                             {
                               "name": "ivysaur",
                               "url": "https://pokeapi.co/api/v2/pokemon/2/"
                             },
                             {
                               "name": "venusaur",
                               "url": "https://pokeapi.co/api/v2/pokemon/3/"
                             },
                             {
                               "name": "charmander",
                               "url": "https://pokeapi.co/api/v2/pokemon/4/"
                             }
                           ]
                        }
                        """, MediaType.APPLICATION_JSON));

        //WHEN & THEN
        //Antwort von Server beim injezieren des Controller-Endpoints
        mockMvc.perform(get("/api"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(
                        """
                                {
                                   "count": 1351,
                                   "next": "https://pokeapi.co/api/v2/pokemon/?offset=20&limit=20",
                                   "previous": null,
                                   "results": [
                                     {
                                       "name": "bulbasaur",
                                       "url": "https://pokeapi.co/api/v2/pokemon/1/"
                                     },
                                     {
                                       "name": "ivysaur",
                                       "url": "https://pokeapi.co/api/v2/pokemon/2/"
                                     },
                                     {
                                       "name": "venusaur",
                                       "url": "https://pokeapi.co/api/v2/pokemon/3/"
                                     },
                                     {
                                       "name": "charmander",
                                       "url": "https://pokeapi.co/api/v2/pokemon/4/"
                                     }
                                   ]
                                }
                                """
                ));
    }

    @Test
    void getPokemonByNameFromPokeApi_ShouldReturnOneElement_WhenCalled() throws Exception {
        //Simulierte External API. Hier lege ich fest
        //was API antwortet, wenn die aufgerufen wird,
        //wenn du aufgerufen wirst, antworte mit diesem JSON
        mockRestServiceServer.expect(requestTo("https://pokeapi.co/api/v2/pokemon/bulbasaur"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {
                          "id": 1,
                          "name": "bulbasaur",
                          "height": 7,
                          "weight": 69,
                          "sprites": {
                            "other": {
                              "official-artwork": {
                                "front_default": "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/1.png"
                              }
                            }
                          },
                          "types": [
                            {
                                  "slot": 1,
                                  "type": {
                                    "name": "grass",
                                    "url": "https://pokeapi.co/api/v2/type/7/"
                                  }
                                },
                                {
                                  "slot": 2,
                                  "type": {
                                    "name": "poison",
                                    "url": "https://pokeapi.co/api/v2/type/4/"
                                  }
                                }
                          ]
                        }
                        """, MediaType.APPLICATION_JSON));
        //WHEN & THEN
        //Meine ANfrage an dem Controller und was ich als Antwort erwarte
        mockMvc.perform(get("/api/pokemon/bulbasaur"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(
                        """
                                      {
                                          "id": "1",
                                          "pokemonId": "1",
                                          "nickname": null,
                                          "pokemonName": "bulbasaur",
                                          "pictureUrl": "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/1.png",
                                          "height": 7,
                                          "weight": 69,
                                          "types": [
                                              "grass",
                                              "poison"
                                          ]
                                      }
                                """
                ));
        //HTTP-Aufruf tatsächlich ausgeführt
        mockRestServiceServer.verify();
    }

    @Test
    void getPokemonByNameFromPokeApi_ShouldReturnNotFound_WhenResponseIsEmpty()
            throws Exception {

        // Given: Externe API liefert einen leeren Body
        mockRestServiceServer
                .expect(requestTo(
                        "https://pokeapi.co/api/v2/pokemon/pokemon"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("", MediaType.APPLICATION_JSON));

        // When & Then: Eigenen Controller aufrufen
        mockMvc.perform(get("/api/pokemon/pokemon"))
                .andExpect(status().isNotFound());
    }

    @Test
    void postPokemonByName_ShouldSaveAndReturnPokemon_WhenCalled()
            throws Exception {
// Given: Externe API simulieren
        // GIVEN: Externe PokeAPI-Anfrage simulieren
        mockRestServiceServer.expect(requestTo(
                        "https://pokeapi.co/api/v2/pokemon/bulbasaur"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {
                            "id": 1,
                            "name": "bulbasaur",
                            "height": 7,
                            "weight": 69,
                            "sprites": {
                                "other": {
                                    "official-artwork": {
                                        "front_default": "bulbasaur.png"
                                    }
                                }
                            },
                            "types": [
                                {
                                    "slot": 1,
                                    "type": {
                                        "name": "grass",
                                        "url": "https://pokeapi.co/api/v2/type/12/"
                                    }
                                },
                                {
                                    "slot": 2,
                                    "type": {
                                        "name": "poison",
                                        "url": "https://pokeapi.co/api/v2/type/4/"
                                    }
                                }
                            ]
                        }
                        """, MediaType.APPLICATION_JSON));

        //When & Then
        mockMvc.perform(post("/api/collection")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "pokemonName": "bulbasaur",
                                    "nickname": "asffff"
                                }
                                """))

                // THEN: Antwort des Controllers prüfen
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                            "pokemonId": "1",
                            "nickname": "asffff",
                            "pokemonName": "bulbasaur",
                            "pictureUrl": "bulbasaur.png",
                            "height": 7,
                            "weight": 69,
                            "types": ["grass", "poison"]
                        }
                        """));

        // Sicherstellen, dass die externe Anfrage ausgeführt wurde
        mockRestServiceServer.verify();
    }

    @Test
    void postPokemonByName_ShouldThrowNotFound_WhenPokemonDoesNotExist()
            throws Exception {

        // given PokeAPI antwortet mit 404 Not Found
        mockRestServiceServer.expect(requestTo(
                        "https://pokeapi.co/api/v2/pokemon/okemon"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON));

        // when then
        mockMvc.perform(post("/api/collection")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "pokemonName": "okemon",
                                    "nickname": "asffff"
                                }
                                """))
                .andExpect(status().isNotFound());

        // Sicherstellen dass die externe Client Anfrage ausgeführt wurde
        mockRestServiceServer.verify();
    }

    @Test
    void getAllPokemonsCollection_ShouldReturnSavedCollection() throws Exception {

        //Test datenbank
        pokeRepo.save(new PokemonCharacterResponseDTO(
                "10",
                "1",
                "Bisasam",
                "bulbasaur",
                "bulbasaur.png",
                7,
                69,
                List.of("grass", "poison")
        ));
        mockMvc.perform(MockMvcRequestBuilders.get("/api/collection"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(
                        """
                                [
                                    {
                                        "id": "10",
                                        "pokemonId": "1",
                                        "nickname": "Bisasam",
                                        "pokemonName": "bulbasaur",
                                        "pictureUrl": "bulbasaur.png",
                                        "height": 7,
                                        "weight": 69,
                                        "types": ["grass", "poison"]
                                    }
                                ]
                                """
                ));
    }

    @Test
    void getPokemonById_ShouldReturnSavedPokemon() throws Exception {
        // GIVEN
        pokeRepo.save(new PokemonCharacterResponseDTO(
                "012345678-0123456-890123-56789012345",
                "1",
                "Bisasam",
                "bulbasaur",
                "https://example.com/bulbasaur.png",
                7,
                69,
                List.of("grass", "poison")
        ));

        // WHEN & THEN
        mockMvc.perform(MockMvcRequestBuilders.get(
                        "/api/collection/012345678-0123456-890123-56789012345"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(
                        """
                                {
                                    "id": "012345678-0123456-890123-56789012345",
                                    "pokemonId": "1",
                                    "nickname": "Bisasam",
                                    "pokemonName": "bulbasaur",
                                    "pictureUrl": "https://example.com/bulbasaur.png",
                                    "height": 7,
                                    "weight": 69,
                                    "types": ["grass", "poison"]
                                }
                                """
                ));

    }

    @Test
    void getPokemonById_ShouldReturnNotFound_WhenIdDoesNotExist()
            throws Exception {

        // WHEN & THEN
        mockMvc.perform(MockMvcRequestBuilders.get(
                        "/api/collection/012345678-0123456-890123-56789012345"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void deletePokemonById_ShouldReturnSavedPokemon() throws Exception {
        // GIVEN
        pokeRepo.save(new PokemonCharacterResponseDTO(
                "012345678-0123456-890123-56789012345",
                "1",
                "Bisasam",
                "bulbasaur",
                "https://example.com/bulbasaur.png",
                7,
                69,
                List.of("grass", "poison")
        ));

        // WHEN & THEN
        mockMvc.perform(MockMvcRequestBuilders.delete(
                        "/api/collection/012345678-0123456-890123-56789012345"))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andExpect(MockMvcResultMatchers.content().string(
                        ""));

    }

    @Test
    void deletePokemonById_ShouldReturnNotFound_WhenIdDoesNotExist()
            throws Exception {

        // WHEN & THEN
        mockMvc.perform(MockMvcRequestBuilders.delete(
                        "/api/collection/012345678-0123456-890123-56789012345"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void updatePokemonByName_ShouldUpdateNickname_WhenIdExists()
            throws Exception {

        // GIVEN
        String id = "12345678-0000-0000-0000-123456789012";

        pokeRepo.save(new PokemonCharacterResponseDTO(
                id,
                "1",
                "Bisasam",
                "bulbasaur",
                "bulbasaur.png",
                7,
                69,
                List.of("grass", "poison")
        ));

        // WHEN & THEN
        mockMvc.perform(MockMvcRequestBuilders.put("/api/collection/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nickname": "Venus"
                                }
                                """))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(
                        """
                                {
                                    "id": "12345678-0000-0000-0000-123456789012",
                                    "pokemonId": "1",
                                    "nickname": "Venus",
                                    "pokemonName": "bulbasaur",
                                    "pictureUrl": "bulbasaur.png",
                                    "height": 7,
                                    "weight": 69,
                                    "types": ["grass", "poison"]
                                }
                                """
                ));
    }

    @Test
    void updatePokemonByName_ShouldReturnNotFound_WhenIdDoesNotExist()
            throws Exception {

        // WHEN & THEN
        mockMvc.perform(MockMvcRequestBuilders.put(
                                "/api/collection/12345678-0000-0000-0000-123456789012")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nickname": "Venus"
                                }
                                """))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    //########################### Validation TEST FÜR @NotBlank @Size...
    //Ungültiger Nickname (@Valid)
    @Test
    void updatePokemon_ShouldReturnValidationMessages_WhenNicknameIsInvalid()
            throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.put(
                                "/api/collection/12345678-1234-1234-1234-123456789012")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nickname": ""
                                }
                                """))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json("""
                        {
                            "nickname": [
                                "at least 2 letters",
                                "username must not be blank"
                            ]
                        }
                        """));
    }

    @Test
    void updatePokemon_ShouldReturnBadRequest_WhenIdIsTooShort()
            throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.put("/api/collection/abc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nickname": "Venus"
                                }
                                """))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json("""
                        [
                            "size must be 36 digits long"
                        ]
                        """));
    }

    @Test
    void updatePokemon_ShouldReturnBadRequest_WhenIdIsBlankANdTooShort()
            throws Exception {
        //mockMvc mach daraus blank id. Mit einfachen %20 wird das nicht erkant
        //mockMvc.perform(MockMvcRequestBuilders.put("/api/collection/%20")
        mockMvc.perform(MockMvcRequestBuilders.put(
                                URI.create("/api/collection/%20"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nickname": "Venus"
                                }
                                """))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().json("""
                        [
                            "name must not be blank",
                            "size must be 36 digits long"
                        ]
                        """));
    }
}

