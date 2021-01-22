package com.example.proyectovinoteca;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

public class AmpliarFotoActivity extends Activity {
    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ampliar_foto);
        ImageView iV = findViewById(R.id.img);
        byte[] byteArray = getIntent().getByteArrayExtra("imagen");
        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        iV.setImageBitmap(bmp);
    }
}
