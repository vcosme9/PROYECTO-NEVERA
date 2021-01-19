package com.example.proyectovinoteca.comentarios;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectovinoteca.R;
import com.example.proyectovinoteca.Tab_Alertas.ClaseAlerta;

import java.util.ArrayList;

public class ComentariosAdapter extends RecyclerView.Adapter<ComentariosAdapter.ProductosViewHolder> {
    private static final String TAG = "ProductosAdapter";

    ArrayList<ClaseComentario> listaComentarios;
    String producto;

    public ComentariosAdapter(ArrayList<ClaseComentario> listaComentarios) {
        this.listaComentarios = listaComentarios;

    }

    @Override
    public ProductosViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.elemento_comentario,null,false);
        return new ProductosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductosViewHolder holder, int position) {
        holder.txtNombre.setText(listaComentarios.get(position).getNombre());
        holder.valoracion.setRating(listaComentarios.get(position).getValoracion());
        holder.comentario.setText(listaComentarios.get(position).getComentario());
    }

    @Override
    public int getItemCount() {
        return listaComentarios.size();
    }
    public class ProductosViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre;
        RatingBar valoracion;
        TextView comentario;

        public ProductosViewHolder(View itemView) {
            super(itemView);
            txtNombre = (TextView) itemView.findViewById(R.id.textView10);
            valoracion = (RatingBar) itemView.findViewById(R.id.ratingBar2);
            comentario = (TextView) itemView.findViewById(R.id.textView12);
        }
    }






}