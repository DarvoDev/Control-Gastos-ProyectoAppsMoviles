package com.example.controlgastos.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.controlgastos.models.Gasto;

import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a la tabla gastos (aquí vive todo el SQL de gastos).
 * Todas las consultas filtran por user_id para que un usuario nunca vea gastos de otro,
 * y usan "?" (consultas parametrizadas) para evitar inyección SQL.
 */
public class GastoDao {

    private final DBHelper helper;

    public GastoDao(Context context) {
        helper = new DBHelper(context);
    }

    // INSERT: regresa el id generado, o -1 si falló.
    public long insertar(Gasto gasto) {
        ContentValues fila = new ContentValues(); // pares columna -> valor
        fila.put("user_id", gasto.userId);
        fila.put("categoria", gasto.categoria);
        fila.put("monto", gasto.monto);
        fila.put("descripcion", gasto.descripcion);
        fila.put("fecha", gasto.fecha);

        SQLiteDatabase db = helper.getWritableDatabase();
        return db.insert("gastos", null, fila);
    }

    /*
     SELECT con filtros opcionales: desde / hasta / categoria en null = sin ese filtro.
     El WHERE se arma paso a paso y cada valor se agrega a la lista de argumentos
     en el mismo orden que sus "?".
     */
    public List<Gasto> listar(long userId, String desde, String hasta, String categoria) {
        StringBuilder where = new StringBuilder("user_id = ?");
        List<String> args = new ArrayList<>();
        args.add(String.valueOf(userId));

        // Las fechas son texto yyyy-MM-dd: ordenadas como texto quedan igual que como fechas,
        // por eso se pueden comparar con >= y <=.
        if (desde != null) {
            where.append(" AND fecha >= ?");
            args.add(desde);
        }
        if (hasta != null) {
            where.append(" AND fecha <= ?");
            args.add(hasta);
        }
        if (categoria != null) {
            where.append(" AND categoria = ?");
            args.add(categoria);
        }

        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT id, user_id, categoria, monto, descripcion, fecha FROM gastos" +
                        " WHERE " + where +
                        " ORDER BY fecha DESC, id DESC", // más reciente primero
                args.toArray(new String[0]));

        List<Gasto> gastos = new ArrayList<>();
        while (c.moveToNext()) { // una vuelta por cada fila del resultado
            gastos.add(new Gasto(
                    c.getLong(0),
                    c.getLong(1),
                    c.getString(2),
                    c.getDouble(3),
                    c.getString(4),
                    c.getString(5)));
        }
        c.close(); // liberar el Cursor siempre
        return gastos;
    }

    // SUM de los montos del usuario entre dos fechas (incluidas).
    public double totalPeriodo(long userId, String desde, String hasta) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT SUM(monto) FROM gastos WHERE user_id = ? AND fecha BETWEEN ? AND ?",
                new String[]{String.valueOf(userId), desde, hasta});

        double total = 0;
        // SUM siempre regresa una fila; si no hubo gastos el valor es NULL, y en ese caso se deja 0.
        if (c.moveToFirst() && !c.isNull(0)) {
            total = c.getDouble(0);
        }
        c.close();
        return total;
    }
}
