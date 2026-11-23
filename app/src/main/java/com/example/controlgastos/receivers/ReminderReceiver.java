package com.example.controlgastos.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.controlgastos.R;
import com.example.controlgastos.utils.NotificationHelper;
import com.example.controlgastos.utils.PrefsManager;

/**
 * Recibe la alarma diaria programada por ReminderScheduler y muestra la notificación.
 */
public class ReminderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        PrefsManager prefs = new PrefsManager(context);
        if (!prefs.isLoggedIn() || !prefs.isRecordatorioActivo()) {
            return;
        }

        String titulo = context.getString(R.string.notif_recordatorio_titulo);
        String mensaje = context.getString(R.string.notif_recordatorio_mensaje);
        NotificationHelper.mostrarNotificacion(
                context,
                NotificationHelper.NOTIF_ID_RECORDATORIO,
                titulo,
                mensaje
        );
    }
}
