package com.example.controlgastos.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.controlgastos.models.User;
import com.example.controlgastos.utils.HashUtil;

public class UserDao {

    private final DBHelper helper;

    public UserDao(Context context) {
        helper = new DBHelper(context);
    }


    //regresa el id del usuario, o -1 si ese correo ya está registrado
    //          (la columna email es UNIQUE, y SQLite rechaza el duplicado).
    public long registrar(String nombre, String email, String contrasena) {
        email = email.trim().toLowerCase(); // "A@x.com" y "a@x.com" son el mismo correo

        ContentValues fila = new ContentValues(); // pares columna -> valor
        fila.put("nombre", nombre);
        fila.put("email", email);
        fila.put("password_hash", HashUtil.hash(email, contrasena));

        SQLiteDatabase db = helper.getWritableDatabase(); // aquí se crea el archivo la 1a vez
        return db.insert("users", null, fila);
    }

    /*
     Busca un usuario con ese correo Y ese hash de contraseña.
     regresa el usuario si las credenciales son correctas; null si no.
     */
    public User login(String email, String contrasena) {
        email = email.trim().toLowerCase();

        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT id, nombre, email FROM users WHERE email = ? AND password_hash = ?",
                new String[]{email, HashUtil.hash(email, contrasena)});

        User usuario = null;
        if (c.moveToFirst()) { // si hubo una fila, las credenciales son correctas
            usuario = new User(c.getLong(0), c.getString(1), c.getString(2));
        }
        c.close();
        return usuario;
    }
}
