package com.example.proyectovinoteca.Tab_Productos;



import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.proyectovinoteca.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class TabProductos extends Fragment {

    private static final String TAG = "ProductosFragment";
    private ProductosViewModel productosViewModel;
    private LinearLayout chargeContainer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //para acceder al firebase
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    ArrayList<ClaseProducto> listaProductos;

    RecyclerView recyclerProductos;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.tab_productos, container, false);
        recyclerProductos = (RecyclerView) vista.findViewById(R.id.recyclerId);
        listaProductos = new ArrayList<>();
        recyclerProductos.setLayoutManager(new LinearLayoutManager(getContext()));

        //carga la imagen de carga
        chargeContainer = vista.findViewById(R.id.chargeContainer);
        ImageView loadIcon = vista.findViewById(R.id.chargingImage);
        Glide.with(this).load(R.drawable.tenor).into(loadIcon);

        loadDataFromFirestore();
        //llenarlista();


        ProductosAdapter adapter = new ProductosAdapter(listaProductos);
        recyclerProductos.setAdapter(adapter);

        return vista;
    }

    private void llenarlista() {
        listaProductos.add(new ClaseProducto("prueba1", "esto es una prueba1", R.drawable.productos));
        //listaProductos.add(new ClaseProducto(productoo, productoo, R.drawable.productos));
        listaProductos.add(new ClaseProducto("prueba2", "esto es una prueba2", R.drawable.productos));
    }

    private void loadDataFromFirestore() {

        if (listaProductos.size() > 0) {
            listaProductos.clear();
        }

        //referencia la coleccion de firebase
        final CollectionReference medidasInfo = db.collection("SENSORES").document("Sensor_RFID").collection("ID");


        //coger la fecha mas nueva
        medidasInfo.orderBy("Fecha", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                            Log.d(TAG, documentSnapshot.getId() + " => " + documentSnapshot.getData());

                            //se guarda la nueva medida
                            ClaseProducto miVino = new ClaseProducto(documentSnapshot.getString("Vino"), documentSnapshot.getString("Fecha"), R.drawable.productos);
                            listaProductos.add(miVino);

                            //el array pasa al adaptador
                            ProductosAdapter adaptador = new ProductosAdapter(listaProductos);
                            recyclerProductos.setAdapter(adaptador);

                            //ocultar el contenedor de la imagen de carga
                            chargeContainer.setVisibility(View.GONE);
                        }
                    }
                });

    }
}