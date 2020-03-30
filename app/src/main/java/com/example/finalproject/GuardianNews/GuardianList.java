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

public class GuardianList extends AppCompatActivity {
    ArrayList<String> webTitles = new ArrayList<>();
    ArrayList<String> webUrls = new ArrayList<>();
    ArrayList<String> sectionNames = new ArrayList<>();

    ListView newslv;
    String jsonResult;
    private ArrayList<GuardianNews> elements = new ArrayList<>();

    //database and adapter
    SQLiteDatabase db = null;
    NewsAdaptor na;


    //internet connection + bundle
    String jsonStr;
    JSONArray resultArray;
    ArrayList<JSONObject> news = new ArrayList<>();



    String baseURL = "https://content.guardianapis.com/search?api-key=1fb36b70-1588-4259-b703-2570ea1fac6a&q=";
    String searchText;

    //Progress bar + AsyncTask
    ProgressBar mProgressBar;
    AsyncTask<String, Integer, String> fetchNews;


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

    private class MyHTTPRequest extends AsyncTask<String, Integer, String> {
        //Type3                Type1
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

        //Type 2
        public void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            //Update GUI stuff only:
            mProgressBar.setVisibility(View.VISIBLE);
            mProgressBar.setProgress(values[0]);
        }

        //Type3
        public void onPostExecute(String fromDoInBackground) {
            //Log.i("HTTP", fromDoInBackground);
            super.onPostExecute(fromDoInBackground);

//            TextView t  = findViewById(R.id.temp);
//            t.setText(baseURL+searchText);


        }
    }
}
