package com.example.proyectovinoteca;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.SearchView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.proyectovinoteca.comentarios.ComentariosActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static android.content.ContentValues.TAG;

public class ClasificacionesFragment extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Vino> listaVinos = new ArrayList<>();
    private ArrayList<Vino> listaCopia = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private AdaptadorRanking adaptador;
    private Spinner spinner;
    private RadioButton rdVal;
    private LinearLayout chargeContainer;
    private LinearLayout containerAll;

    public ClasificacionesFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle bundle){
        loadDataFromFirestore();

        View vista=inflater.inflate(R.layout.opcion_clasificaciones, container, false);
        addItemsOnSpinner(vista);

        chargeContainer = vista.findViewById(R.id.chargeContainer);
        containerAll = vista.findViewById(R.id.containerAll);
        containerAll.setVisibility(View.GONE);

        //carga la imagen de carga
        chargeContainer = vista.findViewById(R.id.chargeContainer);
        ImageView loadIcon = vista.findViewById(R.id.chargingImage);
        Glide.with(this).load(R.drawable.tenor).into(loadIcon);

        rdVal = vista.findViewById(R.id.radioValoracion);
        final SearchView searchView = vista.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                buscador(newText);
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }
        });

        adaptador.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = recyclerView.getChildAdapterPosition(v);
                Intent i=new Intent(getContext(), ComentariosActivity.class);
                startActivity(i);
            }
        });

        recyclerView =vista.findViewById(R.id.recyclerRanking);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return vista;
    }
    public void orderName(ArrayList<Vino> listaVinos){
        Collections.sort(listaVinos, new Comparator<Vino>() {
            public int compare(Vino o1, Vino o2) {
                return o1.getNombre().compareTo(o2.getNombre());
            }
        });

    }
    public void orderValoracion(ArrayList<Vino> listaVinos) {
        Collections.sort(listaVinos, new Comparator<Vino>() {
            public int compare(Vino o1, Vino o2) {
                return Float.compare(o2.getValoracion(), o1.getValoracion());
            }
        });

    }
    public void buscador(String text){
        if(text.isEmpty()){
            listaVinos.clear();
            listaVinos.addAll(listaCopia);
        } else{
            ArrayList<Vino> lista = new ArrayList<>();
            for (int i=0;i<listaVinos.size();i++){
                if(listaVinos.get(i).getNombre().toLowerCase().contains(text))
                    lista.add(listaVinos.get(i));
            }
            listaCopia.addAll(listaVinos);
            listaVinos.clear();
            listaVinos.addAll(lista);
        }

        adaptador.notifyDataSetChanged();
    }
    private void loadDataFromFirestore() {

        if (listaVinos.size() > 0) {
            listaVinos.clear();
        }
        if (listaCopia.size() > 0) {
            listaCopia.clear();
        }
        //referencia la coleccion de firebase
        final CollectionReference medidasInfo = db.collection("coleccion").document("mis_vinos").collection("vinitos");

        //coger la fecha mas nueva
        medidasInfo.orderBy("fecha", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                            Log.d(TAG, documentSnapshot.getId() + " => " + documentSnapshot.getData());

                            //se guarda la nueva medida
                            Vino miVino = new Vino(documentSnapshot.getString("nombre"), documentSnapshot.getDouble("valoracion").floatValue(),documentSnapshot.getString("descripcion"), documentSnapshot.getString("tipo"),  R.drawable.productos);
                            listaVinos.add(miVino);
                            adaptador = new AdaptadorRanking(listaVinos);
                            recyclerView.setAdapter(adaptador);

                            rdVal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    if(rdVal.isChecked()){
                                        orderValoracion(listaVinos);
                                    }
                                    else {
                                        orderName(listaVinos);
                                    }
                                    adaptador.notifyDataSetChanged();
                                }
                            });

                            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                    String tipo=getResources().getStringArray(R.array.tipos)[i];
                                    spinTipo(tipo);
                                    adaptador.notifyDataSetChanged();
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {

                                }
                            });

                            //ocultar el contenedor de la imagen de carga y mostrar el contenido
                            containerAll.setVisibility(View.VISIBLE);
                            chargeContainer.setVisibility(View.GONE);
                        }
                    }
                });
    }
    private void spinTipo(String text){
        if (listaCopia.size() > 0) {
            listaCopia.clear();
        }
        ArrayList<Vino> lista = new ArrayList<>();
        for (int i=0;i<listaVinos.size();i++){
            if(listaVinos.get(i).getTipo().equals(text))
                lista.add(listaVinos.get(i));
        }
        listaCopia.addAll(listaVinos);
        listaVinos.clear();
        listaVinos.addAll(lista);
    }
    private void addItemsOnSpinner(View v) {
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getContext(),R.array.tipos ,android.R.layout.simple_spinner_item);
        spinner=  v.findViewById(R.id.tipo);
        spinner.setAdapter(spinnerAdapter);
    }
}
