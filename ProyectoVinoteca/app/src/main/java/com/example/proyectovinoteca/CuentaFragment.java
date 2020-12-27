package com.example.proyectovinoteca;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

public class CuentaFragment  extends Fragment {

    private FuncionGlobal fG;

    private boolean isCancelled = false, yaGuardado = false;
    private String urlFotoFinal, urlFotoOriginal, nombreOriginal;
    private NetworkImageView fotoUsuario;
    private ImageLoader lectorImagenes;
    private StorageReference storageRef;
    private ProgressDialog dialogo, dialogo2;
    private perfilViewModel perfilViewModel;
    private EditText editText_nombre;
    private FirebaseUser usuario;
    public RelativeLayout btnGuardar, btnFoto, btnCancelar;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        perfilViewModel =
                ViewModelProviders.of(this).get(perfilViewModel.class);
        View root = inflater.inflate(R.layout.opcion_cuenta, container, false);
        RequestQueue colaPeticiones = Volley.newRequestQueue(getContext());
        lectorImagenes = new ImageLoader(colaPeticiones,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap> cache =
                            new LruCache<String, Bitmap>(10);
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }
                });
        fotoUsuario = root.findViewById(R.id.net_foto_user);

        storageRef = FirebaseStorage.getInstance().getReference();
        fG = new FuncionGlobal(getContext());
        fG.contenedor = root.findViewById(R.id.contenedor);
        dialogo = fG.createDialog(getContext(), "Guardando datos", "Por favor espere...");
        dialogo2 = fG.createDialog(getContext(), "Cargando foto", "Por favor espere...");

        // Conectamos con el  FireBase.
        usuario = FirebaseAuth.getInstance().getCurrentUser();

        //aqui se declaran las partes
        Uri urlImagen = usuario.getPhotoUrl();
        if (urlImagen != null) {
            urlFotoFinal = urlImagen.toString();
            urlFotoOriginal = urlImagen.toString();
            fotoUsuario.setImageUrl(urlImagen.toString(), lectorImagenes);
        } else {
            urlFotoFinal = "";
            urlFotoOriginal = "";
            fotoUsuario.setImageResource(R.drawable.user_default);
        }
        nombreOriginal = usuario.getDisplayName();
        btnFoto = root.findViewById(R.id.relLay_foto);
        btnGuardar = root.findViewById(R.id.relLay_guardar);
        btnCancelar = root.findViewById(R.id.relLay_cancelar);
        btnFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                elegirFoto();
            }
        });
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardar();
            }
        });
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelar();
            }
        });
        editText_nombre = root.findViewById(R.id.nombre_ET);
        final TextView textView_correo = root.findViewById(R.id.correo_TV);
        final TextView textView_proveedor = root.findViewById(R.id.proveedorFB_TB);
        final TextView textView_rol = root.findViewById(R.id.rolFB_TB);
        Button cambiarContraseña = root.findViewById(R.id.cambiarConstrasenya);

        cambiarContraseña.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                FirebaseAuth.getInstance().sendPasswordResetEmail(usuario.getEmail()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            fG.mensaje("Revisa tu correo.");
                        } else {
                            fG.mensaje("Ha ocurrido un error. Vuelva a intentarlo más tarde.");
                        }
                    }
                });
            }
        });
        perfilViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            //Aqui se les añade el texto a las partes.
            public void onChanged(@Nullable String s) {

                // Mostrar actual información del usuario
                editText_nombre.setText(usuario.getDisplayName());
                textView_correo.setText(usuario.getEmail());
                textView_proveedor.setText(usuario.getProviderId());
                textView_rol.setText("Rol no definido.");
            }
        });
        return root;
    }

    public void elegirFoto() {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        startActivityForResult(i, 1234);
    }

    @Override
    public void onActivityResult(final int requestCode,
                                 final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1234) {
                dialogo2.show();
                subirFichero(data.getData(), usuario.getUid()+"/foto_de_perfil");
            }
        }
    }

    private void subirFichero(final Uri fichero, String referencia) {
        final StorageReference ref = storageRef.child(referencia);
        UploadTask uploadTask = ref.putFile(fichero);
        Task<Uri> urlTask = uploadTask.continueWithTask(
                new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(
                            @NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            dialogo2.dismiss();
                            throw task.getException();
                        } else {
                            FirebaseStorage.getInstance().getReference(usuario.getUid()+"/foto_de_perfil").getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    urlFotoFinal = task.getResult().toString();
                                    fotoUsuario.setImageUrl(urlFotoFinal, lectorImagenes);
                                    dialogo2.dismiss();
                                }
                            });
                        }
                        return ref.getDownloadUrl();
                    }
                });
    }

    private void guardar() {
        //updating user's profile data
        String nameUser = editText_nombre.getText().toString();
        UserProfileChangeRequest profileUpdate;
        if(urlFotoFinal.equals("") || isCancelled){
            profileUpdate = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(Uri.parse(urlFotoOriginal))
                    .setDisplayName(nameUser)
                    .build();
        } else {
            profileUpdate = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(Uri.parse(urlFotoFinal))
                    .setDisplayName(nameUser)
                    .build();
        }
        if (TextUtils.isEmpty(nameUser)) {
            fG.mensaje("El nombre no puede estar vacío");
        }
        //if the field is not null, process continue to update profile
        else {
            dialogo.show();
            usuario.updateProfile(profileUpdate)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(Task<Void> task) {
                            dialogo.dismiss();
                            if (task.isSuccessful()) {
                                yaGuardado = true;
                                urlFotoOriginal.replace(urlFotoOriginal, urlFotoFinal);
                                nombreOriginal = usuario.getDisplayName();
                                MainActivity.actualizarDatos();
                                isCancelled = false;
                                fG.mensaje("Datos guardados correctamente");
                            } else {
                                fG.mensaje("Ha ocurrido un error. Vuelva a intentarlo más tarde.");
                            }
                        }
                    });
        }
    }
    private void cancelar(){
        if(!yaGuardado){
            fotoUsuario.setImageUrl(usuario.getPhotoUrl().toString(), lectorImagenes);
            editText_nombre.setText(usuario.getDisplayName());
        } else {
            dialogo.show();
            if(usuario.getPhotoUrl() != null) {
                subirFichero(Uri.parse(urlFotoOriginal), usuario.getUid()+"/foto_de_perfil");
            } else {
                fotoUsuario.setImageResource(R.drawable.user_default);
            }
            soloNombreUsuario();
            MainActivity.actualizarDatos();
        }
        isCancelled = true;
        fG.mensaje("Cambios descartados.");
    }

    private void soloNombreUsuario(){
        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                .setDisplayName(nombreOriginal)
                .build();
        usuario.updateProfile(profileUpdate)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        dialogo.dismiss();
                        if (task.isSuccessful()) {
                            MainActivity.actualizarDatos();
                            fG.mensaje("Datos guardados correctamente");
                        } else {
                            fG.mensaje("Ha ocurrido un error. Vuelva a intentarlo más tarde.");
                        }
                    }
                });
    }
}

