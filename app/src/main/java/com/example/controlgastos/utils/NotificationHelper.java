package com.example.controlgastos.utils;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.controlgastos.R;
import com.example.controlgastos.activities.MainActivity;
import com.example.controlgastos.repository.GastoRepository;
import com.example.controlgastos.repository.RepositoryFactory;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Utilidad central para notificaciones locales: canal, envío y alerta de presupuesto.
 */
public final class NotificationHelper {

    public static final String CHANNEL_ID = "control_gastos_notificaciones";
    public static final int NOTIF_ID_PRESUPUESTO = 1001;
    public static final int NOTIF_ID_RECORDATORIO = 1002;

    /** Porcentaje del presupuesto a partir del cual se emite la alerta (80%). */
    public static final double UMBRAL_ALERTA = 0.80;

    private NotificationHelper() {
    }

    /**
     * Crea el canal de notificaciones (requerido en Android 8.0 / API 26+).
     * Es seguro llamarlo varias veces: Android ignora la creación si el canal ya existe.
     */
    public static void crearCanal(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }

        String nombre = context.getString(R.string.notif_canal_nombre);
        String descripcion = context.getString(R.string.notif_canal_desc);
        NotificationChannel canal = new NotificationChannel(
                CHANNEL_ID,
                nombre,
                NotificationManager.IMPORTANCE_DEFAULT
        );
        canal.setDescription(descripcion);

        NotificationManager manager = context.getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.createNotificationChannel(canal);
        }
    }

    /**
     * Verifica si la app tiene permiso para mostrar notificaciones.
     * En Android 13+ (API 33) se requiere POST_NOTIFICATIONS en tiempo de ejecución.
     */
    public static boolean tienePermisoNotificaciones(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return true;
        }
        return ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Construye y muestra una notificación local que abre MainActivity al tocarla.
     */
    public static void mostrarNotificacion(Context context, int notificationId, String titulo, String mensaje) {
        crearCanal(context);

        if (!tienePermisoNotificaciones(context)) {
            return;
        }

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // FLAG_IMMUTABLE es obligatorio desde Android 12 (API 31)
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                notificationId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notificacion)
                .setContentTitle(titulo)
                .setContentText(mensaje)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(mensaje))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat.from(context).notify(notificationId, builder.build());
    }

    /**
     * Revisa si el gasto del mes actual del usuario alcanzó el umbral de alerta (80% del presupuesto)
     * y emite una notificación si la alerta está activa en preferencias.
     * Se llama desde GastoFormActivity al guardar un gasto.
     */
    public static void verificarPresupuesto(Context context, long userId) {
        if (userId <= 0) {
            return;
        }

        PrefsManager prefs = new PrefsManager(context);
        if (!prefs.isAlertaActiva()) {
            return;
        }

        float presupuesto = prefs.getPresupuesto();
        if (presupuesto <= 0f) {
            return;
        }

        String[] rango = rangoMesActual();
        GastoRepository repo = RepositoryFactory.get(context);
        double totalMes = repo.totalPeriodo(userId, rango[0], rango[1]);

        double proporcion = totalMes / presupuesto;
        if (proporcion < UMBRAL_ALERTA) {
            return;
        }

        String totalTexto = formatearMoneda(totalMes);
        String presupuestoTexto = formatearMoneda(presupuesto);

        if (proporcion >= 1.0) {
            String titulo = context.getString(R.string.notif_alerta_excedido_titulo);
            String mensaje = context.getString(R.string.notif_alerta_excedido_mensaje, totalTexto, presupuestoTexto);
            mostrarNotificacion(context, NOTIF_ID_PRESUPUESTO, titulo, mensaje);
        } else {
            int porcentaje = (int) Math.round(proporcion * 100.0);
            String titulo = context.getString(R.string.notif_alerta_titulo);
            String mensaje = context.getString(R.string.notif_alerta_mensaje, porcentaje, totalTexto, presupuestoTexto);
            mostrarNotificacion(context, NOTIF_ID_PRESUPUESTO, titulo, mensaje);
        }
    }

    /** Regresa [primerDiaMes, ultimoDiaMes] en formato yyyy-MM-dd. */
    private static String[] rangoMesActual() {
        Calendar cal = Calendar.getInstance();
        int anio = cal.get(Calendar.YEAR);
        int mes = cal.get(Calendar.MONTH) + 1;
        int ultimoDia = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        String desde = String.format(Locale.US, "%04d-%02d-01", anio, mes);
        String hasta = String.format(Locale.US, "%04d-%02d-%02d", anio, mes, ultimoDia);
        return new String[]{desde, hasta};
    }

    private static String formatearMoneda(double monto) {
        NumberFormat formato = NumberFormat.getCurrencyInstance(Locale.US);
        return formato.format(monto);
    }
}
