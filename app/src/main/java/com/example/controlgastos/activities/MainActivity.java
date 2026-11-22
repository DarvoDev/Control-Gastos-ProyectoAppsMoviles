package com.example.controlgastos.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.controlgastos.R;

/**
 * Pantalla principal (dashboard): total del mes, presupuesto y gastos recientes.
 * Layout: res/layout/activity_main.xml
 *
 * Por ahora solo carga el diseño; la lógica se agregará después.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
