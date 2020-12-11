package com.example.proyectovinoteca.comentarios;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.proyectovinoteca.R;

public class ComentariosActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vino_comentarios);
    }
    public void abrirWeb(View view){
        Intent a = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.torres.es/es/vinos/mas-la-plana"));
        startActivity(a);
    }

}
