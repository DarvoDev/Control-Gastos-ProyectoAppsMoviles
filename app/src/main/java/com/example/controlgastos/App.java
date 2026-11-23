package com.example.controlgastos;

import android.app.Application;
import androidx.appcompat.app.AppCompatDelegate;
import com.example.controlgastos.utils.PrefsManager;

public class App extends Application {
    @Override
    public void onCreate(){
        super.onCreate();

        // Corre antes que cualquier Activity: la primera pantalla ya abre en el modo correcto
        PrefsManager prefs = new PrefsManager(this);
        AppCompatDelegate.setDefaultNightMode(prefs.isDarkMode() ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        // notis (Uli)
    }
}


