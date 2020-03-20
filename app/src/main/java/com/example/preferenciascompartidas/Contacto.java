package com.example.preferenciascompartidas;

import android.content.Context;

public class Contacto {
    private String nombre;
    private String numero;

    public Contacto(String nombre, String numero) {
        this.nombre = nombre;
        this.numero = numero;
    }

    public Contacto(){

    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }
}
