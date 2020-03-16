package com.example.finalproject.nasaImage;

import androidx.appcompat.app.AppCompatActivity;


import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.finalproject.R;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class FavouriteImages extends AppCompatActivity {

    private SQLiteDatabase db;
    private List<NasaImage> images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_images);
        Resources res = this.getResources();
        ListView myList = findViewById(R.id.imageList);
        images = new ArrayList<>();

        loadDataFromDatabase();
        ChatAdapter chatAdapter = new ChatAdapter();
        myList.setAdapter( chatAdapter);
    }

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

    private class ChatAdapter extends BaseAdapter {

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
            int rowLayout;
            LayoutInflater inflater = getLayoutInflater();
            //if (messagesArray.get(position).isSend()) rowLayout = R.layout.send_row_layout;
            //else rowLayout = R.layout.receive_row_layout;
            View newView = inflater.inflate(R.layout.image_item, parent, false);
            if(images !=null) {
                TextView titleText = newView.findViewById(R.id.itemTitle);
                titleText.setText( getItem(position).getTitle());
                TextView dateText = newView.findViewById(R.id.itemDate);
                dateText.setText(getItem(position).getDate());
                TextView urlText = newView.findViewById(R.id.itemUrl);
                urlText.setText(getItem(position).getImageUrl());
                ImageView imageView = newView.findViewById(R.id.itemWindow);
                String fileName = getItem(position).getFileName();
                imageView.setImageBitmap(loadFile(fileName));
            }
            return newView;
        }
    }
}
