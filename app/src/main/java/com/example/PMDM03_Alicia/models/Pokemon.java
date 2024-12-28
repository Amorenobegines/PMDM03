package com.example.PMDM03_Alicia.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Clase que representa un Pokémon.
 * Implementa la interfaz {@link Serializable} para poder ser pasado entre actividades.
 */
public class Pokemon implements Serializable {

    /**
     * Número del Pokémon en la Pokédex.
     */
    private int number;

    /**
     * Nombre del Pokémon.
     */
    private String name;

    /**
     * URL de la API que contiene la información del Pokémon.
     */
    private String url;

    /**
     * ID del Pokémon (índice).
     */
    private int id;

    /**
     * Lista de tipos del Pokémon.
     */
    private List<String> types;

    /**
     * Altura del Pokémon en decímetros.
     */
    private int height;

    /**
     * Peso del Pokémon en hectogramos.
     */
    private int weight;

    /**
     * Indica si el Pokémon ha sido capturado o no.
     */
    private boolean capturado;

    /**
     * Constructor por defecto de la clase Pokemon.
     * Inicializa el atributo capturado a false.
     */
    public Pokemon() {
        this.capturado = false;
    }

    /**
     * Constructor de la clase Pokemon.
     *
     * @param name   Nombre del Pokémon.
     * @param number Número del Pokémon en la Pokédex.
     * @param url    URL de la API que contiene la información del Pokémon.
     * @param types  Lista de tipos del Pokémon.
     * @param weight Peso del Pokémon en hectogramos.
     * @param height Altura del Pokémon en decímetros.
     */
    public Pokemon(String name, int number, String url, ArrayList<String> types, int weight, int height) {
        this.name = name;
        this.number = number;
        this.url = url;
        this.types = types;
        this.weight = weight;
        this.height = height;
    }

    /**
     * Obtiene el nombre del Pokémon.
     *
     * @return El nombre del Pokémon.
     */
    public String getName() {
        return name;
    }

    /**
     * Establece el nombre del Pokémon.
     *
     * @param name El nombre del Pokémon.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Obtiene la URL de la API que contiene la información del Pokémon.
     *
     * @return La URL de la API.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Establece la URL de la API que contiene la información del Pokémon.
     *
     * @param url La URL de la API.
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Obtiene el número del Pokémon en la Pokédex.
     * Extrae el número de la URL si no está establecido.
     *
     * @return El número del Pokémon.
     */
    public int getNumber() {
        if(number == 0){
            String[] urlParts = url.split("/");     // Dividimos la URL en partes
            number = Integer.parseInt(urlParts[urlParts.length - 1]);   // Extraemos el número de la URL
        }
        return number;
    }

    /**
     * Establece el número del Pokémon en la Pokédex.
     *
     * @param number El número del Pokémon.
     */
    public void setNumber(int number) {
        this.number = number;
    }

    /**
     * Obtiene el ID del Pokémon.
     *
     * @return El ID del Pokémon.
     */
    public int getId() {
        return id;
    }

    /**
     * Establece el ID del Pokémon.
     *
     * @param id El ID del Pokémon.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Obtiene la altura del Pokémon.
     *
     * @return La altura del Pokémon en decímetros.
     */
    public double getHeight() {
        return height;
    }

    /**
     * Establece la altura del Pokémon.
     *
     * @param height La altura del Pokémon en decímetros.
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * Obtiene el peso del Pokémon.
     *
     * @return El peso del Pokémon en hectogramos.
     */
    public double getWeight() {
        return weight;
    }

    /**
     * Establece el peso del Pokémon.
     *
     * @param weight El peso del Pokémon en hectogramos.
     */
    public void setWeight(int weight) {
        this.weight = weight;
    }

    /**
     * Obtiene la lista de tipos del Pokémon.
     *
     * @return La lista de tipos del Pokémon.
     */
    public List<String> getTypes() {
        return types;
    }

    /**
     * Establece la lista de tipos del Pokémon.
     *
     * @param types La lista de tipos del Pokémon.
     */
    public void setTypes(List<String> types) {
        this.types = types;
    }

    /**
     * Añade un tipo a la lista de tipos del Pokémon.
     *
     * @param type El tipo a añadir.
     */
    public void addType(String type) {
        if(this.types == null){
            this.types = new ArrayList<>();
        }
        this.types.add(type);
    }

    /**
     * Indica si el Pokémon ha sido capturado.
     *
     * @return true si el Pokémon ha sido capturado, false en caso contrario.
     */
    public boolean isCapturado() {
        return capturado;
    }

    /**
     * Establece si el Pokémon ha sido capturado.
     *
     * @param capturado true si el Pokémon ha sido capturado, false en caso contrario.
     */
    public void setCapturado(boolean capturado) {
        this.capturado = capturado;
    }
}
