package com.example.PMDM03_Alicia.fragments;


import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.PMDM03_Alicia.adapter.ListaPokedexAdapter;
import com.example.PMDM03_Alicia.R;
import com.example.PMDM03_Alicia.models.Pokemon;
import com.example.PMDM03_Alicia.models.PokemonRespuesta;
import com.example.PMDM03_Alicia.pokeapi.PokeapiService;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Fragmento que muestra una lista de Pokémon obtenidos de la PokeAPI.
 * Permite al usuario capturar Pokémon y guardarlos en Firestore.
 * Utiliza Retrofit para realizar las peticiones a la API y RecyclerView para mostrar la lista.
 */
public class Pokedex extends Fragment {

    /**
     * Instancia de Retrofit para realizar las peticiones a la API.
     */
    private Retrofit retrofit;

    /**
     * Etiqueta para los mensajes de log.
     */
    private static final String TAG = "POKEDEX";

    /**
     * RecyclerView para mostrar la lista de Pokémon.
     */
    private RecyclerView recyclerView;

    /**
     * Adaptador para el RecyclerView.
     */
    private ListaPokedexAdapter listaPokedexAdapter;

    /**
     * Offset para la paginación de la lista de Pokémon.
     */
    private int offset;

    /**
     * Indica si es posible cargar más Pokémon.
     */
    private boolean aptoParaCargar;

    /**
     * Instancia de FirebaseFirestore para interactuar con la base de datos.
     */
    private FirebaseFirestore db;

    /**
     * Lista de Pokémon.
     */
    private List<Pokemon> listaPokemon;

    /**
     * Mapa para almacenar el estado de captura de los Pokémon.
     */
    private HashMap<String, Boolean> capturadosMap;

    /**
     * Método llamado para crear la vista del fragmento.
     *
     * @param inflater           El LayoutInflater que se puede utilizar para inflar cualquier vista en el fragmento.
     * @param container          Si no es null, esta es la vista padre a la que se debe adjuntar la interfaz de usuario del fragmento.
     * @param savedInstanceState Si no es null, este fragmento se está reconstruyendo a partir de un estado guardado previamente.
     * @return Devuelve la vista del Fragmento.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.pokedex, container, false);
    }

    /**
     * Método llamado después de que la vista del fragmento ha sido creada.
     * Inicializa el RecyclerView, el adaptador, Retrofit, Firestore y carga los primeros Pokémon.
     *
     * @param view               La vista del fragmento.
     * @param savedInstanceState Si no es null, este fragmento se está reconstruyendo a partir de un estado guardado previamente.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerView);
        listaPokedexAdapter = new ListaPokedexAdapter(requireContext());

        // Configura el listener para capturar los clics en los elementos del RecyclerView
        listaPokedexAdapter.setOnPokemonClickListener(pokemon -> onPokemonClick(pokemon));

        recyclerView.setAdapter(listaPokedexAdapter);   // Asigna el adaptador al RecyclerView
        recyclerView.setHasFixedSize(true);             // Optimiza el rendimiento
        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), 3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int pastVisibleItems = layoutManager.findFirstVisibleItemPosition();
                    if (aptoParaCargar) {
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            Log.i(TAG, "Llegamos al final.");
                            aptoParaCargar = false;
                            offset += 20;
                            obtenerDatos(offset);
                        }
                    }
                }
            }
        });

        retrofit = new Retrofit.Builder()
                .baseUrl("https://pokeapi.co/api/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        aptoParaCargar = true;
        offset = 0;
        db = FirebaseFirestore.getInstance();
        //Habilitar el acceso offline
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
        listaPokemon = new ArrayList<>();
        capturadosMap = new HashMap<>();
        obtenerDatos(offset);
    }

    /**
     * Obtiene los datos de los Pokémon de la PokeAPI.
     *
     * @param offset El offset para la paginación.
     */
    private void obtenerDatos(int offset) {

        PokeapiService service = retrofit.create(PokeapiService.class);
        Call<PokemonRespuesta> pokemonRespuestaCall = service.obtenerListaPokemon(offset, 50);
        pokemonRespuestaCall.enqueue(new Callback<PokemonRespuesta>() {
            @Override
            public void onResponse(Call<PokemonRespuesta> call, Response<PokemonRespuesta> response) {
                aptoParaCargar = true;
                if (response.isSuccessful()) {
                    PokemonRespuesta pokemonRespuesta = response.body();
                    ArrayList<Pokemon> listaPokemonAux = pokemonRespuesta.getResults();

                    // Obtener el estado de captura de todos los Pokémon en bloque
                    db.collection("capturados")
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    QuerySnapshot querySnapshot = task.getResult();
                                    if (querySnapshot != null) {
                                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                            capturadosMap.put(document.getId(), true);
                                        }
                                    }
                                    listaPokedexAdapter.setCapturadosMap(capturadosMap);
                                    // Añadir los Pokémon a la lista
                                    listaPokedexAdapter.adicionarListaPokemon(listaPokemonAux);
                                } else {
                                    Log.e(TAG, "Error al obtener el estado de captura: ", task.getException());
                                }
                            });
                } else {
                    Log.e(TAG, "onResponse: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<PokemonRespuesta> call, Throwable t) {
                aptoParaCargar = true;
                Log.e(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    /**
     * Método llamado cuando se hace clic en un Pokémon.
     * Obtiene los datos completos del Pokémon de la API y lo guarda en Firestore.
     *
     * @param pokemon El Pokémon en el que se ha hecho clic.
     */
    private void onPokemonClick(Pokemon pokemon) {

        Toast.makeText(getContext(), getText(R.string.has_elegido) + " " + pokemon.getName(), Toast.LENGTH_SHORT).show();
        // 1. Obtener los datos completos del Pokémon (incluyendo tipos)
        String url = "https://pokeapi.co/api/v2/pokemon/" + pokemon.getNumber();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                // Manejar la respuesta
                response -> {
                    try {
                        // Extraer los tipos correctamente
                        JSONArray typesArray = response.getJSONArray("types");
                        List<String> types = new ArrayList<>();
                        // Recorrer los tipos y agregarlos a la lista
                        for (int i = 0; i < typesArray.length(); i++) {
                            JSONObject typeObject = typesArray.getJSONObject(i);
                            JSONObject type = typeObject.getJSONObject("type");
                            String typeName = type.getString("name");
                            types.add(typeName);
                        }
                        pokemon.setTypes(types);// Asignar los tipos al Pokémon
                        int weight = response.getInt("weight");
                        int height = response.getInt("height");
                        pokemon.setWeight(weight);
                        pokemon.setHeight(height);

                        // 2. Guardar el Pokémon en Firestore (ahora con los tipos correctos)
                        guardarPokemonEnFirestore(pokemon);
                    } catch (JSONException e) {
                        Log.e(TAG, "Error al parsear el JSON: " + e.getMessage());
                        Toast.makeText(getContext(), "Error al obtener información del Pokémon.", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }, error -> {

        });
        Volley.newRequestQueue(getContext()).add(request);
    }

    /**
     * Guarda el Pokémon en Firestore.
     *
     * @param pokemon El Pokémon a guardar.
     */
    private void guardarPokemonEnFirestore(Pokemon pokemon) {
        // Crear un mapa para guardar los datos en Firestore
        Map<String, Object> pokemonMap = new HashMap<>();
        pokemonMap.put("name", pokemon.getName());
        pokemonMap.put("number", pokemon.getNumber());
        pokemonMap.put("url", pokemon.getUrl());
        pokemonMap.put("types", pokemon.getTypes());
        pokemonMap.put("capturado", true); // Marcar como capturado
        pokemonMap.put("weight", pokemon.getWeight());
        pokemonMap.put("height", pokemon.getHeight());

        db.collection("capturados")
                .document(pokemon.getName().toLowerCase())
                .set(pokemonMap) // Usar el mapa en lugar del objeto Pokemon
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), R.string.capturado + " " + pokemon.getName(), Toast.LENGTH_SHORT).show();
                    // Actualizar el estado del Pokémon en la lista
                    capturadosMap.put(pokemon.getName().toLowerCase(), true);
                    listaPokedexAdapter.setCapturadosMap(capturadosMap);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al guardar en Firestore: " + e.getMessage());
                    Toast.makeText(getContext(), "Error al guardar en Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

}



