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
import com.example.controlgastos.utils.PrefsManager;

/**
 * Registro de nuevos usuarios.
 * Layout: res/layout/activity_register.xml
 */
public class RegisterActivity extends AppCompatActivity {

    private EditText etNombre, etCorreo, etContrasena, etConfirmar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etNombre = findViewById(R.id.etNombre);
        etCorreo = findViewById(R.id.etCorreo);
        etContrasena = findViewById(R.id.etContrasena);
        etConfirmar = findViewById(R.id.etConfirmar);
        Button btnRegistrar = findViewById(R.id.btnRegistrar);
        TextView txtIrLogin = findViewById(R.id.txtIrLogin);

        btnRegistrar.setOnClickListener(v -> registrar());
        txtIrLogin.setOnClickListener(v -> finish()); // cierra esta pantalla y regresa al Login
    }

    private void registrar() {
        // Leer lo que escribió el usuario
        String nombre = etNombre.getText().toString().trim();
        String correo = etCorreo.getText().toString().trim();
        String contrasena = etContrasena.getText().toString();
        String confirmar = etConfirmar.getText().toString();

        // Validaciones: cada una muestra un mensaje y detiene el registro con return
        if (nombre.isEmpty() || correo.isEmpty() || contrasena.isEmpty()) {
            mensaje(R.string.error_campos_vacios);
            return;
        }
        if (!correo.contains("@") || !correo.contains(".")) {
            mensaje(R.string.error_correo_invalido);
            return;
        }
        if (contrasena.length() < 6) {
            mensaje(R.string.error_contrasena_corta);
            return;
        }
        if (!contrasena.equals(confirmar)) {
            mensaje(R.string.error_confirmar_distinta);
            return;
        }

        // Guardar en SQLite: devuelve -1 si el correo ya existe
        long id = new UserDao(this).registrar(nombre, correo, contrasena);
        if (id == -1) {
            mensaje(R.string.error_correo_repetido);
            return;
        }

        mensaje(R.string.msg_cuenta_creada);
        finish();
        /*// Registro correcto: se guarda la sesión para que el usuario entre directo
        new PrefsManager(this).guardarSesion(id, nombre, correo);
        mensaje(R.string.msg_cuenta_creada);

        // Abrir Main y vaciar la pila (Login y Register ya no deben quedar atrás)
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);*/
    }

    private void mensaje(int idTexto) {
        Toast.makeText(this, idTexto, Toast.LENGTH_SHORT).show();
    }
}
