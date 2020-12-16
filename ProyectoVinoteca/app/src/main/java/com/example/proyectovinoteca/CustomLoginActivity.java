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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.List;

import static android.content.ContentValues.TAG;
//
//
public class CustomLoginActivity extends AppCompatActivity {
    private String email;
    private String password;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private String correo;
    private String contraseña;
    private ViewGroup contenedor;
    private EditText etCorreo, etContraseña;
    private TextInputLayout tilCorreo, tilContraseña;
    private ProgressDialog dialogo;
    private static final int RC_SIGN_IN = 123;
    private GoogleApiClient googleApiClient;
    private boolean link;
    private GoogleSignInOptions gso;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_login);
        link = getIntent().getBooleanExtra("link", false);
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

        email = getIntent().getStringExtra("email");
        password = getIntent().getStringExtra("password");
        String appJustOpened = getIntent().getStringExtra("bool");
        etCorreo = findViewById(R.id.correo);
        etCorreo.setText(email);
        etContraseña = findViewById(R.id.contraseña);
        etContraseña.setText(password);
        tilCorreo = findViewById(R.id.til_correo);
        tilContraseña = findViewById(R.id.til_contraseña);
        contenedor = findViewById(R.id.contenedor);
        dialogo = new ProgressDialog(this);
        dialogo.setTitle("Verificando usuario");
        dialogo.setMessage("Por favor espere...");
        if(link){
            loginGoogle(null);
        } else {
            if (auth.getCurrentUser() != null) {
                if (auth.getCurrentUser().isEmailVerified()) {
                    Intent i = new Intent(this, MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                            | Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    finish();
                }
            }
        }
    }
    private void verificaSiUsuarioValidado() {
        Log.d("aaaaaa", "sí, entra por aquí");
        if (FirebaseAuth.getInstance().getCurrentUser() != null && !link && FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
            Intent i = new Intent(this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        } else {
            Toast.makeText(this, "El correo no está autentificado", Toast.LENGTH_SHORT).show();
            //dialogo.dismiss();
        }
    }

    public void inicioSesionCorreo(View v) {
        if (verificaCampos()) {
            //dialogo.show();
            auth.signInWithEmailAndPassword(correo, contraseña)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                verificaSiUsuarioValidado();
                            } else {
                                dialogo.dismiss();
                                mensaje(task.getException().getLocalizedMessage());
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
        } else {
            return true;
        }
        return false;
    }
    public void loginGoogle(View v) {
        /*FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            if (user.isEmailVerified()) {
                Intent i = new Intent(this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
            } else {
                Toast.makeText(this, "Mira tu correo ... ", Toast.LENGTH_LONG).show();
                user.sendEmailVerification();
                FirebaseAuth.getInstance().signOut();
                loginGoogle(null);
            }
        } else {
            List<AuthUI.IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.GoogleBuilder().build());
            startActivityForResult(
                    AuthUI.getInstance().createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .setTheme(R.style.LoginTheme)
                            .setIsSmartLockEnabled(false)
                            .build(),
                    RC_SIGN_IN);
        }*/
        Intent i = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(i, RC_SIGN_IN);

        /*googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.d("aaaaaa", "weón");
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();*/

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK ) {
                GoogleSignInResult result = Auth.GoogleSignInApi
                        .getSignInResultFromIntent(data);
                if (result.isSuccess()) {
                    googleAuth(result.getSignInAccount());
                } else {
                    mensaje("Error de autentificación con Google");
                }
            }
        }

    }
    private void googleAuth(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(
                acct.getIdToken(), null);
        if(link){
            unificarCon(credential);
        } else {

            FirebaseAuth.getInstance().signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Log.d("aaaaaa", "Parece que NO se pudo");
                                mensaje(task.getException().getLocalizedMessage());
                            } else {
                                Log.d("aaaaaa", "Parece que SÍ se pudo");
                                //compruebo si ya existe una cuenta creada con email y pass. Si es así, puedo entrar
                                FirebaseAuth.getInstance().fetchSignInMethodsForEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail()).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                                        SignInMethodQueryResult result = task.getResult();
                                        List<String> signInMethods = result.getSignInMethods();
                                        if(signInMethods.contains(EmailAuthProvider.EMAIL_PASSWORD_SIGN_IN_METHOD)){
                                            Log.d("aaaaaa", "tiene email");
                                            verificaSiUsuarioValidado();
                                        } else {
                                            Log.d("aaaaaa", "o igual no");
                                            Toast.makeText(CustomLoginActivity.this, "No hay ninguna cuenta vinculada con esta cuenta de Google", Toast.LENGTH_SHORT).show();
                                            FirebaseAuth.getInstance().getCurrentUser().delete();
                                        }
                                    }
                                });
                            }
                        }
                    });
        }
    }

    private void unificarCon(AuthCredential credential) {
        auth.getCurrentUser().linkWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>(){
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            verificaSiUsuarioValidado();
                        } else {
                            Log.w("aaaaaa", "Error en linkWithCredential",
                                    task.getException());
                            mensaje("Error al unificar cuentas.");
                        }
                    }
                });
    }
}
