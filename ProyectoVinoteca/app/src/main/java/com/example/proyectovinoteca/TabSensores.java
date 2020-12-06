package com.example.proyectovinoteca;



import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.appyvet.materialrangebar.RangeBar;

public class TabSensores extends Fragment {

    CheckBox checkBoxTem, checkBoxHum;
    RangeBar rangeBarTem, rangeBarHum;
    LinearLayout containerRangoTem, containerRangoHum;
    TextView minValueTem, maxValueTem, minValueHum, maxValueHum;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_sensores, container, false);

        final TextView temperaturaActual = v.findViewById(R.id.txt_temperatura_actual);
        final TextView humedadActual = v.findViewById(R.id.txt_humedad_actual);
        checkBoxTem = v.findViewById(R.id.chk_activar_rango_temperatura);
        checkBoxHum = v.findViewById(R.id.chk_activar_rango_humedad);
        rangeBarTem = v.findViewById(R.id.rng_alerta_temperatura);
        rangeBarHum = v.findViewById(R.id.rng_alerta_humedad);
        containerRangoTem = v.findViewById(R.id.container_rango_temperatura);
        containerRangoHum = v.findViewById(R.id.container_rango_humedad);
        minValueTem = v.findViewById(R.id.txt_temperatura_minima);
        maxValueTem = v.findViewById(R.id.txt_temperatura_maxima);
        minValueHum = v.findViewById(R.id.txt_humedad_minima);
        maxValueHum = v.findViewById(R.id.txt_humedad_maxima);

        //ponemos los valores iniciales en los TextViews
        temperaturaActual.setText("18º");
        humedadActual.setText("65%");

        //le damos funcionalidad al checkbox
        checkBoxTem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCheckBoxClick(v);
            }
        });
        checkBoxHum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCheckBoxClick(v);
            }
        });

        //le damos métodos al rangeBar (al tocar un lado, al cambiar el valor...)
        rangeBarTem.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex, int rightPinIndex, String leftPinValue, String rightPinValue) {
                minValueTem.setText(leftPinValue + "º");
                maxValueTem.setText(rightPinValue + "º");
            }

            @Override
            public void onTouchStarted(RangeBar rangeBar) {

            }

            @Override
            public void onTouchEnded(RangeBar rangeBar) {

            }
        });
        rangeBarHum.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex, int rightPinIndex, String leftPinValue, String rightPinValue) {
                minValueHum.setText(leftPinValue + "%");
                maxValueHum.setText(rightPinValue + "%");
            }

            @Override
            public void onTouchStarted(RangeBar rangeBar) {

            }

            @Override
            public void onTouchEnded(RangeBar rangeBar) {

            }
        });

        //muestra o esconde el rango dependiendo de si el checkbox está o no activado
        if(checkBoxTem.isActivated()){
            containerRangoTem.setVisibility(View.VISIBLE);
        } else {
            containerRangoTem.setVisibility(View.GONE);
        }
        if(checkBoxHum.isActivated()){
            containerRangoHum.setVisibility(View.VISIBLE);
        } else {
            containerRangoHum.setVisibility(View.GONE);
        }

        //los textos que indican el valor máximo y mínimo se inicializan con los valores adecuados
        minValueTem.setText(rangeBarTem.getLeftPinValue() + "º");
        maxValueTem.setText(rangeBarTem.getRightPinValue() + "º");
        minValueHum.setText(rangeBarHum.getLeftPinValue() + "%");
        maxValueHum.setText(rangeBarHum.getRightPinValue() + "%");

        //añadimos funcionalidad a los botones + y -
        final Button restar = v.findViewById(R.id.btn_bajar_tem);
        final Button sumar = v.findViewById(R.id.btn_subir_tem);
        restar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int valor = Integer.valueOf(temperaturaActual.getText().toString().replace("º",""));
                if(valor == 24){
                    sumar.setBackgroundResource(R.drawable.boton_redondo_activado);
                }
                if(valor != 0) {
                    if(valor == 1){
                        restar.setBackgroundResource(R.drawable.boton_redondo_desactivado);
                    }
                    valor--;
                    temperaturaActual.setText(valor + "º");
                }
            }
        });
        sumar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int valor = Integer.valueOf(temperaturaActual.getText().toString().replace("º",""));
                if(valor == 0){
                    restar.setBackgroundResource(R.drawable.boton_redondo_activado);
                }
                if(valor != 24) {
                    if(valor == 23){
                        sumar.setBackgroundResource(R.drawable.boton_redondo_desactivado);
                    }
                    valor++;
                    temperaturaActual.setText(valor + "º");
                }
            }
        });

        return v;
    }

    private void onCheckBoxClick(View v){
        int id = v.getId();
        switch (id){
            case R.id.chk_activar_rango_temperatura:
                if(((CheckBox)v).isChecked()){
                    containerRangoTem.setVisibility(View.VISIBLE);
                } else {
                    containerRangoTem.setVisibility(View.GONE);
                }
                break;
            case R.id.chk_activar_rango_humedad:
                if(((CheckBox)v).isChecked()){
                    containerRangoHum.setVisibility(View.VISIBLE);
                } else {
                    containerRangoHum.setVisibility(View.GONE);
                }
                break;
        }
    }
}