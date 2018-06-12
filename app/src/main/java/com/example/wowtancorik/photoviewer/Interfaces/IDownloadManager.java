package com.example.wowtancorik.photoviewer.Interfaces;

import android.graphics.Bitmap;

import java.util.List;

public interface IDownloadManager {

    void setPhotosCallback(IPhotosCallback photosCallback);

    void loadSmallPhotos(String album, int number, IPhotosCallback photosCallback);

    void loadBigPhoto(int number, ISinglePhotoCallback singlePhotoCallback);

    interface IPhotosCallback {
        void onPhotosLoaded(List<Bitmap> bitmapList, int listSize);
    }

    interface ISinglePhotoCallback {
        void onSinglePhotoLoaded(Bitmap photo);
    }
}
