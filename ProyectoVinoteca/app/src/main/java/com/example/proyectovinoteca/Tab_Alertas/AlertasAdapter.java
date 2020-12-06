package com.example.proyectovinoteca.Tab_Alertas;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectovinoteca.R;

import java.util.ArrayList;

public class AlertasAdapter extends RecyclerView.Adapter<AlertasAdapter.ProductosViewHolder> {
    private static final String TAG = "ProductosAdapter";

    ArrayList<com.example.proyectovinoteca.Tab_Alertas.ClaseAlerta> listaAlertas;
    String producto;

    public AlertasAdapter(ArrayList<ClaseAlerta> listaAlertas) {
        this.listaAlertas = listaAlertas;

    }

    @Override
    public ProductosViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_alertas,null,false);
        return new ProductosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductosViewHolder holder, int position) {
        holder.txtNombre.setText(listaAlertas.get(position).getNombre());
        holder.txtInformacion.setText(listaAlertas.get(position).getInfo());
        holder.foto.setImageResource(listaAlertas.get(position).getImagenId());
    }

    @Override
    public int getItemCount() {
        return listaAlertas.size();
    }
    public class ProductosViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtInformacion;
        ImageView foto;

        public ProductosViewHolder(View itemView) {
            super(itemView);
            txtNombre = (TextView) itemView.findViewById(R.id.idNombre);
            txtInformacion = (TextView) itemView.findViewById(R.id.idInfo);
            foto = (ImageView) itemView.findViewById(R.id.idImagen);
        }
    }






}
