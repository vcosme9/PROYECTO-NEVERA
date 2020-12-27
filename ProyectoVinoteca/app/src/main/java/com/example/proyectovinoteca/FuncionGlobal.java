package com.example.proyectovinoteca;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;

public class FuncionGlobal {
    Context mContext;
    public ViewGroup contenedor;

    public FuncionGlobal(Context mContext) {
        this.mContext = mContext;
    }
    //---------------------------------------------------

    public void mensaje(String mensaje) {
        Snackbar.make(contenedor, mensaje, Snackbar.LENGTH_LONG).show();
    }

    public ProgressDialog createDialog(Context c, String title, String message){
        ProgressDialog dialogo = new ProgressDialog(c);
        dialogo.setTitle(title);
        dialogo.setMessage(message);
        dialogo.setIcon(R.mipmap.ic_custom_launcher_2_round);
        return dialogo;
    }

    //---------------------------------------------------
}
