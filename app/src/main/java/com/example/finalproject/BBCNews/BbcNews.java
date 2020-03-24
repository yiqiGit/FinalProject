package com.example.finalproject.BBCNews;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalproject.MainActivity;
import com.example.finalproject.R;
import com.google.android.material.snackbar.Snackbar;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is the main activity class for BBC News.
 *
 *@author Xiaoting Kong
 *@version 1.0
 */
public class BbcNews extends AppCompatActivity {
    /**
     * An ArrayList of News representing news.
     */
    List<News> newsList;

    /**
     * An ArrayList of News representing favourite news.
     */
    List<News> favouriteList;

    /**
     * A ProgressBar representing progress bar.
     */
    ProgressBar progressBar;

    /**
     * SQL database.
     */
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
        setContentView(R.layout.bbc_activity_bbcnews);
        progressBar = (ProgressBar) findViewById(R.id.ProgressBar);
        progressBar.setVisibility(View.VISIBLE);

        DownloadNews downloadNews = new DownloadNews();

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        // Do AsyncTask work
        downloadNews.execute();
        newsList = downloadNews.getNewsList();

        // Set the whole list in MyListAdapter
        MyListAdapter myListAdapter = new MyListAdapter(this, newsList);
        ListView theList = findViewById(R.id.news_list);
        theList.setAdapter(myListAdapter);

        theList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle dataToPass = new Bundle();
                dataToPass.putString(ITEM_INFORMATION, newsList.get(position).toString());
                dataToPass.putString(ITEM_LINK, newsList.get(position).getLink());

                Intent nextActivity = new Intent(BbcNews.this, EmptyActivity.class);
                nextActivity.putExtras(dataToPass); // send data to next activity
                startActivity(nextActivity); // make the transition
            }
        });

//        theList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
//                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder (BbcNews.this);
//                alertDialogBuilder.setTitle((BbcNews.this).getResources().getString(R.string.title));
//                alertDialogBuilder.setMessage((BbcNews.this).getResources().getString(R.string.message1) + position
//                        +"\n"+ (BbcNews.this).getResources().getString(R.string.message2)+ id+"\n"+ newsList.get(position).getIsFavourite());
//                alertDialogBuilder.setPositiveButton((BbcNews.this).getResources().getString(R.string.back), new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                });
//                alertDialogBuilder.show();
//                return true;
//            }
//        });
    }

    /**
     * This inner class is the class to set listView adapter.
     */
    private class MyListAdapter extends BaseAdapter implements View.OnClickListener {
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
            newView = inflater.inflate(R.layout.bbc_for_list, parent, false);
            bnews = (TextView) newView.findViewById(R.id.news);

            ViewHolder viewHolder = new ViewHolder();
            viewHolder.mBtn = (ImageButton) newView.findViewById(R.id.add);
//            bnews.setText(lists.get(p).getTitle());
            viewHolder.mBtn.setOnClickListener(this);
            viewHolder.mBtn.setTag(p);
            bnews.setText(lists.get(p).getTitle());

            if (lists.get(p).getIsFavourite()) {
                viewHolder.mBtn.setImageResource(R.drawable.bbc_heart2);
            }

            return newView;
        }

        // int count = 0;
        @Override
        public void onClick(View v) {
            int i = (int) v.getTag();
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.mBtn = (ImageButton) v.findViewById(R.id.add);
//            if(v.getId()==R.id.add && count % 2 ==0){
            if (v.getId() == R.id.add) {
                Snackbar.make(viewHolder.mBtn, "Added to favourite list", Snackbar.LENGTH_SHORT).show();
                viewHolder.mBtn.setImageResource(R.drawable.bbc_heart2);
                isFavorite = true;
                newsList.get(i).setIsFavourite(true);
            }
//            else if(v.getId()==R.id.add && count % 2 ==1){
//                Snackbar.make(viewHolder.mBtn, "Moved  from favourite list", Snackbar.LENGTH_INDEFINITE).show();
//                viewHolder.mBtn.setImageResource(R.drawable.bbc_heart1);
//                isFavorite = false;
//                newsList.get(i).setIsFavourite(false);
//            }
//            count= count +1;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.bbc_favourite_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String message = null;

        switch (item.getItemId()) {
            // what to do when the menu item is selected:
            case R.id.item1:
//                message = "Introduction for this page";
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BbcNews.this);
                alertDialogBuilder.setTitle((BbcNews.this).getResources().getString(R.string.bbc_title));
                alertDialogBuilder.setMessage("Introduction for this page,todo....");
                alertDialogBuilder.setPositiveButton((BbcNews.this).getResources().getString(R.string.bbc_back), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialogBuilder.show();
                break;
            case R.id.item2:
                message = "Home page";
                startActivity(new Intent(BbcNews.this, MainActivity.class));
                break;
            case R.id.item3:
                message = "Favourite list";
                break;
            case R.id.item4:
                //Do something here
                message = "Refresh";
                break;
        }

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        return true;
    }

    static class ViewHolder {
        ImageButton mBtn;
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

//            long id = results.getLong(idColIndex);
            // add the news to the array list:
            newsList.add(new News(id, title0, description0, date0, link0, IsFavourite));
        }
    }

    /**
     * This inner class is the class to download BBC news.
     */
    public class DownloadNews extends AsyncTask<String, Integer, List<News>> {
        /**
         * An ArrayList of News representing news.
         */
        private List<News> newsList = new ArrayList<>();

        @Override
        protected List<News> doInBackground(String... params) {
            String ret = null;
            String queryURL = "http://feeds.bbci.co.uk/news/world/us_and_canada/rss.xml";
            try {       // Connect to the server:
                URL url = new URL(queryURL);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inStream = urlConnection.getInputStream();

                // Set up the XML parser
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(inStream, "UTF-8");

                // Iterate over the XML tags
                boolean inItem = false;
                int EVENT_TYPE;
                String title = null;
                String description = null;
                String date = null;
                String link = null;
                // While not the end of the document
                while ((EVENT_TYPE = xpp.getEventType()) != XmlPullParser.END_DOCUMENT) {
                    if (EVENT_TYPE == XmlPullParser.START_TAG) {
                        if (xpp.getName().equalsIgnoreCase("item")) {
                            inItem = true;
                        } else if (xpp.getName().equalsIgnoreCase("title")) {
                            if (inItem) {
                                title = xpp.nextText();
                                publishProgress(20);
                            }
                        } else if (xpp.getName().equalsIgnoreCase("description")) {
                            if (inItem) {
                                description = xpp.nextText();
                                publishProgress(40);
                            }
                        } else if (xpp.getName().equalsIgnoreCase("link")) {
                            if (inItem) {
                                link = xpp.nextText();
                                publishProgress(60);
                            }
                        } else if (xpp.getName().equalsIgnoreCase("pubDate")) {
                            if (inItem) {
                                date = xpp.nextText();
                                publishProgress(80);
                            }
                        }
                    } else if (EVENT_TYPE == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item")) {
                        inItem = false;
                        newsList.add(new News(0, title, description, date, link, false));
                        publishProgress(100);
                    }

                    xpp.next(); // move the pointer to next XML element
                }
                return newsList;

            } catch (MalformedURLException mfe) {
                ret = "Malformed URL exception";
            } catch (IOException ioe) {
                ret = "IO Exception. Is the Wifi connected?";
            } catch (XmlPullParserException pe) {
                ret = "XML Pull exception. The XML is not properly formed";
            }
            return null;
        }

        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(values[0]);
        }

        protected void onPostExecute(List<News> s) {
            super.onPostExecute(s);
            progressBar.setVisibility(View.VISIBLE);
//            progressBar.setVisibility(View.INVISIBLE);
        }

        public List<News> getNewsList() {
            return newsList;
        }
    }

}
