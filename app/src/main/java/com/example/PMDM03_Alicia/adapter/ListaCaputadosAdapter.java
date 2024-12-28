package com.example.PMDM03_Alicia.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.PMDM03_Alicia.R;
import com.example.PMDM03_Alicia.models.Pokemon;

import java.util.ArrayList;
import java.util.List;

/**
 * Adaptador para el RecyclerView que muestra la lista de Pokémon capturados por el usuario.
 * Permite mostrar la imagen, el nombre y el número de cada Pokémon capturado.
 * Todos los Pokémon en esta lista son clicables.
 * Extiende de {@link RecyclerView.Adapter}.
 */
public class ListaCaputadosAdapter extends RecyclerView.Adapter<ListaCaputadosAdapter.ViewHolder> {

    /**
     * Lista de Pokémon capturados que se mostrarán en el RecyclerView.
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
    public ListaCaputadosAdapter(Context context) {
        this.context = context;
        dataset = new ArrayList<>();
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
        Glide.with(context)
                .load("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + p.getNumber() + ".png")
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.fotoImageView);

        // Todos los Pokémon en MisPokemon son clicables
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

