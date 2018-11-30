package com.hacu.micafe.Modelo;

import java.io.Serializable;

/**
 * Created by hacu1 on 30/11/2018.
 */

public class Comentario implements Serializable {
    private int id;
    private int idAdmon;
    private int idRecolector;
    private String comentario;
    private int calificacion;
    private String nombreAdmin;
    private String celular;

    public Comentario() {
    }

    public Comentario(int id, int idAdmon, int idRecolector, String comentario, int calificacion, String nombreAdmin, String celular) {
        this.id = id;
        this.idAdmon = idAdmon;
        this.idRecolector = idRecolector;
        this.comentario = comentario;
        this.calificacion = calificacion;
        this.nombreAdmin = nombreAdmin;
        this.celular = celular;
    }

    public String getNombreAdmin() {
        return nombreAdmin;
    }

    public void setNombreAdmin(String nombreAdmin) {
        this.nombreAdmin = nombreAdmin;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdAdmon() {
        return idAdmon;
    }

    public void setIdAdmon(int idAdmon) {
        this.idAdmon = idAdmon;
    }

    public int getIdRecolector() {
        return idRecolector;
    }

    public void setIdRecolector(int idRecolector) {
        this.idRecolector = idRecolector;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public int getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(int calificacion) {
        this.calificacion = calificacion;
    }
}
