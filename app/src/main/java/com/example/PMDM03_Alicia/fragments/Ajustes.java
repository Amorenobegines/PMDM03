package com.example.PMDM03_Alicia.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.example.PMDM03_Alicia.Login.Login;
import com.example.PMDM03_Alicia.MainActivity;
import com.example.PMDM03_Alicia.MenuPrincipal;
import com.example.PMDM03_Alicia.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Locale;


/**
 * Fragmento que muestra las preferencias de la aplicación.
 * Permite al usuario cambiar el idioma, eliminar todos los Pokémon capturados,
 * ver información sobre la aplicación, acceder a la ayuda y cerrar sesión.
 */
public class Ajustes extends PreferenceFragmentCompat {

    /**
     * Cliente de Google Sign-In para cerrar sesión en Google.
     */
    private GoogleSignInClient mGoogleSignInClient;

    /**
     * SharedPreferences para guardar las preferencias del usuario.
     */
    private SharedPreferences sharedPreferences;

    /**
     * Etiqueta para los mensajes de log.
     */
    private static final String TAG = "Ajustes";

    /**
     * Instancia de FirebaseAuth para la autenticación de usuarios.
     */
    private FirebaseAuth firebaseAuth;

    /**
     * Listener para detectar cambios en el estado de autenticación.
     */
    private FirebaseAuth.AuthStateListener authStateListener;

    /**
     * Método llamado para crear las preferencias del fragmento.
     *
     * @param savedInstanceState Si no es null, este fragmento se está reconstruyendo a partir de un estado guardado previamente.
     * @param rootKey            Si se especifica, solo se deben cargar las preferencias con esta clave raíz.
     */
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        // Configuración de Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);

        // Inicializar SharedPreferences
        sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        // Inicializar Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        // Cambio de idioma
        ListPreference languagePreference = findPreference("idioma");
        if (languagePreference != null) {
            languagePreference.setOnPreferenceChangeListener((preference, newValue) -> {
                new android.app.AlertDialog.Builder(getContext()).setTitle(R.string.cambio_idioma)
                        .setIcon(R.drawable.idioma)
                        .setMessage(R.string.pregunta_idioma)
                        .setPositiveButton("Sí", (dialog, which) -> {
                            setLocale(newValue.toString());
                            startActivity(new Intent(getContext(), MenuPrincipal.class));
                        })
                        .setNegativeButton("No", null)
                        .show();
                return false; // Evitar el cambio automático, manejarlo en el diálogo
            });
        }

        // Eliminar pokemon
        SwitchPreferenceCompat eliminarTodosPokemonPreference = findPreference("eliminar_todos_pokemon");
        if (eliminarTodosPokemonPreference != null) {
            eliminarTodosPokemonPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                boolean eliminarTodosPokemon = (Boolean) newValue;
                if (eliminarTodosPokemon) {
                    new android.app.AlertDialog.Builder(getContext())
                            .setTitle(R.string.eliminarPokemon)
                            .setMessage(R.string.eliminarPokemon)
                            .setIcon(R.drawable.delete)
                            .setPositiveButton("Sí", (dialog, which) -> {
                                eliminarTodosLosPokemon();
                            }).setNegativeButton("No", (dialog, which) -> {
                                eliminarTodosPokemonPreference.setChecked(false);
                            }).show();
                }
                return true;
            });
        }

        // Acerca de
        Preference aboutPreference = findPreference("about");
        if (aboutPreference != null) {
            aboutPreference.setOnPreferenceClickListener(preference -> {
                showAboutDialog();
                return true;
            });
        }

        // Ayuda y preguntas
        Preference helpPreference = findPreference("help");
        if (helpPreference != null) {
            helpPreference.setOnPreferenceClickListener(preference -> {
                showHelpDialog();
                return true;
            });
        }

        // Cerrar sesión
        Preference logoutPreference = findPreference("logout");
        if (logoutPreference != null) {
            logoutPreference.setOnPreferenceClickListener(preference -> {
                new android.app.AlertDialog.Builder(getContext()).setTitle(R.string.CerrarSesion)
                        .setMessage(R.string.pregunta_cerrar_sesion)
                        .setIcon(R.drawable.cerrar_sesion)
                        .setPositiveButton("Si", (dialog, which) -> {
                            FirebaseAuth.getInstance().signOut();   // Cerrar sesión en Firebase
                            signOut();   // Cerrar sesión en Google
                            Toast.makeText(getContext(), R.string.men_cerrar_sesion, Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getContext(), MainActivity.class));
                            getActivity().finish();
                        })
                        .setNegativeButton("No", null)
                        .show();
                return true;
            });
        }

        // Listener de autenticación
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                Log.d(TAG, "onAuthStateChanged() llamado");
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                actualizarPreferencias(firebaseUser);
            }
        };
        firebaseAuth.addAuthStateListener(authStateListener);

    }

    /**
     * Actualiza las preferencias del usuario en función del estado de autenticación.
     *
     * @param firebaseUser El usuario actual de Firebase.
     */
    private void actualizarPreferencias(FirebaseUser firebaseUser) {
        Log.d(TAG, "actualizarPreferencias() llamado");
        // Preferencia "user"
        Preference userPreference = findPreference("user");
        if (userPreference != null) {
            Log.d(TAG, "Preferencia 'user' encontrada");
            if (firebaseUser != null) {
                Log.d(TAG, "Usuario autenticado: " + firebaseUser.getUid());
                String email = null;
                // Buscar el correo electrónico en providerData
                for (UserInfo profile : firebaseUser.getProviderData()) {
                    if (profile.getEmail() != null) {
                        email = profile.getEmail();
                        break; // Encontramos el correo, no necesitamos seguir buscando
                    }
                }
                // Mostrar el correo electrónico
                if (email != null) {
                    userPreference.setSummary(email);
                    Log.d(TAG, "Correo recuperado y mostrado en la preferencia: " + email);
                } else {
                    userPreference.setSummary("Correo: No disponible");
                    Log.d(TAG, "Correo no disponible");
                }
            } else {
                userPreference.setSummary("Usuario no autenticado");
                Log.d(TAG, "Usuario no autenticado");
            }
            // Deshabilitar la preferencia
            userPreference.setEnabled(false);
        } else {
            Log.e(TAG, "Preferencia 'user' NO encontrada");
        }
    }

    /**
     * Cierra la sesión del usuario en Google.
     */
    private void signOut() {
        // Cerrar sesión en Google
        mGoogleSignInClient.signOut().addOnCompleteListener(getActivity(), task -> {
            Toast.makeText(getContext(), R.string.men_cerrar_sesion, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getContext(), Login.class));
            getActivity().finish();
        });
    }

    /**
     * Establece el idioma de la aplicación.
     *
     * @param lang El código del idioma (por ejemplo, "es" para español, "en" para inglés).
     */
    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        // Guardar preferencia de idioma
        SharedPreferences.Editor editor = getActivity().getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("My_Lang", lang);
        editor.apply();

        // Reiniciar actividad para aplicar el cambio de idioma
        getActivity().recreate();
    }

    /**
     * Muestra el diálogo "Acerca de".
     */
    private void showAboutDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.menu_acerca_de)
                .setIcon(R.drawable.ajustes)
                .setMessage(R.string.mensaje_acerca)
                .setPositiveButton("OK", null).show();
    }

    /**
     * Muestra el diálogo de ayuda.
     */
    private void showHelpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Preguntas frecuentes");
        builder.setIcon(R.drawable.help);
        builder.setView(R.layout.dialog_help);
        builder.setPositiveButton("Cerrar", null);
        builder.show();
    }

    /**
     * Elimina todos los Pokémon capturados de Firestore.
     */
    private void eliminarTodosLosPokemon() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("capturados")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            document.getReference().delete();
                        }
                        Toast.makeText(getContext(), R.string.mensaje_eliminados, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), R.string.error_eliminar_pokemon, Toast.LENGTH_SHORT).show();
                    }
                });
    } // Fin eliminar pokemon

    /**
     * Método llamado cuando el fragmento se destruye.
     * Elimina el listener de autenticación.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() llamado");
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

}  // Fin class