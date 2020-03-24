package com.example.finalproject.BBCNews;

import android.os.AsyncTask;

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


public class CatchNews extends AsyncTask<String, Integer, List<News>> {

    private List<News> newsList= new ArrayList<>();

    @Override
    protected List<News> doInBackground(String... params) {
        String ret = null;
        String queryURL = "http://feeds.bbci.co.uk/news/world/us_and_canada/rss.xml";
        try {       // Connect to the server:
            URL url = new URL(queryURL);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            InputStream inStream = urlConnection.getInputStream();

            //Set up the XML parser:
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(false);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(inStream, "UTF-8");


            //Iterate over the XML tags:
            boolean inItem = false;
            int EVENT_TYPE;
            String title = null;String description =null;String date=null; String link = null;
            //While not the end of the document:
            while ((EVENT_TYPE = xpp.getEventType()) != XmlPullParser.END_DOCUMENT) {
                if (EVENT_TYPE == XmlPullParser.START_TAG) {
                    if (xpp.getName().equalsIgnoreCase("item")) {
                        inItem = true;
                    } else if (xpp.getName().equalsIgnoreCase("title")) {
                        if (inItem){
                            title = xpp.nextText();
                        }
                    } else if (xpp.getName().equalsIgnoreCase("description")) {
                        if (inItem) {
                            description = xpp.nextText();
                        }
                    }else if (xpp.getName().equalsIgnoreCase("link")) {
                        if (inItem) {
                            link = xpp.nextText();
                        }
                    }else if (xpp.getName().equalsIgnoreCase("pubDate")) {
                        if (inItem) {
                            date = xpp.nextText();
                        }
                    }
                } else if (EVENT_TYPE == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item")) {
                    inItem = false;
                    newsList.add(new News(0,title,description,date,link,false));
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

    protected void onProgressUpdate(Integer... values){
        super.onProgressUpdate(values);

    }


    protected void onPostExecute(List<News> s){
        super.onPostExecute(s);

    }

    public List<News> getNewsList() {

        return newsList;
    }
}