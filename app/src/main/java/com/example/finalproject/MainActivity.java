package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonFunction1 = (Button) findViewById(R.id.button1);
        Button buttonFunction2 = (Button) findViewById(R.id.button2);
        Button buttonFunction3 = (Button) findViewById(R.id.button3);
        Button buttonFunction4 = (Button) findViewById(R.id.button4);

        buttonFunction1.setOnClickListener(click -> {
            Intent nextPage = new Intent(this, Function1.class);
            startActivity(nextPage);
        });
        buttonFunction2.setOnClickListener(click -> {
            Intent nextPage = new Intent(this, Function2.class);
            startActivity(nextPage);
        });
        buttonFunction3.setOnClickListener(click -> {
            Intent nextPage = new Intent(this, Function3.class);
            startActivity(nextPage);
        });
        buttonFunction4.setOnClickListener(click -> {
            Intent nextPage = new Intent(this, GuardianMainActivity.class);
            startActivity(nextPage);
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
