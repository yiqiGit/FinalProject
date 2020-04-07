package com.example.finalproject.yiqiFunction;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.finalproject.BBCNews.BbcNews;
import com.example.finalproject.GuardianMainActivity;
import com.example.finalproject.R;
import com.example.finalproject.nasaImage.NasaImageOfTheDay;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

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

public class SearchResult extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    AsyncTask<String, Integer, String> fetchImage;
    ProgressBar progressBar;
    String lonInfo;
    String latInfo;


    String imageId = "";
    Bitmap pic = null;
    String picDirectory = "";
    String imageDate = "";
    String imageResource = "";
    String imageServiceVersion = "";
    String imageUrl = "";
    long idDatabase = 0;

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
    public static final String ITEM_PIC = "PICTURE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        Toolbar myToolbar = findViewById(R.id.toolbarSearchResultChang);
        setSupportActionBar(myToolbar);

        //For NavigationDrawer:
        DrawerLayout drawer = findViewById(R.id.drawerSearchResultChang);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer, myToolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.navigationSearchResultChang);
        navigationView.setItemIconTintList(null);//this line avoids the icons to appear shaded gray. src: https://stackoverflow.com/questions/31394265/navigation-drawer-item-icon-not-showing-original-colour
        navigationView.setNavigationItemSelectedListener(this);

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
        fetchImage = new ImageQuery(); //capture image from url and save to database
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
            newRowValues.put(MyOpener.COL_PIC, picDirectory);
            newRowValues.put(MyOpener.COL_IMAGEID, imageId);


            //Now insert in the database:
            long newId = db.insert(MyOpener.TABLE_NAME, null, newRowValues);
            idDatabase = newId;
            //now you have the newId, you can create the Contact object
            ImageInfo imageInfo = new ImageInfo(latInfo,lonInfo,imageDate,imageId,imageResource,imageServiceVersion,imageUrl,picDirectory,newId);
            elements.add(imageInfo);  //add to arraylist
            myAdapter.notifyDataSetChanged();
            Toast.makeText(this, R.string.toastChang, Toast.LENGTH_LONG).show();

        });
        Button showFavourite = (Button)findViewById(R.id.showFavoriteBtnChang);
        showFavourite.setOnClickListener(click -> {
           myList.setVisibility(View.VISIBLE);

        });

        Button hideFavorite = (Button)findViewById(R.id.hideFavoriteBtnChang);
        hideFavorite.setOnClickListener(click -> {
            myList.setVisibility(View.INVISIBLE);
            Snackbar.make(findViewById(R.id.hideFavoriteBtnChang),R.string.snackbarChang3,Snackbar.LENGTH_LONG).show();
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
            dataToPass.putString(ITEM_PIC, elements.get(position).getPic() );

            dataToPass.putInt(ITEM_POSITION, position);
            dataToPass.putLong(ITEM_ID, id);

            Intent nextActivity = new Intent(SearchResult.this, EmptyActivityChang.class);
            nextActivity.putExtras(dataToPass); //send data to next activity
            startActivity(nextActivity); //make the transition

        });

        myList.setOnItemLongClickListener( (p, b, pos, id) -> {  //set on arraylist item click function (delete)
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

    @Override
    //this method is used to get the menu
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
                startActivity(new Intent(SearchResult.this, BbcNews.class));
                break;
            case R.id.guardian:
                startActivity(new Intent(SearchResult.this, GuardianMainActivity.class));
                break;
            case R.id.earth:
                startActivity(new Intent(SearchResult.this, ImageSearch.class));
                break;
            case R.id.nasaImage:
                startActivity(new Intent(SearchResult.this, NasaImageOfTheDay.class));
                break;
            case R.id.mainHelp:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle(R.string.activityMenu)

                        //What is the message:
                        .setMessage(R.string.detailInstruction)


                        .setPositiveButton("OK", (click2, arg) -> {

                        })
                        //You can add extra layout elements:
                        //            .setView(getLayoutInflater().inflate(R.layout.row_layout, null))

                        //Show the dialog
                        .create().show();
                break;
        }

        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.bbc:
                startActivity(new Intent(SearchResult.this, BbcNews.class));
                break;
            case R.id.guardian:
                startActivity(new Intent(SearchResult.this, GuardianMainActivity.class));
                break;
            case R.id.earth:
                startActivity(new Intent(SearchResult.this, ImageSearch.class));
                break;
            case R.id.nasaImage:
                startActivity(new Intent(SearchResult.this, NasaImageOfTheDay.class));
                break;
            case R.id.mainHelp:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle(R.string.activityMenu)

                        //What is the message:
                        .setMessage(R.string.detailInstruction)


                        .setPositiveButton("OK", (click2, arg) -> {

                        })
                        //You can add extra layout elements:
                        //            .setView(getLayoutInflater().inflate(R.layout.row_layout, null))

                        //Show the dialog
                        .create().show();
                break;
        }

        return false;
    }

    //delete the message from database
    protected void deleteMessage(ImageInfo c)
    {
        db.delete(MyOpener.TABLE_NAME, MyOpener.COL_ID + "= ?", new String[] {Long.toString(c.getId())});
    }

    //load data from database, before show the list, we need load all information about the previous searched image in the list
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

            String picDir = results.getString(picColumnIndex);

            //add the new Contact to the array list:
            elements.add(new ImageInfo(latData, lonData,dateData, imageIdData, resourceData,versionData,imageUrlData,picDir,id));
        }

        //At this point, the contactsList array has loaded every row from the cursor.
  //      printCursor(results,db.getVersion());
    }

    //this is an inner class to setup the view of arraylist
    private class MyListAdapter extends BaseAdapter {

        public int getCount() {
            return elements.size();
        }

        public String getItem(int position) {
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
            try {               //show the image from local storage address
                String str = elements.get(position).getPic();
                String[] arrOfStr = str.split("&");
                File f=new File(arrOfStr[0],arrOfStr[1]);
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                iView.setImageBitmap(b);
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }

            //return it to be put in the table
            return newView;
        }

    }

    //this is an inner class to grab information about image from url.
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
                String imageName = imageUrl.substring(imageUrl.length() - 20);// last 20 digit as image name
                Log.i("file", "this is the url name we are looking for: " + imageUrl);

                FileInputStream fis = null;

                if (fileExistance(imageName)) {
                    fis = openFileInput(imageName + ".png");
                    pic = BitmapFactory.decodeStream(fis);
                    picDirectory = saveToInternalStorage(pic,imageName);
                    picDirectory = picDirectory+"&"+imageName;
                    Log.i("file", "this file is from local.");
                } else {
                    URL urlPic = new URL(imageUrl);
                    HttpURLConnection connection = (HttpURLConnection) urlPic.openConnection();
                    connection.connect();
                    int responseCode = connection.getResponseCode();
                    if (responseCode == 200) {
                        pic = BitmapFactory.decodeStream(connection.getInputStream());
                        Log.i("file", "this file is from url or online.");
                        String picDir = saveToInternalStorage(pic,imageName); //save image to local storage
                        picDirectory = picDir+"&"+imageName;

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
            dateInfo.setText(getResources().getString(R.string.dateTextChang) + imageDate);

            TextView idInformation = findViewById(R.id.idInfoChang);
            idInformation.setText(getResources().getString(R.string.imageIdChang) + imageId);

            TextView sourceInformation = findViewById(R.id.resourceInfoChang);
            sourceInformation.setText(getResources().getString(R.string.sourceTextChang) + imageResource);

            TextView serviceVersionInformation = findViewById(R.id.serviceInfoChang);
            serviceVersionInformation.setText(getResources().getString(R.string.versionTextChang) + imageServiceVersion);

            TextView imageUrlInformation = findViewById(R.id.urlInfoChang);
            imageUrlInformation.setText("URL: " + imageUrl);

            progressBar.setVisibility(View.INVISIBLE);


        }

        //check if it is a local file
        public boolean fileExistance(String fname) {
            File file = getBaseContext().getFileStreamPath(fname);
            return file.exists();
        }

        // this method is used to save image into local storage and return an address string
        private String saveToInternalStorage(Bitmap bitmapImage, String name){
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            // path to /data/data/yourapp/app_data/imageDir
            File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
            // Create imageDir
            File myPath=new File(directory,name);

            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(myPath);
                // Use the compress method on the BitMap object to write image to the OutputStream
                bitmapImage.compress(Bitmap.CompressFormat.PNG, 40, fos);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return directory.getAbsolutePath();
        }
    }

}
