package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;

import android.icu.util.Calendar;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;


import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class NasaImageOfTheDay extends AppCompatActivity implements View.OnClickListener{

    private EditText dateBox;
    private DatePickerDialog datePicker;
    private Button searchBtn;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nasa_layout);

        searchBtn = (Button) findViewById(R.id.searchBtn);
        dateBox = findViewById(R.id.dateBox);
        dateBox.setOnClickListener(this);
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
                    }
                }, year, month, day);
        datePicker.show();
    }
}
