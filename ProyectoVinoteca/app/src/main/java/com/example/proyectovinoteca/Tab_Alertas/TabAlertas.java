package com.example.proyectovinoteca.Tab_Alertas;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.proyectovinoteca.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.content.Context.NOTIFICATION_SERVICE;


public class TabAlertas extends Fragment {

    private static final String TAG = "ProductosFragment";
    private AlertasViewModel alertasViewModel;

    private NotificationManager notificationManager;
    static final String CANAL_ID = "mi_canal";
    static final int NOTIFICACION_ID = 1;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //para acceder al firebase
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    ArrayList<ClaseAlerta> listaAlertas;
    RecyclerView recyclerAlertas;

    String fecha = "";

    double temp = 0.0;
    double tempMax = 0.0;
    double tempMin = 0.0;

    double hum = 0.0;
    double humMax = 0.0;
    double humMin = 0.0;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.tab_alertas, container, false);
        recyclerAlertas = (RecyclerView) vista.findViewById(R.id.recyclerId);
        listaAlertas = new ArrayList<>();
        recyclerAlertas.setLayoutManager(new LinearLayoutManager(getContext()));

        loadDataFromFirestoreTemperatura();
        loadDataFromFirestoreHumedad();
        // llenarlista();

        AlertasAdapter adapter = new AlertasAdapter(listaAlertas);
        recyclerAlertas.setAdapter(adapter);

        return vista;
    }

    private void llenarlista() {
        //listaProductos.add(new ClaseProducto(productoo, productoo, R.drawable.productos));
        listaAlertas.add(new ClaseAlerta("Humedad", "El sensor de humedad detectó liquidos en el fondo de la nevera, revísala.", R.drawable.alerta));
    }


    private void loadDataFromFirestoreTemperatura() {

        if (listaAlertas.size() > 0) {
            listaAlertas.clear();
        }
        //referencia la coleccion de firebase

        final CollectionReference medidasInfoTempMax = db.collection("ALERTAS").document("Rango_TempMax").collection("RangoMaximo");
        final CollectionReference medidasInfoTempMin = db.collection("ALERTAS").document("Rango_TempMin").collection("RangoMinimo");
        final CollectionReference medidasInfoTemp = db.collection("SENSORES").document("Sensor_Temperatura").collection("Temperatura");

        //coger la fecha mas nueva de TEMPERATURAS MAXIMAS
        medidasInfoTempMax.orderBy("Fecha", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                            //se guarda la ultima temperatura maxima registrada
                            Log.d(TAG, documentSnapshot.getId() + " => " + documentSnapshot.getData());
                            tempMax = Double.parseDouble(documentSnapshot.getString("Temperatura"));
                            Log.i(TAG, "------------> Rango maximo Temperatura: " + tempMax);
                            //coger la fecha mas nueva de temperaturas
                            medidasInfoTemp.orderBy("Fecha", Query.Direction.DESCENDING)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                                                Log.d(TAG, documentSnapshot.getId() + " => " + documentSnapshot.getData());
                                                temp = Double.parseDouble(documentSnapshot.getString("Temperatura"));
                                                if (temp > tempMax) {
                                                    //se añaden todas las temperaturas superiores a la temperatura maxima
                                                    listaAlertas.add(new ClaseAlerta("Temperatura", fecha + " El sensor de temperatura ha detectado una medición dañina: " + temp + "ºC", R.drawable.alerta));
                                                    Log.i(TAG, "------------> Se añade una alerta con temperatura: " + temp);
                                                    //se envia una notificacion
                                                    notificationManager = (NotificationManager)
                                                            getActivity().getSystemService(NOTIFICATION_SERVICE);
                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                        NotificationChannel notificationChannel = new NotificationChannel(
                                                                CANAL_ID, "Mis Notificaciones",
                                                                NotificationManager.IMPORTANCE_DEFAULT);
                                                        notificationChannel.setDescription("Descripcion del canal");
                                                        notificationManager.createNotificationChannel(notificationChannel);
                                                    }
                                                    NotificationCompat.Builder notificacion
                                                            = new NotificationCompat.Builder(getContext(), CANAL_ID);
                                                    notificacion.setSmallIcon(R.mipmap.ic_launcher);
                                                    notificacion.setContentTitle("ALERTA");
                                                    notificacion.setContentText("Temperatura superior a lo establecido: " + temp);
                                                    PendingIntent intencionPendiente = PendingIntent.getActivity(
                                                            getContext(), 0, new Intent( getContext(), TabAlertas.class), 0);
                                                    notificacion.setContentIntent(intencionPendiente);
                                                    notificationManager.notify(NOTIFICACION_ID, notificacion.build());
                                                }
                                            }
                                            //el array pasa al adaptador
                                            AlertasAdapter adaptador = new AlertasAdapter(listaAlertas);
                                            recyclerAlertas.setAdapter(adaptador);
                                        }
                                    });
                        }
                    }
                });

        //coger la fecha mas nueva de TEMPERATURAS MINIMAS
        medidasInfoTempMin.orderBy("Fecha", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                            //se guarda la ultima temperatura maxima registrada
                            Log.d(TAG, documentSnapshot.getId() + " => " + documentSnapshot.getData());
                            tempMin = Double.parseDouble(documentSnapshot.getString("Temperatura"));
                            Log.i(TAG, "------------> Rango minimo Temperatura: " + tempMin);
                            //coger la fecha mas nueva de temperaturas
                            medidasInfoTemp.orderBy("Fecha", Query.Direction.DESCENDING)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                                                Log.d(TAG, documentSnapshot.getId() + " => " + documentSnapshot.getData());
                                                temp = Double.parseDouble(documentSnapshot.getString("Temperatura"));
                                                if (temp < tempMin) {
                                                    //se añaden todas las temperaturas inferiores a la temperatura minima
                                                    listaAlertas.add(new ClaseAlerta("Temperatura", fecha + " El sensor de temperatura ha detectado una medición dañina: " + temp + "ºC", R.drawable.alerta));
                                                    Log.i(TAG, "------------> Se añade una alerta con temperatura: " + temp);
                                                    //se envia una notificacion
                                                    notificationManager = (NotificationManager)
                                                            getActivity().getSystemService(NOTIFICATION_SERVICE);
                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                        NotificationChannel notificationChannel = new NotificationChannel(
                                                                CANAL_ID, "Mis Notificaciones",
                                                                NotificationManager.IMPORTANCE_DEFAULT);
                                                        notificationChannel.setDescription("Descripcion del canal");
                                                        notificationManager.createNotificationChannel(notificationChannel);
                                                    }
                                                    NotificationCompat.Builder notificacion
                                                            = new NotificationCompat.Builder(getContext(), CANAL_ID);
                                                    notificacion.setSmallIcon(R.mipmap.ic_launcher);
                                                    notificacion.setContentTitle("ALERTA");
                                                    notificacion.setContentText("Temperatura inferior a lo establecido: " + temp);
                                                    PendingIntent intencionPendiente = PendingIntent.getActivity(
                                                            getContext(), 0, new Intent( getContext(), TabAlertas.class), 0);
                                                    notificacion.setContentIntent(intencionPendiente);
                                                    notificationManager.notify(NOTIFICACION_ID, notificacion.build());
                                                }
                                            }
                                            //el array pasa al adaptador
                                            AlertasAdapter adaptador = new AlertasAdapter(listaAlertas);
                                            recyclerAlertas.setAdapter(adaptador);
                                        }
                                    });
                        }
                    }
                });


    }

    public void loadDataFromFirestoreHumedad() {
        final CollectionReference medidasInfoHumMax = db.collection("ALERTAS").document("Rango_HumMax").collection("RangoMaximo");
        final CollectionReference medidasInfoHumMin = db.collection("ALERTAS").document("Rango_HumMin").collection("RangoMinimo");
        final CollectionReference medidasInfoHum = db.collection("SENSORES").document("Sensor_Humedad").collection("Humedad");

        //coger la fecha mas nueva de HUMEDADES MAXIMAS
        medidasInfoHumMax.orderBy("Fecha", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                            //se guarda la ultima temperatura maxima registrada
                            Log.d(TAG, documentSnapshot.getId() + " => " + documentSnapshot.getData());
                            humMax = Double.parseDouble(documentSnapshot.getString("Humedad"));
                            Log.i(TAG, "------------> Rango maximo Humedad: " + humMax);
                            //coger la fecha mas nueva de humedades
                            medidasInfoHum.orderBy("Fecha", Query.Direction.DESCENDING)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                                                Log.d(TAG, documentSnapshot.getId() + " => " + documentSnapshot.getData());
                                                hum = Double.parseDouble(documentSnapshot.getString("Humedad"));
                                                if (hum > humMax) {
                                                    //se añaden todas las humedades superiores a la humedad maxima
                                                    listaAlertas.add(new ClaseAlerta("Humedad", fecha + " El sensor de humedad ha detectado una medición dañina: " + hum, R.drawable.alerta));

                                                    Log.i(TAG, "------------> Se añade una alerta con Humedad: " + hum);
                                                    //se envia una notificacion
                                                    notificationManager = (NotificationManager)
                                                            getActivity().getSystemService(NOTIFICATION_SERVICE);
                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                        NotificationChannel notificationChannel = new NotificationChannel(
                                                                CANAL_ID, "Mis Notificaciones",
                                                                NotificationManager.IMPORTANCE_DEFAULT);
                                                        notificationChannel.setDescription("Descripcion del canal");
                                                        notificationManager.createNotificationChannel(notificationChannel);
                                                    }
                                                    NotificationCompat.Builder notificacion
                                                            = new NotificationCompat.Builder(getContext(), CANAL_ID);
                                                    notificacion.setSmallIcon(R.mipmap.ic_launcher);
                                                    notificacion.setContentTitle("ALERTA");
                                                    notificacion.setContentText("Humedad superior a lo establecido: " + hum);
                                                    PendingIntent intencionPendiente = PendingIntent.getActivity(
                                                            getContext(), 0, new Intent( getContext(), TabAlertas.class), 0);
                                                    notificacion.setContentIntent(intencionPendiente);
                                                    notificationManager.notify(NOTIFICACION_ID, notificacion.build());
                                                }
                                            }
                                            //el array pasa al adaptador
                                            AlertasAdapter adaptador = new AlertasAdapter(listaAlertas);
                                            recyclerAlertas.setAdapter(adaptador);
                                        }
                                    });
                        }
                    }
                });


        //coger la fecha mas nueva de HUMEDADES MINIMAS
        medidasInfoHumMin.orderBy("Fecha", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                            //se guarda la ultima temperatura maxima registrada
                            Log.d(TAG, documentSnapshot.getId() + " => " + documentSnapshot.getData());
                            humMin = Double.parseDouble(documentSnapshot.getString("Humedad"));

                            Log.i(TAG, "------------> Rango minimo humedad: " + humMin);
                            //coger la fecha mas nueva de temperaturas
                            medidasInfoHum.orderBy("Fecha", Query.Direction.DESCENDING)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                                                Log.d(TAG, documentSnapshot.getId() + " => " + documentSnapshot.getData());
                                                hum = Double.parseDouble(documentSnapshot.getString("Humedad"));
                                                if (hum < humMin) {
                                                    //se añaden todas las temperaturas inferiores a la temperatura minima
                                                    listaAlertas.add(new ClaseAlerta("Humedad", fecha + " El sensor de humedad ha detectado una medición dañina: " + hum, R.drawable.alerta));
                                                    Log.i(TAG, "------------> Se añade una alerta con Humedad: " + hum);
                                                    //se envia una notificacion
                                                    notificationManager = (NotificationManager)
                                                            getActivity().getSystemService(NOTIFICATION_SERVICE);
                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                        NotificationChannel notificationChannel = new NotificationChannel(
                                                                CANAL_ID, "Mis Notificaciones",
                                                                NotificationManager.IMPORTANCE_DEFAULT);
                                                        notificationChannel.setDescription("Descripcion del canal");
                                                        notificationManager.createNotificationChannel(notificationChannel);
                                                    }
                                                    NotificationCompat.Builder notificacion
                                                            = new NotificationCompat.Builder(getContext(), CANAL_ID);
                                                    notificacion.setSmallIcon(R.mipmap.ic_launcher);
                                                    notificacion.setContentTitle("ALERTA");
                                                    notificacion.setContentText("Humedad inferior a lo establecido: " + hum);
                                                    PendingIntent intencionPendiente = PendingIntent.getActivity(
                                                            getContext(), 0, new Intent( getContext(), TabAlertas.class), 0);
                                                    notificacion.setContentIntent(intencionPendiente);
                                                    notificationManager.notify(NOTIFICACION_ID, notificacion.build());
                                                }
                                            }
                                            //el array pasa al adaptador
                                            AlertasAdapter adaptador = new AlertasAdapter(listaAlertas);
                                            recyclerAlertas.setAdapter(adaptador);
                                        }
                                    });
                        }
                    }
                });
    }
}
