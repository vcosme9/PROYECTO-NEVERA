package com.example.proyectovinoteca;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Vino {
    private String nombre;
    private float valoracion;
    private String descripcion;
    private String fecha;
    private int imagenId;

    public Vino(String nombre, float valoracion, String descripcion, int imagenId) {
        this.nombre = nombre;
        this.valoracion = valoracion;
        this.descripcion = descripcion;
        this.imagenId=imagenId;
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        fecha=formatter.format(date);
    }

    public Vino() {
        nombre="";
        valoracion=0;
        descripcion="";
        imagenId=R.drawable.productos;
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        fecha=formatter.format(date);
    }

    public int getImagenId() {
        return imagenId;
    }

    public void setImagenId(int imagenId) {
        this.imagenId = imagenId;
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

    @Override
    public String toString() {
        return "Vino{" +
                "nombre='" + nombre + '\'' +
                ", valoracion=" + valoracion +
                ", descripcion='" + descripcion + '\'' +
                ", fecha='" + fecha + '\'' +
                '}';
    }
}
