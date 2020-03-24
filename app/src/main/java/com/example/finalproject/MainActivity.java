package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.finalproject.BBCNews.BbcNews;
import com.example.finalproject.nasaImage.NasaImageOfTheDay;
import com.example.finalproject.yiqiFunction.ImageSearch;
import com.google.android.material.navigation.NavigationView;

/**
 * This class is the main activity class.
 *
 *@version 1.0
 */
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
                startActivity(new Intent(MainActivity.this, BbcNews.class));
                break;
            case R.id.guardian:
                startActivity(new Intent(MainActivity.this, Function4.class));
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
                startActivity(new Intent(MainActivity.this, BbcNews.class));
                break;
            case R.id.guardian:
                startActivity(new Intent(MainActivity.this, Function4.class));
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