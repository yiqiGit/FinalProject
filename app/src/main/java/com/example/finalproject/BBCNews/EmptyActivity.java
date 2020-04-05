package com.example.finalproject.BBCNews;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.finalproject.R;

/**
 * This class is empty activity class.
 *
 *@author Xiaoting Kong
 *@version 1.0
 */
public class EmptyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bbc_activity_empty);

        Intent fromBbcNews = getIntent();
        Bundle dataToPass = fromBbcNews.getExtras();

        DetailsFragment dFragment = new DetailsFragment(); //add a DetailFragment
        dFragment.setArguments(dataToPass); //pass it a bundle for information
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout_bbc, dFragment) //Add the fragment in FrameLayout
                .commit(); //actually load the fragment.
    }
}
