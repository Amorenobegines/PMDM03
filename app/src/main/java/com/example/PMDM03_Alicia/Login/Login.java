package com.example.PMDM03_Alicia.Login;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.PMDM03_Alicia.MenuPrincipal;
import com.example.PMDM03_Alicia.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

/**
 * Clase que gestiona la pantalla de inicio de sesión de la aplicación.
 * Permite a los usuarios iniciar sesión con correo electrónico y contraseña,
 * o con su cuenta de Google.
 * Implementa {@link GoogleApiClient.OnConnectionFailedListener} para manejar
 * posibles fallos en la conexión con los servicios de Google.
 */
public class Login extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient googleApiClient;
    private SignInButton signInButton;
    /**
     * Código de solicitud para el inicio de sesión con Google.
     */
    public static final int SIGN_IN_CODE = 100;
    private static final String TAG = "Login";

    private EditText CorreoLogin, PassLogin;
    private Button Btn_Login;
    private TextView usuarioNuevoTxt;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;

    //Validar los datos
    private String correo = "", password = "";

    /**
     * Método llamado al crear la actividad.
     * Inicializa la interfaz de usuario, configura la barra de acción,
     * establece los listeners para los botones y configura el inicio de sesión con Google.
     *
     * @param savedInstanceState Si la actividad se está reinicializando después de ser cerrada,
     *                           este Bundle contiene los datos que más recientemente suministró en onSaveInstanceState.
     *                           <b><i>Nota: De lo contrario es null.</i></b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActionBar actionBar = getSupportActionBar();    // Obtiene la barra de acción
        actionBar.setTitle("Login");    // Cambia el título de la barra de acción
        actionBar.setDisplayHomeAsUpEnabled(true); // Habilita el botón de retroceso
        actionBar.setDisplayShowHomeEnabled(true);  // Muestra el icono en la barra de acción

        CorreoLogin = findViewById(R.id.CorreoLogin);
        PassLogin = findViewById(R.id.PassLogin);
        Btn_Login = findViewById(R.id.Btn_Login);
        usuarioNuevoTxt = findViewById(R.id.usuarioNuevoTxt);

        firebaseAuth = FirebaseAuth.getInstance();      // Instancia de Firebase Authentication
        progressDialog = new ProgressDialog(this);       // Instancia de ProgressDialog
        progressDialog.setTitle(getString(R.string.EsperePorFavorTxt));
        progressDialog.setCanceledOnTouchOutside(false);

        Btn_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Login", "Clic en boton Login");
                validarDatos();
            }
        });

        usuarioNuevoTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Registro.class));
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        signInButton = findViewById(R.id.signInButton);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent, SIGN_IN_CODE);
            }
        });

    }

    /**
     * Método llamado cuando falla la conexión con los servicios de Google.
     *
     * @param connectionResult El resultado de la conexión fallida.
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //TODO: Implementar el manejo de errores de conexion con google
    }

    /**
     * Método llamado cuando se recibe el resultado de una actividad que se ha iniciado.
     * En este caso, se utiliza para manejar el resultado del inicio de sesión con Google.
     *
     * @param requestCode El código de solicitud que se pasó a startActivityForResult().
     * @param resultCode  El código de resultado devuelto por la actividad hija.
     * @param data        Un Intent, que puede devolver datos de resultado a la persona que llama.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGN_IN_CODE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignResult(result);
        }
    }

    /**
     * Maneja el resultado del inicio de sesión con Google.
     * Si el inicio de sesión es exitoso, llama a {@link #goMainScreen()}.
     * Si falla, muestra un mensaje de error.
     *
     * @param result El resultado del inicio de sesión con Google.
     */
    private void handleSignResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            goMainScreen();
            Toast.makeText(this, R.string.BienvenidoTxt, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.default_web_client_id, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Inicia la actividad {@link MenuPrincipal} y finaliza la actividad actual.
     */
    private void goMainScreen() {
        Intent intent = new Intent(this, MenuPrincipal.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * Valida los datos introducidos por el usuario (correo electrónico y contraseña).
     * Si los datos son válidos, llama a {@link #loginDeUsuario()}.
     */
    private void validarDatos() {
        correo = CorreoLogin.getText().toString().trim();
        password = PassLogin.getText().toString().trim();

        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            Toast.makeText(this, R.string.CorreoInvalido, Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, R.string.ingreseContraseña, Toast.LENGTH_SHORT).show();
        } else {
            loginDeUsuario();
        }

    }

    /**
     * Realiza el inicio de sesión del usuario con correo electrónico y contraseña.
     * Muestra un diálogo de progreso mientras se realiza el inicio de sesión.
     * Si el inicio de sesión es exitoso, inicia la actividad {@link MenuPrincipal}.
     * Si falla, muestra un mensaje de error.
     */
    private void loginDeUsuario() {
        progressDialog.setMessage("Iniciando sesión");
        progressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(correo, password)
                .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {  // Inicio de sesión exitoso
                            progressDialog.dismiss();// Ocultar el diálogo de progreso
                            FirebaseUser user = firebaseAuth.getCurrentUser();// Obtener el usuario actual
                            startActivity(new Intent(Login.this, MenuPrincipal.class));// Iniciar MenuPrincipal
                            Toast.makeText(Login.this, getString(R.string.BienvenidoTxt) + ": " + user.getEmail(), Toast.LENGTH_SHORT).show();// Mostrar mensaje de bienvenida
                            Log.d("Login", "Inicio de sesión exitoso" + task.getException());
                            finish();// Finalizar LoginActivity para que el usuario no pueda volver atrás
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(Login.this, R.string.ErrorLogin, Toast.LENGTH_SHORT).show();
                            Log.d("Login", "Error en el inicio de sesión: " + task.getException());
                        }
                    }

                }).

                addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Login.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
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


}