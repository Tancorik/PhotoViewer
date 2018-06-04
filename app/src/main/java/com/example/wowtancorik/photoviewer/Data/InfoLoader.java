package com.example.wowtancorik.photoviewer.Data;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Callable;


public class InfoLoader implements Callable<String> {

    private final String LOG_TAG = "myLogs in InfoLoader";
    private final String ACCEPT = "application/json";

    private String mUrl;

    public InfoLoader(String url) {
        mUrl = url;
    }

    @Override
    public String call() throws Exception {
        String result = null;
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(mUrl).openConnection();
            httpURLConnection.setRequestProperty("Accept", ACCEPT);
            try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()))) {
                String line;
                StringBuilder stringBuilder = new StringBuilder();
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                result = new String(stringBuilder);
            }
        } catch (MalformedURLException e) {
            Log.i(LOG_TAG, "что-то не так с URL");
        } catch (IOException e){
            Log.i(LOG_TAG, "что-то не так с вводом-выводом");
        }
        return result;
    }
}
