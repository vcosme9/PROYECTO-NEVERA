package com.example.proyectovinoteca;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.proyectovinoteca.AdaptadorRanking;
import com.example.proyectovinoteca.R;
import com.example.proyectovinoteca.Tab_Alertas.ClaseAlerta;
import com.example.proyectovinoteca.Tab_Productos.ProductosAdapter;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CamaraAdapter extends RecyclerView.Adapter<CamaraAdapter.ProductosViewHolder> {
    private static final String TAG = "ProductosAdapter";
    private View.OnClickListener onClickListener;
    SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");

    ArrayList<ClaseFoto> listaFotos;

    public CamaraAdapter(ArrayList<ClaseFoto> listaFotos) {
        this.listaFotos = listaFotos;
    }

    @Override
    public ProductosViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_camara,null,false);
        view.setOnClickListener(onClickListener);
        return new ProductosViewHolder(view);
    }

    public void setOnItemClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public void onBindViewHolder(ProductosViewHolder holder, int position) {
        ClaseFoto f = listaFotos.get(position);
        holder.fecha.setText(holder.formatter.format(f.getFecha()));
        Picasso.get()
                .load(f.getFoto())
                .error(R.drawable.alerta)
                .into(holder.foto);
        holder.foto.setScaleType(ImageView.ScaleType.FIT_END);
    }

    @Override
    public int getItemCount() {
        return listaFotos.size();
    }
    public class ProductosViewHolder extends RecyclerView.ViewHolder {
        TextView fecha;
        ImageView foto;
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");

        public ProductosViewHolder(View itemView) {
            super(itemView);
            fecha = (TextView) itemView.findViewById(R.id.idNombre);
            foto = (ImageView) itemView.findViewById(R.id.idImagen);
        }
    }

}
