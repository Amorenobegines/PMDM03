package com.example.PMDM03_Alicia.models;

import java.util.ArrayList;

/**
 * Clase que representa la respuesta de la API de Pokémon.
 */
public class PokemonRespuesta {

    /**
     * Lista de objetos Pokemon que contiene la respuesta de la API.
     */
    private ArrayList<Pokemon> results;  //Contendrá objetos de la clase Pokemon

    // Getters y setters

    /**
     * Obtiene la lista de objetos Pokemon.
     * @return La lista de objetos Pokemon.
     */
    public ArrayList<Pokemon> getResults() {
        return results;
    }

    /**
     * Establece la lista de objetos Pokemon.
     * @param results La lista de objetos Pokemon a establecer.
     */
    public void setResults(ArrayList<Pokemon> results) {
        this.results = results;
    }
}
