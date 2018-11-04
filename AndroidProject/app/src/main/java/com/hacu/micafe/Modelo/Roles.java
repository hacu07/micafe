package com.hacu.micafe.Modelo;

import java.io.Serializable;

/**
 * Created by hacu1 on 04/11/2018.
 */

public class Roles implements Serializable {

    private Integer id;
    private String rol;

    public Roles() {
    }

    public Roles(Integer id, String rol) {
        this.id = id;
        this.rol = rol;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}
