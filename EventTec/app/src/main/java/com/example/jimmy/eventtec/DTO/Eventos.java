package com.example.jimmy.eventtec.DTO;



import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by Jimmy on 15/05/2016.
 */
public class Eventos implements Serializable {


    public String titulo ;
    public String descripcion ;
    public String fecha ;
    public String imagen ;
    public String remitente ;
    public String borrable ;
    public String mensaje_ID ;
    public String visto;

    private Bitmap auxiliar;

    public Eventos(String titulo, String descripcion, String fecha, String imagen, String remitente, String borrable, String mensaje_ID, String visto) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.imagen=imagen;
        this.remitente = remitente;
        this.borrable = borrable;
        this.mensaje_ID = mensaje_ID;
        this.visto = visto;
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

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getImagen() {
        return imagen;
    }

    public void seStringn(String imagen) {
        this.imagen = imagen;
    }

    public String getRemitente() {
        return remitente;
    }

    public void setRemitente(String remitente) {
        this.remitente = remitente;
    }

    public String getBorrable() {
        return borrable;
    }

    public void setBorrable(String borrable) {
        this.borrable = borrable;
    }

    public String getMensaje_ID() {
        return mensaje_ID;
    }

    public void setMensaje_ID(String mensaje_ID) {
        this.mensaje_ID = mensaje_ID;
    }

    public String getVisto() {
        return visto;
    }
    public void setVisto(String visto) {
        this.visto = visto;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public Bitmap getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(Bitmap auxiliar) {
        this.auxiliar = auxiliar;
    }
}
