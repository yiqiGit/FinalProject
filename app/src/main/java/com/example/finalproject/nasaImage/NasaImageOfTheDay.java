package com.example.finalproject.nasaImage;



import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;

import android.icu.util.Calendar;
import android.media.Image;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;


import com.example.finalproject.R;

public class NasaImageOfTheDay extends AppCompatActivity implements View.OnClickListener{

    private EditText dateBox;
    private DatePickerDialog datePicker;
    private Button searchBtn;
    private String selectedDate;
    private static final String FILE_NAME = "FILE_NAME";
    private static final String FILE_PATH = "FILE_PATH";
    private static final String URL_PATH =
            "https://api.nasa.gov/planetary/apod?api_key=3tB4vqPWVWSdjGS4yOaRaDFMu8m4YUHgrhcRqXII&date=";
    private static FileLogic logic;


    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nasa_layout);

        searchBtn = (Button) findViewById(R.id.searchBtn);
        dateBox = findViewById(R.id.dateBox);

        /*when the date field is touched the method onClick is called for the user to pick a date*/
        dateBox.setOnClickListener(this);
        searchBtn = findViewById(R.id.searchBtn);
        /*This button will make the app connect and download the image*/
        searchBtn.setOnClickListener(click->{
            ImageDownloader imageDownloader = new ImageDownloader();
            imageDownloader.execute(URL_PATH+getSelectedDate());

        });

    }

    private void setSelectedDate(String date){
        this.selectedDate = date;
    }

    public String getSelectedDate(){
        return selectedDate;
    }

    @Override
    public void onClick(View v){
        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
        // date picker dialog
        datePicker = new DatePickerDialog(NasaImageOfTheDay.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        dateBox.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                        setSelectedDate(String.format("%d-%02d-%02d",year,monthOfYear,dayOfMonth));
                    }
                }, year, month, day);
        datePicker.show();
    }
}
