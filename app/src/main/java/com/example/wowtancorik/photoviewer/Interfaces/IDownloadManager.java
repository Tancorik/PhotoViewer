package com.example.wowtancorik.photoviewer.Interfaces;

import android.graphics.Bitmap;

import java.util.List;

public interface IDownloadManager {

    void initDownloadManager(IPhotosRequest photosRequest);

    void loadPhoto(String album, int number, int count, int size);

    interface IPhotosRequest {
        void CallbackPhotos(List<Bitmap> bitmapList);
    }
}
