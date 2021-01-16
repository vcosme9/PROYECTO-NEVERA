 package com.example.proyectovinoteca.Tab_Guia;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.proyectovinoteca.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MapaActivity extends FragmentActivity implements
        OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private Button boton1;
    Button containerVoid;
    private static final int SOLICITUD_PERMISO_ACCESS_FINE_LOCATION = 0 ;
    //private Marker markerPosAc;
    //private LatLng posicionActual;
    private GoogleMap mapa;
    private int cont = 0;
    private List<String> titleMarkers = new ArrayList<>();
    private List<String> descMarkers = new ArrayList<>();
    private List<LatLng> listaLoca = new ArrayList<>();
    private SupportMapFragment mapFragment;
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapa_vinos);

        containerVoid = findViewById(R.id.background_void);
        boton1 = findViewById(R.id.button1);
        boton1.setVisibility(View.GONE);
        mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.mapa);

        //permisos
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            prepararMapa();
        } else {
            solicitarPermiso(Manifest.permission.ACCESS_FINE_LOCATION, "Necesito el " +
                    "permiso siguiente para acceder a la posición actual.",
                    SOLICITUD_PERMISO_ACCESS_FINE_LOCATION, this);
        }

    }
    @Override public void onMapReady(GoogleMap googleMap) {
        containerVoid.setVisibility(View.GONE);
        boton1.setVisibility(View.VISIBLE);
        mapa = googleMap;
        mapa.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        mapa.getUiSettings().setZoomControlsEnabled(false);
        mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(listaLoca.get(0), 15));
        //Creo los markers para cada sitio
        inicializarMarkers();
        mapa.setOnMapClickListener(this);
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            mapa.setMyLocationEnabled(true);
            mapa.getUiSettings().setCompassEnabled(true);
        }
    }
    public void moveCamera(View view) {
        mapa.moveCamera(CameraUpdateFactory.newLatLng(listaLoca.get(cont)));
        cont ++;
        if(cont==listaLoca.size()){
            cont = 0;
        }
    }

    private MarkerOptions anyadirMarket(int localizacion, String title, String desc){
        MarkerOptions res = new MarkerOptions()
                .position(listaLoca.get(localizacion))
                .title(title)
                .snippet(desc)
                .icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .anchor(0.5f, 0.5f);
        return res;
    }

    @Override public void onMapClick(LatLng puntoPulsado) {

    }

    //inicializar en un async (onMapReady)
    private void inicializarMarkers(){
        for(int i = 0; i < listaLoca.size(); i++){
            mapa.addMarker(anyadirMarket(i, titleMarkers.get(i), descMarkers.get(i)));
        }
    }

    private void inicializarMapa(){
        //Localizaciones del mapa
        listaLoca.add(new LatLng(39.47892949176065, -0.6937669945739772)); //bodega1
        listaLoca.add(new LatLng(39.506387788868956, -1.129388169315601)); //bodega2
        listaLoca.add(new LatLng(39.51483322456345, -1.144744385150303)); //bodega3
        listaLoca.add(new LatLng(38.570147404105114, -0.11395233305152443)); //bodega4
        listaLoca.add(new LatLng(38.80883018684332, -0.8156249005118076)); //bodega5
        listaLoca.add(new LatLng(38.50396708817244, -0.7916008735333602)); //bodega6
        listaLoca.add(new LatLng(39.984159900007654, -0.038434673492776336)); //bodega7
        listaLoca.add(new LatLng(40.167760333107196, -0.09666427348764105)); //bodega8
        listaLoca.add(new LatLng(40.053234915863996, 0.05732251534016734)); //bodega9

        //Nombres de los sitios
        titleMarkers.add("Vicente Gandia");
        titleMarkers.add("Bodegas Murviedro");
        titleMarkers.add("Dominio de la Vega, S.L.");
        titleMarkers.add("Bodegas E Mendoza S L");
        titleMarkers.add("Celler del Roure");
        titleMarkers.add("Bodegas Bocopa");
        titleMarkers.add("Templo de Boca");
        titleMarkers.add("Barn D`Alba");
        titleMarkers.add("Carmelitano Bodega y Destilería");

        //Descripciones de los sitios
        descMarkers.add("Ctra. Cheste a Godelleta, s/n, 46370 Chiva, Valencia");
        descMarkers.add("Ampliación Polígono El Romeral, s/n, 46340 Requena, Valencia");
        descMarkers.add("Carretera Madrid-Valencia, Km 270, 46390 San Antonio, Valencia");
        descMarkers.add("Partida Romeral, s/n, 03580, Alicante");
        descMarkers.add("Alcusses, km 11,1, 46640 Mogente/Moixent, Valencia");
        descMarkers.add("Carrer Talladors, 03610 Petrer, Alicante");
        descMarkers.add("Carrer Falcó, 12, 12001 Castelló de la Plana, Castelló");
        descMarkers.add("Partida Vilar La Cal, 10, 12118 el Pla, Castelló");
        descMarkers.add("Strasse Bodolz, 12, 12560 Benicàssim, Castelló");
    }

    /*@Override
    public void onLocationChanged(@NonNull Location location) {
        posicionActual = new LatLng(location.getLatitude(), location.getLongitude());
        if(markerPosAc.equals(null)){
            markerPosAc = mapa.addMarker(new MarkerOptions().position(posicionActual));
        } else {
            markerPosAc.remove();
            markerPosAc = mapa.addMarker(new MarkerOptions().position(posicionActual));
        }
    }*/

    public static void solicitarPermiso(final String permiso, String
            justificacion, final int requestCode, final Activity actividad) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(actividad,
                permiso)){
            new AlertDialog.Builder(actividad).setTitle("Solicitud de permiso").setMessage(justificacion).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    ActivityCompat.requestPermissions(actividad,
                            new String[]{permiso}, requestCode);
                }}).show();
        } else {
            ActivityCompat.requestPermissions(actividad,
                    new String[]{permiso}, requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[]
            permissions, int[] grantResults) {
        if (requestCode == SOLICITUD_PERMISO_ACCESS_FINE_LOCATION) {
            if (grantResults.length== 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            }
            prepararMapa();
        }
    }

    private void prepararMapa(){
        mapFragment.getMapAsync(this);
        //Añado los sitios, sus nombres y sus descripciones
        inicializarMapa();
    }
}
