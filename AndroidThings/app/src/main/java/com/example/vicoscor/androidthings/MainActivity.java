package com.example.vicoscor.androidthings;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.example.vicoscor.comun.Mqtt;
import com.google.firebase.firestore.FirebaseFirestore;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.example.vicoscor.comun.Mqtt.qos;
import static com.example.vicoscor.comun.Mqtt.topicRoot;

public class MainActivity extends Activity implements MqttCallback {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static MqttClient client = null;
    FirebaseFirestore db;
    String fecha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd-MM-yyyy", Locale.getDefault());
        Date date = new Date();
        fecha = dateFormat.format(date);

        Log.i(TAG, "Lista de UART disponibles: " + ArduinoUart.disponibles());
        ArduinoUart uart = new ArduinoUart("MINIUART", 115200);

        db = FirebaseFirestore.getInstance();

        //---RECIBIR SENSOR MAGNETICO POR UART
        Log.d(TAG, "Mandado a Arduino: M");
        uart.escribir("M");
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            Log.w(TAG, "Error en sleep()", e);
        }
        String magnetico = uart.leer();
        Log.d(TAG, "Recibido de Arduino: " + magnetico);

        //---RECIBIR SENSOR RFID POR UART
        Log.d(TAG, "Mandado a Arduino: I");
        uart.escribir("I");
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            Log.w(TAG, "Error en sleep()", e);
        }
        String id = uart.leer();
        Log.d(TAG, "Recibido de Arduino: " + id);

        //---SUBIR DATOS DE LOS SENSORES A FIREBASE
        Map<String, Object> sensorMagnetico = new HashMap<>();
        sensorMagnetico.put("Magnetico", magnetico);
        sensorMagnetico.put("Fecha", fecha);
        db.collection("SENSORES").document("Sensor_Magnetico").collection("Magnetico").add(sensorMagnetico);

        Map<String, Object> sensorID = new HashMap<>();
        sensorID.put("Vino", id);
        sensorID.put("Fecha", fecha);
        db.collection("SENSORES").document("Sensor_RFID").collection("ID").add(sensorID);

        //--RECIBIR DESDE MQTT
        try {
            Log.i(Mqtt.TAG, "Conectando al broker " + Mqtt.broker);
            client = new MqttClient(Mqtt.broker, Mqtt.clientId,
                    new MemoryPersistence());
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setKeepAliveInterval(60);
            connOpts.setWill(topicRoot + "WillTopic", "App desconectada".getBytes(), Mqtt.qos, false);
            client.connect(connOpts);
        } catch (MqttException e) {
            Log.e(Mqtt.TAG, "Error al conectar.", e);
        }
        //--RECIBIR SENSOR TEMPERATURA DESDE MQTT
        try {
            Log.i(Mqtt.TAG, "Suscrito a " + topicRoot + "SensorTemperatura");
            client.subscribe(topicRoot + "SensorMagnetico", qos);
            client.setCallback(this);
        } catch (MqttException e) {
            Log.e(Mqtt.TAG, "Error al suscribir.", e);
        }


    }

    @Override
    public void onDestroy() {
        try {
            Log.i(Mqtt.TAG, "Desconectado");
            client.disconnect();
        } catch (MqttException e) {
            Log.e(Mqtt.TAG, "Error al desconectar.", e);
        }
        super.onDestroy();
    }

    @Override
    public void connectionLost(Throwable cause) {
        Log.d(Mqtt.TAG, "Conexión perdida");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String payload = new String(message.getPayload());
        Log.d(Mqtt.TAG, "Recibiendo: " + topic + "->" + payload);

        Map<String, Object> sensorTemperatura = new HashMap<>();
        sensorTemperatura.put("Temperatura", payload);
        sensorTemperatura.put("Fecha", fecha);
        db.collection("SENSORES").document("Sensor_Temperatura").collection("Temperatura").add(sensorTemperatura);

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        Log.d(Mqtt.TAG, "Entrega completa");
    }
}