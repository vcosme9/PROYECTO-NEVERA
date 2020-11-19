package com.example.proyectovinoteca;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.proyectovinoteca.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CuentaFragment extends Fragment {
    private perfilViewModel perfilViewModel;
    public Button editarPersona;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        perfilViewModel =
                ViewModelProviders.of(this).get(perfilViewModel.class);
        View root = inflater.inflate(R.layout.opcion_cuenta, container, false);

        // Conectamos con el  FireBase.
        final FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();

        //aqui se declaran las partes


        final TextView textView_nombre = root.findViewById(R.id.nombre_TV);
        final TextView textView_correo = root.findViewById(R.id.correo_TV);
        final TextView textView_proveedor = root.findViewById(R.id.proveedorFB_TB);
        final TextView textView_rol = root.findViewById(R.id.rolFB_TB);


        perfilViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            //Aqui se les añade el texto a las partes.
            public void onChanged(@Nullable String s) {

                // Mostrar actual información del usuario
                textView_nombre.setText(usuario.getDisplayName());
                textView_correo.setText(usuario.getEmail());
                textView_proveedor.setText(usuario.getProviderId());
                textView_rol.setText("Rol no definido.");


            }
        });
        return root;
    }
}
