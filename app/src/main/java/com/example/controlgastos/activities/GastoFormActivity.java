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

    // Extra del Intent con el id del gasto a editar. Si no viene, el formulario es para un gasto nuevo.
    public static final String EXTRA_GASTO_ID = "gasto_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gasto_form);
    }
}
