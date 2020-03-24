package com.example.finalproject.yiqiFunction;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.finalproject.R;

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
import java.util.ArrayList;

public class SearchResult extends AppCompatActivity {
    AsyncTask<String, Integer, String> fetchImage;
    ProgressBar progressBar;
    String lonInfo;
    String latInfo;

    private ArrayList<ImageInfo> elements = new ArrayList<>();
 //   private MyListAdapter myAdapter;
    SQLiteDatabase db;

    public static final String ITEM_SELECTED = "ITEM";
    public static final String ITEM_POSITION = "POSITION";
    public static final String ITEM_ID = "ID";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        Intent fromMain = getIntent();
        lonInfo = fromMain.getStringExtra("lonInfo");
        latInfo = fromMain.getStringExtra("latInfo");

        TextView lonInformation = (TextView) findViewById(R.id.lonInfoChang);
        TextView latInformation = (TextView) findViewById(R.id.latInfoChang);
        lonInformation.setText("lon: "+ lonInfo);
        latInformation.setText("lat: "+ latInfo);

        progressBar = findViewById(R.id.simpleProgressBarChang);
        progressBar.setVisibility(View.VISIBLE);

        if (fetchImage != null) {
            fetchImage.cancel(true);
        }
        fetchImage = new ImageQuery();
        fetchImage.execute();

        Button addToFavourite = (Button)findViewById(R.id.addToFavoriteBtnChang);
        addToFavourite.setOnClickListener(click -> {

        });

        Button showFavourite = (Button)findViewById(R.id.showFavoriteBtnChang);
        showFavourite.setOnClickListener(click -> {
            Intent nextPage = new Intent(this, FavouriteList.class);
            startActivity(nextPage);
        });


    }
    private class ImageQuery extends AsyncTask<String, Integer, String> {

        String imageId = "";
        Bitmap pic = null;
        String imageDate = "";
        String imageResource = "";
        String imageServiceVersion = "";
        String imageUrl = "";

        @Override                       //Type 1
        protected String doInBackground(String... strings) {

            String ret = null;
        //    String queryURL = "http://api.openweathermap.org/data/2.5/uvi?appid=7e943c97096a9784391a981c4d878b22&lat=45.348945&lon=-75.759389";
         //   String queryURL = "https://api.nasa.gov/planetary/earth/imagery/?lon=100.75&lat=1.5&date=2014-02-01&api_key=DEMO_KEY";
           String queryURL = "https://api.nasa.gov/planetary/earth/imagery/?lon=" + lonInfo + "&lat=" + latInfo + "&date=2014-02-01&api_key=DEMO_KEY";// will fix later

            try {
                URL queryLink = new URL(queryURL);
                //make http connection
                HttpURLConnection queryConnection = (HttpURLConnection) queryLink.openConnection();
                //get result in Stream
                InputStream queryStream = queryConnection.getInputStream();
                //read Stream
                BufferedReader reader = new BufferedReader(new InputStreamReader(queryStream,"UTF-8"));
                //build String
                StringBuilder sb = new StringBuilder();
                //make Stream to String
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                //result of stream to String
                String result = sb.toString();
                //become JSON Object
                JSONObject json = new JSONObject(result);
                //get uv from value
                 imageDate = json.getString("date");
                 imageId = json.getString("id");
                 imageResource = json.getString("resource");
                 imageServiceVersion = json.getString("service_version");
                 imageUrl = json.getString("url");

                String imageName = imageUrl.substring(imageUrl.length() - 7);// last six digit as image name
                Log.i("file", "this is the url name we are looking for: " + imageUrl);

                FileInputStream fis = null;

                if (fileExistance(imageName)) {
                    fis = openFileInput(imageName + ".png");
                    pic = BitmapFactory.decodeStream(fis);
                    Log.i("file", "this file is from local.");
                } else {
                    URL urlPic = new URL(imageUrl);
                    HttpURLConnection connection = (HttpURLConnection) urlPic.openConnection();
                    connection.connect();
                    int responseCode = connection.getResponseCode();
                    if (responseCode == 200) {
                        pic = BitmapFactory.decodeStream(connection.getInputStream());
                        Log.i("file", "this file is from url or online.");
                        FileOutputStream outputStream = openFileOutput(imageName + ".png", Context.MODE_PRIVATE);
                        pic.compress(Bitmap.CompressFormat.PNG, 40, outputStream);
                        outputStream.flush();
                        outputStream.close();
                    }
                }
                publishProgress(100);

                return "Executed";
            } catch (MalformedURLException mfe) {
                ret = "Malformed URL exception";
            } catch (FileNotFoundException e) {
                ret = "Can not find file.";
            } catch (IOException ioe) {
                ret = "IO Exception. Is the Wifi connected?";
        //    } catch (XmlPullParserException pe) {
        //        ret = "XML Pull exception. The XML is not properly formed";
            }catch (JSONException je) {
                ret = "JSON exception.";
            }
            //What is returned here will be passed as a parameter to onPostExecute:
            return ret;
        }
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(values[0]);
        }

        protected void onPostExecute(String sentFromDoInBackground) {
            super.onPostExecute(sentFromDoInBackground);
            //update GUI Stuff:

            ImageView image = findViewById(R.id.imageChang);
            image.setImageBitmap(pic);

            TextView dateInfo = findViewById(R.id.dateInfoChang);
            dateInfo.setText("Date: " + this.imageDate);

            TextView idInformation = findViewById(R.id.idInfoChang);
            idInformation.setText("id: " + imageId);

            TextView sourceInformation = findViewById(R.id.resourceInfoChang);
            sourceInformation.setText("source:  " + imageResource);

            TextView serviceVersionInformation = findViewById(R.id.serviceInfoChang);
            serviceVersionInformation.setText("Service Version: " + imageServiceVersion);

            progressBar.setVisibility(View.INVISIBLE);


        }

        public boolean fileExistance(String fname) {
            File file = getBaseContext().getFileStreamPath(fname);
            return file.exists();
        }
    }

}
