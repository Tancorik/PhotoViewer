package com.example.wowtancorik.photoviewer.Interfaces;

import android.graphics.Bitmap;

import java.util.List;

public interface IDownloadManager {

    void setRequest(IPhotosCallback photosCallback);

    void loadPhoto(String album, int number, int size, IPhotosCallback photosCallback);



    interface IPhotosCallback {
        void onPhotosLoaded(List<Bitmap> bitmapList);
    }
}
