package com.hacu.micafe.Modelo;

import java.io.Serializable;

/**
 * Created by hacu1 on 29/11/2018.
 */

public class Costo implements Serializable {
    private int id;
    private int idOferta;
    private String titulo;
    private String descripcion;
    private int valor;
    private String fecha;

    public Costo() {
    }

    public Costo(int id, int idOferta, String titulo, String descripcion, int valor, String fecha) {
        this.id = id;
        this.idOferta = idOferta;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.valor = valor;
        this.fecha = fecha;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdOferta() {
        return idOferta;
    }

    public void setIdOferta(int idOferta) {
        this.idOferta = idOferta;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getValor() {
        return valor;
    }

    public void setValor(int valor) {
        this.valor = valor;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
