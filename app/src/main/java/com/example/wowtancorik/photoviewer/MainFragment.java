package com.example.wowtancorik.photoviewer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Фрагмент для стартовой активити
 * Содержит выбор альбома для просмотра
 *
 * * Create by Aleksandr Karpachev
 */

public class MainFragment  extends Fragment {

    public static final String TRANSFER_KEY = "transfer_key_to_RecyclerActivity";

    public static final String TAG = "Main_Fragment";
    private static final String NEW_PHOTO = "http://api-fotki.yandex.ru/api/recent/";
    private static final String POPULAR_PHOTO = "http://api-fotki.yandex.ru/api/top/";

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button newPhotoButton = view.findViewById(R.id.new_interesting_button);
        Button popularPhoroButton = view.findViewById(R.id.popular_button);

        newPhotoButton.setOnClickListener(mListener);
        popularPhoroButton.setOnClickListener(mListener);

    }

    View.OnClickListener mListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String string;
            if (v.getId() == (R.id.new_interesting_button)) {
                string = NEW_PHOTO;
            }
            else {
                string = POPULAR_PHOTO;
            }
            Intent intent = new Intent(getActivity(), RecyclerActivity.class);
            intent.putExtra(TRANSFER_KEY, string);
            startActivity(intent);
        }
    };
}
