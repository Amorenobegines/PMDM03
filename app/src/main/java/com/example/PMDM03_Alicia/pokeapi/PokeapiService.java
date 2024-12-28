package com.example.PMDM03_Alicia.pokeapi;

import com.example.PMDM03_Alicia.models.Pokemon;
import com.example.PMDM03_Alicia.models.PokemonRespuesta;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;


/**
 * Interfaz que define los métodos para acceder a la API de Pokémon.
 */
public interface PokeapiService {

    // Acceder al listado de los pokemon
     @GET("pokemon")
    Call<PokemonRespuesta> obtenerListaPokemon(@Query("offset") int offset, @Query("limit") int limit);

    // Obtiene información de un Pokémon específico por su nombre
    @GET("pokemon/{name}")
    Call<Pokemon> getPokemon(@Path("name") String name);

}
