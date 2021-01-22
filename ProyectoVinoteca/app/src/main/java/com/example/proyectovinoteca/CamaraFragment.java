package com.example.proyectovinoteca;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.proyectovinoteca.AdaptadorComunidad;
import com.example.proyectovinoteca.AmpliarFotoActivity;
import com.example.proyectovinoteca.Articulo;
import com.example.proyectovinoteca.FuncionGlobal;
import com.example.proyectovinoteca.R;
import com.example.proyectovinoteca.comentarios.ComentariosActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CamaraFragment extends Fragment {

    private static final String TAG = "CamaraFragment";
    private RecyclerView recyclerView;
    public CamaraAdapter adaptador;
    private final ArrayList<ClaseFoto> listaFotos = new ArrayList<>();
    private FuncionGlobal fG;
    private ProgressDialog dialogo;
    private ViewGroup vG;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View v = inflater.inflate(R.layout.camara_fragment, container, false);

        fG = new FuncionGlobal(getContext());
        vG = v.findViewById(R.id.contenedor);
        fG.contenedor = vG;
        vG.setVisibility(View.GONE);
        dialogo = fG.createDialog(getContext(), "Cargando datos", "Por favor espere...");
        dialogo.show();

        adaptador = new CamaraAdapter(listaFotos);

        loadDataFromFirestore();

        recyclerView = v.findViewById(R.id.recyclerId);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerView.setAdapter(adaptador);

        adaptador.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RelativeLayout rL = (RelativeLayout) ((LinearLayout)v).getChildAt(0);
                CardView cV = (CardView)rL.getChildAt(0);
                LinearLayout lY = (LinearLayout)cV.getChildAt(0);
                ImageView iV = (ImageView) lY.getChildAt(0);
                Intent i = new Intent(getContext(), AmpliarFotoActivity.class);
                //convertir bitmap en byte array
                try {
                    Bitmap bm = ((BitmapDrawable) iV.getDrawable()).getBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    i.putExtra("imagen", byteArray);
                } catch(Exception e){}
                startActivity(i);
            }
        });

        return v;
    }

    private void loadDataFromFirestore() {

        if (listaFotos.size() > 0) {
            listaFotos.clear();
        }
        //referencia la coleccion de firebase
        final CollectionReference medidasInfo = FirebaseFirestore.getInstance().collection("CAMARA");
        //coger la fecha mas nueva
        medidasInfo.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            //se guarda la nueva medida
                            listaFotos.add(new ClaseFoto(documentSnapshot.getLong("Fecha"), documentSnapshot.getString("Foto")));
                            adaptador.notifyDataSetChanged();
                        }
                        dialogo.hide();
                        vG.setVisibility(View.VISIBLE);
                    }
                });
    }

}
