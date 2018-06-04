package com.example.wowtancorik.photoviewer.Data;

import android.graphics.Bitmap;
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

    private final int SMALL_SIZE    = 1;
    private final int BIG_SIZE      = 2;
    private String mAlbums = "";
    private IPhotosRequest mPhotoRequest;
    private List<PhotoInformation> mPhotoInfoList = new ArrayList<>();

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
    public void loadPhoto(String album, int number, int count, int size) {
        if (!mAlbums.equals(album)) {
            mAlbums = album;
            mPhotoInfoList.clear();
            Log.i(LOG_TAG, mAlbums);
            loadInfo();
        }
        List<Bitmap> list = loadBitmap(number, count, size);
        mPhotoRequest.CallbackPhotos(list);

    }

    private void loadInfo() {
        StringParser stringParser = new StringParser();
        NextPageSearch nextPageSearch = new NextPageSearch();
        ExecutorService executor = Executors.newFixedThreadPool(1);
        Future<String> future;
        String string = mAlbums + "?limit=10";
        while (true) {
            future = executor.submit(new InfoLoader(string));
            try {
                string = future.get();
            } catch (InterruptedException e) {
                Log.i(LOG_TAG, "проблемы в loadInfo <-- DownloadManager");
                e.printStackTrace();
            } catch (ExecutionException e) {
                Log.i(LOG_TAG, "проблемы в loadInfo <-- DownloadManager");
                e.printStackTrace();
            }
            mPhotoInfoList.addAll(stringParser.parse(string));
            string = nextPageSearch.returnNextPage(string);
            if (string == null) break;
        }
        executor.shutdown();
    }

    private List<Bitmap> loadBitmap(int number, int count, int size) {
        if (number > mPhotoInfoList.size()) {
            return null;
        }
        if ((mPhotoInfoList.size() - number) < count) {
            count = mPhotoInfoList.size() - number;
        }
        ExecutorService executor = Executors.newFixedThreadPool(count);
        List<Future<Bitmap>> futures = new ArrayList<>();
        List<Bitmap> bitmaps = new ArrayList<>();
        String urlString;
        number--;
        for (int i = number ; i < number+count; i++){
            if (size == SMALL_SIZE) {
                urlString = mPhotoInfoList.get(i).getSmallSizeHref();
            }
            else {
                urlString = mPhotoInfoList.get(i).getBigSizeHref();
            }
            futures.add(executor.submit(new BitmapLoader(urlString)));
        }
        for (int i = 0; i < count; i++){
            try {
                bitmaps.add(futures.get(i).get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

        }
        executor.shutdown();

        return bitmaps;
    }
}
