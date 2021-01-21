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

import androidx.annotation.Nullable;

import com.example.proyectovinoteca.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RatingComentarioActivity extends Activity {

    EditText coment;
    RatingBar rating;
    Button aceptar;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rating_comentario);

        coment =  findViewById(R.id.editTextTextPersonName);
        rating = findViewById(R.id.ratingBar);

        final FirebaseUser usuario = auth.getCurrentUser();
        final String id = usuario.getUid();

        aceptar = findViewById(R.id.button2);
        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float valoracion = rating.getRating();
                String comentario = coment.getText().toString();
                Log.d("user",usuario.toString());
                ClaseComentario miComentaario= new ClaseComentario(usuario.toString(),valoracion,comentario);
                db.collection("coleccion").document("mis_vinos").collection("vinitos").document("32").collection("Comentarios").document(id).set(miComentaario);

            }
        });

    }

    public void volverAtras(View view){
        Intent a= new Intent(this, ComentariosActivity.class);
        startActivity(a);
    }

}