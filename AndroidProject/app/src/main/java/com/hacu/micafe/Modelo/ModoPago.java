package com.hacu.micafe.Modelo;

/**
 * Created by hacu1 on 14/11/2018.
 */

public class ModoPago {
    private int id;
    private String modo;

    public ModoPago() {
    }

    public ModoPago(int id, String modo) {
        this.id = id;
        this.modo = modo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getModo() {
        return modo;
    }

    public void setModo(String modo) {
        this.modo = modo;
    }
}
