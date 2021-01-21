package com.example.proyectovinoteca;

public class Articulo {
    private String nombre, foto;
    private float valoracion;

    public Articulo(String nombre, String foto, float valoracion) {
        this.nombre = nombre;
        this.foto = foto;
        this.valoracion = valoracion;
    }

    public Articulo() {
    }

    public float getValoracion() {
        return valoracion;
    }

    public void setValoracion(float valoracion) {
        this.valoracion = valoracion;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public String toString() {
        return "Articulo{" +
                "nombre='" + nombre + '\'' +
                ", foto='" + foto + '\'' +
                ", valoracion=" + valoracion +
                '}';
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
