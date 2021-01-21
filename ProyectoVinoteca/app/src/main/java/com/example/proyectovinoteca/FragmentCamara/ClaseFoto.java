package com.example.proyectovinoteca.FragmentCamara;

public class ClaseFoto {

    private long fecha;
    private String foto;

    public ClaseFoto() {
    }

    public ClaseFoto(long fecha, String foto) {
        this.fecha = fecha;
        this.foto = foto;
    }

    public long getFecha() {
        return fecha;
    }

    public void setFecha(long fecha) {
        this.fecha = fecha;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}