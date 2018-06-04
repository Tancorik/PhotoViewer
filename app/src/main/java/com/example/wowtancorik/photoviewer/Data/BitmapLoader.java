package com.example.wowtancorik.photoviewer.Data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;

public class BitmapLoader implements Callable<Bitmap> {

    private final String LOG_TAG = "myLogs";
    private String mUrl;

    public BitmapLoader(String url) {
        mUrl = url;
    }

    @Override
    public Bitmap call() throws Exception {
        Bitmap bitmap = null;
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(mUrl).openConnection();
            try (InputStream inputStream = connection.getInputStream()) {
                bitmap = BitmapFactory.decodeStream(inputStream);
            }
        } catch (IOException e) {
            Log.i(LOG_TAG, "не получилось загрузка");
            bitmap = null;
        }
        return bitmap;
    }
}
