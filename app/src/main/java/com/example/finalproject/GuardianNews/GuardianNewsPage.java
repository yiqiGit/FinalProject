package com.example.finalproject.GuardianNews;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.example.finalproject.R;

public class GuardianNewsPage extends AppCompatActivity {

    TextView titleTV;
    TextView urlTV;
    TextView sectionNameTV;

    String webTitle;
    String webUrl;
    String sectionName;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guardian_news_page);

        titleTV = findViewById(R.id.newsPTitle);
        urlTV = findViewById(R.id.newsPUrl);
        sectionNameTV = findViewById(R.id.newsPSN);

        //get intent
        Intent intent = getIntent();
        webTitle = intent.getStringExtra("title");
        webUrl = intent.getStringExtra("url");
        sectionName = intent.getStringExtra("section");

        //set text
        titleTV.setText(webTitle);
        sectionNameTV.setText(sectionName);

        //set url
        urlTV.setClickable(true);
        urlTV.setMovementMethod(LinkMovementMethod.getInstance());
        String link = "<a href= '"+webUrl+"'>"+ webUrl + "</a>";
        urlTV.setText(Html.fromHtml(link));



    }
}
