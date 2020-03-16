package com.example.finalproject.nasaImage;



import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.app.DatePickerDialog;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.util.Calendar;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.finalproject.R;
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
import java.util.ArrayList;
import java.util.List;

public class NasaImageOfTheDay extends AppCompatActivity implements View.OnClickListener{

    private EditText dateBox;
    private TextView imageTitleText;
    private TextView imageDescriptionText;
    private TextView urlText;
    private TextView hdUrlLink;
    private DatePickerDialog datePicker;
    private Button searchBtn;
    private Button saveButton;
    private Button clearButton;
    private Button favoritesButton;
    private ImageView imageView;
    private static final String FILE_NAME = "FILE_NAME";
    private static final String FILE_PATH = "FILE_PATH";
    private static final String URL_PATH =
            "https://api.nasa.gov/planetary/apod?api_key=3tB4vqPWVWSdjGS4yOaRaDFMu8m4YUHgrhcRqXII&date=";
    private List<NasaImage> imagesArray;
    private String selectedDate;
    private NasaImage myImage;
    private Bitmap image;
    private static SQLiteDatabase db;


    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nasa_layout);

        searchBtn = (Button) findViewById(R.id.searchBtn);
        saveButton = (Button) findViewById(R.id.saveBtn);
        clearButton = (Button) findViewById(R.id.clearBtn);
        favoritesButton = (Button) findViewById(R.id.goToFavBtn);
        imageTitleText = (TextView) findViewById(R.id.imageTitle);
        imageDescriptionText = (TextView) findViewById(R.id.description);
        urlText = (TextView) findViewById(R.id.url);
        hdUrlLink = (TextView) findViewById(R.id.hd_url);
        imageView = (ImageView) findViewById(R.id.preview);
        imagesArray = new ArrayList<NasaImage>();
        dateBox = findViewById(R.id.dateBox);
        db = new DbOpener(this).getWritableDatabase();

        /*when the date field is touched the method onClick is called for the user to pick a date*/
        dateBox.setOnClickListener(this);
        searchBtn = findViewById(R.id.searchBtn);
        /*This button will make the app connect and download the image*/
        searchBtn.setOnClickListener(click->{
            ImageQuery imageQuery = new ImageQuery();
            imageQuery.execute(URL_PATH+getSelectedDate());
        });

        hdUrlLink.setOnClickListener(click->{
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

        clearButton.setOnClickListener(click->{
            dateBox.setText("");
            imageTitleText.setText("");
            imageDescriptionText.setText("");
            imageView.setImageBitmap(null);
            urlText.setText("");
        });
        saveButton.setOnClickListener(click->{
            try {

                long id = insertIntoDb(myImage);
                saveImage();


                Snackbar snackbar = Snackbar
                        .make(findViewById(R.id.myLayout), "Image added to your favorites!", Snackbar.LENGTH_LONG)
                        .setAction("View", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent goToFavorites = new Intent(NasaImageOfTheDay.this,FavouriteImages.class );
                                startActivity(goToFavorites);
                            }
                        });

                snackbar.show();
            }
            catch (IOException ex){
                Log.e("IO Exception", "onCreate: error saving image file." );
            }
        });
        favoritesButton.setOnClickListener(click->{
            Intent goToFavorites = new Intent(this,FavouriteImages.class );
            startActivity(goToFavorites);

        });






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
        long newId = db.insert(DbOpener.TABLE_NAME,DbOpener.COL_HD_URL, newRowValues);
        return newId;
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

    public class ImageQuery extends AsyncTask<String, Integer, String> {


        private HttpURLConnection connection;
        private InputStream response;



        @Override
        protected String doInBackground(String... url) {
            try {
                connection = startConnection(url[0]);
                response = connection.getInputStream();

                JSONObject nasaImage = getJsonObject();
                String date = nasaImage.getString("date");
                String explanation = nasaImage.getString("explanation");
                String title = nasaImage.getString("title");
                String fileName = title +".jpg";
                String imageUrl = nasaImage.getString("url");
                String hdImageUrl = nasaImage.getString("hdurl");
                myImage = new NasaImage(1, date, explanation, title, fileName, imageUrl, hdImageUrl);

                if(existOnDisk(fileName)) {

                    loadFile(fileName);
                    publishProgress(100);
                }
                else{

                    downloadFile(imageUrl);
                    publishProgress(100);
                }



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
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s) {
            //super.onPostExecute(s);

            imageView.setImageBitmap(image);
            imageDescriptionText.setText(myImage.getDescription());
            imageTitleText.setText(myImage.getTitle());
            urlText.setText(("Url: "+ myImage.getImageUrl()));

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
            return new JSONObject(result);
        }


    }
}
