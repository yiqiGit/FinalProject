package com.example.finalproject.yiqiFunction;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
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
import java.io.ByteArrayOutputStream;
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
import java.nio.ByteBuffer;
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


    public static final String ITEM_LAT = "LAT";
    public static final String ITEM_LON = "LON";
    public static final String ITEM_DATE = "DATE";
    public static final String ITEM_IMAGE_ID = "IMAGE_ID";
    public static final String ITEM_IMAGE_SOURCE_INFO = "IMAGE_SOURCE_INFO";
    public static final String ITEM_IMAGE_VERSION_INFO = "IMAGE_SERVICE_VERSION_INFO";
    public static final String ITEM_IMAGE_URL = "IMAGE_URL";
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
        ListView myList = (ListView) findViewById(R.id.theListViewChang);
        loadDataFromDatabase();
        myList.setAdapter(myAdapter = new MyListAdapter());

        Button addToFavourite = (Button)findViewById(R.id.addToFavoriteBtnChang);
        addToFavourite.setOnClickListener(click -> {

            ContentValues newRowValues = new ContentValues();

            //Now provide a value for every database column defined in MyOpener.java:
            //put string message in  column:
            newRowValues.put(MyOpener.COL_DATE, imageDate);
            newRowValues.put(MyOpener.COL_LON, lonInfo);
            newRowValues.put(MyOpener.COL_LAT, latInfo);
            newRowValues.put(MyOpener.COL_RESOURCE, imageResource);
            newRowValues.put(MyOpener.COL_VERSION, imageServiceVersion);
            newRowValues.put(MyOpener.COL_IMAGEURL, imageUrl);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            pic.compress(Bitmap.CompressFormat.PNG, 100, bos);
            byte[] bArray = bos.toByteArray();
            newRowValues.put(MyOpener.COL_PIC, bArray);
            newRowValues.put(MyOpener.COL_IMAGEID, imageId);


            //Now insert in the database:
            long newId = db.insert(MyOpener.TABLE_NAME, null, newRowValues);
            //now you have the newId, you can create the Contact object
            ImageInfo imageInfo = new ImageInfo(latInfo,lonInfo,imageDate,imageId,imageResource,imageServiceVersion,imageUrl,pic,newId);
            elements.add(imageInfo);
            myAdapter.notifyDataSetChanged();
            Toast.makeText(this, "The image has been add to Favourite List", Toast.LENGTH_LONG).show();

        });
        Button showFavourite = (Button)findViewById(R.id.showFavoriteBtnChang);
        showFavourite.setOnClickListener(click -> {
           myList.setVisibility(View.VISIBLE);
        });

        myList.setOnItemClickListener((list, item, position, id) -> {
            //Create a bundle to pass data to the new fragment
            Bundle dataToPass = new Bundle();
            dataToPass.putString(ITEM_LON, elements.get(position).getLon() );
            dataToPass.putString(ITEM_LAT, elements.get(position).getLat() );
            dataToPass.putString(ITEM_DATE, elements.get(position).getDateInfo() );
            dataToPass.putString(ITEM_IMAGE_ID, elements.get(position).getIdInfo() );
            dataToPass.putString(ITEM_IMAGE_SOURCE_INFO, elements.get(position).getSourceInfo() );
            dataToPass.putString(ITEM_IMAGE_VERSION_INFO, elements.get(position).getServiceVersionInfo() );
            dataToPass.putString(ITEM_IMAGE_URL, elements.get(position).getImageUrl() );

            dataToPass.putInt(ITEM_POSITION, position);
            dataToPass.putLong(ITEM_ID, id);

            Intent nextActivity = new Intent(SearchResult.this, EmptyActivityChang.class);
            nextActivity.putExtra("image",elements.get(position).getPic());
            nextActivity.putExtras(dataToPass); //send data to next activity
            startActivity(nextActivity); //make the transition

        });

        myList.setOnItemLongClickListener( (p, b, pos, id) -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            String diaMessage =  (SearchResult.this).getResources().getString(R.string.dialogMessage);
            String deleteMessage = (SearchResult.this).getResources().getString(R.string.deleteMessage);
            alertDialogBuilder.setTitle(deleteMessage.toString())

                    //What is the message:
                    .setMessage(diaMessage.toString()+ pos +" id " +id + "\n" +
                            "?")

                    //what the Yes button does:
                    .setPositiveButton("Yes", (click, arg) -> {
                        deleteMessage(elements.get(pos));
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

    protected void deleteMessage(ImageInfo c)
    {
        db.delete(MyOpener.TABLE_NAME, MyOpener.COL_ID + "= ?", new String[] {Long.toString(c.getId())});
    }

    private void loadDataFromDatabase(){

        //get a database connection:
        MyOpener dbOpener = new MyOpener(this);
        db = dbOpener.getWritableDatabase();


        // We want to get all of the columns. Look at MyOpener.java for the definitions:
        String [] columns = {MyOpener.COL_LON, MyOpener.COL_LAT, MyOpener.COL_DATE,MyOpener.COL_ID, MyOpener.COL_RESOURCE, MyOpener.COL_VERSION, MyOpener.COL_IMAGEID, MyOpener.COL_IMAGEURL, MyOpener.COL_PIC };
        //query all the results from the database:
        Cursor results = db.query(false, MyOpener.TABLE_NAME, columns, null, null, null, null, null, null);
        //Now the results object has rows of results that match the query.
        //find the column indices:
        int lonColumnIndex = results.getColumnIndex(MyOpener.COL_LON);
        int latColumnIndex = results.getColumnIndex(MyOpener.COL_LAT);
        int dateColumnIndex = results.getColumnIndex(MyOpener.COL_DATE);
        int idColumnIndex = results.getColumnIndex(MyOpener.COL_ID);
        int resourceColumnIndex = results.getColumnIndex(MyOpener.COL_RESOURCE);
        int versionColumnIndex = results.getColumnIndex(MyOpener.COL_VERSION);
        int imageIdColumnIndex = results.getColumnIndex(MyOpener.COL_IMAGEID);
        int imageUrlColumnIndex = results.getColumnIndex(MyOpener.COL_IMAGEURL);
        int picColumnIndex = results.getColumnIndex(MyOpener.COL_PIC);

        //iterate over the results, return true if there is a next item:
        while(results.moveToNext())
        {
            String lonData = results.getString(lonColumnIndex);
            String latData = results.getString(latColumnIndex);
            String dateData = results.getString(dateColumnIndex);
            String resourceData = results.getString(resourceColumnIndex);
            String versionData = results.getString(versionColumnIndex);
            String imageIdData = results.getString(imageIdColumnIndex);
            String imageUrlData = results.getString(imageUrlColumnIndex);
            //boolean messageFromIdentifier;
            long id = results.getLong(idColumnIndex);

            byte[] imageData = results.getBlob(picColumnIndex);
            Bitmap stitchBmp = Bitmap.createBitmap(100,100,Bitmap.Config.ARGB_8888);
            stitchBmp.copyPixelsFromBuffer(ByteBuffer.wrap(imageData));

            //add the new Contact to the array list:
            elements.add(new ImageInfo(latData, lonData,dateData, imageIdData, resourceData,versionData,imageUrlData,stitchBmp,id));
        }

        //At this point, the contactsList array has loaded every row from the cursor.
  //      printCursor(results,db.getVersion());
    }

    private void printCursor(Cursor c, int version){
        Log.i("Version Number",Integer.toString(version));
        Log.i("Number of column",Integer.toString(c.getColumnCount()) );
        Log.i("Number of results", Integer.toString(c.getCount()));
        Log.i("Results", DatabaseUtils.dumpCursorToString(c));
        for(int n=0; n<c.getColumnCount();n++){
            c.moveToFirst();
            for(int m=0;m<c.getCount();m++){
                Log.i("Content of column" + c.getColumnName(n), c.getString(n));
                c.moveToNext();
            }
        }
        //•	The number of columns in the cursor.
        //•	The name of the columns in the cursor.
        //•	The number of results in the cursor
        //•	Each row of results in the cursor.
        // System.out
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
