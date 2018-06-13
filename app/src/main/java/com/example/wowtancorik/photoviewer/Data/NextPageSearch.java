package com.example.wowtancorik.photoviewer.Data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Класс пытается найти URL следующей страницы для загрзки с API
 *
 * * Create by Aleksandr Karpachev
 */

public class NextPageSearch {

    public String returnNextPage(String string) {
        String nextPage = null;
        try {
            JSONObject jsonObject = new JSONObject(string);
            jsonObject = jsonObject.getJSONObject("links");
            nextPage = jsonObject.getString("next");
        } catch (JSONException e) {
            e.printStackTrace();
            nextPage = null;
        }
        return nextPage;
    }
}
