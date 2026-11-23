package com.example.controlgastos.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.controlgastos.R;

/**
 * Formulario para crear o editar un gasto.
 * Layout: res/layout/activity_gasto_form.xml
 *
 * Por ahora solo carga el diseño; la lógica se agregará después.
 */
public class GastoFormActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gasto_form);
    }
}
