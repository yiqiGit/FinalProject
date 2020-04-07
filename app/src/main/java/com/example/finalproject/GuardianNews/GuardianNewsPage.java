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

import com.example.finalproject.GuardianMainActivity;
import com.example.finalproject.R;
import com.example.finalproject.yiqiFunction.MyOpener;

/**
 * This is the page which display details about one news with "add to favorite" button
 * which will send this news to the favorite list for future reading.
 *
 *@author Pei Lun Zou
 *@version 1.0
 */

public class GuardianNewsPage extends AppCompatActivity {

    /**
     * The TextView to display the news' title.
     */
    TextView titleTV;
    /**
     * The TextView to display the news' url.
     */
    TextView urlTV;
    /**
     * The TextView to display the news' section name.
     */
    TextView sectionNameTV;

    /**
     * The string that stores the title of the news passed from GuardianList or GuardianFavoriteList
     */
    String webTitle;
    /**
     * The string that stores the url of the news passed from GuardianList or GuardianFavoriteList
     */
    String webUrl;
    /**
     * The string that stores the section name of the news passed from GuardianList or GuardianFavoriteList
     */
    String sectionName;
    /**
     * The database opener.
     */
    GuardianOpener dbHelper;
    /**
     * The database.
     */
    SQLiteDatabase db;


    @RequiresApi(api = Build.VERSION_CODES.N)

    /**
     * This onCreate method gets strings from GuardianList and GuardianFavoriteList,
     * then store them in local variables. If user click on "Save to Favorite" button,
     * the stored local variables will be send to GuardianFavoriteList.
     */
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

        Button goToMain = findViewById(R.id.goToMainButton);
        goToMain.setOnClickListener( (click) -> {

            Intent goMainIntent = new Intent (this, GuardianMainActivity.class);
            startActivity(goMainIntent);
        });

    }


}
