package com.example.finalproject.yiqiFunction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.finalproject.BBCNews.BbcNews;
import com.example.finalproject.GuardianMainActivity;
import com.example.finalproject.R;
import com.example.finalproject.nasaImage.NasaImageOfTheDay;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

public class ImageSearch extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    EditText lonInfoET;
    EditText latInfoET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_search);

        Toolbar myToolbar = findViewById(R.id.toolbarImageSearchChang);
        setSupportActionBar(myToolbar);

        //For NavigationDrawer:
        DrawerLayout drawer = findViewById(R.id.drawerImageSearchChang);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer, myToolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.navigationImageSearchChang);
        navigationView.setItemIconTintList(null);//this line avoids the icons to appear shaded gray. src: https://stackoverflow.com/questions/31394265/navigation-drawer-item-icon-not-showing-original-colour
        navigationView.setNavigationItemSelectedListener(this);

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
            Snackbar.make(findViewById(R.id.helpButtonChang),R.string.snackbarChang,Snackbar.LENGTH_LONG).show();
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(R.string.activityMenu)

                    //What is the message:
                    .setMessage(R.string.detailInstruction)


                    .setPositiveButton("OK", (click2, arg) -> {

                    })
                    //You can add extra layout elements:
                    //            .setView(getLayoutInflater().inflate(R.layout.row_layout, null))

                    //Show the dialog
                    .create().show();
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_toolbar, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //Look at your menu XML file. Put a case for every id in that file:
        switch(item.getItemId())
        {
            //what to do when the menu item is selected:
            case R.id.bbc:
                startActivity(new Intent(ImageSearch.this, BbcNews.class));
                break;
            case R.id.guardian:
                startActivity(new Intent(ImageSearch.this, GuardianMainActivity.class));
                break;
            case R.id.earth:
                startActivity(new Intent(ImageSearch.this, ImageSearch.class));
                break;
            case R.id.nasaImage:
                startActivity(new Intent(ImageSearch.this, NasaImageOfTheDay.class));
                break;
        }

        return true;
    }



    @Override
    public boolean onNavigationItemSelected( MenuItem item) {


        switch(item.getItemId())
        {
            //what to do when the menu item is selected:
            case R.id.bbc:
                startActivity(new Intent(ImageSearch.this, BbcNews.class));
                break;
            case R.id.guardian:
                startActivity(new Intent(ImageSearch.this, GuardianMainActivity.class));
                break;
            case R.id.earth:
                startActivity(new Intent(ImageSearch.this, ImageSearch.class));
                break;
            case R.id.nasaImage:
                startActivity(new Intent(ImageSearch.this, NasaImageOfTheDay.class));
                break;

        }


        return false;
    }




}
