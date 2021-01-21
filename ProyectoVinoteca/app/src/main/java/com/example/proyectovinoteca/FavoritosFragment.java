package com.example.proyectovinoteca;

<<<<<<< Updated upstream
import android.app.Activity;
=======
>>>>>>> Stashed changes
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
<<<<<<< Updated upstream
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
=======
>>>>>>> Stashed changes
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.example.proyectovinoteca.R;
import com.example.proyectovinoteca.comentarios.ComentariosActivity;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;
import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class FavoritosFragment extends Fragment {

    private FuncionGlobal fG;
    private ProgressDialog dialogo;
    private ViewGroup vG;
    private final ArrayList<Articulo> listaArticulos = new ArrayList<>();
    private RecyclerView recyclerView, rV2, rV3;
    public AdaptadorComunidad adaptador;
<<<<<<< Updated upstream
    private String filtro = "nombre";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle bundle) {

        View v = inflater.inflate(R.layout.opcion_favoritos, container, false);
        Button b = v.findViewById(R.id.btn_ver_mas_1);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new CatalogoFragment();
                Bundle args = new Bundle();
                filtro = "valoracion";
                args.putString("filtro", filtro);
                fragment.setArguments(args);
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, fragment);
                fragmentTransaction.commit();
            }
        });
=======

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View v = inflater.inflate(R.layout.opcion_favoritos, container, false);

>>>>>>> Stashed changes
        fG = new FuncionGlobal(getContext());
        vG = v.findViewById(R.id.contenedor);
        fG.contenedor = vG;
        vG.setVisibility(View.GONE);
        dialogo = fG.createDialog(getContext(), "Cargando datos", "Por favor espere...");
        dialogo.show();
        adaptador = new AdaptadorComunidad(listaArticulos);
        loadDataFromFirestore();

        recyclerView = v.findViewById(R.id.recyclerView1);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adaptador);
        rV2 = v.findViewById(R.id.recyclerView2);
        rV2.setHasFixedSize(true);
        rV2.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rV2.setAdapter(adaptador);
        rV3 = v.findViewById(R.id.recyclerView3);
        rV3.setHasFixedSize(true);
        rV3.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rV3.setAdapter(adaptador);

        adaptador.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RelativeLayout rL = (RelativeLayout) ((LinearLayout)v).getChildAt(0);
                TextView tV = (TextView)((LinearLayout)rL.getChildAt(1)).getChildAt(0);
                float fl = Float.valueOf(tV.getText().toString());
                ImageView iV = (ImageView) rL.getChildAt(0);
                Bitmap bm = ((BitmapDrawable) iV.getDrawable()).getBitmap();

                //convertir bitmap en byte array
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                TextView t = (TextView) ((LinearLayout)v).getChildAt(1);
                String n = t.getText().toString();
                Intent i = new Intent(getContext(), ComentariosActivity.class);
                i.putExtra("valoracion", fl);
                i.putExtra("nombre", n);
                i.putExtra("imagen", byteArray);
                startActivity(i);
            }
        });
<<<<<<< Updated upstream

=======
>>>>>>> Stashed changes
        return v;
    }

    private void loadDataFromFirestore() {

        if (listaArticulos.size() > 0) {
            listaArticulos.clear();
        }
        //referencia la coleccion de firebase
        final CollectionReference medidasInfo = FirebaseFirestore.getInstance().collection("COMUNIDAD");
        //coger la fecha mas nueva
        medidasInfo.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            //se guarda la nueva medida
                            listaArticulos.add(new Articulo(documentSnapshot.getString("nombre"), documentSnapshot.getString("foto"), documentSnapshot.getDouble("valoracion").floatValue()));
                            adaptador.notifyDataSetChanged();
                        }
                        dialogo.hide();
                        vG.setVisibility(View.VISIBLE);
                    }
                });
    }
}
