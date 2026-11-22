package com.example.controlgastos.activities;

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
    }
}
