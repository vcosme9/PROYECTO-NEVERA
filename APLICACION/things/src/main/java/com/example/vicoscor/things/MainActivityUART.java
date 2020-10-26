package com.example.vicoscor.things;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;

public class MainActivityUART extends Activity
{

    private static final String TAG = MainActivityUART.class.getSimpleName();
    ArduinoUart uart;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Lista de UART disponibles: " + ArduinoUart.disponibles());
        uart = new ArduinoUart("UART0", 115200);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        Map<String, Object> dato = new HashMap<>();

        //---RECIBIR FECHA POR UART
        Log.d(TAG, "Mandado a Arduino: H");
        uart.escribir("H");
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            Log.w(TAG, "Error en sleep()", e);
        }
        String fecha = uart.leer();
        Log.d(TAG, "Recibido de Arduino: "+fecha);

        //---RECIBIR SENSOR MAGNETICO POR UART
        Log.d(TAG, "Mandado a Arduino: M");
        uart.escribir("M");
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            Log.w(TAG, "Error en sleep()", e);
        }
        String magnetico = uart.leer();
        Log.d(TAG, "Recibido de Arduino: "+magnetico);

        Sensores sensorMagnetico = new Sensores(magnetico,fecha);

        dato.put("Magnetico", sensorMagnetico);
        
        db.collection("SENSORES").document("Sensores").set(dato);
    }


    @Override protected void onDestroy() {
        super.onDestroy();
    }

}