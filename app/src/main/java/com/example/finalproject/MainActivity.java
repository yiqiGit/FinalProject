package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.finalproject.nasaImage.NasaImageOfTheDay;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar myToolbar = (Toolbar)findViewById(R.id.mainBar);
        setSupportActionBar(myToolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer, myToolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null); //this line avoids the icons to appear shaded gray. src: https://stackoverflow.com/questions/31394265/navigation-drawer-item-icon-not-showing-original-colour
        navigationView.setNavigationItemSelectedListener(this);




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_toolbar, menu);
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.bbc:
               startActivity(new Intent(MainActivity.this, Function2.class));
                break;
            case R.id.guardian:
                startActivity(new Intent(MainActivity.this, Bbbnews.class));
                break;
            case R.id.earth:
                startActivity(new Intent(MainActivity.this, ImageSearch.class));
                break;
            case R.id.nasaImage:
                startActivity(new Intent(MainActivity.this, NasaImageOfTheDay.class));
                break;

        }
        return true;
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.bbc:
                startActivity(new Intent(MainActivity.this, Function2.class));
                break;
            case R.id.guardian:
                startActivity(new Intent(MainActivity.this, Bbbnews.class));
                break;
            case R.id.earth:
                startActivity(new Intent(MainActivity.this, ImageSearch.class));
                break;
            case R.id.nasaImage:
                startActivity(new Intent(MainActivity.this, NasaImageOfTheDay.class));
                break;

        }
        return false;
    }
}
