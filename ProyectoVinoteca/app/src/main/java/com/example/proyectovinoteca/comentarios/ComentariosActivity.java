package com.example.proyectovinoteca.comentarios;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectovinoteca.AdaptadorRanking;
import com.example.proyectovinoteca.R;
import com.example.proyectovinoteca.Tab_Alertas.AlertasAdapter;
import com.example.proyectovinoteca.Vino;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class ComentariosActivity extends Activity {

    private RecyclerView recyclerView;
    private final ArrayList<ClaseComentario> listaComentarios = new ArrayList<>();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ComentariosAdapter adaptador;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vino_comentarios);
        adaptador = new ComentariosAdapter(listaComentarios);
        loadDataFromFirestore();
        recyclerView =findViewById(R.id.comentariosrecycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adaptador);

    }
    public void abrirWeb(View view){
        Intent a = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.torres.es/es/vinos/mas-la-plana"));
        startActivity(a);
    }

    private void loadDataFromFirestore() {

        if (listaComentarios.size() > 0) {
            listaComentarios.clear();
        }

        //referencia la coleccion de firebase
        final CollectionReference comentarios = db.collection("Comentarios");

        //coger la fecha mas nueva
        comentarios.orderBy("fecha", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                            Log.d(TAG, documentSnapshot.getId() + " => " + documentSnapshot.getData());

                            //se guarda la nueva medida
                            ClaseComentario Comentario = new ClaseComentario(documentSnapshot.getString("nombre"), documentSnapshot.getDouble("valoracion").floatValue(),documentSnapshot.getString("comentario"));
                            listaComentarios.add(Comentario);
                            adaptador.notifyDataSetChanged();
                            //ocultar el contenedor de la imagen de carga y mostrar el contenido

                        }
                    }
                });
    }

}
