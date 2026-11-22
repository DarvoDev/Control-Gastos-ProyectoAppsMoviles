package com.example.controlgastos.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.controlgastos.R;

/**
 * Ajustes: color del tema, modo oscuro, presupuesto, notificaciones, almacenamiento y cuenta.
 * Layout: res/layout/activity_settings.xml
 *
 * Por ahora solo carga el diseño; la lógica se agregará después.
 */
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }
}
