package com.example.controlgastos.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.example.controlgastos.R;
import com.example.controlgastos.repository.RepositoryFactory;
import com.example.controlgastos.utils.PrefsManager;


public class SettingsActivity extends AppCompatActivity {
    private PrefsManager prefs;
    private EditText etPresupuesto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        prefs = new PrefsManager(this);
        findViewById(R.id.btnAtras).setOnClickListener( v -> {
            finish();
        });
        configurarModoOscuro();
        configurarPresupuesto();
        configurarAlmacenamiento();
        configurarCuenta();
        // configurarNotifiaciones();    Uli


    }
    // modo oscuro
    private void configurarModoOscuro(){
        Switch sw = findViewById(R.id.swModoOscuro);
        sw.setChecked(prefs.isDarkMode()); //primero el estado guardado...
        sw.setOnCheckedChangeListener((boton, activado) ->{
            //listener

            prefs.setDarkMode(activado);
            //android recrea pantallas abiertasa y toma colores de values-night
            AppCompatDelegate.setDefaultNightMode(activado ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        });
    }
    //presupuesyto
    private void configurarPresupuesto(){
        etPresupuesto = findViewById(R.id.etPresupuesto);
        float presupuesto = prefs.getPresupuesto();
        if(presupuesto>0){
            etPresupuesto.setText(String.valueOf(presupuesto));
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        //se guarda al salir la pantalla, al rotar o al cambiar el modo ssorcuros
        String texto = etPresupuesto.getText().toString().trim();
        try {
            float valor = texto.isEmpty() ? 0f : Float.parseFloat(texto);
            if(valor>= 0) prefs.setPresupuesto(valor);
        }catch (NumberFormatException e){
            Toast.makeText(this, "Presupuesto inválido", Toast.LENGTH_SHORT).show();

        }
    }

    // Almanecaniento
    private void configurarAlmacenamiento(){
        RadioGroup rg = findViewById(R.id.rgAlmacenamiento);
        rg.check(RepositoryFactory.MODO_ARCHIVO.equals(prefs.getStorageMode()) ? R.id.rbArchivo : R.id.rbSqlite);
        rg.setOnCheckedChangeListener((grupo, idMarcado) ->{
            prefs.setStorageMode(idMarcado == R.id.rbArchivo ? RepositoryFactory.MODO_ARCHIVO : RepositoryFactory.MODO_SQLITE);
            Toast.makeText(this, "Almacenamiento cambiado", Toast.LENGTH_SHORT).show();
        });
    }

    //CUENTA
    private void configurarCuenta(){
        TextView txtNombre = findViewById(R.id.txtNombreUsuario);
        txtNombre.setText(prefs.getUserName());

        // TODO: txtCorreoUsuario

        findViewById(R.id.btnCerrarSesion).setOnClickListener(v -> {
            prefs.cerrarSesion();
            Intent intent = new Intent(this, LoginActivity.class);
            //borra el historial con "atras", ya no se regresa a la app sin sesion
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}
