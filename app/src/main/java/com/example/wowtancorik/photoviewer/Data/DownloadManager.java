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

    private final String LOG_TAG = "myLogs";
    private final int MAX_PHOTO_COUNT = 100;
    private final int COUNT_POOL_THREADS = 10;

    private final int SMALL_SIZE    = 1;
    private final int BIG_SIZE      = 2;
    private String mAlbums = "";
    private IPhotosRequest mPhotoRequest;
    private List<PhotoInformation> mPhotoInfoList = new ArrayList<>();
    private ExecutorService mExecutor;

    private static class SingletonHolder {
        private static final DownloadManager HOLDER_INSTANCE = new DownloadManager();
    }

    public static DownloadManager getInstance() {
        return SingletonHolder.HOLDER_INSTANCE;
    }

    @Override
    public void initDownloadManager(IPhotosRequest photosRequest) {
        mPhotoRequest = photosRequest;
    }

    @Override
    public void loadPhoto(String album, int number, int size) {
        if (!mAlbums.equals(album)) {
            mAlbums = album;
            mPhotoInfoList.clear();
            Log.i(LOG_TAG, mAlbums);
            loadInfo();
        }

        if (size == SMALL_SIZE) {
            int count;
            mExecutor = Executors.newFixedThreadPool(COUNT_POOL_THREADS);
            for (int i = 0; i < mPhotoInfoList.size(); i += COUNT_POOL_THREADS) {
                if ((mPhotoInfoList.size() - i < COUNT_POOL_THREADS)) {
                    count = mPhotoInfoList.size() - i;
                }
                else {
                    count = COUNT_POOL_THREADS;
                }
                final List<Bitmap> list = loadBitmapList(i, count);

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        mPhotoRequest.CallbackPhotos(list);
                    }
                });
            }
            mExecutor.shutdown();
        }
        else {

        }
    }

    private void loadInfo() {
        StringParser stringParser = new StringParser();
        NextPageSearch nextPageSearch = new NextPageSearch();
        InfoLoader infoLoader = new InfoLoader();
        String stringNextPage = mAlbums + "?limit=10";
        String stringForParse;
        while (true) {
            stringForParse = infoLoader.loadInfoFromAPI(stringNextPage);
            mPhotoInfoList.addAll(stringParser.parse(stringForParse));
            stringNextPage = nextPageSearch.returnNextPage(stringForParse);
            if (stringNextPage == null) break;
            if (mPhotoInfoList.size() >= MAX_PHOTO_COUNT) break;
        }
    }

    private List<Bitmap> loadBitmapList(int number, int count) {
        List<Future<Bitmap>> futures = new ArrayList<>();
        List<Bitmap> bitmaps = new ArrayList<>();
        String urlString;
        for (int i = number ; i < number+count; i++){
                urlString = mPhotoInfoList.get(i).getSmallSizeHref();
            futures.add(mExecutor.submit(new BitmapLoader(urlString)));
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
}
