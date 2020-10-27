package com.example.vicoscor.things;

import android.app.Activity;

public class Sensores {

    private String fecha;
    private String palabras;
    private double numeros;


    public Sensores(String palabras,String fecha) {

        this.palabras = palabras;
        this.fecha = fecha;
    }

    public Sensores(double numeros,String fecha) {

        this.numeros = numeros;
        this.fecha = fecha;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getPalabras() {
        return palabras;
    }

    public void setPalabras(String palabras) {
        this.palabras = palabras;
    }

    public double getNumeros() {
        return numeros;
    }

    public void setNumeros(double numeros) {
        this.numeros = numeros;
    }
}
