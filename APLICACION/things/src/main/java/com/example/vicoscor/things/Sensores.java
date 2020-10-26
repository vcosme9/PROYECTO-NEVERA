package com.example.vicoscor.things;

import android.app.Activity;

public class Sensores  extends Activity {

    private String Magnetico;
    private String id;
    private double humedad;
    private double temperatura;
    private String camara;

    public Sensores(String magnetico, String id) {
        Magnetico = magnetico;
        this.id = id;
    }

    public Sensores(double humedad, double temperatura) {
        this.humedad = humedad;
        this.temperatura = temperatura;
    }

    public String getMagnetico() {
        return Magnetico;
    }

    public void setMagnetico(String magnetico) {
        Magnetico = magnetico;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getHumedad() {
        return humedad;
    }

    public void setHumedad(double humedad) {
        this.humedad = humedad;
    }

    public double getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(double temperatura) {
        this.temperatura = temperatura;
    }
}
