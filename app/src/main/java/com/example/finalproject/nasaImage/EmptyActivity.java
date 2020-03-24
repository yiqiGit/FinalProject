package com.example.finalproject.nasaImage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.finalproject.R;

public class EmptyActivity extends AppCompatActivity {

    Bundle data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty);

        data = new Bundle();

        ItemFragment dFragment = new ItemFragment(); //add a DetailFragment
        Intent fromFavourites = getIntent();

        data.putString(NasaImageOfTheDay.TITLE_KEY,fromFavourites.getStringExtra(NasaImageOfTheDay.TITLE_KEY));
        data.putString(NasaImageOfTheDay.DESCRIPTION_KEY,fromFavourites.getStringExtra(NasaImageOfTheDay.DESCRIPTION_KEY));
        data.putString(NasaImageOfTheDay.URL_KEY,fromFavourites.getStringExtra(NasaImageOfTheDay.URL_KEY));
        data.putString(NasaImageOfTheDay.HD_URL_KEY,fromFavourites.getStringExtra(NasaImageOfTheDay.HD_URL_KEY));
        data.putString(NasaImageOfTheDay.FILE_PATH,fromFavourites.getStringExtra(NasaImageOfTheDay.FILE_PATH));

        dFragment.setArguments( data ); //pass it a bundle for information
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.emptyMessageFrame, dFragment) //Add the fragment in FrameLayout
                .commit();

    }
}
