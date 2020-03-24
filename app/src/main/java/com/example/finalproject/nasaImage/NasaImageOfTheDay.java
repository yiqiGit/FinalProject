package com.example.finalproject.nasaImage;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.DatePickerDialog;

import android.content.ClipData;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.util.Calendar;

import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.finalproject.R;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NasaImageOfTheDay extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener{
    /**
     * When this EditText view is clicked, a popup calendar will show, allowing the user to pick a date.
     */
    private EditText dateBox;
    /**
     * This is the actual date picker element that will pop up.
     */
    private DatePickerDialog datePicker;
    /**
     * Button that triggers the url inquiry.
     */
    private Button searchBtn;
    /**
     * Button that calls methods to save the image to file, and the data itself to database
     */
    private Button saveButton;
    /**
     * Button that clears all the data from the screen, resetting to initial state.
     */
    private Button clearButton;
    /**
     * Button that takes to the favourites page.
     */
    private Button favoritesButton;
    /**
     *
     */
    public static final String DESCRIPTION_KEY = "description";
    public static final String URL_KEY = "url";
    public static final String HD_URL_KEY = "hdUrl";
    public static final String TITLE_KEY = "title";
    public static final String FILE_PATH = "filePath";
    private static final String URL_PATH =
            "https://api.nasa.gov/planetary/apod?api_key=3tB4vqPWVWSdjGS4yOaRaDFMu8m4YUHgrhcRqXII&date=";
    //private List<NasaImage> imagesArray;
    private String selectedDate;
    private NasaImage myImage;
    private Bitmap image;
    private static SQLiteDatabase db;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd MMMM yyyy", Locale.CANADA);
    private SharedPreferences preferences;
    private ItemFragment dFragment;
    private FragmentManager fm = getSupportFragmentManager();
    private TextView imageTitle;
    private TextView urlLink;
    private ImageView imagePreview;
    private RelativeLayout results;
    private ImageView nasaLogo;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nasa_layout);

        preferences = getSharedPreferences(getString(R.string.appPref), Context.MODE_PRIVATE);
        String pref = preferences.getString(getString(R.string.dateKey),"");
        if(pref!=null) welcomeDialog(pref);
        saveSharedPreferences(DATE_FORMAT.format(new Date()));

        searchBtn = (Button) findViewById(R.id.searchBtn);
        saveButton = (Button) findViewById(R.id.saveBtn);
        clearButton = (Button) findViewById(R.id.clearBtn);
        favoritesButton = (Button) findViewById(R.id.goToFavBtn);
        imageTitle = (TextView) findViewById(R.id.imageTitle);
        urlLink = (TextView) findViewById(R.id.hd_url) ;
        imagePreview = (ImageView) findViewById(R.id.preview);
        results = (RelativeLayout) findViewById(R.id.resultsContainer);
        nasaLogo = (ImageView) findViewById(R.id.nasaLogo);


        Toolbar myToolbar = (Toolbar)findViewById(R.id.menuBar);
        setSupportActionBar(myToolbar);

        //imagesArray = new ArrayList<NasaImage>();
        dateBox = findViewById(R.id.dateBox);
        db = new DbOpener(this).getWritableDatabase();

        /*when the date field is touched the method onClick is called for the user to pick a date*/
        dateBox.setOnClickListener(this);
        searchBtn = findViewById(R.id.searchBtn);
        /*This button will make the app connect and download the image*/
        searchBtn.setOnClickListener(click->{
            myImage = null;
            nasaLogo.setVisibility(View.INVISIBLE);
            ImageQuery imageQuery = new ImageQuery();
            imageQuery.execute(URL_PATH+getSelectedDate());

        });



        clearButton.setOnClickListener(click->{
            dateBox.setText("");
            if(myImage!=null) {

                imageTitle.setText("");
                imagePreview.setImageBitmap(null);
                myImage = null;
                image = null;
                results.setVisibility(View.INVISIBLE);
                nasaLogo.setVisibility(View.VISIBLE);

            }
        });

        saveButton.setOnClickListener(click->{
            if(myImage!=null) {
                try {
                    //This makes sure there's no duplicates in the Db already.
                    Cursor cursor = queryForImageFile(myImage.getFileName());
                    if (cursor.getCount() == 0) {
                        printCursor(cursor);
                        insertIntoDb(myImage);
                        saveImage();
                        Snackbar snackbar = Snackbar
                                .make(findViewById(R.id.myLayout), R.string.saveConfirmation, Snackbar.LENGTH_LONG)
                                .setAction("View", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent goToFavorites = new Intent(NasaImageOfTheDay.this, FavouriteImages.class);
                                        startActivity(goToFavorites);
                                    }

                                });

                        snackbar.show();
                    } else {
                        Toast.makeText(this, R.string.duplicatedFile, Toast.LENGTH_LONG).show();
                    }

                } catch (IOException ex) {
                    Log.e("IO Exception", "onCreate: error saving image file.");
                }
            }
            else Toast.makeText(this, R.string.nullImage, Toast.LENGTH_LONG).show();
        });

        favoritesButton.setOnClickListener(click->{
            Intent goToFavorites = new Intent(this,FavouriteImages.class );
            startActivity(goToFavorites);

        });
    }

    private Cursor queryForImageFile(String fName){
        return db.query(true, DbOpener.TABLE_NAME, new String[]{DbOpener.COL_ID}, DbOpener.COL_FILE_NAME + " like ?",
                new String[]{fName}, null, null, null, null);

    }

    private void printCursor(Cursor c){

        int titleInd = c.getColumnIndex(DbOpener.COL_TITLE);
        int fileInd = c.getColumnIndex((DbOpener.COL_FILE_NAME));
        int idInd = c.getColumnIndex(DbOpener.COL_ID);


        if (c.getCount()>0) {
            c.moveToFirst();

            do {

                Log.i("id, file, Title", c.getLong(idInd) + ", \"" + c.getString(titleInd) + "\", " + c.getString(fileInd));

            } while (c.moveToNext());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.tools_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.help:
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                String message = getString(R.string.info);
                dialog.setTitle("Dialog").setMessage(message)
                        .setPositiveButton("Ok", (c, arg) -> {
                        })
                        .create().show();
                break;
        }

        return true;
    }

    private boolean saveSharedPreferences(String s){
        //creates a SharedPreference object, referring to the file contained in the Strings file_key, using the mode MODE_PRIVATE
        //source of help: https://stackoverflow.com/questions/4531396/get-value-of-a-edit-text-field

        if(!s.equals("")) {

            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(getString(R.string.dateKey), s);
            editor.commit();
            return true;
        }
        else return false;

    }

    private void welcomeDialog(String date){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        String message = getString(R.string.welcomeMessage) + " " + date;
        dialog.setTitle("Dialog").setMessage(message)
                .setPositiveButton("Ok", (c, arg) -> {

                })
                .create().show();
    }
    private static long insertIntoDb(NasaImage image){
        //add to the database and get the new ID
        ContentValues newRowValues = new ContentValues();

        //Now provide a value for every database column defined in MyOpener.java:
        //put string name in the NAME column:
        newRowValues.put(DbOpener.COL_DATE,image.getDate() );
        newRowValues.put(DbOpener.COL_DESCRIPTION, image.getDescription());
        newRowValues.put(DbOpener.COL_FILE_NAME, image.getFileName());
        newRowValues.put(DbOpener.COL_TITLE, image.getTitle());
        newRowValues.put(DbOpener.COL_URL, image.getImageUrl());
        newRowValues.put(DbOpener.COL_HD_URL, image.getHdImageUrl());

        //Now insert in the database:
        return db.insert(DbOpener.TABLE_NAME,DbOpener.COL_HD_URL, newRowValues);

    }


    private void saveImage() throws IOException{
        //This is where the Bitmap file is saved to disk
        FileOutputStream outputStream = openFileOutput(myImage.getFileName(), Context.MODE_PRIVATE);
        image.compress(Bitmap.CompressFormat.PNG, 80, outputStream);
        outputStream.flush();
        outputStream.close();
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
                        dateBox.setText(dayOfMonth + "/" + (monthOfYear+1) + "/" + year);
                        setSelectedDate(String.format("%d-%02d-%02d",year,monthOfYear+1,dayOfMonth));
                    }
                }, year, month, day);
        datePicker.show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }


    public class ImageQuery extends AsyncTask<String, Integer, String> {


        private HttpURLConnection connection;
        private InputStream response;
        private ProgressBar progBar = findViewById(R.id.progressBar);



        @Override
        protected String doInBackground(String... url) {
            try {
                connection = startConnection(url[0]);
                response = connection.getInputStream();
                publishProgress(20);

                JSONObject nasaImage = getJsonObject();
                String date = nasaImage.getString("date");
                String explanation = nasaImage.getString("explanation");
                String title = nasaImage.getString("title");
                String fileName = title +".jpg";
                String imageUrl = nasaImage.getString("url");
                String hdImageUrl = nasaImage.getString("hdurl");
                publishProgress(70);
                myImage = new NasaImage(1, date, explanation, title, fileName, imageUrl, hdImageUrl);

                if(!existOnDisk(fileName)) {

                    downloadFile(imageUrl);
                    publishProgress(100);
                }
                else loadFile(fileName);

            }
            catch (MalformedURLException ex){
                Log.e("URL error: ", ex.getMessage());
            }
            catch (IOException ex){
                Log.e("Connection error: ", ex.getMessage());
            }
            catch (JSONException ex){
                Log.e("JSON Object error:", ex.getMessage());
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

            progBar.setVisibility(View.VISIBLE);
            progBar.setProgress(values[0]);
        }


        @Override
        protected void onPostExecute(String s) {

            progBar.setVisibility(View.INVISIBLE);
            imageTitle.setText(myImage.getTitle());
            imagePreview.setImageBitmap(image);
            results.setVisibility(View.VISIBLE);
            //nasaLogo.setVisibility(View.INVISIBLE);

        }

        private HttpURLConnection startConnection(String requestedUrl) throws IOException {

            URL url = new URL(requestedUrl);
            return ((HttpURLConnection) url.openConnection());

        }

        private void downloadFile(String url) throws IOException{
            connection = startConnection(url);
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                Log.i("doInBackground: ", "Downloading " + myImage.getFileName() + " to disk.");
                image = BitmapFactory.decodeStream(connection.getInputStream());

            }
        }

        private void loadFile(String fileName){
            FileInputStream fis = null;
            try {
                fis = openFileInput(fileName);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            image = BitmapFactory.decodeStream(fis);
            Log.i("doInBackground: ", "Image " + fileName + " found on disk.");
        }

        private boolean existOnDisk(String fileName){
            File file = getBaseContext().getFileStreamPath(fileName);
            return file.exists();
        }

        private JSONObject getJsonObject() throws IOException, JSONException {
            BufferedReader reader = new BufferedReader(new InputStreamReader(response, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null)
            {
                sb.append(line + "\n");
            }
            String result = sb.toString();
            publishProgress(40);
            return new JSONObject(result);
        }


    }
}
