package com.example.proyectovinoteca;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ClasificacionesFragment extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private AdaptadorRanking adaptador;
    private ListaVinos listaVinos = new ListaVinos();
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle bundle){
        listaVinos.añadeEjemplos();
        View vista=inflater.inflate(R.layout.opcion_clasificaciones, container, false);
        final RadioButton rdVal = vista.findViewById(R.id.radioValoracion);
        recyclerView =(RecyclerView) vista.findViewById(R.id.recyclerRanking);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adaptador = new AdaptadorRanking(listaVinos);
        recyclerView.setAdapter(adaptador);
        rdVal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(rdVal.isChecked()){
                    orderValoracion(listaVinos);
                    adaptador.notifyDataSetChanged();
                }
            }
        });
        return vista;
    }

    public static void orderValoracion(ListaVinos listaVinos) {
        ArrayList<Vino> list=new ArrayList<Vino>();
        for (int i=0;i<listaVinos.tamaño();i++)
            list.add(listaVinos.elemento(i));
        Collections.sort(list, new Comparator<Vino>() {
            public int compare(Vino o1, Vino o2) {
                return Float.compare(o2.getValoracion(), o1.getValoracion());
            }
        });
        for (int i=0;i<list.size();i++){
            listaVinos.actualiza(i,list.get(i));
        }
    }
}
