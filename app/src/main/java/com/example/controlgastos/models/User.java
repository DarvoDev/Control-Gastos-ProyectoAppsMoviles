package com.example.controlgastos.models;

/** Usuario de la app. */
public class User {

    public long id;
    public String nombre, email;

    public User(long id, String nombre, String email) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
    }
}
