package com.learning.rxjava.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.learning.rxjava.R;
import com.learning.rxjava.fragments.IntroFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        changeFragment(IntroFragment.newInstance());
    }

    private void changeFragment(IntroFragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.root_container, fragment, fragment.getClass().getSimpleName())
                .commit();
    }
}
