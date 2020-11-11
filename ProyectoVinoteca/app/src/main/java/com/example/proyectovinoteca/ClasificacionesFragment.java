package com.example.proyectovinoteca;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.proyectovinoteca.R;

public class ClasificacionesFragment extends Fragment {
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle bundle){
        return inflater.inflate(R.layout.opcion_clasificaciones, container, false);
    }
}
