package com.hacu.micafe.Modelo;

/**
 * Created by hacu1 on 14/11/2018.
 */

public class Finca {
    private int id;
    private int idadministrador;
    private String nombre;
    private String departamento;
    private String municipio;
    private String corregimiento;
    private String vereda;
    private int hectareas;
    private String telefono;
    private String latitud;
    private String longitud;

    public Finca() {
    }

    public Finca(int id, int idadministrador, String nombre, String departamento, String municipio, String corregimiento, String vereda, int hectareas, String telefono, String latitud, String longitud) {
        this.id = id;
        this.idadministrador = idadministrador;
        this.nombre = nombre;
        this.departamento = departamento;
        this.municipio = municipio;
        this.corregimiento = corregimiento;
        this.vereda = vereda;
        this.hectareas = hectareas;
        this.telefono = telefono;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdadministrador() {
        return idadministrador;
    }

    public void setIdadministrador(int idadministrador) {
        this.idadministrador = idadministrador;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getCorregimiento() {
        return corregimiento;
    }

    public void setCorregimiento(String corregimiento) {
        this.corregimiento = corregimiento;
    }

    public String getVereda() {
        return vereda;
    }

    public void setVereda(String vereda) {
        this.vereda = vereda;
    }

    public int getHectareas() {
        return hectareas;
    }

    public void setHectareas(int hectareas) {
        this.hectareas = hectareas;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }
}
