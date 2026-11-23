package com.example.controlgastos.utils;

import java.security.MessageDigest;

/**
 * Convierte una contraseña en un "hash" para no guardarla en texto plano.
 *
 * Hash = función de un solo sentido: de "abc123" se obtiene el hash,
 * pero del hash no se puede recuperar "abc123". (Cifrar sí se puede revertir; hashear no.)
 *
 * Para saber si la contraseña es correcta, se hashea lo que el usuario escribe
 * y se compara con el hash guardado.
 */
public class HashUtil {

    /**
     * Hash SHA-256 de (correo + contraseña).
     *como cada usuario tiene un correo distinto, dos usuarios
     * con la misma contraseña terminan con hashes diferentes.
     */
    public static String hash(String email, String contrasena) {
        try {
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            byte[] bytes = sha.digest((email + contrasena).getBytes());

            // Cada byte se convierte a 2 caracteres hexadecimales (ej. 255 -> "ff").
            StringBuilder texto = new StringBuilder();
            for (byte b : bytes) {
                texto.append(String.format("%02x", b));
            }
            return texto.toString();
        } catch (Exception e) {
            throw new RuntimeException(e); // SHA-256 existe siempre en Android
        }
    }
}
