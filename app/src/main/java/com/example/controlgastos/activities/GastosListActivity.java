package com.example.controlgastos.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.controlgastos.R;

/**
 * Lista completa de gastos con filtros por mes y categoría.
 * Layout: res/layout/activity_gastos_list.xml
 *
 * Por ahora solo carga el diseño; la lógica se agregará después.
 */
public class GastosListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gastos_list);
    }
}
