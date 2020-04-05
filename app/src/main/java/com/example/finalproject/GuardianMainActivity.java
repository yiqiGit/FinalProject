package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.finalproject.BBCNews.BbcNews;
import com.example.finalproject.GuardianNews.GuardianFavoriteList;
import com.example.finalproject.GuardianNews.GuardianList;
import com.example.finalproject.nasaImage.NasaImageOfTheDay;
import com.example.finalproject.yiqiFunction.ImageSearch;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import android.os.Bundle;

/**
 * This is the main activity for The Guardian News app.
 *
 *@author Pei Lun Zou
 *@version 1.0
 */


public class GuardianMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    /**
     * The searchButton is a Button when clicked, the content in the EditText will be sent to the GuardianList activity, and go to GuardianList.
     */
    Button searchButton;

    /**
     * searchText is the user input that is typed into the EditText field.
     */
    String searchText = "";


    /**
     * The onCreate method of this activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guardian_activity_main);

        //ToolBar

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        //For NavigationDrawer:
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer, myToolbar, R.string.guardian_open, R.string.guardian_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        //shared preference on search edit text
        EditText search = findViewById(R.id.searchET);
        SharedPreferences sp = getSharedPreferences("searchText", MODE_PRIVATE);
        String searchResult = sp.getString("searchText", "");
        search.setText(searchResult);




        //search button and onClickListener to send searchText to GuardianList activity.
        searchButton = findViewById(R.id.searchB);
        searchButton.setOnClickListener((click) -> {
            //add base url and search item
            searchText = search.getText().toString();


            Intent intent1 = new Intent(this, GuardianList.class);
            intent1.putExtra("searchWord", searchText);

            startActivity(intent1);
        });


    }

    /**
     * OnCreateOptionsMenu method that inflate the menu items for use in the tool bar.
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.guardiantoolbarmenu, menu);

        return true;
    }

    /**
     * onOptionsItemSelected method add function to each item from the toolBar.
     */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //Look at your menu XML file. Put a case for every id in that file:
        switch(item.getItemId())
        {
            //what to do when the menu item is selected:
            case R.id.overflowMenu:
               //Snackbar.make (findViewById(R.id.overflowMenu), getString(R.string.G_OverFlowMenu), Snackbar.LENGTH_SHORT).show();
                Toast.makeText(this, getString(R.string.G_OverFlowMenu), Toast.LENGTH_SHORT).show();
                break;
            case R.id.homeIcon:

                Intent goHomeIntent = new Intent (this, MainActivity.class);
                startActivity(goHomeIntent);

                break;
            case R.id.infoIcon:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("The Guardian news Reader instruction")
                        .setMessage((getString(R.string.G_Instruction1)) +"\n\n"
                                +(getString(R.string.G_Instruction2)) + "\n\n"
                                +(getString(R.string.G_Instruction3)) + "\n\n"
                                +(getString(R.string.G_Instruction4)) + "\n\n"
                                +(getString(R.string.G_Instruction5)) +"\n\n")


                        .setPositiveButton(getString(R.string.G_Yes), (click, b) -> {
                            Toast.makeText(this,"Yes", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton(getString(R.string.G_No) ,(click, b) -> {
                            Toast.makeText(this,"No", Toast.LENGTH_SHORT).show();
                        })
                        .setNeutralButton(getString(R.string.G_Dismiss), (click, b) -> {

                        })
                        .create().show();
                break;
            case R.id.checkIcon:
                Intent intent = new Intent (this, GuardianFavoriteList.class);
                startActivity(intent);
                break;
        }

        return true;
    }


    /**
     * onNavigationItemSelected method set functions for each menu item.
     */

    @Override
    public boolean onNavigationItemSelected( MenuItem item) {

        String message = null;

        switch(item.getItemId())
        {
            //what to do when the menu item is selected:
            case R.id.g_item1:

                Intent bbcIntent = new Intent(this, BbcNews.class);
                startActivity(bbcIntent);
                message = "Go to BBC News";

                break;
            case R.id.g_item2:

                Intent nasaImgItent = new Intent(this, NasaImageOfTheDay.class);
                startActivity(nasaImgItent);
                message = "Go to NASA Image";
                break;

            case R.id.g_item3:

                Intent nasaEIntent = new Intent (this, ImageSearch.class);
                startActivity(nasaEIntent);
                message="Go to NASA Earth";
                break;

        }

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);

        Toast.makeText(this, "NavigationDrawer: " + message, Toast.LENGTH_SHORT).show();
        return false;
    }



}
