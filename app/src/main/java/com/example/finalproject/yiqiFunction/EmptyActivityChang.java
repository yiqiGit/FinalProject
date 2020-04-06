package com.example.finalproject.yiqiFunction;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.finalproject.R;

public class EmptyActivityChang extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty_chang);

        Bundle dataToPass = getIntent().getExtras(); //get the data that was passed from FragmentExample

        //This is copied directly from FragmentExample.java lines 47-54
        DetailsFragmentChang dFragment = new DetailsFragmentChang();
        dFragment.setArguments( dataToPass ); //pass data to the the fragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayoutChang, dFragment)
                .commit();
    }
}
