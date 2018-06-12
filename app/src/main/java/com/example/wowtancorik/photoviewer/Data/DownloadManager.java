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

    private final int COUNT_PHOTO_IN_PARTY = 10;

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
    public void setPhotosCallback(IPhotosCallback photosCallback) {
        if (mThread == null) {
            throw new NullPointerException("поток еще не создан");
        }
        mThread.setRequest(photosCallback);
    }

    @Override
    public void loadSmallPhotos(final String album, final int startNumber, IPhotosCallback photosCallback) {
        mThread = new MyThread(new MyRunnable(photosCallback) {
            @Override
            public void run() {
                if (!mAlbum.equals(album)) {
                    mAlbum = album;
                    mPhotoInfoList.clear();
                    Log.i(LOG_TAG, mAlbum);
                    loadInfo();
                }

                int count = refineCountInParty(startNumber);
                if (count > 0) {

                    ExecutorService executor = Executors.newFixedThreadPool(COUNT_POOL_THREADS);

                    if (mPhotosCallback != null) {
                        final List<Bitmap> list = loadBitmapList(startNumber, count, executor);

                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                if (mPhotosCallback != null) {
                                    mPhotosCallback.onPhotosLoaded(list, mPhotoInfoList.size());
                                }
                            }
                        });
                    }
                    executor.shutdown();
                }
            }
        });
        mThread.start();
    }

    @Override
    public void loadBigPhoto(final int number, final ISinglePhotoCallback singlePhotoCallback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ExecutorService executor = Executors.newFixedThreadPool(1);
                Future<Bitmap> bitmapFuture;
                String urlString = mPhotoInfoList.get(number).getBigSizeHref();
                bitmapFuture = executor.submit(new BitmapLoader(urlString));
                Bitmap bitmap = null;
                try {
                    bitmap = bitmapFuture.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                executor.shutdown();
                final Bitmap bitmapfinal = bitmap;
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        singlePhotoCallback.onSinglePhotoLoaded(bitmapfinal);
                    }
                });

            }
        }).start();
    }

    private int refineCountInParty(int startNumber) {
        if ((mPhotoInfoList.size() - startNumber < COUNT_PHOTO_IN_PARTY)) {
            return mPhotoInfoList.size() - startNumber;
        }
        else {
            return COUNT_PHOTO_IN_PARTY;
        }
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
