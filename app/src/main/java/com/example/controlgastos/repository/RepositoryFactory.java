package com.example.controlgastos.repository;

import android.content.Context;

import com.example.controlgastos.utils.PrefsManager;

//decide que implementacion de GastoRepository usar segun lo elejido en Ajustes

public class RepositoryFactory {
    public static final String MODO_SQLITE = "sqlite";
    public static final String MODO_ARCHIVO = "archivo";

    public static GastoRepository get(Context context){
        String modo = new PrefsManager(context).getStorageMode();
        if(MODO_ARCHIVO.equals(modo)){
            return new ArchivoGastoRepository(context);
        }
        return new SqliteGastoRepository(context);
    }
}
