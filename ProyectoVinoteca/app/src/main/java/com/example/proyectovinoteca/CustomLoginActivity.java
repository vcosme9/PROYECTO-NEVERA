package com.example.proyectovinoteca;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.auth.UserInfo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.List;

import static android.content.ContentValues.TAG;
//
//
public class CustomLoginActivity extends AppCompatActivity {

    private String email, password;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private ViewGroup contenedor;
    private EditText etCorreo, etContraseña;
    private TextInputLayout tilCorreo, tilContraseña;
    private ProgressDialog dialogo;
    private static final int RC_SIGN_IN = 123;
    private GoogleApiClient googleApiClient;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_login);

        //para el inicio con Google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        SignInButton googleLogin = findViewById(R.id.firebase_ui);
        googleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginGoogle(v);
            }
        });
        Button customLogin = findViewById(R.id.btn_inicio_sesion);
        customLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inicioSesionCorreo(v);
            }
        });

        //en caso de haber puesto el email/contraseña en la pestaña de registro, se pondrán aquí automáticamente
        email = getIntent().getStringExtra("email");
        password = getIntent().getStringExtra("password");
        etCorreo = findViewById(R.id.correo);
        etCorreo.setText(email);
        etContraseña = findViewById(R.id.contraseña);
        etContraseña.setText(password);
        tilCorreo = findViewById(R.id.til_correo);
        tilContraseña = findViewById(R.id.til_contraseña);
        contenedor = findViewById(R.id.contenedor);

        //para poner una carga mientras se realiza una acción
        dialogo = new ProgressDialog(this);
        dialogo.setTitle("Verificando usuario");
        dialogo.setMessage("Por favor espere...");
        dialogo.setIcon(R.mipmap.ic_custom_launcher_2_round);
        //se intenta entrar a la app directamente. Este caso se daría si abrimos la app pero ya
        //habíamos iniciado sesión
        FirebaseUser u = auth.getCurrentUser();
        if (u != null) {
            if(u.isEmailVerified()) {
                abrirApp();
            }
        }
    }

    //abre la app (abre el MainActivity)
    private void abrirApp(){
        Intent i = new Intent(this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

    public void inicioSesionCorreo(View v) {
        if (verificaCampos()) {
            dialogo.show();
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                abrirApp();
                            } else {
                                dialogo.dismiss();
                                Log.d(ValorGlobal.log, task.getException().getMessage());
                                mensaje("Usuario inexistente o no validado");
                            }
                        }
                    });
        }
    }

    public void lanzarRegistro(View v) {
        Intent i = new Intent(this, RegisterActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.putExtra("email", etCorreo.getText().toString());
        i.putExtra("password", etContraseña.getText().toString());
        startActivity(i);
        finish();
    }

    //usado para mostrar mensajes de error por pantalla
    private void mensaje(String mensaje) {
        Snackbar.make(contenedor, mensaje, Snackbar.LENGTH_LONG).show();
    }

    //si se cumplen las condiciones se podrá registrar el usuario
    private boolean verificaCampos() {
        email = etCorreo.getText().toString();
        password = etContraseña.getText().toString();
        tilCorreo.setError(""); tilContraseña.setError("");
        if (email.isEmpty()) {
            tilCorreo.setError("Introduce un correo");
        } else if (!email.matches(".+@.+[.].+")) {
            tilCorreo.setError("Correo no válido");
        } else if (password.isEmpty()) {
            tilContraseña.setError("Introduce una contraseña");
        } else if (password.length()<6) {
            tilContraseña.setError("Ha de contener al menos 6 caracteres");
        } else if (!password.matches(".*[0-9].*")) {
            tilContraseña.setError("Ha de contener un número");
        } else {
            return true;
        }
        return false;
    }

    //inicia la actividad del API de Google
    public void loginGoogle(View v) {
        Intent i = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(i, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK ) {
                GoogleSignInResult result = Auth.GoogleSignInApi
                        .getSignInResultFromIntent(data);
                if (result.isSuccess()) {
                    dialogo.show();
                    googleAuth(result.getSignInAccount());
                } else {
                    Log.d(ValorGlobal.log, result.getStatus().getStatusMessage());
                    mensaje("Error de autentificación con Google");
                }
            }
        }
    }

    //se intenta iniciar sesión con el usuario de Google
    private void googleAuth(GoogleSignInAccount acct) {
    final AuthCredential credential = GoogleAuthProvider.getCredential(
            acct.getIdToken(), null);
        final FirebaseAuth i = FirebaseAuth.getInstance();
        i.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Log.d(ValorGlobal.log, task.getException().toString());
                            dialogo.dismiss();
                            mensaje(task.getException().getLocalizedMessage());
                        } else {
                            //compruebo si ya existe una cuenta creada con email y pass. Si es así, puedo entrar
                            i.fetchSignInMethodsForEmail(i.getCurrentUser().getEmail()).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                                @Override
                                public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                                    //----------------------------
                                    List<? extends UserInfo> providerData = i.getCurrentUser().getProviderData();
                                    for (UserInfo userInfo : providerData ) {
                                        String providerId = userInfo.getProviderId();
                                        Log.d("aaaaaaaaaa", "providerId = " + providerId);
                                    }
                                    //----------------------------
                                    SignInMethodQueryResult result = task.getResult();
                                    List<String> signInMethods = result.getSignInMethods();
                                    if(signInMethods.contains(EmailAuthProvider.EMAIL_PASSWORD_SIGN_IN_METHOD) && i.getCurrentUser().isEmailVerified()){
                                        //por defecto siempre linkeará al usuario si se detecta que se ha verificado el email
                                        try{i.getCurrentUser().linkWithCredential(credential);} catch (Exception e) {}
                                        abrirApp();
                                    } else {
                                        i.getCurrentUser().delete();
                                        //si no es el caso, borro el usuario que se acaba de crear y salgo de la sesión de Google
                                        AuthUI.getInstance().signOut(getApplicationContext()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                dialogo.dismiss();
                                                mensaje("Ninguna cuenta vinculada");
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    }
                });

    }
}
