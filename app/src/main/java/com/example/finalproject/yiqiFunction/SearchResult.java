package com.example.finalproject.yiqiFunction;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finalproject.R;

import org.json.JSONArray;
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


    String imageId = "";
    Bitmap pic = null;
    String imageDate = "";
    String imageResource = "";
    String imageServiceVersion = "";
    String imageUrl = "";

    private ArrayList<ImageInfo> elements = new ArrayList<>();
    private MyListAdapter myAdapter;
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
            ImageInfo imageInfo = new ImageInfo(latInfo,lonInfo,imageDate,imageId,imageResource,imageServiceVersion,imageUrl,pic,0);

            elements.add(imageInfo);
            Toast.makeText(this, "The image has been add to Favourite List", Toast.LENGTH_LONG).show();

        });
        Button showFavourite = (Button)findViewById(R.id.showFavoriteBtnChang);
        showFavourite.setOnClickListener(click -> {
            myAdapter.notifyDataSetChanged();
        });
        ListView myList = (ListView) findViewById(R.id.theListViewChang);
        myList.setAdapter(myAdapter = new MyListAdapter());
        myList.setOnItemLongClickListener( (p, b, pos, id) -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            String diaMessage =  (SearchResult.this).getResources().getString(R.string.dialogMessage);
            String deleteMessage = (SearchResult.this).getResources().getString(R.string.deleteMessage);
            alertDialogBuilder.setTitle(deleteMessage.toString())

                    //What is the message:
                    .setMessage(diaMessage.toString()+ pos +" id " +id + "?")

                    //what the Yes button does:
                    .setPositiveButton("Yes", (click, arg) -> {
                        elements.remove(pos);
                        myAdapter.notifyDataSetChanged();
                    })
                    //What the No button does:
                    .setNegativeButton("No", (click, arg) -> { })


                    //You can add extra layout elements:
            //        .setView(getLayoutInflater().inflate(R.layout.view_of_list_chang, null) )

                    //Show the dialog
                    .create().show();
            return true;
        });

    }
    private class MyListAdapter extends BaseAdapter {

        public int getCount() {
            return elements.size();
        }

        public Bitmap getItem(int position) {
            return elements.get(position).getPic();
        }

        public long getItemId(int position) {
            return (long) elements.get(position).getId();
        }

        public View getView(int position, View old, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View newView;
            //make a new row:

            newView = inflater.inflate(R.layout.view_of_list_chang, parent, false);
            ImageView iView = newView.findViewById(R.id.receiveImageChang);
            iView.setImageBitmap(getItem(position));


            //return it to be put in the table
            return newView;
        }
    }
    private class ImageQuery extends AsyncTask<String, Integer, String> {



        @Override                       //Type 1
        protected String doInBackground(String... strings) {

            String ret = null;
            String queryURL = "https://dev.virtualearth.net/REST/V1/Imagery/Metadata/Aerial/"+lonInfo+ "," + latInfo + "?zl=15&o=&key=AqkcFSd4wmvahPZTWKXDTZkpcRJxS7urDdfPOwpEY7BHJWqXIqjGXF4xrbAaf5rP";// will fix later

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
                JSONArray arrayResult = json.getJSONArray("resourceSets");
                imageId = json.getString("traceId");
                imageServiceVersion = json.getString("statusCode");
                for (int i = 0; i < arrayResult.length(); i++) {

                    JSONArray index = arrayResult.getJSONObject(i).getJSONArray("resources");

                    for (int j = 0; j < index.length(); j++) {
                        imageDate = index.getJSONObject(j).getString("vintageEnd");
                        imageResource = index.getJSONObject(j).getString("__type");
                        imageUrl = index.getJSONObject(j).getString("imageUrl");

                    }
                }
                imageUrl.replaceAll("/\"","");
                String imageName = imageUrl.substring(imageUrl.length() - 20);// last six digit as image name
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
            dateInfo.setText("Date: " + imageDate);

            TextView idInformation = findViewById(R.id.idInfoChang);
            idInformation.setText("id: " + imageId);

            TextView sourceInformation = findViewById(R.id.resourceInfoChang);
            sourceInformation.setText("source:  " + imageResource);

            TextView serviceVersionInformation = findViewById(R.id.serviceInfoChang);
            serviceVersionInformation.setText("Service Version: " + imageServiceVersion);

            TextView imageUrlInformation = findViewById(R.id.urlInfoChang);
            imageUrlInformation.setText("URL: " + imageUrl);

            progressBar.setVisibility(View.INVISIBLE);


        }

        public boolean fileExistance(String fname) {
            File file = getBaseContext().getFileStreamPath(fname);
            return file.exists();
        }
    }

}
