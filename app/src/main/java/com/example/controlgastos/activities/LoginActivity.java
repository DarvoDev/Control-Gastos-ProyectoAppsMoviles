package com.example.controlgastos.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.controlgastos.R;
import com.example.controlgastos.database.UserDao;
import com.example.controlgastos.models.User;
import com.example.controlgastos.utils.PrefsManager;

/**
 * Inicio de sesión con correo y contraseña.
 * Layout: res/layout/activity_login.xml
 */
public class LoginActivity extends AppCompatActivity {

    private EditText etCorreo, etContrasena;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etCorreo = findViewById(R.id.etCorreo);
        etContrasena = findViewById(R.id.etContrasena);
        Button btnIniciarSesion = findViewById(R.id.btnIniciarSesion);
        TextView txtIrRegistro = findViewById(R.id.txtIrRegistro);

        btnIniciarSesion.setOnClickListener(v -> iniciarSesion());
        txtIrRegistro.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void iniciarSesion() {
        // Leer lo que escribió el usuario
        String correo = etCorreo.getText().toString().trim();
        String contrasena = etContrasena.getText().toString();

        // Validar que no estén vacíos
        if (correo.isEmpty() || contrasena.isEmpty()) {
            Toast.makeText(this, R.string.error_campos_vacios, Toast.LENGTH_SHORT).show();
            return;
        }

        // Buscar en SQLite: devuelve el usuario, o null si las credenciales no coinciden
        User usuario = new UserDao(this).login(correo, contrasena);
        if (usuario == null) {
            // Un solo mensaje para "correo no existe" y "contraseña mala":
            // así no se revela qué correos están registrados.
            Toast.makeText(this, R.string.error_credenciales, Toast.LENGTH_SHORT).show();
            return;
        }

        // Login correcto: se guarda la sesión (aquí se crea el archivo de preferencias)
        new PrefsManager(this).guardarSesion(usuario.id, usuario.nombre, usuario.email);

        // Abrir Main y vaciar la pila para que "Atrás" no regrese al login
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
