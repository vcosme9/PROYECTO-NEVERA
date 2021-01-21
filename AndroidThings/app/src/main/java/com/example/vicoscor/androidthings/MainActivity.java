package com.example.vicoscor.androidthings;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.bilal.androidthingscameralib.InitializeCamera;
import com.bilal.androidthingscameralib.OnPictureAvailableListener;
import com.example.vicoscor.comun.Mqtt;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static com.example.vicoscor.comun.Mqtt.qos;
import static com.example.vicoscor.comun.Mqtt.topicRoot;

public class MainActivity extends Activity implements MqttCallback, OnPictureAvailableListener {

    private StorageReference storageRef;
    private static final String TAG = MainActivity.class.getSimpleName();
    public static MqttClient client = null;
    FirebaseFirestore db;
    String fecha;
    int health = 10;
    private InitializeCamera mInitializeCamera;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mInitializeCamera = new InitializeCamera(this, this, 640, 480, 1);

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd-MM-yyyy", Locale.getDefault());
        Date date = new Date();
        fecha = dateFormat.format(date);

        Log.i(TAG, "Lista de UART disponibles: " + ArduinoUart.disponibles());
        ArduinoUart uart = new ArduinoUart("MINIUART", 115200);

        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();
/*
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

            //---RECIBIR SENSOR HUMEDAD POR UART
            Log.d(TAG, "Mandado a Arduino: H");
            uart.escribir("H");
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                Log.w(TAG, "Error en sleep()", e);
            }
            String humedad = uart.leer();
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

            Map<String, Object> sensorHumedad = new HashMap<>();
            sensorMagnetico.put("Magnetico", humedad);
            sensorMagnetico.put("Fecha", fecha);
            db.collection("SENSORES").document("Sensor_Humedad").collection("Humedad").add(sensorMagnetico);*/


        //--CONFIGURAR MQTT
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
            client.subscribe(topicRoot + "SensorTemperatura", qos);
            client.setCallback(this);
        } catch (MqttException e) {
            Log.e(Mqtt.TAG, "Error al suscribir.", e);
        }

        try {
            Log.i(Mqtt.TAG, "Suscrito a " + topicRoot + "SensorHumedad");
            client.subscribe(topicRoot + "SensorHumedad", qos);
            client.setCallback(this);
        } catch (MqttException e) {
            Log.e(Mqtt.TAG, "Error al suscribir.", e);
        }

        try {
            Log.i(Mqtt.TAG, "Suscrito a " + topicRoot + "SensorMagnetico");
            client.subscribe(topicRoot + "SensorMagnetico", qos);
            client.setCallback(this);
        } catch (MqttException e) {
            Log.e(Mqtt.TAG, "Error al suscribir.", e);
        }

        try {
            Log.i(Mqtt.TAG, "Suscrito a " + topicRoot + "SensorID");
            client.subscribe(topicRoot + "SensorID", qos);
            client.setCallback(this);
        } catch (MqttException e) {
            Log.e(Mqtt.TAG, "Error al suscribir.", e);
        }
        final CollectionReference docRef = db.collection("SENSORES").document("Sensor_Magnetico").collection("Magnetico");
        docRef.orderBy("Fecha");
        docRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                Log.d("aaaaaaaaaaaaaaaaa", "hola como estas");
                if(value.getDocumentChanges().get(0).getDocument().get("Magnetico").toString().equals("Puerta abierta")){
                    mInitializeCamera.captureImage();
                    Log.d("aaaaaaaaaaaaaaaaa2", "puetas abiertrwsa");
                }

            }
        });
    }

    @Override
    public void onDestroy() {
        try {
            Log.i(Mqtt.TAG, "Desconectado");
            client.disconnect();
        } catch (MqttException e) {
            Log.e(Mqtt.TAG, "Error al desconectar.", e);
        }
        mInitializeCamera.releaseCameraResources();
        super.onDestroy();
    }

    @Override
    public void connectionLost(Throwable cause) {
        Log.d(Mqtt.TAG, "Conexión perdida");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String payload = new String(message.getPayload());
        Log.d(Mqtt.TAG, "-----------pues si que entra si");

        if (topic.equals(topicRoot + "SensorTemperatura")) {
            Log.d(Mqtt.TAG, "Recibiendo: " + topic + "->" + payload);
            Map<String, Object> sensorTemperatura = new HashMap<>();
            sensorTemperatura.put("Temperatura", payload);
            sensorTemperatura.put("Fecha", fecha);
            db.collection("SENSORES").document("Sensor_Temperatura").collection("Temperatura").add(sensorTemperatura);
        }

        if (topic.equals(topicRoot + "SensorHumedad")) {
            Log.d(Mqtt.TAG, "Recibiendo: " + topic + "->" + payload);
            Map<String, Object> sensorHumedad = new HashMap<>();
            sensorHumedad.put("Humedad", payload);
            sensorHumedad.put("Fecha", fecha);
            db.collection("SENSORES").document("Sensor_Humedad").collection("Humedad").add(sensorHumedad);
        }

        if (topic.equals(topicRoot + "SensorMagnetico")) {
            Log.d(Mqtt.TAG, "Recibiendo: " + topic + "->" + payload);

            if(payload.equals("Puerta abierta")){
                enviarValor("ON");
            }
            if(payload.equals("Puerta Cerrada")){
                enviarValor("OFF");
            }

            Map<String, Object> sensorMagnetico = new HashMap<>();
            sensorMagnetico.put("Magnetico", payload);
            sensorMagnetico.put("Fecha", fecha);
            db.collection("SENSORES").document("Sensor_Magnetico").collection("Magnetico").add(sensorMagnetico);



        }

        if (topic.equals(topicRoot + "SensorID")) {
            Log.d(Mqtt.TAG, "Recibiendo: " + topic + "->" + payload);
            Map<String, Object> sensorID = new HashMap<>();
            sensorID.put("Vino", payload);
            sensorID.put("Fecha", fecha);
            db.collection("SENSORES").document("Sensor_RFID").collection("ID").add(sensorID);
        }
    }

    public void enviarValor(String valor){
        try {
            MqttMessage message = new MqttMessage(valor.getBytes());
            message.setQos(Mqtt.qos);
            message.setRetained(false);
            client.publish(topicRoot+"cmnd/POWER", message);
        } catch (MqttException e) {
            Log.e(Mqtt.TAG, "Error al publicar.", e);
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        Log.d(Mqtt.TAG, "Entrega completa");
    }

    @Override
    public void onPictureAvailable(byte[] imageBytes) {
        final String referencia = "Foto/" + System.currentTimeMillis();
        final StorageReference ref = storageRef.child(referencia);
        String s = null;
        try {
            s = new String(imageBytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Uri uri = Uri.parse(s);
        UploadTask uploadTask = ref.putFile(uri);
        Task<Uri> urlTask = uploadTask.continueWithTask(
                new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(
                            @NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        } else {
                            FirebaseStorage.getInstance().getReference(referencia).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    Map<String, Object> foto = new HashMap<>();
                                    foto.put("Foto", task.getResult().toString());
                                    foto.put("Fecha", fecha);
                                    db.collection("CAMARA").add(foto);
                                }
                            });
                        }
                        return ref.getDownloadUrl();
                    }
                });
    }
}