package com.example.finalproject.BBCNews;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.finalproject.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class FavouriteList extends AppCompatActivity {
    List<News> favouriteList= new ArrayList<>();
    SQLiteDatabase db;
    /**
     * Value representing "item information".
     *
     * {@value #ITEM_INFORMATION}
     */
    public static final String ITEM_INFORMATION = "INFORMATION";

    /**
     * Value representing "item link".
     *
     * {@value #ITEM_LINK}
     */
    public static final String ITEM_LINK = "LINK";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bbc_activity_favourite_list2);

        loadDataFromDatabase();

        // Set the whole list in MyListAdapter
        MyListAdapter myListAdapter = new MyListAdapter(this, favouriteList);
        ListView theList = findViewById(R.id.favour_list);
        theList.setAdapter(myListAdapter);


        theList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle dataToPass = new Bundle();
                dataToPass.putString(ITEM_INFORMATION, favouriteList.get(position).toString());
                dataToPass.putString(ITEM_LINK, favouriteList.get(position).getLink());

                Intent nextActivity = new Intent(FavouriteList.this, EmptyActivity.class);
                nextActivity.putExtras(dataToPass); // send data to next activity
                startActivity(nextActivity); // make the transition
            }
        });

        theList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder (FavouriteList.this);
                alertDialogBuilder.setTitle((FavouriteList.this).getResources().getString(R.string.bbc_delete));
                alertDialogBuilder.setMessage( (FavouriteList.this).getResources().getString(R.string.bbc_message1)+ position
                        +"\n"+ (FavouriteList.this).getResources().getString(R.string.bbc_message2)+ id+"\n"+ favouriteList.get(position).getIsFavourite());
                alertDialogBuilder.setPositiveButton((FavouriteList.this).getResources().getString(R.string.bbc_yes), new DialogInterface.OnClickListener() {
                    News selected = favouriteList.get(position);
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteMessageFromDb(selected);
                        favouriteList.remove(selected);
                        myListAdapter.notifyDataSetChanged();
                    }
                });
                alertDialogBuilder.setNegativeButton((FavouriteList.this).getResources().getString(R.string.bbc_no), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });
                alertDialogBuilder.show();
                return true;
            }
        });
    }


    public class MyListAdapter extends BaseAdapter {
        private Context context;
        private List<News> lists;
        boolean isFavorite;

        public MyListAdapter(Context context, List<News> lists) {
            super();
            this.context = context;
            this.lists = lists;
        }

        public int getCount() {
            return lists.size();
        } //This function tells how many objects to show

        public Object getItem(int position) {
            return lists.get(position);
        }  //This returns the string at position p

        public long getItemId(int p) {
            return lists.get(p).getId();
        } //This returns the database id of the item at position p

        public View getView(int p, View recycled, ViewGroup parent) {
            TextView bnews;
            LayoutInflater inflater = getLayoutInflater();
            View newView = recycled;
            newView = inflater.inflate(R.layout.bbc_for_favour_list, parent, false);
            bnews = (TextView) newView.findViewById(R.id.favour_news);
            bnews.setText(lists.get(p).getTitle());

            return newView;
        }
    }


    private void loadDataFromDatabase() {
        // get a database connection
        DbHandler dbOpener = new DbHandler(this);
        db = dbOpener.getWritableDatabase();

        String[] columns = {DbHandler.COL_ID, DbHandler.COL_TITLE, DbHandler.COL_DESCRIPTION,
                DbHandler.COL_DATE, DbHandler.COL_LINK, DbHandler.COL_FAVOURITE};
        Cursor results = db.query(false, DbHandler.TABLE_NAME, columns, null, null, null,
                null, null, null);
        int idColIndex = results.getColumnIndex(DbHandler.COL_ID);
        int Title_ColIndex = results.getColumnIndex(DbHandler.COL_TITLE);
        int Description_ColIndex = results.getColumnIndex(DbHandler.COL_DESCRIPTION);
        int Date_ColIndex = results.getColumnIndex(DbHandler.COL_DATE);
        int Link_ColIndex = results.getColumnIndex(DbHandler.COL_LINK);
        int IsFavourite_ColIndex = results.getColumnIndex(DbHandler.COL_FAVOURITE);
//        int idColIndex = results.getColumnIndex(DbHandler.COL_ID);

        //iterate over the results, return true if there is a next item:
        while (results.moveToNext()) {
            Boolean IsFavourite;
            long id = results.getLong(idColIndex);
            String title0 = results.getString(Title_ColIndex);
            String description0 = results.getString(Description_ColIndex);
            String date0 = results.getString(Date_ColIndex);
            String link0 = results.getString(Link_ColIndex);
            String IsFavourite0 = results.getString(IsFavourite_ColIndex);
            if (IsFavourite0.equals("true")) {
                IsFavourite = true;
            } else IsFavourite = false;

            // add the news to the array list:
            favouriteList.add(new News(id, title0, description0, date0, link0, IsFavourite));
        }
    }

    protected void deleteMessageFromDb(News favour_news)
    {
        db.delete(DbHandler.TABLE_NAME, DbHandler.COL_ID + "= ?", new String[] {Long.toString(favour_news.getId())});
    }
}