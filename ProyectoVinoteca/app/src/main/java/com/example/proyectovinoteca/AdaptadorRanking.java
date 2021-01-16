package com.example.proyectovinoteca;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class AdaptadorRanking extends
        RecyclerView.Adapter<AdaptadorRanking.ViewHolder> {
    private View.OnClickListener onClickListener;
    private ArrayList<Vino> vinos;
    public AdaptadorRanking(ArrayList<Vino> vinos) {
        this.vinos = vinos;

    }
    public void filter(String tipo,ArrayList<Vino>listaCopia){
        if(vinos.size()>0)
            vinos.clear();
        if(tipo.equals("Seleccionar...")){
            vinos.addAll(listaCopia);

        }
        else {
            for (int i=0;i<listaCopia.size();i++){
                if (listaCopia.get(i).getTipo().equals(tipo))
                    vinos.add(listaCopia.get(i));
            }
        }
        this.notifyDataSetChanged();
    }
    // Creamos el ViewHolder con la vista de un elemento sin personalizar
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflamos la vista desde el xml
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.elemento_ranking, parent, false);
        v.setOnClickListener(onClickListener);

        return new ViewHolder(v);
    }
    // Usando como base el ViewHolder y lo personalizamos
    @Override
    public void onBindViewHolder(ViewHolder holder, int posicion) {
        Vino vino =vinos.get(posicion);
        holder.personaliza(vino);
    }
    // Indicamos el número de elementos de la lista
    @Override public int getItemCount() {
        return vinos.size();
    }
    public void setOnItemClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
    //Creamos nuestro ViewHolder, con los tipos de elementos a modificar
    public static class ViewHolder extends RecyclerView.ViewHolder  {
        private TextView nombre;
        private ImageView foto;
        private RatingBar valoracion;
        private CardView cardView;

        public ViewHolder(final View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card);
            nombre = itemView.findViewById(R.id.idNombre);
            foto = itemView.findViewById(R.id.idImagen);
            valoracion = itemView.findViewById(R.id.rating);
            valoracion.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, final float rating, boolean fromUser) {
                    if(fromUser){
                        final FirebaseFirestore db = FirebaseFirestore.getInstance();
                        final CollectionReference collectionReference = db.collection("coleccion").document("mis_vinos").collection("vinitos");
                        collectionReference.orderBy("nombre")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                                            if(documentSnapshot.getString("nombre").equals(nombre.getText().toString()))
                                                db.collection("coleccion").document("mis_vinos").collection("vinitos").document(documentSnapshot.getId()).update("valoracion",rating);
                                        }

                                    }
                                });
                    }
                }
            });
        }
        public void personaliza(Vino vino){
            nombre.setText(vino.getNombre());
            foto.setImageResource(vino.getImagenId());
            valoracion.setRating(vino.getValoracion());
        }


    }
}
