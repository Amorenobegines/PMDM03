package com.example.PMDM03_Alicia.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.PMDM03_Alicia.R;
import com.example.PMDM03_Alicia.models.Pokemon;

/**
 * Adaptador para el RecyclerView que muestra la lista de Pokémon en la Pokedex.
 * Permite mostrar la imagen, el nombre y el número de cada Pokémon.
 * También permite indicar si un Pokémon ha sido capturado o no.
 * Extiende de {@link RecyclerView.Adapter}.
 */
public class ListaPokedexAdapter extends RecyclerView.Adapter<ListaPokedexAdapter.ViewHolder> {

    /**
     * Lista de Pokémon que se mostrarán en el RecyclerView.
     */
    private List<Pokemon> dataset;

    /**
     * Contexto de la aplicación.
     */
    private Context context;

    /**
     * Listener para los clics en los elementos del RecyclerView.
     */
    private OnPokemonClickListener listener;

    /**
     * Mapa para almacenar el estado de captura de los Pokémon.
     * La clave es el nombre del Pokémon en minúsculas y el valor es un booleano que indica si está capturado o no.
     */
    private HashMap<String, Boolean> capturadosMap;

    /**
     * Interfaz para los clics en los elementos del RecyclerView.
     */
    public interface OnPokemonClickListener {
        /**
         * Método llamado cuando se hace clic en un Pokémon.
         *
         * @param pokemon El Pokémon en el que se ha hecho clic.
         */
        void onPokemonClick(Pokemon pokemon);
    }

    /**
     * Constructor del adaptador.
     *
     * @param context El contexto de la aplicación.
     */
    public ListaPokedexAdapter(Context context) {
        this.context = context;
        dataset = new ArrayList<>();
        capturadosMap = new HashMap<>();
    }

    /**
     * Método llamado cuando se necesita crear un nuevo ViewHolder.
     *
     * @param parent   El ViewGroup padre al que se añadirá la nueva vista.
     * @param viewType El tipo de vista de la nueva vista.
     * @return Un nuevo ViewHolder que contiene la vista para el elemento.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pokedex, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Establece el listener para los clics en los elementos del RecyclerView.
     *
     * @param listener El listener para los clics.
     */
    public void setOnPokemonClickListener(OnPokemonClickListener listener) {
        this.listener = listener;
    }

    /**
     * Método llamado para mostrar los datos en el ViewHolder en la posición especificada.
     *
     * @param holder   El ViewHolder que debe ser actualizado para representar el contenido del elemento en la posición dada.
     * @param position La posición del elemento dentro del conjunto de datos del adaptador.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Pokemon p = dataset.get(position);
        holder.nombreTextView.setText(p.getName());
        holder.numeroTextView.setText(String.valueOf(p.getNumber()));

        // Usar el contexto de la vista
        Context context = holder.itemView.getContext();

        Glide.with(context)
                .load("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + p.getNumber() + ".png")
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.fotoImageView);

        // Comprobar si el Pokémon está capturado usando el HashMap
        if (capturadosMap.containsKey(p.getName().toLowerCase())) {
            boolean capturado = capturadosMap.get(p.getName().toLowerCase());
            if (capturado) {
                // Si está capturado, deshabilitar la vista
                holder.itemView.setAlpha(0.5f); // Hacerlo más transparente
                holder.itemView.setEnabled(false); // Deshabilitar el click
            } else {
                // Si no está capturado, habilitar la vista
                holder.itemView.setAlpha(1.0f); // Hacerlo completamente opaco
                holder.itemView.setEnabled(true); // Habilitar el click
            }
        } else {
            // Si no esta en el mapa, habilitar la vista
            holder.itemView.setAlpha(1.0f); // Hacerlo completamente opaco
            holder.itemView.setEnabled(true); // Habilitar el click
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPokemonClick(p);
            }
        });
    }

    /**
     * Obtiene el número total de elementos en el conjunto de datos que mantiene el adaptador.
     *
     * @return El número total de elementos en este adaptador.
     */
    @Override
    public int getItemCount() {
        return dataset.size();
    }

    /**
     * Añade una lista de Pokémon al conjunto de datos del adaptador.
     *
     * @param listaPokemon La lista de Pokémon a añadir.
     */
    public void adicionarListaPokemon(ArrayList<Pokemon> listaPokemon) {
        dataset.addAll(listaPokemon);
        notifyDataSetChanged();
    }

    /**
     * Establece el mapa de Pokémon capturados.
     *
     * @param capturadosMap El mapa de Pokémon capturados.
     */
    public void setCapturadosMap(HashMap<String, Boolean> capturadosMap) {
        this.capturadosMap = capturadosMap;
        notifyDataSetChanged();
    }

    /**
     * Clase ViewHolder que describe la vista de un elemento y su metadatos sobre su posición dentro del RecyclerView.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        /**
         * ImageView para mostrar la foto del Pokémon.
         */
        private ImageView fotoImageView;

        /**
         * TextView para mostrar el nombre del Pokémon.
         */
        private TextView nombreTextView;

        /**
         * TextView para mostrar el número del Pokémon.
         */
        private TextView numeroTextView;

        /**
         * Constructor del ViewHolder.
         *
         * @param itemView La vista del elemento.
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fotoImageView = itemView.findViewById(R.id.fotoImageView);
            nombreTextView = itemView.findViewById(R.id.nombreTextView);
            numeroTextView = itemView.findViewById(R.id.numeroTextView);
        }
    }
}


