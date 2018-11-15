package com.hacu.micafe.Modelo;

/**
 * Created by hacu1 on 14/11/2018.
 */

public class Oferta {
    private int id;
    private int idAdministrador;
    private int idFinca;
    private int idModoPago;
    private int valorPago;
    private int vacantes;
    private int diasTrabajo;
    private String planta;
    private String servicios;
    private int ventaTotal;
    private String fechacreacion;

    public Oferta() {
    }

    public Oferta(int id, int idAdministrador, int idFinca, int idModoPago, int valorPago, int vacantes, int diasTrabajo, String planta, String servicios, int ventaTotal, String fechacreacion) {
        this.id = id;
        this.idAdministrador = idAdministrador;
        this.idFinca = idFinca;
        this.idModoPago = idModoPago;
        this.valorPago = valorPago;
        this.vacantes = vacantes;
        this.diasTrabajo = diasTrabajo;
        this.planta = planta;
        this.servicios = servicios;
        this.ventaTotal = ventaTotal;
        this.fechacreacion = fechacreacion;
    }

    public int getIdAdministrador() {
        return idAdministrador;
    }

    public void setIdAdministrador(int idAdministrador) {
        this.idAdministrador = idAdministrador;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdFinca() {
        return idFinca;
    }

    public void setIdFinca(int idFinca) {
        this.idFinca = idFinca;
    }

    public int getIdModoPago() {
        return idModoPago;
    }

    public void setIdModoPago(int idModoPago) {
        this.idModoPago = idModoPago;
    }

    public int getValorPago() {
        return valorPago;
    }

    public void setValorPago(int valorPago) {
        this.valorPago = valorPago;
    }

    public int getVacantes() {
        return vacantes;
    }

    public void setVacantes(int vacantes) {
        this.vacantes = vacantes;
    }

    public int getDiasTrabajo() {
        return diasTrabajo;
    }

    public void setDiasTrabajo(int diasTrabajo) {
        this.diasTrabajo = diasTrabajo;
    }

    public String getPlanta() {
        return planta;
    }

    public void setPlanta(String planta) {
        this.planta = planta;
    }

    public String getServicios() {
        return servicios;
    }

    public void setServicios(String servicios) {
        this.servicios = servicios;
    }

    public int getVentaTotal() {
        return ventaTotal;
    }

    public void setVentaTotal(int ventaTotal) {
        this.ventaTotal = ventaTotal;
    }

    public String getFechacreacion() {
        return fechacreacion;
    }

    public void setFechacreacion(String fechacreacion) {
        this.fechacreacion = fechacreacion;
    }
}
