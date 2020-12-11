package com.example.proyectovinoteca.Tab_Alertas;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


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
    double temp = 0.0;
    String fecha = "";
    double tempMax = 0.0;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.tab_alertas, container, false);
        recyclerAlertas = (RecyclerView) vista.findViewById(R.id.recyclerId);
        listaAlertas = new ArrayList<>();
        recyclerAlertas.setLayoutManager(new LinearLayoutManager(getContext()));

        //rangoMaximoTemperatura();
        loadDataFromFirestoreTemperatura();
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

        final CollectionReference medidasInfo = db.collection("SENSORES").document("Sensor_Temperatura").collection("Temperatura");


        //coger la fecha mas nueva
        medidasInfoTempMax.orderBy("Fecha", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                            Log.d(TAG, documentSnapshot.getId() + " => " + documentSnapshot.getData());
                            tempMax = Double.parseDouble(documentSnapshot.getString("Temperatura"));
                            Log.i(TAG, "--------------------" + tempMax);
                            //referencia la coleccion de firebase
                            final CollectionReference medidasInfo = db.collection("SENSORES").document("Sensor_Temperatura").collection("Temperatura");


                        if(temp > 18.0){
                            listaAlertas.add(new ClaseAlerta("Temperatura", fecha +" El sensor de temperatura ha detectado una medición dañina: " + temp +"ºC", R.drawable.alerta));

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
                            notificacion.setContentTitle("Título");
                            notificacion.setContentText("Texto de la notificación.");
                            notificationManager.notify(NOTIFICACION_ID, notificacion.build());

                        }


                            //coger la fecha mas nueva
                            medidasInfo.orderBy("Fecha", Query.Direction.DESCENDING)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                                                Log.d(TAG, documentSnapshot.getId() + " => " + documentSnapshot.getData());
                                                temp = Double.parseDouble(documentSnapshot.getString("Temperatura"));
                                                if (temp > tempMax) {
                                                    listaAlertas.add(new ClaseAlerta("Temperatura", fecha + " El sensor de temperatura ha detectado una medición dañina: " + temp + "ºC", R.drawable.alerta));
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

    //notificacion

}
