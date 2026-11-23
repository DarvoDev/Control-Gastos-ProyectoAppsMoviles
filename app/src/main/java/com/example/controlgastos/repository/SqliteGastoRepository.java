package com.example.controlgastos.repository;

import android.content.Context;

import com.example.controlgastos.models.Gasto;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementación de GastoRepository con SQLite.
 * ESQUELETO: por ahora regresa valores vacíos para que el resto del equipo pueda compilar.
 */
public class SqliteGastoRepository implements GastoRepository {

    private final Context context;

    public SqliteGastoRepository(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override
    public long insertar(Gasto gasto) {
        // TODO Integrante 2: INSERT con GastoDao
        return -1;
    }

    @Override
    public List<Gasto> listar(long userId, String desde, String hasta, String categoria) {
        // TODO Integrante 2: SELECT con filtros con GastoDao
        return new ArrayList<>();
    }

    @Override
    public double totalPeriodo(long userId, String desde, String hasta) {
        // TODO Integrante 2: SELECT SUM(monto) con GastoDao
        return 0;
    }
}
