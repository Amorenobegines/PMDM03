package com.example.PMDM03_Alicia.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.PMDM03_Alicia.DetailPokemon;
import com.example.PMDM03_Alicia.R;
import com.example.PMDM03_Alicia.adapter.ListaCaputadosAdapter;
import com.example.PMDM03_Alicia.models.Pokemon;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragmento que muestra la lista de Pokémon capturados por el usuario.
 * Los Pokémon se obtienen de Firestore y se muestran en un RecyclerView.
 * Permite al usuario ver los detalles de un Pokémon capturado al hacer clic en él.
 */
public class MisPokemon extends Fragment {

    /**
     * Etiqueta para los mensajes de log.
     */
    private static final String TAG = "MisPokemon";

    /**
     * RecyclerView para mostrar la lista de Pokémon capturados.
     */
    private RecyclerView recyclerView;

    /**
     * Adaptador para el RecyclerView.
     */
    private ListaCaputadosAdapter listaCaputadosAdapter;

    /**
     * Instancia de FirebaseFirestore para interactuar con la base de datos.
     */
    private FirebaseFirestore db;

    /**
     * Lista de Pokémon capturados.
     */
    private List<Pokemon> listaPokemon;

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
        return inflater.inflate(R.layout.mis_pokemon, container, false);
    }

    /**
     * Método llamado después de que la vista del fragmento ha sido creada.
     * Inicializa el RecyclerView, el adaptador, Firestore y carga los Pokémon capturados.
     *
     * @param view               La vista del fragmento.
     * @param savedInstanceState Si no es null, este fragmento se está reconstruyendo a partir de un estado guardado previamente.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerView_mis_pokemon);
        listaCaputadosAdapter = new ListaCaputadosAdapter(requireContext());

        // Configura el listener para capturar los clics en los elementos del RecyclerView
        listaCaputadosAdapter.setOnPokemonClickListener(pokemon -> onPokemonClick(pokemon));

        recyclerView.setAdapter(listaCaputadosAdapter);   // Asigna el adaptador al RecyclerView
        recyclerView.setHasFixedSize(true);             // Optimiza el rendimiento
        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), 3);
        recyclerView.setLayoutManager(layoutManager);

        // Configura Firestore
        db = FirebaseFirestore.getInstance();
        listaPokemon = new ArrayList<>();
        obtenerDatos();
    }

    /**
     * Obtiene los datos de los Pokémon capturados de Firestore.
     * Los datos se obtienen de la colección "capturados".
     */
    private void obtenerDatos() {
        db.collection("capturados").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {   // Si la tarea se ha completado correctamente
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null) {// Si la consulta tiene resultados
                    listaPokemon.clear();   // Limpia la lista
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) { // Recorre los documentos
                        Pokemon pokemon = new Pokemon();
                        pokemon.setName(document.getString("name"));
                        pokemon.setNumber(document.getLong("number").intValue());
                        pokemon.setUrl(document.getString("url"));
                        List<String> types = (List<String>) document.get("types");
                        pokemon.setTypes(types);
                        pokemon.setWeight(document.getLong("weight").intValue());
                        pokemon.setHeight(document.getLong("height").intValue());
                        listaPokemon.add(pokemon);
                    }
                    listaCaputadosAdapter.adicionarListaPokemon(new ArrayList<>(listaPokemon));
                }
            } else {
                Log.e(TAG, "Error al obtener los Pokémon capturados: ", task.getException());
            }
        });
    }

    /**
     * Método llamado cuando se hace clic en un Pokémon.
     * Inicia la actividad {@link DetailPokemon} para mostrar los detalles del Pokémon.
     *
     * @param pokemon El Pokémon en el que se ha hecho clic.
     */
    private void onPokemonClick(Pokemon pokemon) {
        Intent intent = new Intent(getContext(), DetailPokemon.class);
        intent.putExtra("Pokemon", pokemon);
        startActivity(intent);
        // Aquí puedes implementar la lógica para cuando se hace clic en un Pokémon de MisPokemon
        Log.d(TAG, "Se ha hecho clic en el Pokémon: " + pokemon.getName());
    }
}
