package com.example.controlgastos.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.example.controlgastos.receivers.ReminderReceiver;

import java.util.Calendar;

/**
 * Programa y cancela el recordatorio diario usando AlarmManager.setInexactRepeating().
 */
public final class ReminderScheduler {

    private static final int REQUEST_CODE_RECORDATORIO = 2001;
    private static final int HORA_DEFECTO = 20;
    private static final int MINUTO_DEFECTO = 0;

    private ReminderScheduler() {
    }

    /**
     * Programa una alarma diaria a la hora indicada en formato "HH:mm".
     */
    public static void programarRecordatorio(Context context, String horaHHmm) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) {
            return;
        }

        int[] horaMinuto = parsearHora(horaHHmm);
        Calendar calendario = Calendar.getInstance();
        calendario.set(Calendar.HOUR_OF_DAY, horaMinuto[0]);
        calendario.set(Calendar.MINUTE, horaMinuto[1]);
        calendario.set(Calendar.SECOND, 0);
        calendario.set(Calendar.MILLISECOND, 0);

        // Si la hora de hoy ya pasó, se programa para mañana
        if (calendario.getTimeInMillis() <= System.currentTimeMillis()) {
            calendario.add(Calendar.DAY_OF_MONTH, 1);
        }

        PendingIntent pendingIntent = crearPendingIntent(context, PendingIntent.FLAG_UPDATE_CURRENT);
        if (pendingIntent != null) {
            alarmManager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendario.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
            );
        }
    }

    /**
     * Cancela el recordatorio diario si existe.
     */
    public static void cancelarRecordatorio(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = crearPendingIntent(context, PendingIntent.FLAG_NO_CREATE);
        if (alarmManager != null && pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }

    /**
     * Lee las preferencias guardadas y reprograma el recordatorio si el usuario tiene sesión
     * activa y el recordatorio encendido; en caso contrario lo cancela.
     */
    public static void reprogramarSiActivo(Context context) {
        PrefsManager prefs = new PrefsManager(context);
        if (prefs.isLoggedIn() && prefs.isRecordatorioActivo()) {
            programarRecordatorio(context, prefs.getHoraRecordatorio());
        } else {
            cancelarRecordatorio(context);
        }
    }

    private static PendingIntent crearPendingIntent(Context context, int flagBase) {
        Intent intent = new Intent(context, ReminderReceiver.class);
        return PendingIntent.getBroadcast(
                context,
                REQUEST_CODE_RECORDATORIO,
                intent,
                flagBase | PendingIntent.FLAG_IMMUTABLE
        );
    }

    private static int[] parsearHora(String horaHHmm) {
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
                    // Se usa la hora por defecto si el texto guardado no es válido
                }
            }
        }
        return new int[]{HORA_DEFECTO, MINUTO_DEFECTO};
    }
}
