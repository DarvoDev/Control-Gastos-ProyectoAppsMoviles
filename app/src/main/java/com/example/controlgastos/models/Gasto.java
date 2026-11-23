package com.example.controlgastos.models;

/** Un gasto registrado por un usuario (una fila de la tabla gastos). */
public class Gasto {

    public long id;
    public long userId;          // dueño del gasto (FK a users.id)
    public String categoria;
    public double monto;
    public String descripcion;
    public String fecha;         // formato yyyy-MM-dd (así se ordena y compara bien como texto)

    public Gasto() {
    }

    public Gasto(long id, long userId, String categoria, double monto, String descripcion, String fecha) {
        this.id = id;
        this.userId = userId;
        this.categoria = categoria;
        this.monto = monto;
        this.descripcion = descripcion;
        this.fecha = fecha;
    }
}
