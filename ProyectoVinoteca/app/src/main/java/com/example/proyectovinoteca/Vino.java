package com.example.proyectovinoteca;


import com.example.proyectovinoteca.comentarios.ClaseComentario;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Vino {
    private String nombre, descripcion, fecha, tipo, foto;
    private float valoracion;



    public List<ClaseComentario> getComentarios() {
        return comentarios;
    }

    public void setComentarios(List<ClaseComentario> comentarios) {
        this.comentarios = comentarios;
    }

    private List<ClaseComentario> comentarios;
    public Vino(String nombre, float valoracion, String descripcion, String tipo,  String foto) {
        this.nombre = nombre;
        this.foto = foto;
        this.valoracion = valoracion;
        this.descripcion = descripcion;
        this.tipo=tipo;

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
        fecha=formatter.format(date);

    }
    public Vino(String nombre, float valoracion, String descripcion, String tipo, String foto, List<ClaseComentario> comentarios) {
        this.nombre = nombre;
        this.foto = foto;
        this.valoracion = valoracion;
        this.descripcion = descripcion;
        this.tipo=tipo;
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
        fecha=formatter.format(date);
        this.comentarios=new ArrayList<>();
        this.comentarios=comentarios;
    }
    public Vino() {
        nombre="";
        valoracion=0;
        descripcion="";
        tipo="Tinto";
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        fecha=formatter.format(date);
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

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
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
