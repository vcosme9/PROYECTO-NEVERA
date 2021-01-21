package com.example.proyectovinoteca.comentarios;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import androidx.annotation.Nullable;

import com.example.proyectovinoteca.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

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
        final String comentario = coment.getText().toString();

        rating = findViewById(R.id.ratingBar);
        final float valoracion = rating.getRating();

        aceptar = findViewById(R.id.button2);
        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseUser usuario = auth.getCurrentUser();

                Date d = new Date(); CharSequence s = DateFormat.format("MMMM d, yyyy ", d.getTime());

                Map<String, Object> comentarios = new HashMap<>();
                comentarios.put("Comentario", comentario);
                comentarios.put("Valoracion", valoracion);
                comentarios.put("Nombre", usuario);
                comentarios.put("Fecha", d);
                db.collection("Comentarios").add(comentarios);

            }
        });

    }

    public void volverAtras(View view){
        Intent a= new Intent(this, ComentariosActivity.class);
        startActivity(a);
    }

}
