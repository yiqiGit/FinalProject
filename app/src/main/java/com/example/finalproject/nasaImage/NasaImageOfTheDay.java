package com.example.finalproject.nasaImage;



import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.DatePickerDialog;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.util.Calendar;

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


import com.example.finalproject.BBCNews.BbcNews;
import com.example.finalproject.GuardianMainActivity;
import com.example.finalproject.R;
import com.example.finalproject.yiqiFunction.ImageSearch;
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
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
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
     *This field is used as a key for the item description when adding information on {@link Bundle} to other activities
     */
    public static final String DESCRIPTION_KEY = "description";
    /**
     *This field is used as a key for the item url when adding information on {@link Bundle} to other activities
     */
    public static final String URL_KEY = "url";
    /**
     *This field is used as a key for the item's hd url when adding information on {@link Bundle} to other activities
     */
    public static final String HD_URL_KEY = "hdUrl";
    /**
     *This field is used as a key for the item's title when adding information on {@link Bundle} to other activities
     */
    public static final String TITLE_KEY = "title";
    /**
     *This field is used as a key for the image's path url when adding information on {@link Bundle} to other activities
     */
    public static final String FILE_PATH = "filePath";
    /**
     * This is the root url used to access the nasaImages API. Simply concatenate the date string at the end.
     */
    private static final String URL_PATH =
            "https://api.nasa.gov/planetary/apod?api_key=3tB4vqPWVWSdjGS4yOaRaDFMu8m4YUHgrhcRqXII&date=";
    /**
     *This variable holds the selected date as a String.
     */
    private String selectedDate;
    /**
     * This Class holds the information about each image that is downloaded and saved.
     */
    private NasaImage myImage;
    /**
     * This is where the {@link Bitmap} image file is stored during program execution
     */
    private Bitmap image;
    /**
     * This is a helper object to access {@link SQLiteDatabase} methods.
     */
    private static SQLiteDatabase db;
    /**
     * This is a format for the date that holds the last login date information.
     */
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd MMMM yyyy", Locale.CANADA);
    /**
     * Shared preferences object to help store last login date.
     */
    private SharedPreferences preferences;
    /**
     * This is where the image's title is displayed after downloaded.
     */
    private TextView imageTitle;
    /**
     * This is where the actual image is displayed after downloaded.
     */
    private TextView urlLink;
    private ImageView imagePreview;
    /**
     * This object holds the other elements that display image data. Its purpose is simply to turn the visibility on or off
     */
    private RelativeLayout results;
    /**
     * This object holds the main page Nasa logo
     */
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
        urlLink = (TextView) findViewById(R.id.hd_url_result) ;
        imagePreview = (ImageView) findViewById(R.id.preview);
        results = (RelativeLayout) findViewById(R.id.resultsContainer);
        nasaLogo = (ImageView) findViewById(R.id.nasaLogo);

        urlLink.setOnClickListener(click->{
            if(myImage.getHdImageUrl()!=null) {
                Intent browserIntent = new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(myImage.getHdImageUrl()));
                startActivity(browserIntent);
            }
            else{
                Toast.makeText(this, "HD url not available.", Toast.LENGTH_LONG).show();
            }
        });


        //imagesArray = new ArrayList<NasaImage>();
        dateBox = findViewById(R.id.dateBox);
        dateBox.setText("");
        db = new DbOpener(this).getWritableDatabase();

        /*when the date field is touched the method onClick is called for the user to pick a date*/
        dateBox.setOnClickListener(this);
        searchBtn = findViewById(R.id.searchBtn);
        /*This button will make the app connect and download the image*/
        searchBtn.setOnClickListener(click->{
            if(dateBox.getText().toString().equals("")){
                Toast.makeText(NasaImageOfTheDay.this, R.string.emptyDate,Toast.LENGTH_LONG).show();
            }
            else {

                if(image!=null) clearScreen();
                //Get a current date as reference
                Calendar currentDate = Calendar.getInstance();
                LocalDate date = LocalDate.now();


                Calendar userDate = (Calendar) currentDate.clone();
                currentDate.set(date.getYear(), date.getMonthValue(), date.getDayOfMonth());
                //Parse the selected date to Calendar object

                int year = Integer.parseInt(selectedDate.substring(0, 4));
                int month = Integer.parseInt(selectedDate.substring(5, 7));
                int day = Integer.parseInt(selectedDate.substring(8, 10));
                userDate.set(year, month, day);
                //Compare the two dates, since a future date is not accepted by the Nasa api
                if (userDate.after(currentDate)) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(NasaImageOfTheDay.this);
                    String message = getString(R.string.wrongDate);
                    dialog.setTitle("Warning:").setMessage(message)
                            .setPositiveButton("Ok", (c, arg) -> {
                            })
                            .create().show();
                } else {
                    myImage = null;
                    nasaLogo.setVisibility(View.INVISIBLE);
                    ImageQuery imageQuery = new ImageQuery();
                    try{
                        imageQuery.execute(URL_PATH + getSelectedDate());
                    }
                    catch (Exception ex){
                        AlertDialog.Builder dialog = new AlertDialog.Builder(NasaImageOfTheDay.this);
                        String message = ex.getMessage();
                        dialog.setTitle("Warning:").setMessage(message)
                                .setPositiveButton("Ok", (c, arg) -> {
                                })
                                .create().show();
                    }
                }
            }



        });



        clearButton.setOnClickListener(click->{
            clearScreen();
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
        Toolbar myToolbar = (Toolbar)findViewById(R.id.mainMenuBar);
        setSupportActionBar(myToolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer, myToolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nasa_mainNav);
        navigationView.setItemIconTintList(null); //this line avoids the icons to appear shaded gray. src: https://stackoverflow.com/questions/31394265/navigation-drawer-item-icon-not-showing-original-colour
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void clearScreen(){
        dateBox.setText("");
        if(myImage!=null) {

            imageTitle.setText("");
            imagePreview.setImageBitmap(null);
            myImage = null;
            image = null;
            results.setVisibility(View.INVISIBLE);
            nasaLogo.setVisibility(View.VISIBLE);

        }
    }

    /**
     * This method queries the database for the image's filename, and returns a cursor with the results, if any.
     * @param fName the file name that is being searched for
     * @return Cursor object holding the results
     */
    private Cursor queryForImageFile(String fName){
        return db.query(true, DbOpener.TABLE_NAME, new String[]{DbOpener.COL_ID}, DbOpener.COL_FILE_NAME + " like ?",
                new String[]{fName}, null, null, null, null);

    }

    /**
     * This method logs the information about the image file, which is contained in the {@link Cursor} object.
     * @param c Cursor object holding the results from the query for image file name.
     */
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
                dialog.setTitle("Help").setMessage(message)
                        .setPositiveButton("Ok", (c, arg) -> {
                        })
                        .create().show();
                break;
            case R.id.about:
                AlertDialog.Builder dialogAbout = new AlertDialog.Builder(this);
                String messageAbout = getString(R.string.about);
                dialogAbout.setTitle("Info").setMessage(messageAbout)
                        .setPositiveButton("Ok", (c, arg) -> {
                        })
                        .create().show();
            case R.id.bbc:
                startActivity(new Intent(this, BbcNews.class));
                break;
            case R.id.guardian:
                startActivity(new Intent(this, GuardianMainActivity.class));
                break;
            case R.id.earth:
                startActivity(new Intent(this, ImageSearch.class));
                break;
            case R.id.nasaImage:
                startActivity(new Intent(this, NasaImageOfTheDay.class));
                break;
        }

        return true;
    }


        /**
         * This method saves a string to the project's {@link SharedPreferences}
         * @param s String to be saved in the SharedPreferences
         * @return true if successful, or false otherwise.
         */
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

    /**
     * This method displays a welcome message informing the last login date.
     * @param date last login date.
     */
    private void welcomeDialog(String date){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        String message = getString(R.string.welcomeMessage) + " " + date;
        dialog.setTitle("Welcome").setMessage(message)
                .setPositiveButton("Ok", (c, arg) -> {

                })
                .create().show();
    }

    /**
     * Inserts the information contained in the NasaImage object to the database
     * @param image NasaImage object
     * @return long variable with the id generated for this last inserted row
     */
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

    /**
     * Saves the {@link Bitmap} image file to disk
     * @throws IOException
     */
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
        int currDay = cldr.get(Calendar.DAY_OF_MONTH);
        int currMonth = cldr.get(Calendar.MONTH);
        int currYear = cldr.get(Calendar.YEAR);
        setSelectedDate("");
        dateBox.setText("");

        // date picker dialog
        datePicker = new DatePickerDialog(NasaImageOfTheDay.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        dateBox.setText(dayOfMonth + "/" + (monthOfYear+1) + "/" + year);
                        setSelectedDate(String.format("%d-%02d-%02d",year,monthOfYear+1,dayOfMonth));
                    }
                }, currYear, currMonth, currDay);
        datePicker.show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()){
            case R.id.bbc:
                startActivity(new Intent(this, BbcNews.class));
                break;
            case R.id.guardian:
                startActivity(new Intent(this, GuardianMainActivity.class));
                break;
            case R.id.earth:
                startActivity(new Intent(this, ImageSearch.class));
                break;
            case R.id.nasaImage:
                startActivity(new Intent(this, NasaImageOfTheDay.class));
                break;
        }


        return true;
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
                throw new RuntimeException("There was an error while processing the request.");
            }
            catch (IOException ex){
                Log.e("Connection error: ", ex.getMessage());
                throw new RuntimeException("There was an error while processing the request.");
            }
            catch (JSONException ex){
                Log.e("JSON Object error:", ex.getMessage());
                throw new RuntimeException("There was an error while processing the request.");
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


        }

        /**
         * Establishes a {@link HttpURLConnection} for the requested url
         * @param requestedUrl the url to be accessed
         * @return a connection object of type HttpURLConnection
         * @throws IOException
         */
        private HttpURLConnection startConnection(String requestedUrl) throws IOException {

            URL url = new URL(requestedUrl);
            return ((HttpURLConnection) url.openConnection());

        }

        /**
         * downloads the image file from the requested url
         * @param url the url for the image
         * @throws IOException
         */
        private void downloadFile(String url) throws IOException{
            connection = startConnection(url);
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                Log.i("doInBackground: ", "Downloading " + myImage.getFileName() + " to disk.");
                image = BitmapFactory.decodeStream(connection.getInputStream());

            }
        }

        /**
         * Loads the image file with name <fileName> from disk into the field image
         * @param fileName image fileName
         */
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

        /**
         * Checks if the given fileName exists on disk
         * @param fileName
         * @return true if exists or false if it doesn't
         */
        private boolean existOnDisk(String fileName){
            File file = getBaseContext().getFileStreamPath(fileName);
            return file.exists();
        }

        /**
         * Creates a {@link BufferedReader} to store the data from the InputStream. Creates a {@link StringBuilder} to
         * append the data read from the BufferedReader into a single String. Creates and return a {@link JSONObject} using this newly formed String.
         * @return JSONObject
         * @throws IOException
         * @throws JSONException
         */
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
