package com.example.finalproject.GuardianNews;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import android.os.Bundle;

import com.example.finalproject.R;

/**
 * This is list of news found with the key word entered in the GuardianMainActivity.
 *
 *@author Pei Lun Zou
 *@version 1.0
 */


public class GuardianList extends AppCompatActivity {

    /**
     * ArrayList of String which stores the titles of news.
     */
    ArrayList<String> webTitles = new ArrayList<>();

    /**
     * ArrayList of String which stores the urls of news.
     */
    ArrayList<String> webUrls = new ArrayList<>();

    /**
     * ArrayList of String which stores the section names of news.
     */
    ArrayList<String> sectionNames = new ArrayList<>();

    /**
     * ListView that display all news.
     */
    ListView newslv;

    /**
     * jsonResult is the string that combine base url of The Guardian with the user input key word.
     */
    String jsonResult;

    /**
     * ArrayList of GuardianNews called elements.
     */
    private ArrayList<GuardianNews> elements = new ArrayList<>();

    //database and adapter
    /**
     * db is the database
     */
    SQLiteDatabase db = null;

    /**
     * na is a NewsAdaptor.
     */
    NewsAdaptor na;


    //internet connection + bundle

    /**
     * jsonStr is the string that put the fetched result into one string.
     */
    String jsonStr;
    /**
     * This is an array of JSON objects.
     */
    JSONArray resultArray;

    /**
     * ArrayList of JSONobject which stores the fetched JSON objects.
     */
    ArrayList<JSONObject> news = new ArrayList<>();


    /**
     * The API url for The Guardian news.
     */
    String baseURL = "https://content.guardianapis.com/search?api-key=1fb36b70-1588-4259-b703-2570ea1fac6a&q=";

    /**
     * User input get from GuardianMainActivity.
     */
    String searchText;

    //Progress bar + AsyncTask
    /**
     * The progress bar that shows the progress of data fetching.
     */
    ProgressBar mProgressBar;

    /**
     * AsyncTask for internet connection and data fetching.
     */
    AsyncTask<String, Integer, String> fetchNews;


    /**
     * onCreate method of GuardianList which displays the title, and contains
     * functionality of progress bar and list view with onclickListener.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guardian_list);

        //ListView
        newslv = findViewById(R.id.guardianListView);
        Intent getInfo = getIntent();
        searchText = getInfo.getStringExtra("searchWord");

        mProgressBar = findViewById(R.id.progressBar);
        //mProgressBar.setVisibility(View.VISIBLE);

        // Fragment
        FrameLayout fl = findViewById(R.id.fragmentLocation);
        Boolean isTablet= findViewById(R.id.fragmentLocation) != null;

        //Internet Connection

        if (fetchNews != null) {
            fetchNews.cancel(true);
        }
        fetchNews = new MyHTTPRequest();

        try {
            jsonResult = fetchNews.execute(baseURL + searchText).get();
        } catch (InterruptedException e) {
            Log.e("Interrupt", e.getMessage());
        } catch (Exception e) {
            Log.e("ConcurrentException", e.getMessage());
        }

        //JSON array
        try {
            JSONObject jResponse = new JSONObject(jsonResult).getJSONObject("response");
            resultArray = jResponse.getJSONArray("results");

            for (int i = 0; i < resultArray.length(); i++) {
                news.add(resultArray.getJSONObject(i));
            }
            Log.e("NEWS", String.valueOf(news.size()));
            for (JSONObject j : news) {
                webTitles.add(j.getString("webTitle"));
                webUrls.add(j.getString("webUrl"));
                sectionNames.add(j.getString("sectionName"));
            }

        }catch (Exception e){
            Log.e("JsonException", e.getMessage());
        }

        for (int i = 0; i < webTitles.size(); i++) {
            elements.add(new GuardianNews(webTitles.get(i), webUrls.get(i), sectionNames.get(i)));
        }

        // ListView Adaptor
        na = new NewsAdaptor();

        newslv.setAdapter(na);

        //ListView onclickListener
        newslv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), GuardianNewsPage.class);
                intent.putExtra("title", webTitles.get(position));
                intent.putExtra("url", webUrls.get(position));
                intent.putExtra("section", sectionNames.get(position));
                startActivity(intent);
            }
        });

        //Fragment onLongclick
        newslv.setOnItemLongClickListener((parent, view, position, id) -> {
            if (isTablet) {
                Fragment fg2 = getSupportFragmentManager().findFragmentById(R.id.fragmentLocation);

                if (fg2 != null){
                    getSupportFragmentManager().beginTransaction().remove(fg2).commit();
                }

            }
            return true;

        });


    }

    /**
     * Adaptor class for the list view to display each row with guardianrow layout.
     */

    private class NewsAdaptor extends BaseAdapter {

        Context context;
        ArrayList<String> rowTitle;
        ArrayList<String> rowUrl;

        @Override
        public int getCount() {
            return elements.size();
        }

        @Override
        public String getItem(int position) {
            return elements.get(position).getTitle();
        }

        @Override
        public long getItemId(int position) {
            return (long) position;
        }

        @Override
        public View getView(int position, View old, ViewGroup parent) {

            LayoutInflater inflater = getLayoutInflater();
            View newView;

            newView = inflater.inflate(R.layout.guardianrow, parent, false);
            TextView titleText = newView.findViewById(R.id.titleRow);
            TextView urlText = newView.findViewById(R.id.urlRow);
            TextView snText = newView.findViewById(R.id.snRow);
            titleText.setText(webTitles.get(position));
            urlText.setText(webUrls.get(position));
            snText.setText(sectionNames.get(position));

            return newView;

        }

    }

    /**
     * HTTPRequest class which extends from AsyncTask for internet connection.
     */

    private class MyHTTPRequest extends AsyncTask<String, Integer, String> {

        /**
         * doInBackground returns the string item of AsyncTask
         */

        //Type1
        public String doInBackground(String... args) {
            try {

                //create a URL object of what server to contact:
                Log.e("URL", args[0]);
                URL url = new URL(args[0]);

                //open the connection
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                publishProgress(25);

                //get response code
                InputStream response = urlConnection.getInputStream();
                int responsecode = urlConnection.getResponseCode();

                publishProgress(50);

                //check code 200

                if (responsecode != 200)
                    throw new RuntimeException("HttpResponseCode: " + responsecode);
                else {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response, "UTF-8"), 8);
                    StringBuilder sb = new StringBuilder();

                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    jsonStr = sb.toString(); //result is the whole string
                    publishProgress(75);
                }


            } catch (Exception e) {
                Log.e("AsyncError", e.getMessage());
            }

            publishProgress(100);
            return jsonStr;

        }
        /**
         * onProgressUpdate returns the integer item of AsyncTask
         */
        //Type 2
        public void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            //Update GUI stuff only:
            mProgressBar.setVisibility(View.VISIBLE);
           // mProgressBar.setProgress(values[0]);
        }

        /**
         * onPostExecute returns the last string item of AsyncTask
         */
        //Type3
        public void onPostExecute(String fromDoInBackground) {
            //Log.i("HTTP", fromDoInBackground);
            super.onPostExecute(fromDoInBackground);

//            TextView t  = findViewById(R.id.temp);
//            t.setText(baseURL+searchText);


        }
    }
}
