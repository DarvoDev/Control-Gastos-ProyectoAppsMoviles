package com.example.controlgastos.activities;

import android.Manifest;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.controlgastos.R;
import com.example.controlgastos.models.Gasto;
import com.example.controlgastos.repository.GastoRepository;
import com.example.controlgastos.repository.RepositoryFactory;
import com.example.controlgastos.utils.NotificationHelper;
import com.example.controlgastos.utils.PrefsManager;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Pantalla principal (dashboard): saludo, total del mes, barra de presupuesto y gastos recientes.
 * Layout: res/layout/activity_main.xml
 */
public class MainActivity extends AppCompatActivity {

    private static final int MAX_RECIENTES = 5;

    private PrefsManager prefs;
    private TextView txtSaludo;
    private TextView txtMesActual;
    private TextView txtTotalMes;
    private ProgressBar progressPresupuesto;
    private TextView txtRestante;
    private TextView txtPresupuesto;
    private TextView txtSinGastos;
    private LinearLayout layoutRecientes;

    private final ActivityResultLauncher<String> launcherPermisoNotificaciones =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), concedido -> {
                if (!concedido) {
                    Toast.makeText(this, R.string.permiso_notif_denegado, Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = new PrefsManager(this);
        if (!prefs.isLoggedIn() || prefs.getUserId() <= 0) {
            redirigirALogin();
            return;
        }

        setContentView(R.layout.activity_main);

        // Asegura que el canal exista desde el primer ingreso
        NotificationHelper.crearCanal(this);
        pedirPermisoNotificacionesSiHaceFalta();

        txtSaludo = findViewById(R.id.txtSaludo);
        txtMesActual = findViewById(R.id.txtMesActual);
        txtTotalMes = findViewById(R.id.txtTotalMes);
        progressPresupuesto = findViewById(R.id.progressPresupuesto);
        txtRestante = findViewById(R.id.txtRestante);
        txtPresupuesto = findViewById(R.id.txtPresupuesto);
        txtSinGastos = findViewById(R.id.txtSinGastos);
        layoutRecientes = findViewById(R.id.layoutRecientes);

        ImageView btnAjustes = findViewById(R.id.btnAjustes);
        TextView txtVerTodos = findViewById(R.id.txtVerTodos);
        ImageButton btnAgregarGasto = findViewById(R.id.btnAgregarGasto);

        btnAjustes.setOnClickListener(v ->
                startActivity(new Intent(this, SettingsActivity.class)));

        txtVerTodos.setOnClickListener(v ->
                startActivity(new Intent(this, GastosListActivity.class)));

        btnAgregarGasto.setOnClickListener(v ->
                startActivity(new Intent(this, GastoFormActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!prefs.isLoggedIn() || prefs.getUserId() <= 0) {
            redirigirALogin();
            return;
        }
        cargarDashboard();
    }

    private void pedirPermisoNotificacionesSiHaceFalta() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && !NotificationHelper.tienePermisoNotificaciones(this)) {
            launcherPermisoNotificaciones.launch(Manifest.permission.POST_NOTIFICATIONS);
        }
    }

    private void cargarDashboard() {
        // 1. Saludo con el nombre del usuario en sesión
        String nombre = prefs.getUserName();
        if (nombre == null || nombre.trim().isEmpty()) {
            nombre = getString(R.string.usuario_nombre_ejemplo);
        }
        txtSaludo.setText(getString(R.string.main_saludo_formato, nombre));

        // 2. Mes actual y rango yyyy-MM-dd para filtrar los gastos del mes
        Calendar hoy = Calendar.getInstance();
        int anio = hoy.get(Calendar.YEAR);
        int mesIndex = hoy.get(Calendar.MONTH); // 0..11
        int mesNumero = mesIndex + 1;
        int ultimoDia = hoy.getActualMaximum(Calendar.DAY_OF_MONTH);

        String[] meses = getResources().getStringArray(R.array.meses);
        String nombreMes = (mesIndex >= 0 && mesIndex < meses.length) ? meses[mesIndex] : "";
        txtMesActual.setText(nombreMes + " " + anio);

        String desde = String.format(Locale.US, "%04d-%02d-01", anio, mesNumero);
        String hasta = String.format(Locale.US, "%04d-%02d-%02d", anio, mesNumero, ultimoDia);

        // 3. Consultar total del mes y gastos recientes del usuario
        long userId = prefs.getUserId();
        GastoRepository repo = RepositoryFactory.get(this);
        double totalMes = repo.totalPeriodo(userId, desde, hasta);
        List<Gasto> gastosDelMes = repo.listar(userId, desde, hasta, null);

        // 4. Mostrar total y estado del presupuesto
        txtTotalMes.setText(formatearMoneda(totalMes));
        actualizarBarraPresupuesto(totalMes, prefs.getPresupuesto());

        // 5. Mostrar lista de gastos recientes (solo lectura)
        mostrarRecientes(gastosDelMes);
    }

    private void actualizarBarraPresupuesto(double totalMes, float presupuesto) {
        int colorPrimario = obtenerColorPrimario();
        int colorError = ContextCompat.getColor(this, R.color.error);

        if (presupuesto <= 0f) {
            progressPresupuesto.setProgress(0);
            progressPresupuesto.setProgressTintList(ColorStateList.valueOf(colorPrimario));
            txtRestante.setText(R.string.main_sin_presupuesto);
            txtRestante.setTextColor(ContextCompat.getColor(this, R.color.texto_secundario));
            txtPresupuesto.setText("");
            return;
        }

        double porcentaje = (totalMes / presupuesto) * 100.0;
        int progreso = (int) Math.min(100, Math.max(0, Math.round(porcentaje)));
        progressPresupuesto.setProgress(progreso);
        txtPresupuesto.setText(getString(R.string.main_presupuesto_formato, formatearMoneda(presupuesto)));

        if (totalMes > presupuesto) {
            // Si supera el 100%, la barra y el texto cambian a rojo
            progressPresupuesto.setProgressTintList(ColorStateList.valueOf(colorError));
            txtRestante.setTextColor(colorError);
            txtRestante.setText(getString(
                    R.string.main_excedido_formato,
                    formatearMoneda(totalMes - presupuesto)
            ));
        } else {
            progressPresupuesto.setProgressTintList(ColorStateList.valueOf(colorPrimario));
            txtRestante.setTextColor(colorPrimario);
            txtRestante.setText(getString(
                    R.string.main_restante_formato,
                    formatearMoneda(presupuesto - totalMes)
            ));
        }
    }

    private void mostrarRecientes(List<Gasto> gastos) {
        // Limpiar los <include> de vista previa (o los ítems anteriores)
        layoutRecientes.removeAllViews();

        if (gastos == null || gastos.isEmpty()) {
            txtSinGastos.setVisibility(View.VISIBLE);
            return;
        }

        txtSinGastos.setVisibility(View.GONE);
        LayoutInflater inflater = LayoutInflater.from(this);
        int limite = Math.min(MAX_RECIENTES, gastos.size());

        for (int i = 0; i < limite; i++) {
            Gasto gasto = gastos.get(i);
            View fila = inflater.inflate(R.layout.item_gasto, layoutRecientes, false);

            ImageView imgCategoria = fila.findViewById(R.id.imgCategoria);
            TextView txtDescripcion = fila.findViewById(R.id.txtDescripcion);
            TextView txtDetalle = fila.findViewById(R.id.txtDetalle);
            TextView txtMonto = fila.findViewById(R.id.txtMonto);

            imgCategoria.setImageResource(iconoParaCategoria(gasto.categoria));

            String descripcion = (gasto.descripcion != null && !gasto.descripcion.trim().isEmpty())
                    ? gasto.descripcion.trim()
                    : gasto.categoria;
            txtDescripcion.setText(descripcion);

            String detalle = gasto.categoria + " • " + formatearFechaCorta(gasto.fecha);
            txtDetalle.setText(detalle);

            txtMonto.setText(formatearMoneda(gasto.monto));

            layoutRecientes.addView(fila);
        }
    }

    private int iconoParaCategoria(String categoria) {
        if ("Comida".equalsIgnoreCase(categoria)) {
            return R.drawable.ic_comida;
        }
        return R.drawable.ic_cartera;
    }

    private int obtenerColorPrimario() {
        TypedValue typedValue = new TypedValue();
        if (getTheme().resolveAttribute(androidx.appcompat.R.attr.colorPrimary, typedValue, true)) {
            return typedValue.data;
        }
        return ContextCompat.getColor(this, R.color.tema_azul);
    }

    private String formatearMoneda(double monto) {
        NumberFormat formato = NumberFormat.getCurrencyInstance(Locale.US);
        return formato.format(monto);
    }

    /** Convierte "yyyy-MM-dd" en "dd MMM yyyy" (ej. "22 nov 2026"); si no coincide, devuelve el original. */
    private String formatearFechaCorta(String fechaIso) {
        if (fechaIso == null) {
            return "";
        }
        String[] partes = fechaIso.split("-");
        if (partes.length != 3) {
            return fechaIso;
        }
        try {
            int anio = Integer.parseInt(partes[0]);
            int mes = Integer.parseInt(partes[1]);
            int dia = Integer.parseInt(partes[2]);
            String[] meses = getResources().getStringArray(R.array.meses);
            if (mes >= 1 && mes <= meses.length) {
                String mesCorto = meses[mes - 1].substring(0, 3).toLowerCase(Locale.getDefault());
                return dia + " " + mesCorto + " " + anio;
            }
        } catch (NumberFormatException ignored) {
            // Devuelve la fecha tal cual si no tiene formato esperado
        }
        return fechaIso;
    }

    private void redirigirALogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
