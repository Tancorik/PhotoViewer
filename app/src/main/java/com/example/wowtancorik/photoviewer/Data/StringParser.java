package com.example.wowtancorik.photoviewer.Data;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс распарсивает строку и возвращает список с информацией о фотографиях
 *
 * * Create by Aleksandr Karpachev
 */

public class StringParser {

    private final String LOG_TAG = "myLogs";
    private final String ENTRY_KYE = "entries";
    private final String SMALL_SIZE_KEY = "S";
    private final String BIG_SIZE_KEY = "XXL";
    private final String HREF_KEY = "href";

    public StringParser() {

    }

    public List<PhotoInformation> parse(String stringtoparse) {
        List<PhotoInformation> photoInformationList = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(stringtoparse);
            JSONArray jsonArray = jsonObject.getJSONArray(ENTRY_KYE);
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = (JSONObject) jsonArray.get(i);
                jsonObject = jsonObject.getJSONObject("img");
                JSONObject jsonObjectSmall = jsonObject.getJSONObject(SMALL_SIZE_KEY);
                JSONObject jsonObjectBig = jsonObject.getJSONObject(BIG_SIZE_KEY);
                String smallHref = jsonObjectSmall.getString(HREF_KEY);
//                Log.i(LOG_TAG, smallHref);
                String bigHref = jsonObjectBig.getString(HREF_KEY);
//                Log.i(LOG_TAG, bigHref);
                photoInformationList.add(new PhotoInformation(smallHref, bigHref));
            }
        } catch (JSONException e) {
            Log.i(LOG_TAG, "Проблемы в парсере!");
            e.printStackTrace();
        }
        return photoInformationList;
    }
}
