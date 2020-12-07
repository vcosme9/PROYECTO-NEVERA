package com.example.proyectovinoteca.Tab_Guia;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.proyectovinoteca.R;

public class ComprarVinos extends Activity {

    private Button Compra1;
    private Button compra2;
    private Button compra3;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comprar_vinos);
        Compra1 = findViewById(R.id.button);
        //el boton de la 1 pagina web
        Compra1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSite(null);
            }
        });
        //el boton de la 2 pagina web
        compra2 = findViewById(R.id.button3);
        compra2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSite2(null);
            }
        });
        compra3 = findViewById(R.id.button5);
        compra3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSite3(null);
            }
        });

    }
    //funcion que abre la 1 pagina web
    public void btnSite (View View) {

        String url = "https://www.gourness.com/";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }
    //funcion que abre la 2 pagina web

    public void btnSite2 (View View) {

        String url = "https://www.drinksco.es/";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }
    //funcion que abre la 3 pagina web

    public void btnSite3 (View View) {

        String url = "https://www.misumiller.es/";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }




}
