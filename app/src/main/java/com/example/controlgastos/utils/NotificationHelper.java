package com.example.controlgastos.utils;

import android.content.Context;

/**
 * Utilidad central para notificaciones locales: canal, envío y alerta de presupuesto.
 *
 * Esqueleto inicial (Fase 0) con los contratos que usan otros módulos:
 * - {@link #verificarPresupuesto(Context, long)}: llamada desde GastoFormActivity al guardar un gasto.
 * - {@link #crearCanal(Context)}: llamada desde App.onCreate().
 *
 * La implementación completa se agregará en la Fase 1.
 */
public final class NotificationHelper {

    public static final String CHANNEL_ID = "control_gastos_notificaciones";

    private NotificationHelper() {
    }

    /**
     * Crea el canal de notificaciones (requerido en Android 8.0 / API 26+).
     */
    public static void crearCanal(Context context) {
        // TODO (Integrante 4): crear NotificationChannel en API 26+.
    }

    /**
     * Construye y muestra una notificación local que abre MainActivity al tocarla.
     */
    public static void mostrarNotificacion(Context context, int notificationId, String titulo, String mensaje) {
        // TODO (Integrante 4): verificar permiso POST_NOTIFICATIONS y mostrar notificación con PendingIntent.
    }

    /**
     * Revisa si el gasto del mes del usuario alcanzó el umbral de alerta (80% del presupuesto)
     * y emite una notificación si la alerta está activa en preferencias.
     */
    public static void verificarPresupuesto(Context context, long userId) {
        // TODO (Integrante 4): consultar PrefsManager y RepositoryFactory.get(context).totalPeriodo(...).
    }
}
