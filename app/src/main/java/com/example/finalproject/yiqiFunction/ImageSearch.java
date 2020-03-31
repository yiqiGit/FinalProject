package com.example.finalproject.yiqiFunction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finalproject.R;
import com.google.android.material.snackbar.Snackbar;

public class ImageSearch extends AppCompatActivity {
    EditText lonInfoET;
    EditText latInfoET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_search);

        Button searchBtn = (Button) findViewById(R.id.searchBtnChang);
        Button helpBtn = (Button) findViewById(R.id.helpButtonChang);

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

        helpBtn.setOnClickListener(click->{
            Snackbar.make(findViewById(R.id.helpButtonChang),"You click help",Snackbar.LENGTH_LONG).show();
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Activity Manual")

                    //What is the message:
                    .setMessage("Type in lat and lon of image, then a new page will be loaded and all information will be present." +
                            "There are 2 buttons on next page, the add button can add the image into the database, the show button can show the full" +
                            "list of image. If you click on the item, detial informaiton of that item will be shown up.")


                    .setPositiveButton("OK", (click2, arg) -> {

                    })
                    //You can add extra layout elements:
                    //            .setView(getLayoutInflater().inflate(R.layout.row_layout, null))

                    //Show the dialog
                    .create().show();
        });


    }


}
