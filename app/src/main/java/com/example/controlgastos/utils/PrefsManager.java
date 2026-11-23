package com.example.controlgastos.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefsManager {
    private final SharedPreferences prefs;

    public PrefsManager(Context context) {
        prefs = context.getSharedPreferences("control_gastos_prefs", Context.MODE_PRIVATE);
    }
    // ---- Sesión ----
    public void guardarSesion(long userId, String nombre, String email) {
        prefs.edit()
                .putBoolean("logged_in", true)
                .putLong("user_id", userId)
                .putString("user_name", nombre)
                .putString("user_email", email)
                .apply();
    }
    //---------------------------valores por defecto--------------------------
    public boolean isLoggedIn()   { return prefs.getBoolean("logged_in", false); }
    public long getUserId()       { return prefs.getLong("user_id", -1); }
    public String getUserName()   { return prefs.getString("user_name", ""); }
    public String getUserEmail()  { return prefs.getString("user_email", ""); }

    public void cerrarSesion() {
        prefs.edit().remove("logged_in").remove("user_id").remove("user_name").remove("user_email").apply();
    }

    public boolean isDarkMode()         { return prefs.getBoolean("dark_mode", false); }
    public void setDarkMode(boolean b)  { prefs.edit().putBoolean("dark_mode", b).apply(); }

    // ---- Presupuesto mensual (0 = sin presupuesto definido) ----
    public float getPresupuesto()          { return prefs.getFloat("presupuesto_mensual", 0f); }
    public void setPresupuesto(float monto) { prefs.edit().putFloat("presupuesto_mensual", monto).apply(); }

    // ---- Almacenamiento: "sqlite" o "archivo" ----
    public String getStorageMode()         { return prefs.getString("storage_mode", "sqlite"); }
    public void setStorageMode(String m)   { prefs.edit().putString("storage_mode", m).apply(); }

    // ---- Notificaciones ----
    public boolean isRecordatorioActivo()        { return prefs.getBoolean("recordatorio_activo", false); }
    public void setRecordatorioActivo(boolean b) { prefs.edit().putBoolean("recordatorio_activo", b).apply(); }

    // Hora del recordatorio en formato "HH:mm"
    public String getHoraRecordatorio()          { return prefs.getString("recordatorio_hora", "20:00"); }
    public void setHoraRecordatorio(String hora) { prefs.edit().putString("recordatorio_hora", hora).apply(); }

    public boolean isAlertaActiva()              { return prefs.getBoolean("alerta_presupuesto", true); }
    public void setAlertaActiva(boolean b)       { prefs.edit().putBoolean("alerta_presupuesto", b).apply(); }
}
