package com.example.proyectovinoteca;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class editarPerfil extends Activity {

    private EditText campoNombre;
    private String email;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();

        Button volver1 = findViewById(R.id.btn_volver_1);
        Button volver2 = findViewById(R.id.btn_volver_2);
        Button cambiar1 = findViewById(R.id.cambiarPerfil);
        Button cambiar2 = findViewById(R.id.cambiarExtra);
        Button cambiarContraseña = findViewById(R.id.cambiarConstrasenya);
        campoNombre = findViewById(R.id.nombre_ET);
        email = usuario.getEmail();

        campoNombre.setText(usuario.getDisplayName());

        View.OnClickListener volver = new View.OnClickListener(){
            public void onClick(View view) {
                finish();
            }
        };

        cambiarContraseña.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                FirebaseAuth.getInstance().sendPasswordResetEmail(email);
                Toast.makeText(editarPerfil.this, "Revisa tu correo", Toast.LENGTH_SHORT).show();
            }
        });
        cambiar1.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                actualizarNombre();
            }
        });
        volver1.setOnClickListener(volver);
        volver2.setOnClickListener(volver);

    }

    private void actualizarNombre() {
        String nameUser = campoNombre.getText().toString();
        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                .setDisplayName(nameUser)
                .build();

        if (TextUtils.isEmpty(nameUser)) {
            Toast.makeText(this, "Has de introducir un nombre de usuario", Toast.LENGTH_SHORT).show();
        }

        //if the field is not null, process continue to update profile
        else {
            FirebaseAuth.getInstance().getCurrentUser().updateProfile(profileUpdate)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(Task<Void> task) {
                            if (task.isSuccessful()) { //success on updating user profile
                                Toast.makeText(editarPerfil.this, "Nombre de usuario actualizado", Toast.LENGTH_SHORT).show();
                            } else { //failed on updating user profile
                                Toast.makeText(editarPerfil.this, "Ha ocurrido un error",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}
