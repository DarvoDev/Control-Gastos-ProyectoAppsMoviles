package com.example.controlgastos.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.controlgastos.R;
import com.example.controlgastos.models.Gasto;
import com.example.controlgastos.repository.RepositoryFactory;
import com.example.controlgastos.utils.NotificationHelper;
import com.example.controlgastos.utils.PrefsManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Formulario para registrar un gasto nuevo (los gastos no se editan ni se eliminan).
 * Layout: res/layout/activity_gasto_form.xml
 */
public class GastoFormActivity extends AppCompatActivity {

    // Clave para guardar la fecha elegida al rotar la pantalla.
    private static final String ESTADO_FECHA = "fecha";

    // Formato en que se guarda la fecha y formato en que se muestra.
    private final SimpleDateFormat formatoBd = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private final SimpleDateFormat formatoVista = new SimpleDateFormat("d MMM yyyy", Locale.forLanguageTag("es-MX"));

    private EditText etMonto, etDescripcion;
    private Spinner spCategoria;
    private TextView txtFecha;

    private final Calendar fecha = Calendar.getInstance(); // empieza en hoy
    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gasto_form);

        // Sin sesión no se puede saber de quién es el gasto.
        userId = new PrefsManager(this).getUserId();
        if (userId == -1) {
            finish();
            return;
        }

        etMonto = findViewById(R.id.etMonto);
        etDescripcion = findViewById(R.id.etDescripcion);
        spCategoria = findViewById(R.id.spCategoria);
        txtFecha = findViewById(R.id.txtFecha);

        // Al rotar, Android recrea la Activity: los EditText y el Spinner se restauran solos,
        // pero la fecha elegida hay que recuperarla a mano.
        if (savedInstanceState != null) {
            try {
                fecha.setTime(formatoBd.parse(savedInstanceState.getString(ESTADO_FECHA)));
            } catch (ParseException | NullPointerException e) {
                // si falla, se queda la fecha de hoy
            }
        }
        mostrarFecha();

        findViewById(R.id.btnAtras).setOnClickListener(v -> finish());
        txtFecha.setOnClickListener(v -> elegirFecha());
        findViewById(R.id.btnGuardar).setOnClickListener(v -> guardar());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ESTADO_FECHA, formatoBd.format(fecha.getTime()));
    }

    // Abre el calendario con la fecha actual del formulario.
    private void elegirFecha() {
        DatePickerDialog dialogo = new DatePickerDialog(this,
                (vista, anio, mes, dia) -> {
                    fecha.set(anio, mes, dia);
                    mostrarFecha();
                },
                fecha.get(Calendar.YEAR),
                fecha.get(Calendar.MONTH),
                fecha.get(Calendar.DAY_OF_MONTH));
        // No se permiten gastos a futuro.
        dialogo.getDatePicker().setMaxDate(System.currentTimeMillis());
        dialogo.show();
    }

    private void mostrarFecha() {
        txtFecha.setText(formatoVista.format(fecha.getTime()));
    }

    private void guardar() {
        // Validar el monto: obligatorio, numérico y mayor a 0.
        String textoMonto = etMonto.getText().toString().trim();
        double monto;
        try {
            monto = Double.parseDouble(textoMonto);
        } catch (NumberFormatException e) {
            monto = 0; // vacío o no numérico cuenta como inválido
        }
        if (monto <= 0) {
            etMonto.setError(getString(R.string.error_monto_invalido));
            etMonto.requestFocus();
            return;
        }

        Gasto gasto = new Gasto();
        gasto.userId = userId;
        gasto.categoria = spCategoria.getSelectedItem().toString();
        gasto.monto = monto;
        gasto.descripcion = etDescripcion.getText().toString().trim(); // opcional
        gasto.fecha = formatoBd.format(fecha.getTime());

        // La fábrica decide si se guarda en SQLite o en el archivo JSON (según Ajustes).
        long id = RepositoryFactory.get(this).insertar(gasto);
        if (id == -1) {
            Toast.makeText(this, R.string.error_guardar_gasto, Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, R.string.msg_gasto_guardado, Toast.LENGTH_SHORT).show();
        // Revisa si con este gasto se llegó al 80% del presupuesto (Integrante 4).
        NotificationHelper.verificarPresupuesto(this, userId);
        finish();
    }
}
