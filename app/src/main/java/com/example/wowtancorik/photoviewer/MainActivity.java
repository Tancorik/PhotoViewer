package com.example.wowtancorik.photoviewer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;

import com.example.wowtancorik.photoviewer.Data.BitmapLoader;
import com.example.wowtancorik.photoviewer.Data.DownloadManager;
import com.example.wowtancorik.photoviewer.Data.InfoLoader;
import com.example.wowtancorik.photoviewer.Data.NextPageSearch;
import com.example.wowtancorik.photoviewer.Data.StringParser;
import com.example.wowtancorik.photoviewer.Interfaces.IDownloadManager;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class MainActivity extends AppCompatActivity {

    public static final String TRANSFER_KEY = "transfer_key_to_RecyclerActivity";

    private static final String NEW_PHOTO = "http://api-fotki.yandex.ru/api/recent/";
    private static final String POPULAR_PHOTO = "http://api-fotki.yandex.ru/api/top/";

    private Button mNewPhotoButton;
    private Button mPopularPhoroButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNewPhotoButton = findViewById(R.id.new_interesting_button);
        mPopularPhoroButton = findViewById(R.id.popular_button);

        mNewPhotoButton.setOnClickListener(mListener);
        mPopularPhoroButton.setOnClickListener(mListener);
    }

    View.OnClickListener mListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String string;
            if (v.getId() == (R.id.new_interesting_button)) {
                string = NEW_PHOTO;
            }
            else {
                string = POPULAR_PHOTO;
            }
            Intent intent = new Intent(MainActivity.this, RecyclerActivity.class);
            intent.putExtra(TRANSFER_KEY, string);
            startActivity(intent);
        }
    };



}
