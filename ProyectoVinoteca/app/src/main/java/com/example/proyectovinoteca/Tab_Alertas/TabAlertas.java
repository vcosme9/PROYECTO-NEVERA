package com.example.proyectovinoteca.Tab_Alertas;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectovinoteca.R;
import com.example.proyectovinoteca.Tab_Productos.ClaseProducto;
import com.example.proyectovinoteca.Tab_Productos.ProductosAdapter;
import com.example.proyectovinoteca.Tab_Productos.ProductosViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class TabAlertas extends Fragment {

    private static final String TAG = "ProductosFragment";
    private AlertasViewModel alertasViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //para acceder al firebase
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    ArrayList<ClaseAlerta> listaAlertas;

    RecyclerView recyclerProductos;
    String producto;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.tab_alertas, container, false);
        recyclerProductos = (RecyclerView) vista.findViewById(R.id.recyclerId);
        listaAlertas = new ArrayList<>();
        recyclerProductos.setLayoutManager(new LinearLayoutManager(getContext()));

        //loadDataFromFirestore();
        llenarlista();


        AlertasAdapter adapter = new AlertasAdapter(listaAlertas);
        recyclerProductos.setAdapter(adapter);

        return vista;
    }

    private void llenarlista() {
        listaAlertas.add(new ClaseAlerta("Temperatura", "El sensor de temperatura ha detectado una medición dañina: 8ºC", R.drawable.alerta));
        //listaProductos.add(new ClaseProducto(productoo, productoo, R.drawable.productos));
        listaAlertas.add(new ClaseAlerta("Humedad", "El sensor de humedad detectó liquidos en el fondo de la nevera, revísala.", R.drawable.alerta));
    }

   /* private  void consultarFirebase() {
        //FirebaseFirestore db = FirebaseFirestore.getInstance();

       // db.collection("SENSORES").document("productos").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    producto = task.getResult().getString("producto");

                    Log.d("Firestore", "dato 1:" + producto);
                } else {
                    Log.e("Firestore", "Error al leer", task.getException());
                }
            }
        });
    }
*/

    private void loadDataFromFirestore() {

        if (listaAlertas.size() > 0) {
            listaAlertas.clear();
        }

        //referencia la coleccion de firebase
        final CollectionReference medidasInfo = db.collection("SENSORES").document("productos").collection("prod");


        //coger la fecha mas nueva
        medidasInfo.orderBy("fecha", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                            Log.d(TAG, documentSnapshot.getId() + " => " + documentSnapshot.getData());

                            //se guarda la nueva medida y la pasa a historialvo
                            ClaseAlerta mimedida = new ClaseAlerta(documentSnapshot.getString("producto"), documentSnapshot.getString("fecha"), R.drawable.alerta);
                            listaAlertas.add(mimedida);

                        }


                        //el array pasa al adaptador
                        AlertasAdapter adaptador = new AlertasAdapter(listaAlertas);
                        recyclerProductos.setAdapter(adaptador);

                    }
                });

    }
}
