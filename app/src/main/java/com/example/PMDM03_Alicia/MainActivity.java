package com.example.PMDM03_Alicia;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.PMDM03_Alicia.Login.Login;
import com.example.PMDM03_Alicia.Login.Registro;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

/**
 * Clase principal de la aplicación (MainActivity).
 * Esta actividad sirve como punto de entrada a la aplicación,
 * ofreciendo al usuario la opción de iniciar sesión o registrarse.
 * Si el usuario ya está autenticado, se le redirige directamente a {@link MenuPrincipal}.
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Botón para iniciar la actividad de inicio de sesión ({@link Login}).
     */
    private Button Btn_Login;

    /**
     * Botón para iniciar la actividad de registro ({@link Registro}).
     */
    private Button Btn_Registro;

    /**
     * Instancia de FirebaseAuth para gestionar la autenticación de usuarios.
     */
    private FirebaseAuth mAuth;

    /**
     * Cliente de Google Sign-In (no se utiliza directamente en esta clase, pero se declara por si se necesita en el futuro).
     */
    private GoogleSignInClient googleSignInClient;

    /**
     * Etiqueta para los mensajes de log.
     */
    private static final String TAG = "MainActivity";

    /**
     * Código de solicitud para el inicio de sesión (no se utiliza directamente en esta clase, pero se declara por si se necesita en el futuro).
     */
    private static final int RC_SIGN_IN = 9001;

    /**
     * Método llamado al crear la actividad.
     * Inicializa la interfaz de usuario, configura los listeners de clic para los botones
     * y comprueba si el usuario ya está autenticado.
     *
     * @param savedInstanceState Si la actividad se está reinicializando después de ser cerrada,
     *                           este Bundle contiene los datos que más recientemente suministró en onSaveInstanceState.
     *                           <b><i>Nota: De lo contrario es null.</i></b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate() llamado");

        setContentView(R.layout.activity_main);

        Btn_Login = findViewById(R.id.Btn_Login);
        Btn_Registro = findViewById(R.id.Btn_Registro);

        mAuth = FirebaseAuth.getInstance();

        // Comprobar si el usuario ya está autenticado
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Si el usuario está autenticado, redirigir a MenuPrincipal
            Log.d(TAG, "Usuario ya autenticado. Redirigiendo a MenuPrincipal.");
            startActivity(new Intent(this, MenuPrincipal.class));
            finish(); // Finalizar MainActivity para que el usuario no pueda volver atrás
        }

        // Configurar los listeners de clic para los botones
        Btn_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Iniciar la actividad de inicio de sesión
                Log.d(TAG, "Clic en boton Login");
                startActivity(new Intent(MainActivity.this, Login.class));
            }
        });

        Btn_Registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Iniciar la actividad de registro
                Log.d(TAG, "Clic en boton Registro");
                startActivity(new Intent(MainActivity.this, Registro.class));
            }
        });
    }
}