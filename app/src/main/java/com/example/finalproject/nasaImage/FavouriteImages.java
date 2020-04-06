package com.example.finalproject.nasaImage;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;


import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalproject.R;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FavouriteImages extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener{

    /*some resources were adapted from:
    * https://www.tutlane.com/tutorial/android/android-popup-menu-with-examples
    * https://code.tutsplus.com/tutorials/android-sdk-implement-a-share-intent--mobile-8433
    * */
    private static SQLiteDatabase db;
    private List<NasaImage> images;
    private long itemId;
    private ImageAdapter imageAdapter;
    private int itemPosition;
    private Intent sharingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_images);
        Resources res = this.getResources();
        ListView myList = findViewById(R.id.imageList);
        images = new ArrayList<>();
        Button goBackBtn = (Button) findViewById(R.id.goBack) ;
        goBackBtn.setOnClickListener(click-> finish());

        loadDataFromDatabase();
        //Sorts the images by date from newest to oldest (See compareTo method in the NasaImage class)
        Collections.sort(images);
        imageAdapter = new ImageAdapter();
        myList.setAdapter( imageAdapter);
        sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");

        //This sets the action to be executed when one item from the list is clicked.
        myList.setOnItemClickListener(( parent, v,position, id) -> {
                itemId = id;
                itemPosition = position;
                PopupMenu popup = new PopupMenu(FavouriteImages.this, v);
                popup.setOnMenuItemClickListener(FavouriteImages.this);
                popup.getMenuInflater().inflate(R.menu.options, popup.getMenu());

                popup.show();

        });
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.share:

                String imageToShare = images.get(itemPosition).getHdImageUrl();
                sharingIntent.putExtra(Intent.EXTRA_TEXT, imageToShare);
                startActivity(Intent.createChooser(sharingIntent,"Share via"));
                break;
            case R.id.delete:
                deleteItemDialog();
                break;
            case R.id.open:
                Bundle dataToPass = new Bundle();
                dataToPass.putString(NasaImageOfTheDay.DESCRIPTION_KEY, images.get(itemPosition).getDescription());
                dataToPass.putString(NasaImageOfTheDay.TITLE_KEY, images.get(itemPosition).getTitle());
                dataToPass.putString(NasaImageOfTheDay.URL_KEY, images.get(itemPosition).getImageUrl());
                dataToPass.putString(NasaImageOfTheDay.HD_URL_KEY, images.get(itemPosition).getHdImageUrl());
                dataToPass.putString(NasaImageOfTheDay.FILE_PATH, images.get(itemPosition).getFileName());


                Intent nextActivity = new Intent(FavouriteImages.this, EmptyActivity.class);
                nextActivity.putExtras(dataToPass); //send data to next activity
                startActivity(nextActivity); //make the transition
                break;

        }
        return false;
    }

    /**
     * This method loads the data from the database and stores it into the {@link ArrayList} images.
     */
    private void loadDataFromDatabase()
    {
        //get a database connection:
        DbOpener dbOpener = new DbOpener(this);
        db = dbOpener.getWritableDatabase();


        // We want to get all of the columns. Look at MyOpener.java for the definitions:
        String [] columns = {DbOpener.COL_ID,DbOpener.COL_DATE, DbOpener.COL_DESCRIPTION, DbOpener.COL_TITLE,
                            DbOpener.COL_FILE_NAME, DbOpener.COL_URL, DbOpener.COL_HD_URL};
        //query all the results from the database:
        Cursor results = db.query(false, DbOpener.TABLE_NAME, columns, null,
                null, null, null, null, null);

        //Now the results object has rows of results that match the query.
        //find the column indices:
        int idColumnIndex = results.getColumnIndex(DbOpener.COL_ID);
        int dateColumnIndex = results.getColumnIndex(DbOpener.COL_DATE);
        int descriptionColumnIndex = results.getColumnIndex(DbOpener.COL_DESCRIPTION);
        int titleColumnIndex = results.getColumnIndex(DbOpener.COL_TITLE);
        int fileNameColumnIndex = results.getColumnIndex(DbOpener.COL_FILE_NAME);
        int urlColumnIndex = results.getColumnIndex(DbOpener.COL_URL);
        int hdUrlColumnIndex = results.getColumnIndex(DbOpener.COL_HD_URL);

        //iterate over the results, return true if there is a next item:
        while(results.moveToNext())
        {
            String date = results.getString(dateColumnIndex);
            String description = results.getString(descriptionColumnIndex);
            String title = results.getString(titleColumnIndex);
            String fileName = results.getString(fileNameColumnIndex);
            String url = results.getString(urlColumnIndex);
            String hdUrl = results.getString(hdUrlColumnIndex);
            long id = results.getLong(idColumnIndex);

            //add the new Contact to the array list:
            images.add(new NasaImage(id, date, description, title, fileName, url, hdUrl));
        }


    }

    /**
     * Displays a dialog box and if the ok button is pressed, the item in the current position is deleted from the database
     * and the adapter updates the {@link ListView}
     */
    private void deleteItemDialog(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        String message = getString(R.string.popUpMessage);
        dialog.setTitle("Dialog").setMessage(message)
                .setPositiveButton("Ok", (c, arg) -> {

                    String toastMessage;
                    if (deleteFromDb(itemId))
                        toastMessage = getString(R.string.deleted_true);
                    else
                        toastMessage = getString(R.string.deleted_false);
                    Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show();
                    images.remove(itemPosition);
                    imageAdapter.notifyDataSetChanged();
                })
                .setNegativeButton("Cancel", (c, arg) -> {
                })
                .create().show();
    }

    private static boolean deleteFromDb(long id){
        try {
            db.delete(DbOpener.TABLE_NAME, DbOpener.COL_ID + "=?", new String[]{Long.toString(id)});

        }
        catch (Exception e){return false;}
        return true;
    }

    private Bitmap loadFile(String fileName){
        FileInputStream fis = null;
        try {
            fis = openFileInput(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Log.i("doInBackground: ", "Image " + fileName + " found on disk.");
        return BitmapFactory.decodeStream(fis);

    }


    /*Class ImageAdapter handles the items in the ListView*/
    private class ImageAdapter extends BaseAdapter {

        @Override
        public int getCount(){
            return images.size();
        }

        @Override
        public NasaImage getItem(int position){

            return images.get(position);

        }

        @Override
        public long getItemId(int position){
            return images.get(position).getId();
        }

        @Override
        public View getView(int position, View old, ViewGroup parent){
            View recycleView;
            LayoutInflater inflater = getLayoutInflater();

            if (old==null) {
                recycleView = inflater.inflate(R.layout.image_item, parent, false);

            }
            else {
                recycleView = old;
            }

            Log.i("FavouriteImages. ", "List item position: "+ position);
            //View newView = inflater.inflate(R.layout.image_item, parent, false);

            if(images !=null) {
                TextView titleText = recycleView.findViewById(R.id.itemTitle);
                titleText.setText( getItem(position).getTitle());
                TextView dateText = recycleView.findViewById(R.id.itemDate);
                dateText.setText(getItem(position).getDate());
                TextView urlText = recycleView.findViewById(R.id.itemUrl);
                urlText.setText(getItem(position).getImageUrl());
                ImageView imageView = recycleView.findViewById(R.id.itemWindow);
                String fileName = getItem(position).getFileName();
                imageView.setImageBitmap(loadFile(fileName));
            }
            return recycleView;
        }

        private View setView(View view){
            TextView titleText = view.findViewById(R.id.itemTitle);

            TextView dateText = view.findViewById(R.id.itemDate);

            TextView urlText = view.findViewById(R.id.itemUrl);

            ImageView imageView = view.findViewById(R.id.itemWindow);
            return view;
        }
    }
}
