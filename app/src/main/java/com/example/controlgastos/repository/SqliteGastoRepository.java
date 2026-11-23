package com.example.controlgastos.repository;

import android.content.Context;

import com.example.controlgastos.database.GastoDao;
import com.example.controlgastos.models.Gasto;

import java.util.List;

/**
 * Implementación de GastoRepository con SQLite.
 * No tiene SQL propio: delega en GastoDao. Su papel es cumplir el contrato GastoRepository
 * para que las pantallas puedan usar SQLite o el archivo JSON sin saber cuál es.
 */
public class SqliteGastoRepository implements GastoRepository {

    private final GastoDao dao;

    public SqliteGastoRepository(Context context) {
        dao = new GastoDao(context.getApplicationContext());
    }

    @Override
    public long insertar(Gasto gasto) {
        return dao.insertar(gasto);
    }

    @Override
    public List<Gasto> listar(long userId, String desde, String hasta, String categoria) {
        return dao.listar(userId, desde, hasta, categoria);
    }

    @Override
    public double totalPeriodo(long userId, String desde, String hasta) {
        return dao.totalPeriodo(userId, desde, hasta);
    }
}
