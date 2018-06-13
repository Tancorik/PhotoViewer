package com.example.wowtancorik.photoviewer;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Адаптер для RecyclerView, который отображает маленькие фотографии
 *
 * * Create by Aleksandr Karpachev
 */

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder> {

    private List<Bitmap> mBitmapList = new ArrayList<>();
    private IRecyclerAdapterWorkListener mRecyclerAdapterWorkListener;

    MyRecyclerAdapter(IRecyclerAdapterWorkListener recyclerAdapterWorkListener) {
        mRecyclerAdapterWorkListener = recyclerAdapterWorkListener;
    }

    public void addToRecyclerView(List<Bitmap> bitmapList) {
        mBitmapList.addAll(bitmapList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_for_recycler, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.mImageView.setImageBitmap(mBitmapList.get(position));
        holder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecyclerAdapterWorkListener.onItemClick(position);
            }
        });

        if (position == mBitmapList.size()-1) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    mRecyclerAdapterWorkListener.onLastPositionVisible(position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mBitmapList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageView;
        ViewHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.image_view);
        }
    }

    /**
     * Интерфейс следит за событиями адаптера
     */
    interface IRecyclerAdapterWorkListener {
        void onLastPositionVisible(int lastPosition);
        void onItemClick(int position);
    }
}
