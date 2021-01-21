package com.example.proyectovinoteca;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.proyectovinoteca.comentarios.ClaseComentario;
import com.example.proyectovinoteca.comentarios.ComentariosActivity;
import com.example.proyectovinoteca.comentarios.ComentariosActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static android.content.ContentValues.TAG;

public class ClasificacionesFragment extends Fragment {
    private RecyclerView recyclerView;
    private final ArrayList<Vino> listaVinos = new ArrayList<>();
    private final ArrayList<Vino> listaCopia = new ArrayList<>();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private AdaptadorRanking adaptador;
    private Spinner spinner;
    private RadioButton rdVal;
    private RadioButton rdPop;
    private boolean valorando=false;
    public ClasificacionesFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle bundle){


        View vista=inflater.inflate(R.layout.opcion_clasificaciones, container, false);
        adaptador = new AdaptadorRanking(listaVinos);
        loadDataFromFirestore();
        listaCopia.addAll(listaVinos);
        recyclerView =vista.findViewById(R.id.recyclerRanking);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adaptador);

        final RadioGroup mRadioGroup = (RadioGroup) vista.findViewById(R.id.radioGroup);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int id = mRadioGroup.getCheckedRadioButtonId();
                View radioButton = mRadioGroup.findViewById(id);
                if (radioButton.getId() == R.id.radioNombre) {
                    orderNombre();
                } else if (radioButton.getId() == R.id.radioValoracion) {
                    orderValoracion();
                }
            }
        });
        spinner=vista.findViewById(R.id.tipo);
        addItemsOnSpinner(vista);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String tipo=getResources().getStringArray(R.array.tipos)[i];
                if(listaCopia.size()==0){
                    listaCopia.addAll(listaVinos);
                }
                Log.d("hola",listaCopia.toString());
                adaptador.filter(tipo,listaCopia);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        SearchView searchView = vista.findViewById(R.id.searchView);
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
                CardView cV = (CardView) ((LinearLayout)v).getChildAt(0);
                TextView tV = (TextView)cV.getChildAt(1);
                float fl = Float.valueOf(((RatingBar)cV.getChildAt(2)).getRating());
                ImageView iV = (ImageView) cV.getChildAt(0);

                Intent i = new Intent(getContext(), ComentariosActivity.class);
                //convertir bitmap en byte array
                try {
                    Bitmap bm = ((BitmapDrawable) iV.getDrawable()).getBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    i.putExtra("imagen", byteArray);
                } catch(Exception e){}
                String n = tV.getText().toString();
                i.putExtra("valoracion", fl);
                i.putExtra("nombre", n);
                startActivity(i);
            }
        });


        return vista;
    }



    public void orderValoracion() {
        if (listaVinos.size() > 0) {
            listaVinos.clear();
        }
        if (listaCopia.size() > 0) {
            listaCopia.clear();
        }
        //referencia la coleccion de firebase
        final CollectionReference medidasInfo = db.collection("coleccion").document("mis_vinos").collection("vinitos");

        //coger la fecha mas nueva
        medidasInfo.orderBy("valoracion", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            final CollectionReference comentarios = db.collection("coleccion").document("mis_vinos").collection("vinitos").document(documentSnapshot.getString("nombre")).collection("Comentarios");
                            final List<ClaseComentario>listaComentarios=new ArrayList<>();
                            comentarios.orderBy("fecha", Query.Direction.DESCENDING)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            for (QueryDocumentSnapshot coment:task.getResult()){
                                                ClaseComentario miComentario= new ClaseComentario(coment.getString("nombre"),coment.getDouble("valoracion").floatValue(),coment.getString("comentario"));
                                                listaComentarios.add(miComentario);
                                            }
                                        }
                                    });
                            Log.d(TAG, documentSnapshot.getId() + " => " + documentSnapshot.getData());

                            //se guarda la nueva medida
                            Vino miVino = new Vino(documentSnapshot.getString("nombre"), documentSnapshot.getDouble("valoracion").floatValue(),documentSnapshot.getString("descripcion"), documentSnapshot.getString("tipo"), documentSnapshot.getString("foto"), listaComentarios);
                            listaVinos.add(miVino);
                            adaptador.notifyDataSetChanged();
                            //ocultar el contenedor de la imagen de carga y mostrar el contenido

                        }
                        recyclerView.setAdapter(adaptador);
                    }
                });


    }
    public void orderNombre() {
        if (listaVinos.size() > 0) {
            listaVinos.clear();
        }
        if (listaCopia.size() > 0) {
            listaCopia.clear();
        }
        //referencia la coleccion de firebase
        final CollectionReference medidasInfo = db.collection("coleccion").document("mis_vinos").collection("vinitos");

        //coger la fecha mas nueva
        medidasInfo.orderBy("nombre", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            final CollectionReference comentarios = db.collection("coleccion").document("mis_vinos").collection("vinitos").document(documentSnapshot.getString("nombre")).collection("Comentarios");
                            final List<ClaseComentario>listaComentarios=new ArrayList<>();
                            comentarios.orderBy("fecha", Query.Direction.DESCENDING)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            for (QueryDocumentSnapshot coment:task.getResult()){
                                                ClaseComentario miComentario= new ClaseComentario(coment.getString("nombre"),coment.getDouble("valoracion").floatValue(),coment.getString("comentario"));
                                                listaComentarios.add(miComentario);
                                            }
                                        }
                                    });
                            Log.d(TAG, documentSnapshot.getId() + " => " + documentSnapshot.getData());

                            //se guarda la nueva medida
                            Vino miVino = new Vino(documentSnapshot.getString("nombre"), documentSnapshot.getDouble("valoracion").floatValue(),documentSnapshot.getString("descripcion"), documentSnapshot.getString("tipo"), documentSnapshot.getString("foto"), listaComentarios);
                            listaVinos.add(miVino);
                            adaptador.notifyDataSetChanged();
                            //ocultar el contenedor de la imagen de carga y mostrar el contenido

                        }
                        recyclerView.setAdapter(adaptador);
                    }
                });


    }
    public void buscador(String text){
        if(text.isEmpty()){
            listaVinos.clear();
            listaVinos.addAll(listaCopia);
            listaCopia.clear();
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
        medidasInfo.orderBy("nombre", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            final CollectionReference comentarios = db.collection("coleccion").document("mis_vinos").collection("vinitos").document(documentSnapshot.getString("nombre")).collection("Comentarios");
                            final List<ClaseComentario>listaComentarios=new ArrayList<>();
                            comentarios.orderBy("fecha", Query.Direction.DESCENDING)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            for (QueryDocumentSnapshot coment:task.getResult()){
                                                ClaseComentario miComentario= new ClaseComentario(coment.getString("nombre"),coment.getDouble("valoracion").floatValue(),coment.getString("comentario"));
                                                listaComentarios.add(miComentario);
                                            }
                                        }
                                    });
                            Log.d(TAG, documentSnapshot.getId() + " => " + documentSnapshot.getData());

                            //se guarda la nueva medida
                            Vino miVino = new Vino(documentSnapshot.getString("nombre"), documentSnapshot.getDouble("valoracion").floatValue(),documentSnapshot.getString("descripcion"), documentSnapshot.getString("tipo"), documentSnapshot.getString("foto"), listaComentarios);
                            listaVinos.add(miVino);
                            adaptador.notifyDataSetChanged();
                            //ocultar el contenedor de la imagen de carga y mostrar el contenido

                        }
                    }
                });
    }


    private void addItemsOnSpinner(View v) {
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getContext(),R.array.tipos ,android.R.layout.simple_spinner_item);
        spinner=  v.findViewById(R.id.tipo);
        spinner.setAdapter(spinnerAdapter);
    }

}
