package com.example.controlgastos.activities;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.example.controlgastos.R;
import com.example.controlgastos.utils.NotificationHelper;
import com.example.controlgastos.utils.PrefsManager;
import com.example.controlgastos.utils.ReminderScheduler;
import java.util.Locale;


public class SettingsActivity extends AppCompatActivity {
    private PrefsManager prefs;
    private EditText etPresupuesto;

    private final ActivityResultLauncher<String> launcherPermisoNotificaciones =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), concedido -> {
                if (!concedido) {
                    Toast.makeText(this, R.string.permiso_notif_denegado, Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        prefs = new PrefsManager(this);
        findViewById(R.id.btnAtras).setOnClickListener( v -> {
            finish();
        });
        configurarModoOscuro();
        configurarPresupuesto();
        configurarAlmacenamiento();
        configurarCuenta();
        configurarNotificaciones();


    }
    // modo oscuro
    private void configurarModoOscuro(){
        Switch sw = findViewById(R.id.swModoOscuro);
        sw.setChecked(prefs.isDarkMode()); //primero el estado guardado...
        sw.setOnCheckedChangeListener((boton, activado) ->{
            //listener

            prefs.setDarkMode(activado);
            //android recrea pantallas abiertasa y toma colores de values-night
            AppCompatDelegate.setDefaultNightMode(activado ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        });
    }
    //presupuesyto
    private void configurarPresupuesto(){
        etPresupuesto = findViewById(R.id.etPresupuesto);
        float presupuesto = prefs.getPresupuesto();
        if(presupuesto>0){
            etPresupuesto.setText(String.valueOf(presupuesto));
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        //se guarda al salir la pantalla, al rotar o al cambiar el modo ssorcuros
        String texto = etPresupuesto.getText().toString().trim();
        try {
            float valor = texto.isEmpty() ? 0f : Float.parseFloat(texto);
            if(valor>= 0) prefs.setPresupuesto(valor);
        }catch (NumberFormatException e){
            Toast.makeText(this, "Presupuesto inválido", Toast.LENGTH_SHORT).show();

        }
    }

    // Almanecaniento
    private void configurarAlmacenamiento(){
        RadioGroup rg = findViewById(R.id.rgAlmacenamiento);
        rg.check("archivo".equals(prefs.getStorageMode()) ? R.id.rbArchivo : R.id.rbSqlite);
        rg.setOnCheckedChangeListener((grupo, idMarcado) ->{
            prefs.setStorageMode(idMarcado == R.id.rbArchivo ? "archivo" : "sqlite");
            Toast.makeText(this, "Almacenamiento cambiado", Toast.LENGTH_SHORT).show();
        });
    }

    //CUENTA
    private void configurarCuenta(){
        TextView txtNombre = findViewById(R.id.txtNombreUsuario);
        txtNombre.setText(prefs.getUserName());

        TextView txtCorreo = findViewById(R.id.txtCorreoUsuario);
        txtCorreo.setText(prefs.getUserEmail());

        findViewById(R.id.btnCerrarSesion).setOnClickListener(v -> {
            prefs.cerrarSesion();
            Intent intent = new Intent(this, LoginActivity.class);
            //borra el historial con "atras", ya no se regresa a la app sin sesion
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    // NOTIFICACIONES (Integrante 4)
    private void configurarNotificaciones() {
        Switch swRecordatorio = findViewById(R.id.swRecordatorio);
        View layoutHora = findViewById(R.id.layoutHora);
        TextView txtHora = findViewById(R.id.txtHora);
        Switch swAlertaPresupuesto = findViewById(R.id.swAlertaPresupuesto);

        boolean recordatorioActivo = prefs.isRecordatorioActivo();
        swRecordatorio.setChecked(recordatorioActivo);
        txtHora.setText(prefs.getHoraRecordatorio());
        actualizarEstadoHora(layoutHora, recordatorioActivo);

        swRecordatorio.setOnCheckedChangeListener((boton, activado) -> {
            prefs.setRecordatorioActivo(activado);
            actualizarEstadoHora(layoutHora, activado);

            if (activado) {
                pedirPermisoNotificacionesSiHaceFalta();
                String hora = prefs.getHoraRecordatorio();
                ReminderScheduler.programarRecordatorio(this, hora);
                Toast.makeText(this, getString(R.string.msg_recordatorio_activado, hora), Toast.LENGTH_SHORT).show();
            } else {
                ReminderScheduler.cancelarRecordatorio(this);
                Toast.makeText(this, R.string.msg_recordatorio_desactivado, Toast.LENGTH_SHORT).show();
            }
        });

        layoutHora.setOnClickListener(v -> {
            int[] horaActual = parsearHora(prefs.getHoraRecordatorio());
            TimePickerDialog dialog = new TimePickerDialog(
                    this,
                    (view, horaSeleccionada, minutoSeleccionado) -> {
                        String nuevaHora = String.format(Locale.US, "%02d:%02d", horaSeleccionada, minutoSeleccionado);
                        prefs.setHoraRecordatorio(nuevaHora);
                        txtHora.setText(nuevaHora);
                        if (prefs.isRecordatorioActivo()) {
                            ReminderScheduler.programarRecordatorio(this, nuevaHora);
                        }
                        Toast.makeText(this, getString(R.string.msg_hora_recordatorio, nuevaHora), Toast.LENGTH_SHORT).show();
                    },
                    horaActual[0],
                    horaActual[1],
                    true
            );
            dialog.show();
        });

        swAlertaPresupuesto.setChecked(prefs.isAlertaActiva());
        swAlertaPresupuesto.setOnCheckedChangeListener((boton, activado) -> {
            prefs.setAlertaActiva(activado);
            if (activado) {
                pedirPermisoNotificacionesSiHaceFalta();
            }
        });
    }

    private void actualizarEstadoHora(View layoutHora, boolean activo) {
        layoutHora.setEnabled(activo);
        layoutHora.setAlpha(activo ? 1.0f : 0.5f);
    }

    private void pedirPermisoNotificacionesSiHaceFalta() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && !NotificationHelper.tienePermisoNotificaciones(this)) {
            launcherPermisoNotificaciones.launch(Manifest.permission.POST_NOTIFICATIONS);
        }
    }

    private int[] parsearHora(String horaHHmm) {
        if (horaHHmm != null && horaHHmm.contains(":")) {
            String[] partes = horaHHmm.split(":");
            if (partes.length == 2) {
                try {
                    int hora = Integer.parseInt(partes[0].trim());
                    int minuto = Integer.parseInt(partes[1].trim());
                    if (hora >= 0 && hora <= 23 && minuto >= 0 && minuto <= 59) {
                        return new int[]{hora, minuto};
                    }
                } catch (NumberFormatException ignored) {
                    // Si el valor guardado no es válido, usa 20:00
                }
            }
        }
        return new int[]{20, 0};
    }
}
