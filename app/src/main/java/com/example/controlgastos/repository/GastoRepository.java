package com.example.controlgastos.repository;

import com.example.controlgastos.models.Gasto;

import java.util.List;

/**
 * Contrato para guardar y leer gastos.
 * Las pantallas solo conocen esta interfaz; no saben si los datos van a SQLite
 * (SqliteGastoRepository) o a un archivo JSON (ArchivoGastoRepository).
 * Se obtiene siempre con RepositoryFactory.get(context).
 *
 * Fechas siempre en formato yyyy-MM-dd.
 */
public interface GastoRepository {

    /** Guarda un gasto nuevo. Regresa el id generado, o -1 si falló. */
    long insertar(Gasto gasto);

    /** Actualiza un gasto existente (por gasto.id y gasto.userId). Regresa las filas afectadas. */
    int actualizar(Gasto gasto);

    /** Elimina el gasto solo si pertenece a ese usuario. Regresa las filas afectadas. */
    int eliminar(long id, long userId);

    /** Busca un gasto del usuario. Regresa null si no existe. */
    Gasto obtenerPorId(long id, long userId);

    /**
     * Gastos del usuario, del más reciente al más antiguo.
     * desde / hasta / categoria en null = sin ese filtro.
     */
    List<Gasto> listar(long userId, String desde, String hasta, String categoria);

    /** Suma de los montos del usuario entre desde y hasta (incluidos). 0 si no hay gastos. */
    double totalPeriodo(long userId, String desde, String hasta);
}
