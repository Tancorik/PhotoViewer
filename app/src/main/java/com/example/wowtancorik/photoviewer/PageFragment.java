package com.example.wowtancorik.photoviewer;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.wowtancorik.photoviewer.Data.DownloadManager;
import com.example.wowtancorik.photoviewer.Interfaces.IDownloadManager;

/**
 * Фрагмент для ViewPager, отображает большую фоторафию
 *
 * * Create by Aleksandr Karpachev
 */

public class PageFragment extends Fragment implements IDownloadManager.ISinglePhotoCallback{

    public static final String POSITION_KEY = "position_key";
    private int mPosition;
    ImageView mImageView;

    public static PageFragment newInstance(int position) {
        PageFragment pageFragment = new PageFragment();
        Bundle argument = new Bundle();
        argument.putInt(POSITION_KEY, position);
        pageFragment.setArguments(argument);
        return pageFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mPosition = getArguments().getInt(POSITION_KEY, 0);
        return inflater.inflate(R.layout.fragment_single_photo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mImageView = view.findViewById(R.id.image_view_for_simple_photo);
        DownloadManager downloadManager = DownloadManager.getInstance();
        downloadManager.loadBigPhoto(mPosition, this);
    }

    /**
     * как только фотография загрузилась сразу отображается
     *
     * @param photo
     */
    @Override
    public void onSinglePhotoLoaded(Bitmap photo) {
        mImageView.setImageBitmap(photo);
    }
}
