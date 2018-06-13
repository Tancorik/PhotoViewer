package com.example.wowtancorik.photoviewer.Data;

/**
 *
 * Класс хранит URL для малькой и для большой фотографии
 *
 * * Create by Aleksandr Karpachev
 */

public class PhotoInformation {

    private String mSmallSizeHref;
    private String mBigSizeHref;

    public PhotoInformation(String smallSizeHref, String bigSizeHref) {
        mSmallSizeHref = smallSizeHref;
        mBigSizeHref = bigSizeHref;
    }

    public void setSmallSizeHref(String smallSizeHref) {
        mSmallSizeHref = smallSizeHref;
    }

    public void setBigSizeHref(String bigSizeHref) {
        mBigSizeHref = bigSizeHref;
    }

    public String getSmallSizeHref() {
        return mSmallSizeHref;
    }

    public String getBigSizeHref() {
        return mBigSizeHref;
    }
}
