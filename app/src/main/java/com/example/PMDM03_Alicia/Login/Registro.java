package com.example.PMDM03_Alicia.Login;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

import com.example.PMDM03_Alicia.MainActivity;
import com.example.PMDM03_Alicia.MenuPrincipal;
import com.example.PMDM03_Alicia.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

/**
 * Actividad para el registro de nuevos usuarios.
 * Permite a los usuarios crear una cuenta proporcionando su nombre, correo electrónico y contraseña.
 * Utiliza Firebase Authentication para la creación de la cuenta y Firebase Realtime Database para almacenar la información del usuario.
 */
public class Registro extends AppCompatActivity {

    /**
     * EditText para el nombre del usuario.
     */
    EditText NombreEt;

    /**
     * EditText para el correo electrónico del usuario.
     */
    EditText CorreoEt;

    /**
     * EditText para la contraseña del usuario.
     */
    EditText ContraseñaEt;

    /**
     * EditText para confirmar la contraseña del usuario.
     */
    EditText ConfirmarContraseñaEt;

    /**
     * Botón para registrar al usuario.
     */
    Button RegistrarUsuario;

    /**
     * TextView para redirigir al usuario a la actividad de inicio de sesión si ya tiene una cuenta.
     */
    TextView TengoCuentaTXT;

    /**
     * Instancia de FirebaseAuth para la autenticación de usuarios.
     */
    FirebaseAuth firebaseAuth;

    /**
     * Diálogo de progreso para mostrar al usuario mientras se realiza una operación.
     */
    ProgressDialog progressDialog;

    /**
     * Variables para almacenar los datos introducidos por el usuario.
     */
    String nombre = " ", correo = " ", password = "", confirmarPassword = "";

    /**
     * Método llamado al crear la actividad.
     * Inicializa la interfaz de usuario, configura la barra de acción,
     * establece los listeners para los botones y configura Firebase.
     *
     * @param savedInstanceState Si la actividad se está reinicializando después de ser cerrada,
     *                           este Bundle contiene los datos que más recientemente suministró en onSaveInstanceState.
     *                           <b><i>Nota: De lo contrario es null.</i></b>
     */
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        ActionBar actionBar = getSupportActionBar();    // Obtiene la barra de acción
        actionBar.setTitle(R.string.RegistrarUsuario);    // Cambia el título de la barra de acción
        actionBar.setDisplayHomeAsUpEnabled(true); // Habilita el botón de retroceso
        actionBar.setDisplayShowHomeEnabled(true);  // Muestra el icono en la barra de acción

        NombreEt = findViewById(R.id.NombreEt);
        CorreoEt = findViewById(R.id.CorreoEt);
        ContraseñaEt = findViewById(R.id.ContraseñaEt);
        ConfirmarContraseñaEt = findViewById(R.id.ConfirmarContraseñaEt);
        RegistrarUsuario = findViewById(R.id.RegistrarUsuario);
        TengoCuentaTXT = findViewById(R.id.TengoCuentaTXT);


        firebaseAuth = FirebaseAuth.getInstance();      // Instancia de Firebase Authentication
        progressDialog = new ProgressDialog(this);       // Instancia de ProgressDialog

        progressDialog.setTitle("Espere un momento...");        // Título del ProgressDialog
        progressDialog.setCanceledOnTouchOutside(false);    // Evita que se cancele al tocar fuera de la pantalla

        RegistrarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarDatos(); // Llamada al método para validar los datos
            }
        });

        TengoCuentaTXT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Registro.this, Login.class));
            }
        });
    }

    /**
     * Valida los datos introducidos por el usuario.
     * Comprueba que los campos no estén vacíos, que el correo tenga un formato válido y que las contraseñas coincidan.
     * Si los datos son válidos, llama a {@link #CrearCuenta()}.
     */
    private void validarDatos() {
        nombre = NombreEt.getText().toString();
        correo = CorreoEt.getText().toString();
        password = ContraseñaEt.getText().toString();
        confirmarPassword = ConfirmarContraseñaEt.getText().toString();

        if (TextUtils.isEmpty(nombre)) {
            Toast.makeText(this, "Ingrese su nombre", Toast.LENGTH_SHORT).show();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            Toast.makeText(this, "Ingrese su correo", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Ingrese su contraseña", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(confirmarPassword)) {
            Toast.makeText(this, "Confirme su contraseña", Toast.LENGTH_SHORT).show();
        } else if (!password.equals(confirmarPassword)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
        } else {
            CrearCuenta();
        }
    }

    /**
     * Crea una nueva cuenta de usuario con el correo electrónico y la contraseña proporcionados.
     * Muestra un diálogo de progreso mientras se crea la cuenta.
     * Si la creación de la cuenta es exitosa, llama a {@link #GuardarInformacion()}.
     * Si falla, muestra un mensaje de error.
     */
    private void CrearCuenta() {
        progressDialog.setMessage("Creando cuenta...");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(correo, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        // Crear un HashMap con los datos del usuario
                        GuardarInformacion();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(Registro.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Guarda la información del usuario en Firebase Realtime Database.
     * Muestra un diálogo de progreso mientras se guarda la información.
     * Si la información se guarda correctamente, redirige al usuario a {@link MenuPrincipal}.
     * Si falla, muestra un mensaje de error.
     */
    private void GuardarInformacion() {
        progressDialog.setMessage("Guardando información...");
        progressDialog.show();

        // Obtener el UID del usuario actual
        String uid = firebaseAuth.getUid();

        // Crear un HashMap con los datos del usuario
        HashMap<String, String> datos = new HashMap<>();
        datos.put("uid", uid);
        datos.put("correo", correo);
        datos.put("nombre", nombre);
        datos.put("password", password);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Usuarios");
        databaseReference.child(uid)
                .setValue(datos)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Toast.makeText(Registro.this, "Cuenta creada con éxito", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Registro.this, MenuPrincipal.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(Registro.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
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
