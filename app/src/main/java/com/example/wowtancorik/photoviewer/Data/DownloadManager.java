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

/**
 * Класс организовывает загрузку и обработки данных в отдельном потоке.
 * Возвращает партию маленьких фотографий или одну большую, в зависимости от вызываемого метода
 *
 * * Create by Aleksandr Karpachev
 */
public class DownloadManager implements IDownloadManager {

    private final int COUNT_PHOTO_IN_PARTY = 10;

    private final String LOG_TAG = "myLogs";
    private final int MAX_PHOTO_COUNT = 100;
    private final int COUNT_POOL_THREADS = 10;

    private String mAlbum = "";
    private List<PhotoInformation> mPhotoInfoList = new ArrayList<>();
    private MyThread mThread;

    /**
     * класс определен как синглтон
     */
    private static class SingletonHolder {
        private static final DownloadManager HOLDER_INSTANCE = new DownloadManager();
    }

    public static DownloadManager getInstance() {
        return SingletonHolder.HOLDER_INSTANCE;
    }

    /**
     * Установить callback для партии маленьких фоток
     * и передать в поток который обрабатывает запрос и возвращает
     *
     * @param photosCallback        callback для маленьких фоток
     */
    @Override
    public void setPhotosCallback(IPhotosCallback photosCallback) {
        if (mThread == null) {
            throw new NullPointerException("поток еще не создан");
        }
        mThread.setCallback(photosCallback);
    }

    /**
     * Огранизация загрузки и отправки партии маленьки фоток
     * запускает пул из COUNT_POOL_THREADS потоков для загрузки фотографий
     * и ставит отправку в очередь главного потока.
     * Если активити пересоздаться, а отправка ей стоит в очереди, то
     * отправка не произойдет. Для этого активит передате нулевой callback.
     * Тем самым мы исключаем нежданные фотографии в пересозданную активити.
     * Пересозданная активити вновь вызывает этот метод и получает только те фотографии, которые
     * запросила.
     *
     * @param album             URL альбома
     * @param startNumber       начальная позиция фотографии в альбоме(списке фоток)
     * @param photosCallback    callback для маленьких фоток
     */
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

    /**
     * Загрузка одной большой фотографии
     *
     * @param number                номер фотографии в списке
     * @param singlePhotoCallback   callback для большой фотографии
     */
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

    /**
     * Переопределяет количество маленьких фотографий в партии, если вдруг в списке
     * осталось меньше фоток чем размер партии
     *
     * @param startNumber       стартовый номер фотографии в списке
     * @return
     */
    private int refineCountInParty(int startNumber) {
        if ((mPhotoInfoList.size() - startNumber < COUNT_PHOTO_IN_PARTY)) {
            return mPhotoInfoList.size() - startNumber;
        }
        else {
            return COUNT_PHOTO_IN_PARTY;
        }
    }

    /**
     * Организовывает постраничную загрузку информации с API
     * распарсивание этой информации для получения URL маленьких и больших фотографий
     */
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

    /**
     * Загружаем фотографии вызывая BitmapLoader в пуле потоков
     * для ускорения загрузки партии маленьких фотографий
     *
     * @param number        номер первой фотографии партии в списке фотографий
     * @param count         количество фотографий
     * @param executor      экземпляр пула потоков
     * @return              возвращает список (партию) фотографий списком Bitmap-ов
     */
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

    /**
     * Определяем класс потока содержащий определенный Runnable, в который
     * передает callback, что бы для каждого потока был свой callback,
     * и когда в потоке callback станет null поток завершится.
     */
    private class MyThread extends Thread {
        MyRunnable myRunnable;

        public MyThread(MyRunnable runnable) {
            super(runnable);
            myRunnable = runnable;
        }

        private void setCallback(IPhotosCallback photosCallback) {
            myRunnable.setPhotosCallbackOther(photosCallback);
        }
    }

    private abstract class MyRunnable implements Runnable {
        public volatile IPhotosCallback mPhotosCallback;

        public MyRunnable(IPhotosCallback photosCallback) {
            this.mPhotosCallback = photosCallback;
        }

        public void setPhotosCallbackOther(IPhotosCallback photosCallback) {
            this.mPhotosCallback = photosCallback;
        }
    }
}
