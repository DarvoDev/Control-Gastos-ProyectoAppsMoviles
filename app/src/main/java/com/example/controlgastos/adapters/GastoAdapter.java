package com.example.controlgastos.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.controlgastos.R;
import com.example.controlgastos.models.Gasto;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Convierte la lista de gastos en filas del ListView usando res/layout/item_gasto.xml.
 * Usa el patrón ViewHolder: el ListView recicla las filas que salen de la pantalla
 * (convertView), así no se infla ni se hace findViewById en cada fila.
 */
public class GastoAdapter extends BaseAdapter {

    // Formato en que se guarda la fecha y formato en que se muestra.
    private static final SimpleDateFormat FORMATO_BD = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private static final SimpleDateFormat FORMATO_VISTA = new SimpleDateFormat("d MMM yyyy", Locale.forLanguageTag("es-MX"));

    private final LayoutInflater inflater;
    private List<Gasto> gastos = new ArrayList<>();

    public GastoAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    // Reemplaza los datos y le avisa al ListView que se vuelva a dibujar.
    public void setDatos(List<Gasto> nuevos) {
        gastos = nuevos;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return gastos.size();
    }

    @Override
    public Gasto getItem(int position) {
        return gastos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return gastos.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            // Fila nueva: se infla el XML y se buscan las vistas una sola vez.
            convertView = inflater.inflate(R.layout.item_gasto, parent, false);
            holder = new ViewHolder();
            holder.imgCategoria = convertView.findViewById(R.id.imgCategoria);
            holder.txtDescripcion = convertView.findViewById(R.id.txtDescripcion);
            holder.txtDetalle = convertView.findViewById(R.id.txtDetalle);
            holder.txtMonto = convertView.findViewById(R.id.txtMonto);
            convertView.setTag(holder);
        } else {
            // Fila reciclada: las vistas ya están guardadas en el holder.
            holder = (ViewHolder) convertView.getTag();
        }

        Gasto gasto = getItem(position);

        // Si no hay descripción, se muestra la categoría como título.
        boolean sinDescripcion = gasto.descripcion == null || gasto.descripcion.trim().isEmpty();
        holder.txtDescripcion.setText(sinDescripcion ? gasto.categoria : gasto.descripcion);
        holder.txtDetalle.setText(gasto.categoria + " • " + formatearFecha(gasto.fecha));
        holder.txtMonto.setText(String.format(Locale.US, "$%,.2f", gasto.monto));
        holder.imgCategoria.setImageResource(iconoDe(gasto.categoria));

        return convertView;
    }

    // "2026-11-22" -> "22 nov 2026". Si la fecha no tiene el formato esperado, se muestra tal cual.
    private static String formatearFecha(String fecha) {
        try {
            Date d = FORMATO_BD.parse(fecha);
            return FORMATO_VISTA.format(d);
        } catch (ParseException | NullPointerException e) {
            return fecha;
        }
    }

    // Solo existe un ícono específico (comida); el resto usa el de cartera.
    private static int iconoDe(String categoria) {
        if ("Comida".equals(categoria)) {
            return R.drawable.ic_comida;
        }
        return R.drawable.ic_cartera;
    }

    // Guarda las referencias a las vistas de una fila.
    private static class ViewHolder {
        ImageView imgCategoria;
        TextView txtDescripcion, txtDetalle, txtMonto;
    }
}
