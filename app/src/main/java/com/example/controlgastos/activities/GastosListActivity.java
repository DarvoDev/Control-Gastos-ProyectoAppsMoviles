package com.example.controlgastos.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.controlgastos.R;
import com.example.controlgastos.adapters.GastoAdapter;
import com.example.controlgastos.models.Gasto;
import com.example.controlgastos.repository.RepositoryFactory;
import com.example.controlgastos.utils.PrefsManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Lista de gastos del usuario con filtros por mes (del año actual) y categoría.
 * Las filas son solo de lectura: los gastos no se editan ni se eliminan.
 * Layout: res/layout/activity_gastos_list.xml
 */
public class GastosListActivity extends AppCompatActivity {

    private final SimpleDateFormat formatoBd = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    private Spinner spMes, spCategoria;
    private ListView listGastos;
    private TextView txtTotalFiltrado, txtVacio;
    private GastoAdapter adapter;
    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gastos_list);

        userId = new PrefsManager(this).getUserId();
        if (userId == -1) {
            finish();
            return;
        }

        spMes = findViewById(R.id.spMes);
        spCategoria = findViewById(R.id.spCategoria);
        listGastos = findViewById(R.id.listGastos);
        txtTotalFiltrado = findViewById(R.id.txtTotalFiltrado);
        txtVacio = findViewById(R.id.txtVacio);

        adapter = new GastoAdapter(this);
        listGastos.setAdapter(adapter);

        // El filtro de mes empieza en el mes actual (posición 0 = Enero, igual que Calendar.MONTH).
        // Solo la primera vez: al rotar, el Spinner recuerda lo que eligió el usuario.
        if (savedInstanceState == null) {
            spMes.setSelection(Calendar.getInstance().get(Calendar.MONTH));
        }

        // Al cambiar cualquier filtro se vuelve a consultar.
        AdapterView.OnItemSelectedListener alCambiarFiltro = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cargarGastos();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        spMes.setOnItemSelectedListener(alCambiarFiltro);
        spCategoria.setOnItemSelectedListener(alCambiarFiltro);

        findViewById(R.id.btnAtras).setOnClickListener(v -> finish());
        findViewById(R.id.btnAgregar).setOnClickListener(v ->
                startActivity(new Intent(this, GastoFormActivity.class)));
    }

    // Se llama al abrir y cada vez que se regresa a esta pantalla (del formulario o de Ajustes),
    // así se ve el gasto recién agregado o los datos del almacenamiento elegido.
    @Override
    protected void onResume() {
        super.onResume();
        cargarGastos();
    }

    private void cargarGastos() {
        // Rango del mes elegido, en el año actual: del día 1 al último día del mes.
        // Primero el día 1 y luego el mes: si hoy fuera 31 y el mes elegido tuviera 30 días,
        // Calendar se pasaría al mes siguiente.
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.MONTH, spMes.getSelectedItemPosition());
        String desde = formatoBd.format(cal.getTime());
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        String hasta = formatoBd.format(cal.getTime());

        // Posición 0 = "Todas" -> null (sin filtro de categoría).
        String categoria = spCategoria.getSelectedItemPosition() == 0
                ? null
                : spCategoria.getSelectedItem().toString();

        // Se pide el repositorio cada vez para respetar el almacenamiento elegido en Ajustes.
        List<Gasto> gastos = RepositoryFactory.get(this).listar(userId, desde, hasta, categoria);
        adapter.setDatos(gastos);

        // El total se suma aquí (y no con totalPeriodo) porque también debe respetar la categoría.
        double total = 0;
        for (Gasto g : gastos) {
            total += g.monto;
        }
        txtTotalFiltrado.setText(String.format(Locale.US, "$%,.2f", total));

        // Mensaje de lista vacía.
        boolean vacia = gastos.isEmpty();
        txtVacio.setText(R.string.sin_gastos_filtro);
        txtVacio.setVisibility(vacia ? View.VISIBLE : View.GONE);
        listGastos.setVisibility(vacia ? View.GONE : View.VISIBLE);
    }
}
