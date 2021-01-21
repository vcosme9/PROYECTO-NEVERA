package com.example.proyectovinoteca.comentarios;

import android.view.View;
import android.widget.RatingBar;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ClaseComentario {
    private String nombre;
    private float valoracion;
    private String comentario;
    private String fecha;

    public ClaseComentario(String nombre, float valoracion, String comentario) {
        this.nombre = nombre;
        this.valoracion = valoracion;
        this.comentario = comentario;
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
        fecha=formatter.format(date);
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public float getValoracion() {
        return valoracion;
    }

    public void setValoracion(float valoracion) {
        this.valoracion = valoracion;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
