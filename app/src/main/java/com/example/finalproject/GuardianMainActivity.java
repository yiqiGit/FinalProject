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

import com.example.finalproject.GuardianNews.GuardianFavoriteList;
import com.example.finalproject.GuardianNews.GuardianList;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import android.os.Bundle;

public class GuardianMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    Button searchButton;
    String searchText = "";

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




        //search button
        searchButton = findViewById(R.id.searchB);
        searchButton.setOnClickListener((click) -> {
            //add base url and search item
            searchText = search.getText().toString();


            Intent intent1 = new Intent(this, GuardianList.class);
            intent1.putExtra("searchWord", searchText);

            startActivity(intent1);
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.guardiantoolbarmenu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String message = null;
        //Look at your menu XML file. Put a case for every id in that file:
        switch(item.getItemId())
        {
            //what to do when the menu item is selected:
            case R.id.overflowMenu:
                Toast.makeText(this,"You clicked on the overflow menu", Toast.LENGTH_SHORT).show();
                break;
            case R.id.questionIcon:
                Toast.makeText(this,"You clicked on question icon", Toast.LENGTH_SHORT).show();
                break;
            case R.id.infoIcon:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("The Guardian news Reader instruction")
                        .setMessage("Here is the instruction for the reader")
                        .setPositiveButton("Yes", (click, b) -> {
                            Toast.makeText(this,"Yes", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("No" ,(click, b) -> {
                            Toast.makeText(this,"No", Toast.LENGTH_SHORT).show();
                        })
                        .setNeutralButton("dismiss", (click, b) -> {

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



    @Override
    public boolean onNavigationItemSelected( MenuItem item) {

        String message = null;

        switch(item.getItemId())
        {
            //what to do when the menu item is selected:
            case R.id.questionIcon:
                message="Question";
                break;
            case R.id.infoIcon:

                message="Info";
                break;
            case R.id.checkIcon:
                this.finish();
                message="Check";
                break;

        }

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);

        Toast.makeText(this, "NavigationDrawer: " + message, Toast.LENGTH_SHORT).show();
        return false;
    }



}
