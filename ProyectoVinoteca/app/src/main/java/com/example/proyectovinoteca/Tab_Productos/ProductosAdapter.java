package com.example.proyectovinoteca.Tab_Productos;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.proyectovinoteca.R;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class ProductosAdapter extends RecyclerView.Adapter<ProductosAdapter.ProductosViewHolder> {
    private static final String TAG = "ProductosAdapter";

    ArrayList<ClaseProducto> listaProductos;
    String producto;

    public ProductosAdapter(ArrayList<ClaseProducto> listaProducto) {
        this.listaProductos = listaProducto;

    }

    @Override
    public ProductosViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem,null,false);
        return new ProductosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductosViewHolder holder, int position) {
        holder.txtNombre.setText(listaProductos.get(position).getNombre());
        holder.txtInformacion.setText(listaProductos.get(position).getInfo());
        holder.foto.setImageResource(listaProductos.get(position).getImagenId());
    }

    @Override
    public int getItemCount() {
        return listaProductos.size();
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