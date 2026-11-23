package com.example.controlgastos.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.controlgastos.utils.ReminderScheduler;

/**
 * Reprograma el recordatorio diario al encender el dispositivo, ya que Android
 * borra las alarmas de AlarmManager cuando el teléfono se apaga o reinicia.
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || !Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            return;
        }
        ReminderScheduler.reprogramarSiActivo(context);
    }
}
