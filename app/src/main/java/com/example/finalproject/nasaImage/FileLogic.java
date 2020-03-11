package com.example.finalproject.nasaImage;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class FileLogic {

    void startConnection(String requestedUrl){
        try {
            URL url = new URL(requestedUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            InputStream response = urlConnection.getInputStream();
        }
        catch (MalformedURLException ex){

        }
        catch (IOException ex){

        }

    }

    void downloadFile(){

    }

    boolean existOnDisk(String file){
        return false;
    }

}
