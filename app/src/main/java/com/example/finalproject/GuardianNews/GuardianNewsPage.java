package com.example.finalproject.GuardianNews;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalproject.R;
import com.example.finalproject.yiqiFunction.MyOpener;

public class GuardianNewsPage extends AppCompatActivity {

    TextView titleTV;
    TextView urlTV;
    TextView sectionNameTV;

    String webTitle;
    String webUrl;
    String sectionName;

 GuardianOpener dbHelper;
 SQLiteDatabase db;


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


        //Button add favorite

        Button favButton = findViewById(R.id.favButton);
        favButton.setOnClickListener((click) -> {

//            // intent send info to GuardianFavoriteList
//            Intent intent2 = new Intent (this, GuardianFavoriteList.class);
//            intent2.putExtra("favTitle", webTitle);
//            intent2.putExtra("favUrl", webUrl);
//            intent2.putExtra("favSectionName", sectionName);

            // put info into database
         dbHelper = new GuardianOpener(getApplicationContext());
         db = dbHelper.getWritableDatabase();

        ContentValues newRowValues = new ContentValues();
        newRowValues.put (GuardianOpener.COL_TITLE, webTitle);
        newRowValues.put (GuardianOpener.COL_URL, webUrl);
        newRowValues.put (GuardianOpener.COL_SECTIONNAME, sectionName);

        long newId = db.insert(GuardianOpener.TABLE_NAME, null, newRowValues);


            Toast.makeText(this, "This page has been added to Favorite", Toast.LENGTH_SHORT).show();


        });

    }


}
