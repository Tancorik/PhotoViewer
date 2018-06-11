package com.example.wowtancorik.photoviewer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.wowtancorik.photoviewer.Data.DownloadManager;
import com.example.wowtancorik.photoviewer.Interfaces.IDownloadManager;

import java.util.List;

import static com.example.wowtancorik.photoviewer.MainActivity.TRANSFER_KEY;

public class RecyclerActivity extends AppCompatActivity implements IDownloadManager.IPhotosRequest{

    private String mURLCollection;
    private boolean mEnd = false;
    MyRecyclerAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        mAdapter = new MyRecyclerAdapter();

        Intent intent = getIntent();
        mURLCollection = intent.getStringExtra(TRANSFER_KEY);

        recyclerView.setAdapter(mAdapter);

        final DownloadManager downloadManager = DownloadManager.getInstance();
        downloadManager.initDownloadManager(this);

        Thread newTread = new Thread(new Runnable() {
            @Override
            public void run() {
                downloadManager.loadPhoto(mURLCollection, 1, 1);
            }
        });
        newTread.start();
    }

    @Override
    public void CallbackPhotos(List<Bitmap> bitmapList) {
        mAdapter.addToRecyclerView(bitmapList);
        mAdapter.notifyDataSetChanged();
    }

}
