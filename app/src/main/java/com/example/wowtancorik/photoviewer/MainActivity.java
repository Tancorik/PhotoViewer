package com.example.wowtancorik.photoviewer;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * Стартовая активити держит MainFragment
 *
 * * Create by Aleksandr Karpachev
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_main_holder, MainFragment.newInstance(), MainFragment.TAG)
                .commit();
    }
}
