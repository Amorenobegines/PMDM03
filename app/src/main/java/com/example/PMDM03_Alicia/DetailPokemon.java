package com.example.PMDM03_Alicia;

import static android.content.Intent.getIntent;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.PMDM03_Alicia.adapter.ListaPokedexAdapter;
import com.example.PMDM03_Alicia.fragments.MisPokemon;
import com.example.PMDM03_Alicia.fragments.Pokedex;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.PMDM03_Alicia.databinding.DetailPokemonBinding;
import com.example.PMDM03_Alicia.models.Pokemon;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Actividad que muestra los detalles de un Pokémon.
 * Permite visualizar la imagen, nombre, ID, tipo, peso y altura del Pokémon.
 * También ofrece la opción de eliminar el Pokémon de la base de datos Firestore.
 */
public class DetailPokemon extends AppCompatActivity {

    /**
     * ImageView para mostrar la imagen del Pokémon.
     */
    private ImageView pokemonImageView;

    /**
     * TextView para mostrar el nombre del Pokémon.
     */
    private TextView pokemonNameTextView;

    /**
     * TextView para mostrar el ID del Pokémon.
     */
    private TextView pokemonIdTextView;

    /**
     * TextView para mostrar el tipo del Pokémon.
     */
    private TextView pokemonTypeTextView;

    /**
     * TextView para mostrar el peso del Pokémon.
     */
    private TextView pokemonWeightTextView;

    /**
     * TextView para mostrar la altura del Pokémon.
     */
    private TextView pokemonHeightTextView;

    /**
     * Botón para eliminar el Pokémon.
     */
    private Button btnEliminarPokemon;

    /**
     * Objeto Pokemon que contiene los datos del Pokémon a mostrar.
     */
    private Pokemon pokemon;

    /**
     * Instancia de FirebaseFirestore para interactuar con la base de datos.
     */
    private FirebaseFirestore db;

    /**
     * Método llamado al crear la actividad.
     * Inicializa la interfaz de usuario, obtiene los datos del Pokémon de la actividad anterior,
     * configura el botón de eliminar y muestra los detalles del Pokémon.
     *
     * @param savedInstanceState Si la actividad se está reinicializando después de ser cerrada,
     *                           este Bundle contiene los datos que más recientemente suministró en onSaveInstanceState.
     *                           <b><i>Nota: De lo contrario es null.</i></b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_pokemon);

        // Obtiene la barra de acción
        actionBar();

        // Inicializar las vistas
        pokemonImageView = findViewById(R.id.detalle_imageview);
        pokemonNameTextView = findViewById(R.id.detalle_name);
        pokemonIdTextView = findViewById(R.id.detalle_id);
        pokemonTypeTextView = findViewById(R.id.detalle_type);
        pokemonWeightTextView = findViewById(R.id.detalle_peso);
        pokemonHeightTextView = findViewById(R.id.detalle_altura);
        btnEliminarPokemon = findViewById(R.id.btn_eliminar_pokemon);

        // Inicializar Firebase
        db = FirebaseFirestore.getInstance();

        // Obtener los datos del Pokémon de la actividad anterior
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("Pokemon")) {
            pokemon = (Pokemon) intent.getSerializableExtra("Pokemon");
            if (pokemon != null) {
                mostrarDetallesPokemon(pokemon);
            } else {
                Toast.makeText(this, "Error: Datos del Pokémon no encontrados", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Error: Intento inválido", Toast.LENGTH_SHORT).show();
        }
        btnEliminarPokemon.setOnClickListener(v -> confirmarEliminarPokemon());
    }

    /**
     * Muestra los detalles del Pokémon en la interfaz de usuario.
     * Realiza una solicitud a la API de PokeAPI para obtener el peso, la altura y los tipos del Pokémon.
     *
     * @param pokemon Objeto Pokemon con los datos del Pokémon a mostrar.
     */
    private void mostrarDetallesPokemon(Pokemon pokemon) {
        if (pokemon != null) {
            pokemonNameTextView.setText(pokemon.getName());
            pokemonIdTextView.setText("#" + String.valueOf(pokemon.getNumber()));
            Glide.with(this)
                    .load("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + pokemon.getNumber() + ".png")
                    .into(pokemonImageView);
            // Realizar la solicitud a la API para obtener la altura, el peso y los tipos
            String url = "https://pokeapi.co/api/v2/pokemon/" + pokemon.getNumber();
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                    response -> {
                        try {
                            int weight = response.getInt("weight");
                            int height = response.getInt("height");
                            pokemon.setWeight(weight);
                            pokemon.setHeight(height);
                            pokemonWeightTextView.setText(getString(R.string.weight) + ": " + String.valueOf(pokemon.getWeight()));
                            pokemonHeightTextView.setText(getString(R.string.height) + ": " + String.valueOf(pokemon.getHeight()));
                            // Obtener los tipos
                            JSONArray typesArray = response.getJSONArray("types");
                            List<String> types = new ArrayList<>();
                            for (int i = 0; i < typesArray.length(); i++) {
                                JSONObject typeObject = typesArray.getJSONObject(i);
                                JSONObject type = typeObject.getJSONObject("type");
                                String typeName = type.getString("name");
                                types.add(typeName);
                            }
                            pokemon.setTypes(types);
                            // Mostrar los tipos
                            StringBuilder typesText = new StringBuilder();
                            for (String type : pokemon.getTypes()) {
                                typesText.append(type).append(", ");
                            }
                            if (typesText.length() > 0) {
                                typesText.delete(typesText.length() - 2, typesText.length()); // Eliminar la última coma y espacio
                            }
                            pokemonTypeTextView.setText(getString(R.string.tipo) + ": " + typesText.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }, error -> {
                // Manejar el error
            });
            // Agregar la solicitud a la cola de solicitudes
            Volley.newRequestQueue(this).add(request);
        }
    }

    /**
     * Método llamado cuando se pulsa el botón de retroceso en la barra de acción.
     * Finaliza la actividad actual.
     *
     * @return true si el evento se ha consumido, false si no.
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    /**
     * Muestra un diálogo de confirmación para eliminar el Pokémon.
     * Si el usuario confirma, llama a {@link #eliminarPokemon()}.
     */
    private void confirmarEliminarPokemon() {
        if (pokemon != null) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.eliminarPokemon)
                    .setMessage(R.string.mens_toast_eliminar)
                    .setIcon(R.drawable.eliminar_pokemon)
                    .setPositiveButton("Sí", (dialog, which) -> eliminarPokemon())
                    .setNegativeButton("No", null)
                    .show();
        } else {
            Toast.makeText(this, "Error: Pokémon no encontrado", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Elimina el Pokémon de la base de datos Firestore y regresa a la actividad anterior.
     */
    private void eliminarPokemon() {
        if (pokemon != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            // Eliminar el Pokémon de Firestore
            db.collection("capturados")
                    .document(pokemon.getName().toLowerCase())
                    .delete();
            Toast.makeText(this, "Pokemon eliminado " + pokemon.getName(), Toast.LENGTH_SHORT).show();
            onSupportNavigateUp(); // Regresar al menú MisPokemon
        } else {
            Toast.makeText(this, "Error: Pokémon no encontrado", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Configura la barra de acción con el título, el icono de retroceso y el color de fondo.
     */
    private void actionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.details);    // Cambia el título de la barra de acción
        actionBar.setHomeAsUpIndicator(R.drawable.back); // Cambia el icono de retroceso
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.pokemon_detail));  // Cambia el color de la barra de acción
        actionBar.setDisplayHomeAsUpEnabled(true); // Habilita el botón de retroceso
        actionBar.setDisplayShowHomeEnabled(true);  // Muestra el icono en la barra de acción
    }

}




