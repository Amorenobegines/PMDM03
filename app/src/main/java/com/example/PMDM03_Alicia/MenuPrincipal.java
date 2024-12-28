package com.example.PMDM03_Alicia;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import com.example.PMDM03_Alicia.adapter.ViewPagerAdapter;
import com.example.PMDM03_Alicia.fragments.MisPokemon;
import com.example.PMDM03_Alicia.fragments.Pokedex;
import com.example.PMDM03_Alicia.fragments.Ajustes;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;


import java.util.Locale;

public class MenuPrincipal extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_menu_principal);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_mis_pokemon);
        bottomNav.setOnItemSelectedListener(navListener);

        Fragment selectedFragment = new MisPokemon();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();

        // Ocultar la ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

    }

    private NavigationBarView.OnItemSelectedListener navListener = item -> {
        int itemId = item.getItemId();

        Fragment selectedFragment = null;

        if (itemId == R.id.nav_mis_pokemon) {
            selectedFragment = new MisPokemon();
        } else if (itemId == R.id.nav_pokedex) {
            selectedFragment = new Pokedex();
        } else if (itemId == R.id.nav_ajustes) {
            selectedFragment = new Ajustes();
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();

        return true;
    };

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new MisPokemon(), "Capturados");
        adapter.addFragment(new Pokedex(), "Pokédex");
        adapter.addFragment(new Ajustes(), "Ajustes");
        viewPager.setAdapter(adapter);
    }

    // Método para cargar el idioma guardado
    public void loadLocale() {
        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        String language = prefs.getString("My_Lang", "es");
        setLocale(language);
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }



}