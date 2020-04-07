package com.example.finalproject.BBCNews;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

import com.example.finalproject.GuardianMainActivity;
import com.example.finalproject.MainActivity;
import com.example.finalproject.R;
import com.example.finalproject.nasaImage.NasaImageOfTheDay;
import com.example.finalproject.yiqiFunction.ImageSearch;
import com.google.android.material.navigation.NavigationView;
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
 * @author Xiaoting Kong
 * @version 1.0
 */
public class BbcNews extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    /**
     * An ArrayList of News representing news.
     */
    List<News> newsList = new ArrayList<>();
    List<News> favouriteList1 = new ArrayList<>();
    MyListAdapter myListAdapter;
    ListView theList;
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
     * <p>
     * {@value #ITEM_INFORMATION}
     */
    public static final String ITEM_INFORMATION = "INFORMATION";

    /**
     * Value representing "item link".
     * <p>
     * {@value #ITEM_LINK}
     */
    public static final String ITEM_LINK = "LINK";

    /**
     * onDownloadComplete method of BBC News activity.
     * Initialize list adapter and use it for the listView after downloading news from BBC is complete.
     */
    public void onDownloadComplete(){
        myListAdapter = new MyListAdapter(this, newsList);
        theList.setAdapter(myListAdapter);
    }

    /**
     * onCreate method of BBC News activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bbc_activity_bbcnews);

        // Load favourites from database.
        loadDataFromDatabase();

        progressBar = (ProgressBar) findViewById(R.id.ProgressBar);
        progressBar.setVisibility(View.VISIBLE);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer, myToolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null); //this line avoids the icons to appear shaded gray. src: https://stackoverflow.com/questions/31394265/navigation-drawer-item-icon-not-showing-original-colour
        navigationView.setNavigationItemSelectedListener(this);

        // Do AsyncTask work
        DownloadNews downloadNews = new DownloadNews();
        downloadNews.execute();

        // Add favourites to the whole list
        if (favouriteList1 != null) {
            for (int i = 0; i < favouriteList1.size(); i++) {
                newsList.add(favouriteList1.get(i));
            }
        }

        // Get the Data Repository in write mode
        DbHandler dbOpener = new DbHandler(BbcNews.this);
        db = dbOpener.getWritableDatabase();

        // Set the whole list in MyListAdapter
        theList = findViewById(R.id.news_list);

        /**
         * setOnItemClickListener method of theList.
         */
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

        /**
         * setOnItemLongClickListener method of theList.
         */
        theList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BbcNews.this);
                alertDialogBuilder.setTitle((BbcNews.this).getResources().getString(R.string.bbc_checkinfo));
                alertDialogBuilder.setMessage((BbcNews.this).getResources().getString(R.string.bbc_message1) + position
                        + "\n" + (BbcNews.this).getResources().getString(R.string.bbc_message2) + id + "\n" + newsList.get(position).getIsFavourite());
                alertDialogBuilder.setPositiveButton((BbcNews.this).getResources().getString(R.string.back), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialogBuilder.show();
                return true;
            }
        });

        Button btn1 = (Button)findViewById(R.id.bbc_refresh);
        /**
         * setOnClickListener method of btn1.
         */
        btn1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                finish();
                startActivity(new Intent(BbcNews.this, BbcNews.class));
            }
        });
    }

    /**
     * This inner class is the class to set listView adapter.
     */
    private class MyListAdapter extends BaseAdapter implements View.OnClickListener {
        private Context context;
        private List<News> lists;
        boolean isFavorite;

        /**
         * Constructor of this MyListAdapter inner class.
         */
        public MyListAdapter(Context context, List<News> lists) {
            super();
            this.context = context;
            this.lists = lists;
        }

        /**
         * This function tells how many objects to show.
         */
        public int getCount() {
            return lists.size();
        }

        /**
         * This returns the string at position p.
         *
         * @param position  an int representing position
         */
        public Object getItem(int position) {
            return lists.get(position);
        }

        /**
         * This returns the database id of the item at position p.
         *
         * @param p  an int representing position
         */
        public long getItemId(int p) {
            return lists.get(p).getId();
        }

        /**
         * getView method of this MyListAdapter inner class.
         */
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

        /**
         * onClick method of this MyListAdapter inner class.
         */
        @Override
        public void onClick(View v) {
            int i = (int) v.getTag();
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.mBtn = (ImageButton) v.findViewById(R.id.add);
            if (v.getId() == R.id.add && newsList.get(i).getIsFavourite()==false) {
                Snackbar.make(viewHolder.mBtn, (BbcNews.this).getResources().getString(R.string.bbc_addToFavour), Snackbar.LENGTH_SHORT).show();
                viewHolder.mBtn.setImageResource(R.drawable.bbc_heart2);
                isFavorite = true;
                newsList.get(i).setIsFavourite(true);

                if (newsList.get(i).getIsFavourite() == true) {
                    //Create a new map of values, where column names are the keys
                    ContentValues cValues = new ContentValues();

                    String title_data = newsList.get(i).getTitle();
                    String des_data = newsList.get(i).getDescription();
                    String date_data = newsList.get(i).getDate();
                    String link_data = newsList.get(i).getLink();

                    cValues.put(DbHandler.COL_TITLE, title_data);
                    cValues.put(DbHandler.COL_DESCRIPTION, des_data);
                    cValues.put(DbHandler.COL_DATE, date_data);
                    cValues.put(DbHandler.COL_LINK, link_data);
                    cValues.put(DbHandler.COL_FAVOURITE, "true");
                    long newRowId = db.insert(DbHandler.TABLE_NAME, null, cValues);
                    newsList.get(i).setId(newRowId);

                }
            }
        }
    }

    /**
     * This inner class is the ViewHolder class.
     */
    public static class ViewHolder {
        ImageButton mBtn;
    }

    /**
     * onCreateOptionsMenu method of this BBC News activity.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.bbc_favourite_menu, menu);
        return true;
    }

    /**
     * onOptionsItemSelected method of this BBC News activity.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String message = null;

        switch (item.getItemId()) {
            // what to do when the menu item is selected:
            case R.id.bbc_bbc:
                startActivity(new Intent(BbcNews.this, BbcNews.class));
                break;
            case R.id.bbc_guardian:
                startActivity(new Intent(BbcNews.this, GuardianMainActivity.class));
                break;
            case R.id.bbc_earth:
                startActivity(new Intent(BbcNews.this, ImageSearch.class));
                break;
            case R.id.bbc_nasaImage:
                startActivity(new Intent(BbcNews.this, NasaImageOfTheDay.class));
                break;
            case R.id.item1:
//                message = "Introduction for this page";
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BbcNews.this);
                alertDialogBuilder.setTitle((BbcNews.this).getResources().getString(R.string.bbc_title));
                alertDialogBuilder.setMessage((BbcNews.this).getResources().getString(R.string.bbc_intro));
                LayoutInflater inflater = getLayoutInflater();
                alertDialogBuilder.setView(inflater.inflate(R.layout.bbc_intro, null));
                alertDialogBuilder.setPositiveButton((BbcNews.this).getResources().getString(R.string.bbc_back), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialogBuilder.show();
                break;
            case R.id.item3:
                message = (BbcNews.this).getResources().getString(R.string.bbc_favouriteList);
                startActivity(new Intent(BbcNews.this, FavouriteList.class));
                break;
        }

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        return true;
    }

    /**
     * onNavigationItemSelected method of this BBC News activity.
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.bbc:
                startActivity(new Intent(BbcNews.this, BbcNews.class));
                break;
            case R.id.guardian:
                startActivity(new Intent(BbcNews.this, GuardianMainActivity.class));
                break;
            case R.id.earth:
                startActivity(new Intent(BbcNews.this, ImageSearch.class));
                break;
            case R.id.nasaImage:
                startActivity(new Intent(BbcNews.this, NasaImageOfTheDay.class));
                break;
            case R.id.mainHelp:
                Toast.makeText(this, R.string.snackbarChang, Toast.LENGTH_LONG).show();
                break;
        }

        return false;
    }


    /**
     * This inner class is the class to download BBC news.
     */
    public class DownloadNews extends AsyncTask<String, Integer, List<News>> {

        /**
         * Download BBC news within doInBackground method.
         */
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
                        boolean isLinkInDb = false;

                        for (int i = 0; i < favouriteList1.size(); i++) {
                            if ((favouriteList1.get(i).getLink()).equals(link)) {
                                isLinkInDb = true;
                                break;
                            }
                        }

//                        for (int i = 0; i < newsList.size(); i++) {
//                            if ((newsList.get(i).getLink()).equals(link)) {
//                                isLinkInDb = true;
//                                break;
//                            }
//                        }

                        if (!isLinkInDb) {
                            newsList.add(new News(0, title, description, date, link, false));
                        }


                    }

                    xpp.next(); // move the pointer to next XML element
                }
                Log.i("INFO", "Success, number of news pulled from BBC: " + newsList.size());
                return /*newsList1*/ newsList;
            } catch (MalformedURLException mfe) {
                ret = "Malformed URL exception";
            } catch (IOException ioe) {
                ret = "IO Exception. Is the Wifi connected?";
            } catch (XmlPullParserException pe) {
                ret = "XML Pull exception. The XML is not properly formed";
            }
            Log.i("INFO", "Failure, "+ ret + ", number of news pulled from BBC: "+ newsList.size());
            publishProgress(100);
            return newsList;
        }

        /**
         * Set progress bar within onProgressUpdate method.
         */
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(values[0]);
        }

        /**
         * Set progress bar within onPostExecute method and notify UI thread about download complete.
         */
        protected void onPostExecute(List<News> s) {
            super.onPostExecute(s);
            onDownloadComplete();
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Load favourite news from database.
     */
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

        // iterate over the results, return true if there is a next item:
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
            favouriteList1.add(new News(id, title0, description0, date0, link0, IsFavourite));
        }
    }

    private boolean firstLanuch = false;

    /**
     * onResume method of this BBC News activity.
     */
    public void onResume() {
        super.onResume();
        if (firstLanuch)
            restartActivity(BbcNews.this);
        firstLanuch = true;
        // use boolean varible ignore the first launch.
    }

    /**
     * restartActivity method of this BBC News activity.
     */
    public static void restartActivity(Activity activity) {
        Intent intent = new Intent();
        intent.setClass(activity, activity.getClass());
        activity.startActivity(intent);
        activity.overridePendingTransition(0, 0);
        activity.finish();
    }
}
