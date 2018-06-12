package com.example.wowtancorik.photoviewer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.wowtancorik.photoviewer.Data.DownloadManager;
import com.example.wowtancorik.photoviewer.Interfaces.IDownloadManager;

import java.util.List;

import static com.example.wowtancorik.photoviewer.MainFragment.TRANSFER_KEY;

public class RecyclerActivity extends AppCompatActivity implements IDownloadManager.IPhotosCallback,
        MyRecyclerAdapter.IRecyclerAdapterWorkListener {

    public static final String COUNT_PHOTO_KEY = "count_photo_key";
    public static final String NUMBER_PHOTO_KEY = "number_photo_key";

    private String mURLCollection;
    private MyRecyclerAdapter mAdapter;
    private ProgressBar mProgressBar;
    private DownloadManager mDownloadManager;
    private int mPhotoListSize;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler);

        mProgressBar = findViewById(R.id.progress_bar);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        mAdapter = new MyRecyclerAdapter(this);

        Intent intent = getIntent();
        mURLCollection = intent.getStringExtra(TRANSFER_KEY);

        recyclerView.setAdapter(mAdapter);

        mDownloadManager = DownloadManager.getInstance();
        mDownloadManager.loadSmallPhotos(mURLCollection, 0, this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        DownloadManager downloadManager = DownloadManager.getInstance();
        downloadManager.setPhotosCallback(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        DownloadManager downloadManager = DownloadManager.getInstance();
        downloadManager.setPhotosCallback(null);
    }

    @Override
    public void onPhotosLoaded(List<Bitmap> bitmapList, int listSize) {
        mPhotoListSize = listSize;
        mProgressBar.setVisibility(View.INVISIBLE);
        mAdapter.addToRecyclerView(bitmapList);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLastPositionVisible(int lastPosition) {
        if (lastPosition < mPhotoListSize-2) {
            mProgressBar.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Подгружаем фотографии", Toast.LENGTH_SHORT).show();
            mDownloadManager.loadSmallPhotos(mURLCollection, lastPosition + 1, this);
        }
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(this, PagerActivity.class);
        intent.putExtra(COUNT_PHOTO_KEY, mPhotoListSize);
        intent.putExtra(NUMBER_PHOTO_KEY, position);
        startActivity(intent);
    }
}
