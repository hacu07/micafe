package com.hacu.micafe.Modelo;

import java.io.Serializable;

/**
 * Created by hacu1 on 06/11/2018.
 */

public class Usuarios implements Serializable {
    private int id;
    private String nombre;
    private String correo;
    private String contrasenia;
    private String tipodocumento;
    private String cedula;
    private String celular;
    private String fechanacimiento;
    private String direccion;
    private String departamento;
    private String municipio;
    private String urlimagen;
    private int idrol;

    public Usuarios() {
    }

    public Usuarios(int id, String nombre, String correo, String contrasenia,String tipodocumento, String cedula, String celular, String fechanacimiento, String direccion, String departamento, String municipio, String urlimagen, int idrol) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.contrasenia = contrasenia;
        this.tipodocumento = tipodocumento;
        this.cedula = cedula;
        this.celular = celular;
        this.fechanacimiento = fechanacimiento;
        this.direccion = direccion;
        this.departamento = departamento;
        this.municipio = municipio;
        this.urlimagen = urlimagen;
        this.idrol = idrol;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }

    public String getTipodocumento() {
        return tipodocumento;
    }

    public void setTipodocumento(String tipodocumento) {
        this.tipodocumento = tipodocumento;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getFechanacimiento() {
        return fechanacimiento;
    }

    public void setFechanacimiento(String fechanacimiento) {
        this.fechanacimiento = fechanacimiento;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
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

    public String getUrlimagen() {
        return urlimagen;
    }

    public void setUrlimagen(String urlimagen) {
        this.urlimagen = urlimagen;
    }

    public int getIdrol() {
        return idrol;
    }

    public void setIdrol(int idrol) {
        this.idrol = idrol;
    }
}
