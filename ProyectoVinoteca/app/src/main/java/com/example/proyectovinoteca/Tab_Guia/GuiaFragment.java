package com.example.proyectovinoteca.Tab_Guia;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.proyectovinoteca.R;

public class GuiaFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle bundle){
        View v = inflater.inflate(R.layout.opcion_guia, container, false);

        Button verMapa = v.findViewById(R.id.btn_ver_mapa);
        verMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lanzarMapa(v);
            }
        });

        return v;
    }

    public void lanzarMapa(View view){
        Intent i = new Intent(getContext(), MapaActivity.class);
        startActivity(i);
    }
}
