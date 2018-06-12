package com.example.wowtancorik.photoviewer.Data;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.wowtancorik.photoviewer.Interfaces.IDownloadManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DownloadManager implements IDownloadManager {

    public static final int SMALL_SIZE    = 1;
    public static final int BIG_SIZE      = 2;

    private final String LOG_TAG = "myLogs";
    private final int MAX_PHOTO_COUNT = 100;
    private final int COUNT_POOL_THREADS = 10;


    private String mAlbum = "";
    private List<PhotoInformation> mPhotoInfoList = new ArrayList<>();
    private MyThread mThread;


    private static class SingletonHolder {
        private static final DownloadManager HOLDER_INSTANCE = new DownloadManager();
    }

    public static DownloadManager getInstance() {
        return SingletonHolder.HOLDER_INSTANCE;
    }

    @Override
    public void setRequest(IPhotosCallback photosCallback) {
        if (mThread == null) {
            throw new NullPointerException("поток еще не создан");
        }
        mThread.setRequest(photosCallback);
    }

    @Override
    public void loadPhoto(final String album, final int number, final int size, IPhotosCallback photosCallback) {
        mThread = new MyThread(new MyRunnable(photosCallback) {
            @Override
            public void run() {


                if (!mAlbum.equals(album)) {
                    mAlbum = album;
                    mPhotoInfoList.clear();
                    Log.i(LOG_TAG, mAlbum);
                    loadInfo();
                }

                if (size == SMALL_SIZE) {
                    int count;
                    ExecutorService executor = Executors.newFixedThreadPool(COUNT_POOL_THREADS);
                    for (int i = 0; i < mPhotoInfoList.size(); i += COUNT_POOL_THREADS) {
                        if ((mPhotoInfoList.size() - i < COUNT_POOL_THREADS)) {
                            count = mPhotoInfoList.size() - i;
                        }
                        else {
                            count = COUNT_POOL_THREADS;
                        }
                        if (mPhotosCallback == null) {
                            break;
                        }
                        final List<Bitmap> list = loadBitmapList(i, count, executor);

                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                if (mPhotosCallback != null) {
                                    mPhotosCallback.onPhotosLoaded(list);
                                }
                            }
                        });
                    }
                    executor.shutdown();
                }
                else {

                }
            }
        });
        mThread.start();
    }

    private void loadInfo() {
        StringParser stringParser = new StringParser();
        NextPageSearch nextPageSearch = new NextPageSearch();
        InfoLoader infoLoader = new InfoLoader();
        String stringNextPage = mAlbum + "?limit=10";
        String stringForParse;
        while (true) {
            stringForParse = infoLoader.loadInfoFromAPI(stringNextPage);
            mPhotoInfoList.addAll(stringParser.parse(stringForParse));
            stringNextPage = nextPageSearch.returnNextPage(stringForParse);
            if (stringNextPage == null) break;
            if (mPhotoInfoList.size() >= MAX_PHOTO_COUNT) break;
        }
    }

    private List<Bitmap> loadBitmapList(int number, int count, ExecutorService executor) {
        List<Future<Bitmap>> futures = new ArrayList<>();
        List<Bitmap> bitmaps = new ArrayList<>();
        String urlString;
        for (int i = number ; i < number+count; i++){
                urlString = mPhotoInfoList.get(i).getSmallSizeHref();
            futures.add(executor.submit(new BitmapLoader(urlString)));
        }
        for (int i = 0; i <futures.size(); i++){
            try {
                bitmaps.add(futures.get(i).get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return bitmaps;
    }

    private class MyThread extends Thread {
        MyRunnable myRunnable;

        public MyThread(MyRunnable runnable) {
            super(runnable);
            myRunnable = runnable;
        }

        private void setRequest(IPhotosCallback photosCallback) {
            myRunnable.setPhotosRequestOther(photosCallback);
        }
    }

    private abstract class MyRunnable implements Runnable {
        public volatile IPhotosCallback mPhotosCallback;

        public MyRunnable(IPhotosCallback photosCallback) {
            this.mPhotosCallback = photosCallback;
        }

        public void setPhotosRequestOther(IPhotosCallback photosCallback) {
            this.mPhotosCallback = photosCallback;
        }
    }
}
