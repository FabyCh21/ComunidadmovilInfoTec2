package com.example.jimmy.eventtec.DTO;

/**
 * Created by Josue on 6/4/2017.
 */
public class Departamento {
    private String codigoDep;

    public Departamento(String nombre) {
        this.codigoDep = nombre;
    }

    public String getCodigoDep() {
        return codigoDep;
    }

    public void setCodigoDep(String codigoDep) {
        this.codigoDep = codigoDep;
    }
}
