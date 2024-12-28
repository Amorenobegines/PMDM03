package com.example.PMDM03_Alicia.adapter;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Adaptador para el ViewPager que permite mostrar una lista de Fragmentos con sus respectivos títulos.
 * Extiende de {@link FragmentPagerAdapter}.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {

    /**
     * Lista de Fragmentos que se mostrarán en el ViewPager.
     */
    private final List<Fragment> fragmentList = new ArrayList<>();

    /**
     * Lista de títulos para cada Fragmento.
     */
    private final List<String> fragmentTitleList = new ArrayList<>();

    /**
     * Constructor del adaptador.
     *
     * @param manager El FragmentManager para interactuar con los fragmentos.
     */
    public ViewPagerAdapter(FragmentManager manager) {
        super(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    /**
     * Obtiene el Fragmento en la posición especificada.
     *
     * @param position La posición del Fragmento a obtener.
     * @return El Fragmento en la posición especificada.
     */
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    /**
     * Obtiene el número total de Fragmentos en el adaptador.
     *
     * @return El número total de Fragmentos.
     */
    @Override
    public int getCount() {
        return fragmentList.size();
    }

    /**
     * Añade un Fragmento y su título a la lista.
     *
     * @param fragment El Fragmento a añadir.
     * @param title    El título del Fragmento.
     */
    public void addFragment(Fragment fragment, String title) {
        fragmentList.add(fragment); // Agrega el fragmento
        fragmentTitleList.add(title);
    }

    /**
     * Obtiene el título del Fragmento en la posición especificada.
     *
     * @param position La posición del Fragmento del que se quiere obtener el título.
     * @return El título del Fragmento en la posición especificada.
     */
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentTitleList.get(position);
    }
}