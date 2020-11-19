package com.example.proyectovinoteca;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterActivity extends Activity {
    private String email;
    private String password;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private String correo = "";
    private String contraseña = "";
    private ViewGroup contenedor;
    private EditText etCorreo, etContraseña, etNombre;
    private TextInputLayout tilCorreo, tilContraseña;
    private ProgressDialog dialogo;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        email = getIntent().getStringExtra("email");
        password = getIntent().getStringExtra("password");
        etCorreo = (EditText) findViewById(R.id.correo);
        etCorreo.setText(email);
        etContraseña = (EditText) findViewById(R.id.contraseña);
        etNombre = (EditText) findViewById(R.id.nombre);
        etContraseña.setText(password);
        tilCorreo = (TextInputLayout) findViewById(R.id.til_correo);
        tilContraseña = (TextInputLayout) findViewById(R.id.til_contraseña);
        contenedor = (ViewGroup) findViewById(R.id.contenedor);
        dialogo = new ProgressDialog(this);
        dialogo.setTitle("Verificando usuario");
        dialogo.setMessage("Por favor espere...");
        verificaSiUsuarioValidado();
    }
    private void verificaSiUsuarioValidado() {
        if (auth.getCurrentUser() != null) {
            Intent i = new Intent(this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            i.putExtra("email", etCorreo.getText());
            i.putExtra("password", etContraseña.getText());
            startActivity(i);
            finish();
        }
    }

    public void lanzarInicioSesion(View v) {
        Intent i = new Intent(this, CustomLoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        String res = etCorreo.getText().toString();
        i.putExtra("email", etCorreo.getText().toString());
        i.putExtra("password", etContraseña.getText().toString());
        startActivity(i);
        finish();
    }
    public void registroCorreo(View v) {
        if (verificaCampos()) {
            dialogo.show();
            auth.createUserWithEmailAndPassword(correo, contraseña)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                updatename();
                            } else {
                                dialogo.dismiss();
                                mensaje(task.getException().getLocalizedMessage());
                            }
                        }
                    });
        }
    }

    private void updatename() {
        //updating user's profile data
        String nameUser = etNombre.getText().toString();
        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                .setDisplayName(nameUser)
                .build();

        if (TextUtils.isEmpty(nameUser)) {
            etNombre.setError("Introduce tu nombre completo");
        }

        //if the field is not null, process continue to update profile
        else {
            auth.getCurrentUser().updateProfile(profileUpdate)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(Task<Void> task) {
                            if (task.isSuccessful()) { //success on updating user profile
                                verificaSiUsuarioValidado();
                            } else { //failed on updating user profile
                                Toast.makeText(RegisterActivity.this, "Ha ocurrido un error",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void mensaje(String mensaje) {
        Snackbar.make(contenedor, mensaje, Snackbar.LENGTH_LONG).show();
    }
    private boolean verificaCampos() {
        correo = etCorreo.getText().toString();
        contraseña = etContraseña.getText().toString();
        tilCorreo.setError(""); tilContraseña.setError("");
        if (correo.isEmpty()) {
            tilCorreo.setError("Introduce un correo");
        } else if (!correo.matches(".+@.+[.].+")) {
            tilCorreo.setError("Correo no válido");
        } else if (contraseña.isEmpty()) {
            tilContraseña.setError("Introduce una contraseña");
        } else if (contraseña.length()<6) {
            tilContraseña.setError("Ha de contener al menos 6 caracteres");
        } else if (!contraseña.matches(".*[0-9].*")) {
            tilContraseña.setError("Ha de contener un número");
        } else if (etNombre.getText().toString().isEmpty()){
            tilContraseña.setError("Has de introducir un nombre de usuario");
        } else {
            return true;
        }
        return false;
    }
    public void firebaseUI(View v) {
        startActivity(new Intent(this, LoginActivity.class));
    }
}
