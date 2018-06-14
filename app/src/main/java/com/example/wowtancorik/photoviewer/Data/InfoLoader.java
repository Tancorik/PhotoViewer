package com.example.wowtancorik.photoviewer.Data;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Класс загружает необработанную информацию с API
 *
 * * Create by Aleksandr Karpachev
 */

public class InfoLoader {

    private static final String LOG_TAG = "myLogs in InfoLoader";
    private static final String ACCEPT = "application/json";

    public String loadInfoFromAPI(String url) {
        String result = null;
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
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
