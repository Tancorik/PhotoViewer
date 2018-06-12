package com.example.wowtancorik.photoviewer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.example.wowtancorik.photoviewer.Data.DownloadManager;
import com.example.wowtancorik.photoviewer.Interfaces.IDownloadManager;

import java.util.List;

import static com.example.wowtancorik.photoviewer.MainFragment.TRANSFER_KEY;

public class RecyclerActivity extends AppCompatActivity implements IDownloadManager.IPhotosCallback {

    private String mURLCollection;
    private MyRecyclerAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        mAdapter = new MyRecyclerAdapter();

        Intent intent = getIntent();
        mURLCollection = intent.getStringExtra(TRANSFER_KEY);

        recyclerView.setAdapter(mAdapter);

        DownloadManager downloadManager = DownloadManager.getInstance();
        downloadManager.loadPhoto(mURLCollection, 1, DownloadManager.SMALL_SIZE, this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        DownloadManager downloadManager = DownloadManager.getInstance();
        downloadManager.setRequest(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        DownloadManager downloadManager = DownloadManager.getInstance();
        downloadManager.setRequest(null);
    }

    @Override
    public void onPhotosLoaded(List<Bitmap> bitmapList) {
        mAdapter.addToRecyclerView(bitmapList);
        mAdapter.notifyDataSetChanged();
    }

}
