package com.example.finalproject.yiqiFunction;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.example.finalproject.R;

public class ImageSearch extends AppCompatActivity {
    EditText lonInfoET;
    EditText latInfoET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_search);

        Button searchBtn = (Button) findViewById(R.id.searchBtnChang);

        lonInfoET = (EditText) findViewById(R.id.lonChang);
        SharedPreferences shared = getSharedPreferences("lonInfo",Context.MODE_PRIVATE);
        lonInfoET.setText(shared.getString("lonInfo",""));

        latInfoET = (EditText) findViewById(R.id.latChang);
        SharedPreferences shared2 = getSharedPreferences("latInfo",Context.MODE_PRIVATE);
        lonInfoET.setText(shared2.getString("latInfo",""));

        searchBtn.setOnClickListener(click -> {
            Intent nextPage = new Intent(this, SearchResult.class);
            nextPage.putExtra("lonInfo", lonInfoET.getText().toString());
            nextPage.putExtra("latInfo", latInfoET.getText().toString());
            startActivity(nextPage);
        });

    }


}
