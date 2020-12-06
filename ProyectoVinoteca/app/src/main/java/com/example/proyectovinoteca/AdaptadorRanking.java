package com.example.proyectovinoteca;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


public class AdaptadorRanking extends
        RecyclerView.Adapter<AdaptadorRanking.ViewHolder> {
    protected View.OnClickListener onClickListener;
    protected RepositorioGlobal vinos;
    public AdaptadorRanking(RepositorioGlobal vinos) {
        this.vinos = vinos;
    }
    //Creamos nuestro ViewHolder, con los tipos de elementos a modificar
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nombre;
        public ImageView foto;
        public RatingBar valoracion;

        public ViewHolder(View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.idNombre);
            foto = itemView.findViewById(R.id.idImagen);
            valoracion = itemView.findViewById(R.id.ratingBar);
        }
        public void personaliza(Vino vino){
            nombre.setText(vino.getNombre());
            foto.setImageResource(vino.getImagenId());
            valoracion.setRating(vino.getValoracion());

        }
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
        Vino vino =vinos.elemento(posicion);
        holder.personaliza(vino);
    }
    // Indicamos el número de elementos de la lista
    @Override public int getItemCount() {
        return vinos.tamaño();
    }

}
