package com.example.wowtancorik.photoviewer.Interfaces;

import android.graphics.Bitmap;

import java.util.List;

/**
 * Интрефейс для Менеджера загрузки
 *
 * * Create by Aleksandr Karpachev
 */

public interface IDownloadManager {

    /**
     * получить callback
     */
    void setPhotosCallback(IPhotosCallback photosCallback);

    /**
     * организовать загрузку маленьких фотографий
     *
     * @param album             URL альбома
     * @param number            номер первой фотографии в списке
     * @param photosCallback    callback
     */
    void loadSmallPhotos(String album, int number, IPhotosCallback photosCallback);

    /**
     * Организовать загрузку большой фотографии
     *
     * @param number                номер фотографии
     * @param singlePhotoCallback   callback
     */
    void loadBigPhoto(int number, ISinglePhotoCallback singlePhotoCallback);

    /**
     * интерфейс для callback-а списка маленькх фотографий
     */
    interface IPhotosCallback {
        void onPhotosLoaded(List<Bitmap> bitmapList, int listSize);
    }

    /**
     * интрефейс для callback-а большой фотографии
     */
    interface ISinglePhotoCallback {
        void onSinglePhotoLoaded(Bitmap photo);
    }
}
