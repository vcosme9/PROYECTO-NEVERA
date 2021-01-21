package com.example.proyectovinoteca;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdaptadorComunidad extends
        RecyclerView.Adapter<AdaptadorComunidad.ViewHolder> {
    private View.OnClickListener onClickListener;
    private ArrayList<Articulo> articulos;
    public AdaptadorComunidad(ArrayList<Articulo> articulos) {
        this.articulos = articulos;

    }
    // Creamos el ViewHolder con la vista de un elemento sin personalizar
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflamos la vista desde el xml
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.elemento_comunidad, parent, false);
        v.setOnClickListener(onClickListener);
        return new ViewHolder(v);
    }
    // Usando como base el ViewHolder y lo personalizamos
    @Override
    public void onBindViewHolder(ViewHolder holder, int posicion) {
        Articulo articulo = articulos.get(posicion);
        holder.personaliza(articulo);
    }
    // Indicamos el número de elementos de la lista
    @Override public int getItemCount() {
        return articulos.size();
    }
    public void setOnItemClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
    //Creamos nuestro ViewHolder, con los tipos de elementos a modificar
    public static class ViewHolder extends RecyclerView.ViewHolder  {
        public TextView nombre, valoracion;
        public LinearLayout container;
        public ImageView foto;

        public ViewHolder(final View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.container);
            nombre = itemView.findViewById(R.id.nombre);
            valoracion = itemView.findViewById(R.id.valoracion);
            foto = itemView.findViewById(R.id.foto);
        }
        public void personaliza(Articulo articulo){
            nombre.setText(articulo.getNombre());
            valoracion.setText(String.valueOf(articulo.getValoracion()));
            Picasso.get()
                    .load(articulo.getFoto())
                    .error(R.drawable.alerta)
                    .into(foto);
            foto.setScaleType(ImageView.ScaleType.FIT_END);
        }


    }
}