package com.example.proyectovinoteca;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.LruCache;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

import com.example.proyectovinoteca.Tab_Guia.GuiaFragment;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        DrawerLayout.DrawerListener {

    final static int CODIGO_EDITAR = 0;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    //ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar toolbar;

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Se asigna el NavigationView
        navigationView = findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(this);

        //Inicialización atributos (el menú no está siendo usado actualmente)
        Menu menu = navigationView.getMenu();
        View header = navigationView.getHeaderView(0);
        toolbar = findViewById(R.id.toolbar);
        toolbar();
        drawerLayout = findViewById(R.id.drawerLayout);

        //Implementación datos del usuario
        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();

        // Inicialización Volley (Hacer solo una vez en Singleton o Applicaction) (para la foto)
        RequestQueue colaPeticiones = Volley.newRequestQueue(getApplicationContext());
        ImageLoader lectorImagenes = new ImageLoader(colaPeticiones,
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

        // Foto de usuario. Si no se encuentra una, se le asignará una por defecto
        NetworkImageView fotoUsuario = (NetworkImageView) header.findViewById(R.id.imagen);
        Uri urlImagen = usuario.getPhotoUrl();
        if (urlImagen != null) {
            fotoUsuario.setImageUrl(urlImagen.toString(), lectorImagenes);
        } else {
            fotoUsuario.setImageResource(R.drawable.user_default);
        }

        // Otros datos del usuario
        TextView nombre = header.findViewById(R.id.nombre);
        TextView email = header.findViewById(R.id.email);
        nombre.setText(usuario.getDisplayName());
        email.setText(usuario.getEmail());

        //Carga de la primera pantalla (por defecto, la pantalla "Vinoteca")
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.container, new VinotecaFragment());
        fragmentTransaction.commit();

    }

    //Métodos necesarios por defecto que aún no tienen uso
    //--------------------------------------------------------
    @Override
    public void onDrawerSlide(@NonNull View view, float v) {
        //cambio en la posición del drawer
    }

    @Override
    public void onDrawerOpened(@NonNull View view) {
        //el drawer se ha abierto completamente
        Toast.makeText(this, "Prueba",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDrawerClosed(@NonNull View view) {
        //el drawer se ha cerrado completamente
    }

    @Override
    public void onDrawerStateChanged(int i) {
        //cambio de estado, puede ser STATE_IDLE, STATE_DRAGGING or STATE_SETTLING
    }
    //--------------------------------------------------------

    //Toolbar customizado
    public void toolbar(){
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if(ab != null){
            //Icono hamburguesa
            ab.setHomeAsUpIndicator(R.drawable.ic_custom_menu_menu);
            ab.setDisplayHomeAsUpEnabled(true);
            //Si quiero ponerle un título
            ab.setTitle("");
        }
    }

    //Funcionalidad del botón de hanburguesa (abrir menú lateral al pulsarlo)
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == android.R.id.home){
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Acciones al tocar cada botón del menú lateral
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        //Cerrar el menú lateral al pulsar un botón
        drawerLayout.closeDrawer(GravityCompat.START);

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        switch (menuItem.getItemId()) {
            case R.id.op1:
                fragmentTransaction.replace(R.id.container, new CuentaFragment());
                break;
            case R.id.op2:
                fragmentTransaction.replace(R.id.container, new VinotecaFragment());
                break;
            case R.id.op3:
                fragmentTransaction.replace(R.id.container, new ClasificacionesFragment());
                break;
            case R.id.op4:
                fragmentTransaction.replace(R.id.container, new FavoritosFragment());
                break;
            case R.id.op5:
                fragmentTransaction.replace(R.id.container, new GuiaFragment());
                break;
            case R.id.other1:
                fragmentTransaction.replace(R.id.container, new AyudaFragment());
                break;
            case R.id.exit:
                cerrarSesion();
                break;
            default:
                throw new IllegalArgumentException("opción de menú no implementada");
        }

        fragmentTransaction.commit();

        return true;
    }

    //Cerrar sesión y volver al login
    public void cerrarSesion(){
        AuthUI.getInstance().signOut(getApplicationContext())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent i = new Intent(getApplicationContext(),CustomLoginActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                                | Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        finish();
                    }
                });
    }

    public void lanzarEditarPerfil (View view){
        Intent i = new Intent(this, editarPerfil.class);
        startActivityForResult(i, CODIGO_EDITAR);
    }

    @Override public void onActivityResult(int requestCode, int resultCode,
                                           Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CODIGO_EDITAR){
            View header = navigationView.getHeaderView(0);
            TextView nombre = header.findViewById(R.id.nombre);
            nombre.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container, new CuentaFragment());
            fragmentTransaction.commit();
        }
    }

}