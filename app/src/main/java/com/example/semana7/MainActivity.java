package com.example.semana7;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

public class MainActivity extends AppCompatActivity {

    private ImageView mImageView;
    private SensorManager sensorManager;
    private Sensor sensor;
    private Bitmap loadedBitmap;

    private static final String IMAGE_KEY = "image_bitmap";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageView = findViewById(R.id.idImagen);
        Button downloadButton = findViewById(R.id.idBtnCargar);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        // Se restaura el estado de la imagen si se ha guardado
        if (savedInstanceState != null) {
            String encodedImage = savedInstanceState.getString(IMAGE_KEY);
            if (encodedImage != null) {
                byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
                loadedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                mImageView.setImageBitmap(loadedBitmap);
            }
        }

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final Bitmap bitmap = loadImageFromNetwork("https://th.bing.com/th/id/OIP.ej7y_UHWQt6gQoJm7-wdmwEsCo?rs=1&pid=ImgDetMain");
                        mImageView.post(new Runnable() {
                            @Override
                            public void run() {
                                loadedBitmap = bitmap;
                                mImageView.setImageBitmap(bitmap);
                            }
                        });
                    }
                }).start();
            }
        });
    }

    private Bitmap loadImageFromNetwork(String url) {
        Bitmap bitmap = null;
        try {
            java.net.URL imageUrl = new java.net.URL(url);
            HttpURLConnection connection = (HttpURLConnection) imageUrl.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    // Se guarda la imagen
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (loadedBitmap != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            loadedBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            String encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
            outState.putString(IMAGE_KEY, encodedImage);
        }
    }

    // Se restaura el estado d laimagen
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String encodedImage = savedInstanceState.getString(IMAGE_KEY);
        if (encodedImage != null) {
            byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
            loadedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            mImageView.setImageBitmap(loadedBitmap);
        }
    }
}