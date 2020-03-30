package com.example.finalproject.GuardianNews;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.finalproject.R;

import java.util.ArrayList;

public class GuardianFavoriteList extends AppCompatActivity {

    protected static ArrayList<Long> favId = new ArrayList<>();
    protected static ArrayList<String> favWebTitles = new ArrayList<>();
    protected static ArrayList<String> favWebUrls = new ArrayList<>();
    protected static ArrayList<String> favSectionNames = new ArrayList<>();

    ListView favoritelv;
    protected static FavAdaptor fa;

    SQLiteDatabase db = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guardian_favorite_list);


        LoadDataFromDatabase();

        favoritelv = findViewById(R.id.guardianfavlist);
        fa = new FavAdaptor();

        favoritelv.setAdapter(fa);

        //ListView onclicklistener to go to news page

        favoritelv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), GuardianNewsPage.class);
                intent.putExtra("title", favWebTitles.get(position));
                intent.putExtra("url", favWebUrls.get(position));
                intent.putExtra("section", favSectionNames.get(position));
                startActivity(intent);
            }
        });



        //ListView onclickListener to delete
        favoritelv.setOnItemLongClickListener((p, b, pos, id) -> {
            AlertDialog.Builder deleteAlert = new AlertDialog.Builder(this);

            deleteAlert.setTitle("Delete")

                    //What is the message:
                    .setMessage("Do you want to delete this from favorite list?")

                    //what the Yes button does:
                    .setPositiveButton("Yes", (click, arg) -> {

                        db.delete(GuardianOpener.TABLE_NAME, GuardianOpener.COL_URL + "=?", new String[]{favWebUrls.get(pos)});

                        favWebTitles.remove(pos);
                        favWebUrls.remove(pos);
                        favSectionNames.remove(pos);
                        fa.notifyDataSetChanged();
                    })
                    //What the No button does:
                    .setNegativeButton("No", (click, arg) -> {
                    })

                    //An optional third button:
                    .setNeutralButton("Maybe", (click, arg) -> {
                    })


                    //Show the dialog
                    .create().show();
            return true;
        });

       fa.notifyDataSetChanged();


    }


    private void LoadDataFromDatabase(){

       GuardianOpener dbOpener = new GuardianOpener(this);
       db = dbOpener.getWritableDatabase();

        String[] columns = {GuardianOpener.COL_ID, GuardianOpener.COL_TITLE, GuardianOpener.COL_URL, GuardianOpener.COL_SECTIONNAME};

        Cursor results = db.query (false, GuardianOpener.TABLE_NAME, columns, null, null, null, null, null, null);

        int idColIndex = results.getColumnIndex(GuardianOpener.COL_ID);
        int titleColIndex = results.getColumnIndex(GuardianOpener.COL_TITLE);
        int urlColIndex = results.getColumnIndex(GuardianOpener.COL_URL);
        int sectionColIndex = results.getColumnIndex(GuardianOpener.COL_SECTIONNAME);

        while (results.moveToNext()){
            long id = results.getLong(idColIndex);


            String title = results.getString(titleColIndex);
            String url = results.getString(urlColIndex);
            String sectionName = results.getString(sectionColIndex);


            favId.add(id);
            favWebTitles.add(title);
            favWebUrls.add(url);
            favSectionNames.add(sectionName);

        }

        printCursor(results, db.getVersion());
        results.close();

    }

    private void printCursor (Cursor c, int version){

        Log.i("CURSOR", "Database Version Number: " + version
                + " Number of columns: " + c.getColumnCount()
                + " Number of results: " + c.getCount());

        String Header = "";
        for(int i = 0; i< c.getColumnCount();i++){
            Header = Header.concat(c.getColumnName(i)+" ");
        }
        Log.i("CURSOR","Columns "+ Header);

        c.moveToFirst();
        while(c.isAfterLast()==false){
            String Results = "";
            for(int i = 0; i< c.getColumnCount();i++){
                Results = Results.concat(c.getString(i)+" ");
            }
            Log.i("CURSOR","Row "+ c.getPosition()+ " : " + Results);
            c.moveToNext();
        }


    }








    protected class FavAdaptor extends BaseAdapter {

        Context context;
        ArrayList<String> rowTitle;
        ArrayList<String> rowUrl;

        @Override
        public int getCount() {
            return favWebTitles.size();
        }

        @Override
        public String getItem(int position) {
            return favWebTitles.get(position);
        }

        @Override
        public long getItemId(int position) {
            return (long) position;
        }

        @Override
        public View getView(int position, View old, ViewGroup parent) {

            LayoutInflater inflater = getLayoutInflater();
            View newView;

            newView = inflater.inflate(R.layout.guardianfavoriterow, parent, false);
            TextView titleText = newView.findViewById(R.id.favtitleRow);
            TextView urlText = newView.findViewById(R.id.favurlRow);
            TextView snText = newView.findViewById(R.id.favsnRow);

            titleText.setText(favWebTitles.get(position));
            urlText.setText(favWebUrls.get(position));
            snText.setText(favSectionNames.get(position));

            Log.e("title", titleText.getText().toString());

            return newView;

        }

    }

    @Override
    protected void onPause(){
        super.onPause();
        favId.clear();
       favWebTitles.clear();
        favWebUrls.clear();
        favSectionNames.clear();



    }

    @Override
    protected void onStop(){
        super.onStop();
        favId.clear();
        favWebTitles.clear();
        favWebUrls.clear();
        favSectionNames.clear();

    }

    @Override
    protected void onDestroy(){
       super.onDestroy();
        favId.clear();
        favWebTitles.clear();
        favWebUrls.clear();
        favSectionNames.clear();

    }



}
