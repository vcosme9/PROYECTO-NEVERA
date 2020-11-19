package com.example.vicoscor.androidthings;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Skeleton of an Android Things activity.
 * <p>
 * Android Things peripheral APIs are accessible through the PeripheralManager
 * For example, the snippet below will open a GPIO pin and set it to HIGH:
 * <p>
 * PeripheralManager manager = PeripheralManager.getInstance();
 * try {
 * Gpio gpio = manager.openGpio("BCM6");
 * gpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
 * gpio.setValue(true);
 * } catch (IOException e) {
 * Log.e(TAG, "Unable to access GPIO");
 * }
 * <p>
 * You can find additional examples on GitHub: https://github.com/androidthings
 */
public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();
    public String producto="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd-MM-yyyy", Locale.getDefault());
        Date date = new Date();
        String fecha = dateFormat.format(date);

        Log.i(TAG, "Lista de UART disponibles: " + ArduinoUart.disponibles());
        ArduinoUart uart = new ArduinoUart("MINIUART", 115200);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

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

        //---RECIBIR SENSOR RFID POR UART
        Log.d(TAG, "Mandado a Arduino: I");
        uart.escribir("I");
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            Log.w(TAG, "Error en sleep()", e);
        }
        String id = uart.leer();
        Log.d(TAG, "Recibido de Arduino: "+id);

        //---SUBIR DATOS DE LOS SENSORES A FIREBASE
        Map<String, Object> sensorMagnetico = new HashMap<>();
        sensorMagnetico.put("Magnetico", magnetico);
        sensorMagnetico.put("Fecha", fecha);
        db.collection("SENSORES").document("Sensor_Magnetico").collection("Magnetico").add(sensorMagnetico);

        Map<String, Object> sensorID = new HashMap<>();
        sensorID.put("Vino", id);
        sensorID.put("Fecha", fecha);
        db.collection("SENSORES").document("Sensor_RFID").collection("ID").add(sensorID);

    }
}