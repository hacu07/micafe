package com.hacu.micafe.Modelo;

import java.io.Serializable;

/**
 * Created by hacu1 on 27/11/2018.
 */

public class Pesada implements Serializable {
    private int id;
    private int kilos;
    private String fecha;
    private int idrecolector;
    private int idoferta;

    public Pesada() {
    }

    public Pesada(int id, int kilos, String fecha, int idrecolector, int idoferta) {
        this.id = id;
        this.kilos = kilos;
        this.fecha = fecha;
        this.idrecolector = idrecolector;
        this.idoferta = idoferta;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getKilos() {
        return kilos;
    }

    public void setKilos(int kilos) {
        this.kilos = kilos;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public int getIdrecolector() {
        return idrecolector;
    }

    public void setIdrecolector(int idrecolector) {
        this.idrecolector = idrecolector;
    }

    public int getIdoferta() {
        return idoferta;
    }

    public void setIdoferta(int idoferta) {
        this.idoferta = idoferta;
    }
}
