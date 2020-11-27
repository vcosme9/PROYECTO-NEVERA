package com.example.proyectovinoteca;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.proyectovinoteca.Tab_Alertas.TabAlertas;
import com.example.proyectovinoteca.Tab_Productos.TabProductos;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class VinotecaFragment extends Fragment {

    // Nombres de las pestañas
    private String[] nombres = new String[]{"Mediciones","Mis Vinos","Alertas"};

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle bundle){
        View v = inflater.inflate(R.layout.opcion_vinoteca, container, false);

        //Pestañas
        ViewPager2 viewPager = v.findViewById(R.id.viewpager);
        viewPager.setAdapter(new MiPagerAdapter(getActivity()));
        TabLayout tabs = v.findViewById(R.id.tabs);
        new TabLayoutMediator(tabs, viewPager,
                new TabLayoutMediator.OnConfigureTabCallback() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position){
                        tab.setText(nombres[position]);
                    }
                }
        ).attach();

        return v;
    }

    //Añade las pestañas e implementa los fragments de cada una
    public class MiPagerAdapter extends FragmentStateAdapter {
        public MiPagerAdapter(FragmentActivity activity){
            super(activity);
        }
        @Override
        public int getItemCount() {
            return 3;
        }
        @Override @NonNull
        public Fragment createFragment(int position) {
            switch (position) {
                case 0: return new TabTemperatura();
                case 1: return new TabProductos();
                //case 2: return new TabHistorial();
                case 2: return new TabAlertas();
            }
            return null;
        }
    }

}
