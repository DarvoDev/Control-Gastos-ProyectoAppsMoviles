package com.example.controlgastos.repository;

import android.content.Context;

import com.example.controlgastos.models.Gasto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

//implementacion de GastoRepository que guarda los gastos en un json
//dentro del almacenamiento interno de la app (/data/data/<paquete>/files/gastos.json)
public class ArchivoGastoRepository implements GastoRepository {
    private static final String ARCHIVO = "gastos.json";
    private final Context context;

    public ArchivoGastoRepository(Context context) {
        this.context = context.getApplicationContext();
    }

    //lectura y escritura archivo

    //lee archivo completo y lo convierte en un array json
    private JSONArray leer() {
        try (FileInputStream fis = context.openFileInput(ARCHIVO);
             BufferedReader br = new BufferedReader(new InputStreamReader(fis, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String linea;
            while ((linea = br.readLine()) != null) {
                sb.append(linea);
            }
            return new JSONArray(sb.toString());
        } catch (FileNotFoundException e) {
            return new JSONArray(); //primera vez: el archivo todavia no existe
        } catch (IOException | JSONException e) {
            return new JSONArray();
        }
    }

    //sobrescribe el archivo con el array completo
    private boolean escribir(JSONArray datos) {
        try (FileOutputStream fos = context.openFileOutput(ARCHIVO, Context.MODE_PRIVATE)) {
            fos.write(datos.toString().getBytes(StandardCharsets.UTF_8));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    //conversion Gasto <-> JSON

    private JSONObject aJson(Gasto g) throws JSONException {
        JSONObject o = new JSONObject();
        o.put("id", g.id);
        o.put("user_id", g.userId);
        o.put("categoria", g.categoria);
        o.put("monto", g.monto);
        o.put("descripcion", g.descripcion);
        o.put("fecha", g.fecha);
        return o;
    }

    private Gasto deJson(JSONObject o) {
        return new Gasto(
                o.optLong("id"),
                o.optLong("user_id"),
                o.optString("categoria"),
                o.optDouble("monto"),
                o.optString("descripcion"),
                o.optString("fecha"));
    }

    //metodos del contrato

    @Override
    public long insertar(Gasto gasto) {
        JSONArray datos = leer();

        //simula el AUTOINCREMENT de SQLite: id = mayor id + 1
        long nuevoId = 1;
        for (int i = 0; i < datos.length(); i++) {
            nuevoId = Math.max(nuevoId, datos.optJSONObject(i).optLong("id") + 1);
        }
        gasto.id = nuevoId;

        try {
            datos.put(aJson(gasto));
        } catch (JSONException e) {
            return -1;
        }
        return escribir(datos) ? nuevoId : -1;
    }

    @Override
    public List<Gasto> listar(long userId, String desde, String hasta, String categoria) {
        List<Gasto> lista = new ArrayList<>();
        JSONArray datos = leer();

        for (int i = 0; i < datos.length(); i++) {
            JSONObject o = datos.optJSONObject(i);
            String fecha = o.optString("fecha");

            //mismos filtros que el WHERE de SQLite, null = sin ese filtro
            //las fechas yyyy-MM-dd se pueden comparar como texto
            if (o.optLong("user_id") != userId) continue;
            if (desde != null && fecha.compareTo(desde) < 0) continue;
            if (hasta != null && fecha.compareTo(hasta) > 0) continue;
            if (categoria != null && !categoria.equals(o.optString("categoria"))) continue;

            lista.add(deJson(o));
        }

        //del mas reciente al mas antiguo (como ORDER BY fecha DESC, id DESC)
        lista.sort((a, b) -> {
            int c = b.fecha.compareTo(a.fecha);
            return c != 0 ? c : Long.compare(b.id, a.id);
        });
        return lista;
    }

    @Override
    public double totalPeriodo(long userId, String desde, String hasta) {
        double total = 0;
        for (Gasto g : listar(userId, desde, hasta, null)) {
            total += g.monto;
        }
        return total;
    }
}
