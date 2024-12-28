package com.example.PMDM03_Alicia;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Actividad de presentación (SplashActivity).
 * Esta actividad se muestra al iniciar la aplicación y tiene como objetivo
 * verificar si el usuario ya está autenticado.
 * Si el usuario está autenticado, se le redirige a {@link MenuPrincipal}.
 * Si no está autenticado, se le redirige a {@link MainActivity}.
 * La actividad se muestra durante 3 segundos antes de realizar la verificación.
 */
public class SplashActivity extends AppCompatActivity {

    /**
     * Instancia de FirebaseAuth para gestionar la autenticación de usuarios.
     */
    private FirebaseAuth firebaseAuth;

    /**
     * Etiqueta para los mensajes de log.
     */
    private static final String TAG = "SplashActivity";

    /**
     * Método llamado al crear la actividad.
     * Inicializa la interfaz de usuario, la instancia de FirebaseAuth y
     * programa la tarea de verificación del usuario para que se ejecute después de 3 segundos.
     *
     * @param savedInstanceState Si la actividad se está reinicializando después de ser cerrada,
     *                           este Bundle contiene los datos que más recientemente suministró en onSaveInstanceState.
     *                           <b><i>Nota: De lo contrario es null.</i></b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Log.d(TAG, "onCreate() llamado");

        firebaseAuth = FirebaseAuth.getInstance();

        // Crea un nuevo Handler para programar una tarea en un hilo separado
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Verificar si el usuario está autenticado antes de lanzar la actividad principal
                verificarUsuario();
            }
        }, 3000); // 3000 milisegundos = 3 segundos
    }

    /**
     * Verifica si el usuario está autenticado.
     * Si el usuario está autenticado, se le redirige a {@link MenuPrincipal}.
     * Si no está autenticado, se le redirige a {@link MainActivity}.
     * Finalmente, finaliza la actividad actual.
     */
    private void verificarUsuario() {
        Log.d(TAG, "verificarUsuario() llamado");
        // Obtener el usuario actual
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            // El usuario no está autenticado, redirigir a la actividad de inicio de sesión
            Log.d(TAG, "Usuario no autenticado");
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
        } else {
            // El usuario está autenticado, redirigir a la actividad principal
            Log.d(TAG, "Usuario autenticado: " + firebaseUser.getUid());
            startActivity(new Intent(SplashActivity.this, MenuPrincipal.class));
        }
        Log.d(TAG, "Finalizando SplashActivity");
        finish();
    }
}