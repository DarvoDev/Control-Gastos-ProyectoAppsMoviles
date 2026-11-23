package com.example.controlgastos.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.controlgastos.R;

/**
 * Pantalla de inicio. Mostrará el logo y decidirá si ir a Login o a Main según la sesión guardada.
 * Layout: res/layout/activity_splash.xml
 *
 * Por ahora solo carga el diseño; la lógica se agregará después.
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //primero consultamos el xml para verificar si hay una sesion activa
        SharedPreferences prefs = getSharedPreferences("control_gastos_prefs", MODE_PRIVATE);
        boolean haySesion = prefs.getBoolean("logged_in", false);

        //en base a lo que este almacenado la variable haySesion
        //redireccionaremos a el MainActivity o a el login
        Intent intent;
        if (haySesion) {
            intent = new Intent(this, MainActivity.class);
        } else {
            intent = new Intent(this, LoginActivity.class);
        }
        startActivity(intent);
        finish();
    }

}
