package com.hacu.micafe.Modelo;

import java.io.Serializable;

/**
 * Created by hacu1 on 24/11/2018.
 */

public class Experiencia implements Serializable {
    private int id;
    private int idRecolector;
    private String empresa;
    private String cargo;
    private String funciones;
    private int tiempo; //Meses de trabajo
    private String supervisor;
    private String contactosupervisor;

    public Experiencia() {
    }

    public Experiencia(int id, int idRecolector, String empresa, String cargo, String funciones, int tiempo, String supervisor, String contactosupervisor) {
        this.id = id;
        this.idRecolector = idRecolector;
        this.empresa = empresa;
        this.cargo = cargo;
        this.funciones = funciones;
        this.tiempo = tiempo;
        this.supervisor = supervisor;
        this.contactosupervisor = contactosupervisor;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdRecolector() {
        return idRecolector;
    }

    public void setIdRecolector(int idRecolector) {
        this.idRecolector = idRecolector;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getFunciones() {
        return funciones;
    }

    public void setFunciones(String funciones) {
        this.funciones = funciones;
    }

    public int getTiempo() {
        return tiempo;
    }

    public void setTiempo(int tiempo) {
        this.tiempo = tiempo;
    }

    public String getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(String supervisor) {
        this.supervisor = supervisor;
    }

    public String getContactosupervisor() {
        return contactosupervisor;
    }

    public void setContactosupervisor(String contactosupervisor) {
        this.contactosupervisor = contactosupervisor;
    }
}
