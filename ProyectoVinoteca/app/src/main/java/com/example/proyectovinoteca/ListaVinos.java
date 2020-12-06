package com.example.proyectovinoteca;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class ListaVinos implements RepositorioGlobal {
    protected List<Vino> listaVinos;

    public ListaVinos() {
        listaVinos = new ArrayList<Vino>();
    }

    @Override
    public Vino elemento(int id) {
        return listaVinos.get(id);
    }
    @Override
    public void añade(Vino vino){
        listaVinos.add(vino);
    }
    @Override
    public void nuevo() {
        Vino vino = new Vino();
        listaVinos.add(vino);

    }

    @Override
    public void borrar(int id) {
        listaVinos.remove(id);

    }

    @Override
    public int tamaño() {
        return listaVinos.size();
    }

    @Override
    public void actualiza(int id, Vino vino) {
        listaVinos.set(id, vino);
    }
    public void añadeEjemplos(){
        añade(new Vino("vino 1", 4, "fsaaffdsafafssa", R.drawable.productos));
        añade(new Vino("vino 2", 2, "fsaaffdsafafssa", R.drawable.productos));
        añade(new Vino("vino 3", 1, "fsaaffdsafafssa", R.drawable.productos));
        añade(new Vino("vino 4", 4, "fsaaffdsafafssa", R.drawable.productos));
        añade(new Vino("vino 5", 5, "fsaaffdsafafssa", R.drawable.productos));

    }


}
