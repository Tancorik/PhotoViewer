package com.example.wowtancorik.photoviewer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import static com.example.wowtancorik.photoviewer.RecyclerActivity.COUNT_PHOTO_KEY;
import static com.example.wowtancorik.photoviewer.RecyclerActivity.NUMBER_PHOTO_KEY;

public class PagerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pager);

        Intent intent = getIntent();
        int count = intent.getIntExtra(COUNT_PHOTO_KEY, 0);
        int number = intent.getIntExtra(NUMBER_PHOTO_KEY, 0);


        ViewPager viewPager = findViewById(R.id.view_pager);
        PagerAdapter pagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), count);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(number);
    }
}
