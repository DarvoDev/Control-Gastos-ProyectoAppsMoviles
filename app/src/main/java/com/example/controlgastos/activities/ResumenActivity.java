package com.example.controlgastos.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.controlgastos.R;

/**
 * Resumen de gastos agrupados por categoría.
 * Layout: res/layout/activity_resumen.xml
 *
 * Por ahora solo carga el diseño; la lógica se agregará después.
 */
public class ResumenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resumen);
    }
}
