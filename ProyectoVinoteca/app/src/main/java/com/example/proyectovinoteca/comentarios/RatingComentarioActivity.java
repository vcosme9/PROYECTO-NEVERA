package com.example.proyectovinoteca.comentarios;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.proyectovinoteca.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RatingComentarioActivity extends Activity {

    EditText coment;
    RatingBar rating;
    Button aceptar;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private String nombre;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rating_comentario);
        nombre=getIntent().getStringExtra("nombre");
        coment =  findViewById(R.id.editTextTextPersonName);
        rating = findViewById(R.id.ratingBar);
        final FirebaseUser usuario = auth.getCurrentUser();

        aceptar = findViewById(R.id.button2);
        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float valoracion = rating.getRating();
                String comentario = coment.getText().toString();
                Intent i = new Intent(getApplicationContext(),ComentariosActivity.class);
                ClaseComentario miComentaario= new ClaseComentario(usuario.getDisplayName(),valoracion,comentario);
                db.collection("coleccion").document("mis_vinos").collection("vinitos").document(nombre).collection("Comentarios").document(usuario.getUid()).set(miComentaario);
            }
        });

    }

    public void volverAtras(View view){
        Intent a= new Intent(this, ComentariosActivity.class);
        startActivity(a);
    }

}